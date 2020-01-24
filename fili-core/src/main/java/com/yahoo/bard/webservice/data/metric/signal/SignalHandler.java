// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class can apply transformations to metrics which know how to accept signals.
 * White or black listed signals are not
 */
public class SignalHandler {

    /**
     * A substitute for Boolean to indicate Yes, No or Uknown in the case where delegates might service a signal.
     */
    public enum Accepts {
        TRUE,
        FALSE,
        MAYBE
    }

    private final List<String> whitelist;
    private final List<String> blacklist;
    private final Function<String, MetricTransformer> transformers;

    /**
     * Constructor.
     *
     * @param whitelist  Signals that this handler directly supports.
     * @param blacklist  Signals that this handler will neither support nor delegate.
     * @param transformers  A function mapping signal values to transform functions
     */
    public SignalHandler(
            List<String> whitelist,
            List<String> blacklist,
            Function<String, MetricTransformer> transformers
    ) {
        this.blacklist = blacklist;
        this.whitelist = whitelist;
        this.transformers = transformers;
    }

    /**
     * Determine if this signal handler or it's dependents accepts this type of signal.
     *
     * @param signalName The name of the signal.
     *
     * @return TRUE if this metric directly or indirectly supports this signal, FALSE if it refuses, MAYBE if it
     * doesn't assert authority.
     */
    public Accepts accepts(String signalName) {
        return whitelist.contains(signalName) ? Accepts.TRUE :
                blacklist.contains(signalName) ?
                        Accepts.FALSE :
                        Accepts.MAYBE;
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
        return new SignalHandler(newWhiteList, newBlackList, transformers);
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
        return new SignalHandler(newWhiteList, newBlackList, transformers);
    }

    /**
     * Return the metric transformer for a given signal and values.
     *
     * @param logicalMetric The metric which should be transformed by this signal handler.
     * @param signalName  The name of the signal to be processed.
     * @param signalValues The map of values used to transform this metric.
     *
     * @return A LogicalMetric transformed by this signal.
     * @throws UnknownSignalValueException if the signal value is unacceptable by the transformer.
     */
    public LogicalMetric acceptSignal(LogicalMetric logicalMetric, String signalName, Map<String, String> signalValues)
            throws UnknownSignalValueException {
        return transformers.apply(signalName).apply(logicalMetric, signalName, signalValues);
    }
}
