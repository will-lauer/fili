// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

import static com.yahoo.bard.webservice.data.metric.protocol.MetricTransformer.IDENTITY_TRANSFORM;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * A collection of signal names supported.
 */
public class BuiltInProtocols {
    public static final String NO_OP_PROTOCOL_NAME = "noop";
    public static final Protocol NO_OP_PROTOCOL = new Protocol(NO_OP_PROTOCOL_NAME, IDENTITY_TRANSFORM);

    public static final String REAGGREGATION = "reaggregation";
    public static final Protocol REAGGREGATION_PROTOCOL = new Protocol(REAGGREGATION, IDENTITY_TRANSFORM);

    public static Set<Protocol> DEFAULT_PROTOCOLS = Sets.newHashSet(REAGGREGATION_PROTOCOL, NO_OP_PROTOCOL);

    public static ProtocolSupport getDefaultProtocolSupport() {
        return new ProtocolSupport(DEFAULT_PROTOCOLS);
    }
}
