// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo;
import com.yahoo.bard.webservice.data.metric.protocol.Protocol;
import com.yahoo.bard.webservice.data.metric.protocol.ProtocolMetric;
import com.yahoo.bard.webservice.data.metric.protocol.UnknownProtocolValueException;

import java.util.List;
import java.util.Map;

/**
 * Given an ordered collection of Protocols, provide a mapping function to transform a metric by any applicable
 * protocols.
 */
public class ProtocolListLogicalMetricParameterMapper implements LogicalMetricParameterMapper {

    List<Protocol> protocols;

    public ProtocolListLogicalMetricParameterMapper(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    @Override
    public LogicalMetric apply(
            LogicalMetric logicalMetric,
            Map<String, String> parameterValues
    )
            throws UnknownProtocolValueException {
        if (! (logicalMetric instanceof ProtocolMetric)) {
            return logicalMetric;
        }
        LogicalMetric current = logicalMetric;
        LogicalMetricInfo info = new LogicalMetricInfo("foo");

        for (Protocol p: protocols) {
            ProtocolMetric protocolMetric;
            if (!(current instanceof ProtocolMetric)) {
                break;
            }
            protocolMetric = (ProtocolMetric) current;

            if (!parameterValues.containsKey(p.getCoreParameterName())) {
                continue;
            }
            if (! protocolMetric.accepts(p.getContractName())) {
                continue;
            }
            current = protocolMetric.accept(info, p.getContractName(), parameterValues);
        }

        return current;
    }
}
