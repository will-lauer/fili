// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;
import com.yahoo.bard.webservice.data.metric.protocol.utils.LogicalMetricParameterMapper;
import com.yahoo.bard.webservice.data.metric.protocol.utils.MetricDetail;
import com.yahoo.bard.webservice.data.metric.protocol.utils.MetricDetailParser;
import com.yahoo.bard.webservice.data.metric.protocol.utils.ProtocolListLogicalMetricParameterMapper;
import com.yahoo.bard.webservice.web.BadApiRequestException;
import com.yahoo.bard.webservice.web.ErrorMessageFormat;
import com.yahoo.bard.webservice.web.util.BardConfigResources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.ws.rs.core.PathSegment;

public class ProtocolAwareDataApiRequestImpl extends DataApiRequestImpl {

    LogicalMetricParameterMapper logicalMetricParameterMapper;

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolAwareDataApiRequestImpl.class);

    public ProtocolAwareDataApiRequestImpl(
            final String tableName,
            final String granularity,
            final List<PathSegment> dimensions,
            final String logicalMetrics,
            final String intervals,
            final String apiFilters,
            final String havings,
            final String sorts,
            final String count,
            final String topN,
            final String format,
            final String downloadFilename,
            final String timeZoneId,
            final String asyncAfter,
            final String perPage,
            final String page,
            final BardConfigResources bardConfigResources
    ) throws BadApiRequestException {
        super(
                tableName,
                granularity,
                dimensions,
                logicalMetrics,
                intervals,
                apiFilters,
                havings,
                sorts,
                count,
                topN,
                format,
                downloadFilename,
                timeZoneId,
                asyncAfter,
                perPage,
                page,
                bardConfigResources
        );
        logicalMetricParameterMapper = bardConfigResources.getMetricParameterMapper();
    }


    /**
     * Extracts the list of metrics from the url metric query string and generates a set of LogicalMetrics.
     * <p>
     * If the query contains undefined metrics, {@link com.yahoo.bard.webservice.web.BadApiRequestException} will be
     * thrown.
     *
     * @param apiMetricQuery  URL query string containing the metrics separated by ','
     * @param metricDictionary  Metric dictionary contains the map of valid metric names and logical metric objects
     *
     * @return set of metric objects
     */
    protected LinkedHashSet<LogicalMetric> generateLogicalMetrics(
            String apiMetricQuery,
            MetricDictionary metricDictionary
    ) {
        LinkedHashSet<LogicalMetric> result = new LinkedHashSet<>();

        MetricDetailParser parser = new MetricDetailParser(apiMetricQuery);
        List<String> invalidMetricNames = new ArrayList<>();

        for (MetricDetail metricDetail: parser.getMetricDetails()) {
            LogicalMetric logicalMetric = metricDictionary.get(metricDetail.getApiName());
            if (logicalMetric == null) {
                invalidMetricNames.add(metricDetail.getColumnName());
            } else {
                LogicalMetric newMetric = logicalMetricParameterMapper.apply(logicalMetric, metricDetail.getParams());
                if (newMetric != logicalMetric) {
                    metricDictionary.putIfAbsent(metricDetail.getColumnName(), newMetric);
                }
                result.add(newMetric);
            }
        }
        if (!invalidMetricNames.isEmpty()) {
            String message = ErrorMessageFormat.METRICS_UNDEFINED.logFormat(invalidMetricNames);
            LOG.error(message);
            throw new BadApiRequestException(message);
        }
        return result;
    }
}
