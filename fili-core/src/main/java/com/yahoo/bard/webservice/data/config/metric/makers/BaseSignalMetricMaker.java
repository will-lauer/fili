// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.config.metric.makers;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;
import com.yahoo.bard.webservice.data.metric.TemplateDruidQuery;
import com.yahoo.bard.webservice.data.metric.mappers.ResultSetMapper;
import com.yahoo.bard.webservice.data.metric.signal.SignalHandler;
import com.yahoo.bard.webservice.data.metric.signal.SignalMetricImpl;

import java.util.List;

/**
 * Signal Metrics can be transformed based on the signals they accept.
 */
public abstract class BaseSignalMetricMaker extends MetricMaker implements MakeFromMetrics {

    /**
     * Construct a fully specified MetricMaker.
     *
     * @param metrics  A mapping of metric names to the corresponding LogicalMetrics. Used to resolve metric names
     * when making the logical metric.
     */
    public BaseSignalMetricMaker(MetricDictionary metrics) {
        super(metrics);
    }

    @Override
    protected LogicalMetric makeInner(LogicalMetricInfo logicalMetricInfo, List<String> dependentMetrics) {
        return makeInnerWithResolvedDependencies(logicalMetricInfo, resolveDependencies(metrics, dependentMetrics));
    }

    @Override
    public LogicalMetric makeInnerWithResolvedDependencies(
            LogicalMetricInfo logicalMetricInfo,
            List<LogicalMetric> dependentMetrics
    ) {
        TemplateDruidQuery partialQuery = makePartialQuery(logicalMetricInfo, dependentMetrics);
        ResultSetMapper calculation = makeCalculation(logicalMetricInfo, dependentMetrics);
        SignalHandler signalHandler = makeSignalHandler(logicalMetricInfo, dependentMetrics);
        return new SignalMetricImpl(logicalMetricInfo, partialQuery, calculation, signalHandler);
    }

    /**
     * Create the post processing mapper for this LogicalMetric.
     *
     * @param logicalMetricInfo  The identity metadata for the metric
     * @param dependentMetrics  The metrics this metric depends on
     *
     * @return  A mapping function to apply to the result set containing this metric
     */
    abstract protected ResultSetMapper makeCalculation(
            LogicalMetricInfo logicalMetricInfo,
            List<LogicalMetric> dependentMetrics
    );

    /**
     * Create the partial query for this LogicalMetric.
     *
     * @param logicalMetricInfo  The identity metadata for the metric
     * @param dependentMetrics  The metrics this metric depends on
     *
     * @return  A model describing the query formula for this metric
     */
    abstract protected TemplateDruidQuery makePartialQuery(
            LogicalMetricInfo logicalMetricInfo,
            List<LogicalMetric> dependentMetrics
    );

    /**
     * Create the signal handler for this LogicalMetric.
     *
     * @param logicalMetricInfo  The identity metadata for the metric
     * @param dependentMetrics  The metrics this metric depends on
     *
     * @return  A signal handler defining which signals this metric does and does not support
     */

    abstract protected SignalHandler makeSignalHandler(
            LogicalMetricInfo logicalMetricInfo,
            List<LogicalMetric> dependentMetrics
    );
}
