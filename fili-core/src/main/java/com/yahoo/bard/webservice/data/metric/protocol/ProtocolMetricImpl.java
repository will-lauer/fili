// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricImpl;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;
import com.yahoo.bard.webservice.data.metric.TemplateDruidQuery;
import com.yahoo.bard.webservice.data.metric.mappers.ResultSetMapper;

import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Implement a metric that supports protocols.
 */
public class ProtocolMetricImpl extends LogicalMetricImpl implements ProtocolMetric {

    protected final ProtocolSupport protocolSupport;

    /**
     * Constructor.
     *
     * @param logicalMetricInfo  The metadata for the metric
     * @param templateDruidQuery  Query the metric needs
     * @param calculation  Mapper for the metric
     * @param protocolSupport  A identify and return protocols supported for this metric.
     */
    public ProtocolMetricImpl(
            @NotNull LogicalMetricInfo logicalMetricInfo,
            @NotNull TemplateDruidQuery templateDruidQuery,
            ResultSetMapper calculation,
            ProtocolSupport protocolSupport
    ) {
        super(logicalMetricInfo, templateDruidQuery, calculation);
        this.protocolSupport = protocolSupport;
    }

    @Override
    public boolean accepts(final String signalName) {
        return protocolSupport.accepts(signalName).equals(ProtocolSupport.Accepts.TRUE);
    }

    @Override
    public LogicalMetric accept(String protocolName, Map<String, String> signalData) throws UnknownProtocolValueException {
        Protocol protocol = protocolSupport.getProtocol(protocolName);
        return protocol.getMetricTransformer().apply(this, protocol, signalData);
    }

    @Override
    public ProtocolSupport getProtocolSupport() {
        return protocolSupport;
    }
}
