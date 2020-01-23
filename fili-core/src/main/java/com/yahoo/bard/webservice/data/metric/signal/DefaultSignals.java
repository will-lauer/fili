// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A collection of signal names supported.
 */
public class DefaultSignals {
    public static String REAGGREGATION = "reaggregation";

    public static Map<String, MetricTransformer> sharedDefaultTransformerMap = new HashMap<>();

    /**
     * A friendly default function for metric transformation.
     */
    public static BiFunction<String, Map<String, String>, MetricTransformer> simpleMetricTransformFunction =
            (signal, values) -> {
                return sharedDefaultTransformerMap.getOrDefault(signal, MetricTransformer.IDENTITY_TRANSFORM);
            };
}
