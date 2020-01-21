// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import static com.yahoo.bard.webservice.data.time.DefaultTimeGrain.DAY;
import static com.yahoo.bard.webservice.data.time.DefaultTimeGrain.MONTH;

import com.yahoo.bard.webservice.data.config.metric.makers.AggregationAverageMaker;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Transform a metric by applying a time based aggregation to it.
 */
public class AggregationTypeTransformer implements MetricTransformer {

    public static final MetricDictionary DICTIONARY = new MetricDictionary();
    public static final String DAY_AVG = "dayAvg";
    public static final String MONTH_AVG = "monthAvg";

    private AggregationAverageMaker dailyAverageMaker = new AggregationAverageMaker(DICTIONARY, DAY);
    private AggregationAverageMaker monthlyAverageMaker = new AggregationAverageMaker(DICTIONARY, MONTH);

    Map<String, AggregationAverageMaker> makerMap = new HashMap<>();

    /**
     * Constructor.
     */
    public AggregationTypeTransformer() {
        makerMap.put(DAY_AVG, dailyAverageMaker);
        makerMap.put(MONTH_AVG, monthlyAverageMaker);
    }

    @Override
    public LogicalMetric apply(LogicalMetric logicalMetric, String signalName, Map<String, String> signalData) {
        String mappingFunction = signalData.get(signalName);
        if (makerMap.containsKey(mappingFunction)) {
            LogicalMetricInfo logicalMetricInfo = logicalMetric.getLogicalMetricInfo();
            return makerMap.get(mappingFunction)
                    .makeInnerWithResolvedDependencies(logicalMetricInfo, Collections.singletonList(logicalMetric));
        }
        return logicalMetric;
    }
}
