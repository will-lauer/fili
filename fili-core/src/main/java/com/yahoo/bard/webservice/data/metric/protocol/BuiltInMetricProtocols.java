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

    /**
     * The names for standard protocol contracts supplied to makers by default.
     *
     * Intentionally mutable to be managed at config time before building makers.
     */
    private static final Set<String> STANDARD_CONTRACTS = new HashSet<>();

    /**
     * Add a protocol to the global protocol dictionary and as a default protocol.
     *
     * @param protocol  a new protocol to be supported globally
     *
     */
    public static void addAsStandardProtocol(Protocol protocol) {
        STANDARD_CONTRACTS.add(protocol.getContractName());
        ProtocolDictionary.DEFAULT.put(protocol.getContractName(), protocol);
    }

    /**
     * Remove a protocol contract from the standard protocol list.
     *
     * @param contractName  the name of the protocol contract to be removed.
     *
     * @return true if this contract was previously supported
     */
    public static boolean removeFromStandardProtocols(String contractName) {
        return STANDARD_CONTRACTS.remove(contractName);
    }

    public static final String REAGGREGATION_CONTRACT_NAME = "reaggregation";
    public static final String REAGG_CORE_PARAMETER = "reagg";

    public static final Protocol REAGGREGATION_PROTOCOL = new Protocol(
            REAGGREGATION_CONTRACT_NAME,
            REAGG_CORE_PARAMETER,
            TimeAverageMetricTransformer.INSTANCE
    );

    static {
        addAsStandardProtocol(REAGGREGATION_PROTOCOL);
    }

    /**
     * Build the default protocol support for Protocol Metric Makers.
     *
     * @return  A Protocol Support describing the default protocols supported throughout the system.
     */
    public static ProtocolSupport getStandardProtocolSupport() {
        return new ProtocolSupport(STANDARD_CONTRACTS.stream()
                .map(ProtocolDictionary.DEFAULT::get)
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
