// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class can apply transformations to metrics which know how to accept signals.
 * White or black listed signals are not
 */
public class SignalHandler {

    public static SignalHandler DEFAULT_SIGNAL_HANDLER = new SignalHandler(
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            DefaultSignals.simpleMetricTransformFunction
    );

    private final List<String> blacklist;
    private final List<String> whitelist;
    List<SignalHandler> delegates;
    private final Function<String, MetricTransformer> transformers;

    /**
     * Constructor.
     *
     * @param whitelist  Signals that this handler directly supports.
     * @param blacklist  Signals that this handler will neither support nor delegate.
     * @param delegates  Dependent signal handlers that can be delegated to.
     * @param transformers  A function mapping signal values to transform functions
     */
    public SignalHandler(
            List<String> whitelist,
            List<String> blacklist,
            List<SignalHandler> delegates,
            Function<String, MetricTransformer> transformers
    ) {
        this.blacklist = blacklist;
        this.whitelist = whitelist;
        this.delegates = delegates;
        this.transformers = transformers;
    }

    /**
     * Determine if this signal handler or it's dependents accepts this type of signal.
     *
     * @param signalName The name of the signal.
     *
     * @return true if this metric directly or indirectly supports this signal.
     */
    public boolean accepts(String signalName) {
        if (whitelist.contains(signalName)) {
            return true;
        }
        if (blacklist.contains(signalName)) {
            return false;
        }
        return delegates.stream().anyMatch(it -> it.accepts(signalName));
    }

    /**
     * Create a signal handler which doesn't accepts a certain set of signals.
     *
     * @param signal  A signal to not handle.
     *
     * @return A signal handler with additional signals bound.
     */
    public SignalHandler withoutSignal(String signal) {
        return withoutSignals(Collections.singletonList(signal));
    }

    /**
     * Create a signal handler which doesn't accepts a certain set of signals.
     *
     * @param signals  The signals to not handle.
     *
     * @return A signal handler with additional signals bound.
     */
    public SignalHandler withoutSignals(List<String> signals) {
        List<String> newWhiteList = whitelist.stream()
                .filter(signal -> !signals.contains(signal))
                .collect(Collectors.toList());
        List<String> newBlackList = Stream.concat(
                signals.stream(),
                blacklist.stream()
        ).collect(Collectors.toList());
        return new SignalHandler(newWhiteList, newBlackList, delegates, transformers);
    }

    /**
     * Create a signal handler which accepts a certain set of signals.
     *
     * @param signals  The signals to not handle.
     *
     * @return A signal handler with additional signals bound.
     */
    public SignalHandler withSignals(List<String> signals) {
        List<String> newWhiteList = Stream.concat(
                signals.stream(),
                whitelist.stream()
        ).collect(Collectors.toList());
        List<String> newBlackList = blacklist.stream()
                .filter(signal -> !signals.contains(signal))
                .collect(Collectors.toList());
        return new SignalHandler(newWhiteList, newBlackList, delegates, transformers);
    }

    /**
     * Return the metric transformer for a given signal and values.
     *
     * @param signalValues
     *
     * @return A LogicalMetric transformed by this signal.
     */
    public LogicalMetric acceptSignal(LogicalMetric logicalMetric, String signalName, Map<String,String> signalValues)
            throws UnknownSignalValueException {
        transformers.apply(signalName).apply(logicalMetric, signalName, signalValues);
    }

}
