// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import spock.lang.Specification

class BuiltInMetricProtocolsSpec extends Specification {

    ProtocolDictionary protocolDictionary = BuiltInMetricProtocols.DEFAULT_PROTOCOL_DICTIONARY;
    String testProtocol = "Foo"
    Protocol p = Mock(Protocol)

    def setup() {
        p.getContractName() >> testProtocol
    }

    def cleanup() {
        protocolDictionary.remove(testProtocol)
        BuiltInMetricProtocols.STANDARD_PROTOCOLS.remove(testProtocol)
    }

    def "Add a Standard Protocol modifies default contracts"() {
        when:
        BuiltInMetricProtocols.addAsStandardProtocol(p)

        then:
        protocolDictionary.containsKey(testProtocol)
        protocolDictionary.get(testProtocol) == p
        BuiltInMetricProtocols.getStandardProtocolSupport().accepts(testProtocol)
    }

    def "RemoveFromStandardProtocols"() {
        when:
        BuiltInMetricProtocols.addAsStandardProtocol(p)

        then:
        protocolDictionary.containsKey(testProtocol)
        protocolDictionary.get(testProtocol) == p
        BuiltInMetricProtocols.getStandardProtocolSupport().accepts(testProtocol)

        when:
        BuiltInMetricProtocols.removeFromStandardProtocols(testProtocol)

        then:
        ! BuiltInMetricProtocols.getStandardProtocolSupport().accepts(testProtocol)

    }

    def "GetStandardProtocolSupport wraps new protocols"() {
        setup:
        BuiltInMetricProtocols.addAsStandardProtocol(p)
        ProtocolSupport protocolSupport = BuiltInMetricProtocols.getStandardProtocolSupport()

        expect:
        protocolSupport.accepts(testProtocol)
        protocolSupport.getProtocol(testProtocol) == p
    }
}
