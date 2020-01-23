// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;

import java.util.Map;

/**
 * An interface for transforming metrics into other metrics.
 */
public interface MetricTransformer {

    /**
     * Transform a metric using a signal name, and signal data.
     *
     * @param logicalMetric  The metric to transform.
     * @param signalName The name of the signal being applied.
     * @param signalData  The data associated with that signal.
     *
     * @throws UnknownSignalValueException if this transformer doesn't know how to accept this signal
     * @return A new metric based on the signal;
     */
    LogicalMetric apply(LogicalMetric logicalMetric, String signalName, Map<String, String> signalData)
            throws UnknownSignalValueException;

    MetricTransformer EMPTY_TRANSFORM = (metric, name, map) -> {
        throw new UnknownSignalValueException(name, map.toString());
    };

    MetricTransformer IDENTITY_TRANSFORM = (metric, name, map) -> {
        return metric;
    };
}
