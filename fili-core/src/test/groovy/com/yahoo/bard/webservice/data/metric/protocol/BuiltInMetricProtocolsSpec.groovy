// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import static com.yahoo.bard.webservice.data.metric.protocol.ProtocolSupport.Accepts.MAYBE
import static com.yahoo.bard.webservice.data.metric.protocol.ProtocolSupport.Accepts.TRUE

import spock.lang.Specification

class BuiltInMetricProtocolsSpec extends Specification {

    String testProtocol = "Foo"
    Protocol p = Mock(Protocol)

    def setup() {
        p.getContractName() >> testProtocol
    }

    def cleanup() {
        ProtocolDictionary.DEFAULT.remove(testProtocol)
        BuiltInMetricProtocols.STANDARD_CONTRACTS.remove(testProtocol)
    }

    def "Add a Standard Protocol modifies default contracts"() {
        when:
        BuiltInMetricProtocols.addAsStandardProtocol(p)

        then:
        ProtocolDictionary.DEFAULT.containsKey(testProtocol)
        ProtocolDictionary.DEFAULT.get(testProtocol) == p
        BuiltInMetricProtocols.getStandardProtocolSupport().accepts(testProtocol)
    }

    def "RemoveFromStandardProtocols"() {
        when:
        BuiltInMetricProtocols.addAsStandardProtocol(p)

        then:
        ProtocolDictionary.DEFAULT.containsKey(testProtocol)
        ProtocolDictionary.DEFAULT.get(testProtocol) == p
        BuiltInMetricProtocols.getStandardProtocolSupport().accepts(testProtocol) == TRUE

        when:
        BuiltInMetricProtocols.removeFromStandardProtocols(testProtocol)

        then:
        BuiltInMetricProtocols.getStandardProtocolSupport().accepts(testProtocol) == MAYBE

    }

    def "GetStandardProtocolSupport wraps new protocols"() {
        setup:
        BuiltInMetricProtocols.addAsStandardProtocol(p)
        ProtocolSupport protocolSupport = BuiltInMetricProtocols.getStandardProtocolSupport()

        expect:
        protocolSupport.accepts(testProtocol) == TRUE
        protocolSupport.getProtocol(testProtocol) == p
    }
}
