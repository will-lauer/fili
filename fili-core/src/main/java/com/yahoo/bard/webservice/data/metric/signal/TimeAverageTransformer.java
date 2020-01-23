// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import com.yahoo.bard.webservice.data.config.metric.makers.AggregationAverageMaker;
import com.yahoo.bard.webservice.data.config.metric.makers.MakeFromMetrics;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;
import com.yahoo.bard.webservice.data.time.DefaultTimeGrain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An interface for transforming metrics into other metrics.
 */
public class TimeAverageTransformer implements MetricTransformer {

    private static final MetricDictionary EMPTY_METRIC_DICTIONARY = new MetricDictionary();

    public static final String BASE_SIGNAL = DefaultSignals.REAGGREGATION;

    public static final TimeAverageTransformer INSTANCE = new TimeAverageTransformer();

    public String nameFormat = "%s%s";
    public String longNameFormat = "%s (%s)";
    public String descriptionFormat = "The %s of %s.";

    private static final String DAILY_AVERAGE = "dayAvg";
    private static final String DAILY_AVERAGE_LONG = "Daily Average";
    private static final String WEEKLY_AVERAGE = "weekAvg";
    private static final String WEEKLY_AVERAGE_LONG = "Weekly Average";
    private static final String MONTHLY_AVERAGE = "monthAvg";
    private static final String MONTHLY_AVERAGE_LONG = "Monthly Average";

    private AggregationAverageMaker dayMaker = new AggregationAverageMaker(EMPTY_METRIC_DICTIONARY, DefaultTimeGrain.DAY);
    private AggregationAverageMaker weekMaker = new AggregationAverageMaker(EMPTY_METRIC_DICTIONARY, DefaultTimeGrain.WEEK);
    private AggregationAverageMaker monthMaker = new AggregationAverageMaker(EMPTY_METRIC_DICTIONARY, DefaultTimeGrain.MONTH);

    private Map<String, MakeFromMetrics> metricMakerMap = new HashMap<>();
    private Map<String, String> formatLongName = new HashMap<>();

    /**
     * Constructor.
     */
    public TimeAverageTransformer() {
        metricMakerMap.put(DAILY_AVERAGE, dayMaker);
        metricMakerMap.put(WEEKLY_AVERAGE, weekMaker);
        metricMakerMap.put(MONTHLY_AVERAGE, monthMaker);

        formatLongName.put(DAILY_AVERAGE, DAILY_AVERAGE_LONG);
        formatLongName.put(WEEKLY_AVERAGE, WEEKLY_AVERAGE_LONG);
        formatLongName.put(MONTHLY_AVERAGE, MONTHLY_AVERAGE_LONG);
    }

    @Override
    public LogicalMetric apply(LogicalMetric logicalMetric, String signalName, Map<String, String> signalData)
            throws UnknownSignalValueException {
        String makerValue = signalData.get(BASE_SIGNAL);
        if (! metricMakerMap.containsKey(makerValue) ) {
            throw new UnknownSignalValueException(BASE_SIGNAL, makerValue);
        }
        MakeFromMetrics maker = metricMakerMap.get(makerValue);

        LogicalMetricInfo info = makeNewLogicalMetricInfo(logicalMetric.getLogicalMetricInfo(), makerValue);

        return metricMakerMap.get(makerValue)
                .makeInnerWithResolvedDependencies(info, Collections.singletonList(logicalMetric));
    }

    /**
     * Build the new identity metadata for the transformed metric.
     *
     * @param info  The identity metadata from the existing metric
     * @param makerValue The type of time reaggregation being performed.
     *
     * @return  A metric info for a time-ly logical metric.
     */
    protected LogicalMetricInfo makeNewLogicalMetricInfo(
            LogicalMetricInfo info,
            final String makerValue
    ) {
        String makeValueName = formatLongName.get(makerValue);
        String name = String.format(nameFormat, makerValue, info.getName());
        String longName = String.format(longNameFormat, info.getLongName(), makeValueName);
        String description = String.format(descriptionFormat, info.getDescription(), makeValueName);
        return new LogicalMetricInfo(name, longName, info.getCategory(), description, info.getType());
    }
}
