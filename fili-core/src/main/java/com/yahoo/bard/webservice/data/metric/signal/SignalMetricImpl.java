// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricImpl;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;
import com.yahoo.bard.webservice.data.metric.TemplateDruidQuery;
import com.yahoo.bard.webservice.data.metric.mappers.ResultSetMapper;

import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Implement a metric that handles signals.
 */
public class SignalMetricImpl extends LogicalMetricImpl implements SignalMetric {

    protected final SignalHandler signalHandler;

    /**
     * Constructor.
     *
     * @param logicalMetricInfo  The metadata for the metric
     * @param templateDruidQuery  Query the metric needs
     * @param calculation  Mapper for the metric
     * @param signalHandler  Signal handler to process reconfiguration signals
     */
    public SignalMetricImpl(
            @NotNull LogicalMetricInfo logicalMetricInfo,
            @NotNull TemplateDruidQuery templateDruidQuery,
            ResultSetMapper calculation,
            SignalHandler signalHandler
    ) {
        super(logicalMetricInfo, templateDruidQuery, calculation);
        this.signalHandler = signalHandler;
    }

    @Override
    public boolean accepts(final String signalName) {
        return signalHandler.accepts(signalName).equals(SignalHandler.Accepts.TRUE);
    }

    @Override
    public LogicalMetric accept(String signalName, Map<String, String> signalData) throws UnknownSignalValueException {
        return signalHandler.acceptSignal(this, signalName, signalData);
    }

    @Override
    public SignalHandler getSignalHandler() {
        return signalHandler;
    }
}
