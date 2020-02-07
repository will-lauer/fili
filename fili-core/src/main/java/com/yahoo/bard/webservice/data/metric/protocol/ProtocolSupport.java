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
        FALSE,
        MAYBE  // If this Handler cannot accept the protocol, but doesn't make positive claims if another might.
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
        protocolMap = protocols.stream().collect(Collectors.toMap(Protocol::getName, Function.identity()));
        this.blacklist = blacklist;
    }

    /**
     * Determine if this protocol handler or accepts this protocol.
     *
     * @param protocolName The name of the protocol to test for.
     *
     * @return TRUE if this metric directly or indirectly supports this signal, FALSE if it refuses, MAYBE if it
     * doesn't assert authority.
     */
    public Accepts accepts(String protocolName) {
        return protocolMap.containsKey(protocolName) ? Accepts.TRUE :
                blacklist.contains(protocolName) ?
                        Accepts.FALSE :
                        Accepts.MAYBE;
    }

    /**
     * Create a modified protocol handler which doesn't accepts a certain protocol.
     *
     * @param protocolName  The name of a protocol to not handle.
     *
     * @return A protocol handler with additional signals bound.
     */
    public ProtocolSupport withoutProtocol(String protocolName) {
        return withoutProtocols(Collections.singleton(protocolName));
    }

    /**
     * Create a modified protocol handler which doesn't accepts certain protocols.
     *
     * @param protocolNames  The names of protocols to not handle.
     *
     * @return A protocol handler with protocols not supported.
     */
    public ProtocolSupport withoutProtocols(Collection<String> protocolNames) {

        List<Protocol> protocols =
                protocolMap.values().stream()
                        .filter(protocol -> !protocolNames.contains(protocol.getName()))
                        .collect(Collectors.toList());
        List<String> newBlackList = Stream.concat(protocolNames.stream(), blacklist.stream())
                .collect(Collectors.toList());

        return new ProtocolSupport(protocols, newBlackList);
    }

    /**
     * Create a modified protocol handler which accepts certain protocols.
     *
     * @param addedProtocols  The protocols to handle.
     *
     * @return A protocol handler with additional protocols supported.
     */
    public ProtocolSupport withProtocols(Collection<Protocol> addedProtocols) {

        List<Protocol> newProtocols = Stream.concat(addedProtocols.stream(), protocolMap.values().stream())
                .collect(Collectors.toList());

        List<String> newBlackList = blacklist.stream()
                .filter(name -> !addedProtocols.contains(name))
                .collect(Collectors.toList());

        return new ProtocolSupport(newProtocols, newBlackList);
    }

    /**
     * Retrieve the Protocol for a given protocol name.
     */
    Protocol getProtocol(String protocolName) {
        return protocolMap.get(protocolName);
    }

}
