// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A collection of signal names supported.
 */
public class DefaultSignals {
    public static String REAGGREGATION = "reaggregation";

    public static final Map<String, MetricTransformer> DEFAULT_SHARED_METRIC_TRANSFORMERS = new HashMap<>();


    public static SignalHandler DEFAULT_SIGNAL_HANDLER = new SignalHandler(
            Collections.emptyList(),
            Collections.emptyList(),
            DefaultSignals.defaultMetricTransformersClosure
    );
    /**
     * A friendly default function for metric transformation.  It closes over the shared static
     * 'DEFAULT_SHARED_METRIC_TRANSFORMERS' collection.
     */
    public static Function<String, MetricTransformer> defaultMetricTransformersClosure =
            (signal) -> {
                return DEFAULT_SHARED_METRIC_TRANSFORMERS.getOrDefault(signal, MetricTransformer.IDENTITY_TRANSFORM);
            };
}
