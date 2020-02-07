// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

/**
 * A collection of signal names supported.
 */
public class BuiltInProtocols {
    public static final String NO_OP_PROTOCOL_NAME = "noOp";
    public static final Protocol NO_OP_PROTOCOL = new Protocol(NO_OP_PROTOCOL_NAME, MetricTransformer.IDENTITY_TRANSFORM);

    public static final String REAGGREGATION = "reaggregation";
    public static final Protocol REAGGREGATION_PROTOCOL = new Protocol(REAGGREGATION, MetricTransformer.IDENTITY_TRANSFORM);
}
