// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given a metric dictionary, produce a metric dictionary with dynamic metrics populated into it.
 */
public class DynamiciMetricResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DynamiciMetricResolver.class);

    LogicalMetricParameterMapper metricParameterMapper;

    /**
     * Constructor.
     *
     * @param metricParameterMapper  The parameter mapper to transform those metrics.
     */
    public DynamiciMetricResolver(LogicalMetricParameterMapper metricParameterMapper) {
        this.metricParameterMapper = metricParameterMapper;
    }

    public MetricDictionary resolve(String granularity, String requestedMetrics, MetricDictionary metricDictionary) {
        MetricDictionary result = new MetricDictionary();
        result.putAll(metricDictionary);

        MetricDetailParser parser = new MetricDetailParser(requestedMetrics);
        for (MetricDetail metricDetail : parser.metricDetails) {
            LogicalMetric metric = metricDictionary.get(metricDetail.apiName);
            LogicalMetric newMetric = metricParameterMapper.apply(metric, metricDetail.getParams());
            if (newMetric != metric) {
                result.put(metricDetail.getColumnName(), newMetric);
            }
        }
        return result;
    }
}
