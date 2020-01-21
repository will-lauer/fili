// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class can apply transformations to metrics which know how to accept signals.
 * It can also refuse signals that are intentionally inaccessible.
 */
public class SignalHandler {

    public static SignalHandler DEFAULT_SIGNAL_HANDLER = new SignalHandler(Collections.emptyList(), new HashMap<>());
    private static Map<String, SignalHandler> signalHandlers = new HashMap<>();

    private final List<String> blacklist;
    private final Map<String, MetricTransformer> knownTransformers;

    /**
     * Constructor.
     *
     * @param blacklist  Signals that this handler explicitly does not dispatch.
     * @param knownTransformers  A collection of functions to handle particular categories of signal
     */
    public SignalHandler(
            final List<String> blacklist,
            final Map<String, MetricTransformer> knownTransformers
    ) {
        this.blacklist = blacklist;
        this.knownTransformers = knownTransformers;
    }

    /**
     * Determine if this metric accepts this type of signal.
     *
     * @param metric  The metric to test.
     * @param signalName The name of the signal.
     *
     * @return true if this metric directly or indirectly supports this signal.
     */
    public boolean accepts(LogicalMetric metric, String signalName) {
        if (blacklist.contains(signalName)) {
            return false;
        }
        if (metric instanceof SignalMetric) {
            return ((SignalMetric) metric).accepts(signalName);
        }
        return false;
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
        List<String> newBlackList = Stream.concat(
                signals.stream(),
                blacklist.stream()
        ).collect(Collectors.toList());
        return new SignalHandler(newBlackList, knownTransformers);
    }
}
