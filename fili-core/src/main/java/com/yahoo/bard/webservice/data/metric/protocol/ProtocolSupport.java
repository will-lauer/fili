// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An object to describe which protocols a metric can support and to invoke the apply for that protocol.
 *
 * In the case where a stack of metrics might be delegated to, white or black listing allows the handler to indicate
 * whether delegation should be allowed.
 */
public class ProtocolSupport {

    /**
     * A substitute for Boolean to indicate Yes, No or Uknown in the case where delegates might service a signal.
     */
    public enum Accepts {
        TRUE,
        REJECT,  // This Protocol is not supported and probably shouldn't be supported in metrics that close over it
        MAYBE  // This protocol isn't directly supported by this metric
    }

    private final Collection<String> blacklist;
    private final Map<String, Protocol> protocolMap;

    /**
     * Constructor.
     *
     * @param protocols A collection of protocols this handler will support.
     */
    public ProtocolSupport(
            Collection<Protocol> protocols
    ) {
        this(protocols, Collections.emptySet());
    }

    /**
     * Constructor.
     *
     * @param protocols A collection of protocols this handler will support.
     * @param blacklist  Protocols that this handler will neither support nor delegate.
     */
    public ProtocolSupport(
            Collection<Protocol> protocols,
            Collection<String> blacklist
    ) {
        protocolMap = protocols.stream().collect(Collectors.toMap(Protocol::getContractName, Function.identity()));
        this.blacklist = blacklist;
    }

    /**
     * Determine if this protocol handler or accepts this protocol.
     *
     * @param contractName The name of the protocol to test for.
     *
     * @return TRUE if this metric directly or indirectly supports this signal, FALSE if it refuses, MAYBE if it
     * doesn't assert authority.
     */
    public Accepts accepts(String contractName) {
        return protocolMap.containsKey(contractName) ? Accepts.TRUE :
                blacklist.contains(contractName) ?
                        Accepts.REJECT :
                        Accepts.MAYBE;
    }

    /**
     * Create a modified protocol handler which doesn't accepts a certain protocol.
     *
     * @param contractName  The name of a protocol to not handle.
     *
     * @return A protocol handler with additional signals bound.
     */
    public ProtocolSupport withoutProtocol(String contractName) {
        return withoutProtocols(Collections.singleton(contractName));
    }

    /**
     * Create a modified protocol handler which doesn't accepts protocols.
     *
     * @param contractNames  The names of protocols to not handle.
     *
     * @return A protocol handler with protocols not supported.
     */
    public ProtocolSupport withoutProtocols(Collection<String> contractNames) {

        List<Protocol> protocols =
                protocolMap.values().stream()
                        .filter(protocol -> !contractNames.contains(protocol.getContractName()))
                        .collect(Collectors.toList());
        List<String> newBlackList = Stream.concat(contractNames.stream(), blacklist.stream())
                .collect(Collectors.toList());

        return new ProtocolSupport(protocols, newBlackList);
    }

    /**
     * Create a modified protocol handler which doesn't accepts protocols blacklisted in these other protocols.
     *
     * @param protocolSupport  The protocol supports whose blacklists should not be handled.
     *
     * @return A protocol handler with additional protocols not supported.
     */
    public ProtocolSupport combineBlacklists(Collection<ProtocolSupport> protocolSupport) {

        List<String> protocols =
                protocolSupport.stream()
                        .flatMap(support -> support.blacklist.stream())
                        .collect(Collectors.toList());
        return withoutProtocols(protocols);
    }

    /**
     * Create a modified protocol handler which accepts additional protocols.
     *
     * @param addedProtocols  The protocols to handle.
     *
     * @return A protocol handler with additional protocols supported.
     */
    public ProtocolSupport withProtocols(Collection<Protocol> addedProtocols) {

        Collection<Protocol> newProtocols = Stream.concat(addedProtocols.stream(), protocolMap.values().stream())
                .collect(Collectors.toSet());

        Collection<String> newBlackList = blacklist.stream()
                .filter(name -> !addedProtocols.contains(name))
                .collect(Collectors.toSet());

        return new ProtocolSupport(newProtocols, newBlackList);
    }

    /**
     * Retrieve the Protocol for a given protocol contract name.
     *
     * @param contractName a protocol contract name
     *
     * @return The Protocol for this protocol contract name
     */
    Protocol getProtocol(String contractName) {
        return protocolMap.get(contractName);
    }
}
