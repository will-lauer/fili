// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.protocol.UnknownProtocolValueException;

import java.util.Map;

public interface LogicalMetricParameterMapper {
    LogicalMetric apply(LogicalMetric logicalMetric, Map<String, String> parameterValues)
            throws UnknownProtocolValueException;
}
