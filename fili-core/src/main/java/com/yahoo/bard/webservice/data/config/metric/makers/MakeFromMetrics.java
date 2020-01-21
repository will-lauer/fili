// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.config.metric.makers;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;

import java.util.List;

/**
 * Interface to indicate a metric make supports building from resolved metrics without use of a metric dictionary.
 */
public interface MakeFromMetrics {

    /**
     * Delegated to for actually making the metric after building dependencies.
     *
     * @param logicalMetricInfo  Logical metric info provider
     * @param dependentMetrics  Metrics this metric depends on
     *
     * @return the new logicalMetric
     */
    LogicalMetric makeInnerWithResolvedDependencies(
            LogicalMetricInfo logicalMetricInfo,
            List<LogicalMetric> dependentMetrics
    );
}
