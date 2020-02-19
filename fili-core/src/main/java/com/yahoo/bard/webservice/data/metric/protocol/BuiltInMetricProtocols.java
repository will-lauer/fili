// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A collection of signal names supported.
 */
public class BuiltInMetricProtocols {

    // The names for default protocols
    // Intentionally mutable
    private static final Set<String> DEFAULT_PROTOCOL_CONTRACTS = new HashSet<>();

    // The dictionary for default protocols
    // intentionally mutable
    private static final ProtocolDictionary PROTOCOL_DICTIONARY = new ProtocolDictionary();

    /**
     * Add a protocol to the shared protocol dictionary.
     *
     * @param protocol  a new protocol
     *
     * @return any existing protocol with the same contract name as this one.
     */
    public static Protocol addProtocol(Protocol protocol) {
        return PROTOCOL_DICTIONARY.put(protocol.getContractName(), protocol);
    }

    /**
     * Add a protocol to the shared protocol dictionary and as a default protocol.
     *
     * @param protocol  a new protocol to be supported globally
     *
     * @return any existing protocol with the same contract name as this one.
     */
    public static Protocol addDefaultProtocol(Protocol protocol) {
        DEFAULT_PROTOCOL_CONTRACTS.add(protocol.getContractName());
        return PROTOCOL_DICTIONARY.put(protocol.getContractName(), protocol);
    }

    /**
     * Remove a protocol from the default protocols and protocol dictionary.
     *
     * @param protocolName  the name of the protocol to be removed.
     *
     * @return The existing protocol correspnding to this protocol contract name (if any).
     */
    public static Protocol removeDefaultProtocol(String protocolName) {
        DEFAULT_PROTOCOL_CONTRACTS.remove(protocolName);
        return PROTOCOL_DICTIONARY.remove(protocolName);
    }

    public static final String REAGGREGATION_CONTRACT_NAME = "reaggregation";
    public static final String REAGG_CORE_PARAMETER = "reagg";

    public static final Protocol REAGGREGATION_PROTOCOL = new Protocol(
            REAGGREGATION_CONTRACT_NAME,
            REAGG_CORE_PARAMETER,
            TimeAverageMetricTransformer.INSTANCE
    );

    static {
        addDefaultProtocol(REAGGREGATION_PROTOCOL);
    }

    /**
     * Build the default protocol support for Protocol Metric Makers.
     *
     * @return  A Protocol Support describing the default protocols supported throughout the system.
     */
    public static ProtocolSupport getDefaultProtocolSupport() {
        return new ProtocolSupport(DEFAULT_PROTOCOL_CONTRACTS.stream()
                .map(PROTOCOL_DICTIONARY::get)
                .collect(Collectors.toList()));
    }

    /**
     * Constructor.
     *
     * Private to prevent instance creation.
     */
    private BuiltInMetricProtocols() {
    }
}
