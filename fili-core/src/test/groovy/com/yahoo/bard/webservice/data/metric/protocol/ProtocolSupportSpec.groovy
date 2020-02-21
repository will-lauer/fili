// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import com.yahoo.bard.webservice.data.metric.LogicalMetric

import spock.lang.Specification

class ProtocolSupportSpec extends Specification {

    MetricTransformer metricTransformer = Mock(MetricTransformer)
    LogicalMetric logicalMetric = Mock(LogicalMetric)

    Protocol fooProtocol
    Protocol barProtocol
    Protocol bazProtocol

    ProtocolSupport protocolSupport

    String protocolName1 = "foo"
    String protocolName2 = "bar"
    String protocolName3 = "baz"


    def setup() {
        fooProtocol = new Protocol(protocolName1, metricTransformer)
        barProtocol = new Protocol(protocolName2, metricTransformer)
        bazProtocol = new Protocol(protocolName3, metricTransformer)
        protocolSupport = new ProtocolSupport([fooProtocol], [protocolName2])
    }

    def "Accepts is true for whitelists false for blacklists or maybe for neither"() {
        expect:
        protocolSupport.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        protocolSupport.accepts("bar")  == ProtocolSupport.Accepts.REJECT
        protocolSupport.accepts("baz")  == ProtocolSupport.Accepts.MAYBE
    }

    def "Without protocol supresses both previously unknown and known protocols"() {
        setup:
        ProtocolSupport test1 = protocolSupport.withoutProtocol("foo")
        ProtocolSupport test2 = protocolSupport.withoutProtocol("baz")

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.REJECT
        test1.accepts("bar")  == ProtocolSupport.Accepts.REJECT
        test1.accepts("baz")  == ProtocolSupport.Accepts.MAYBE

        test2.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        test2.accepts("bar")  == ProtocolSupport.Accepts.REJECT
        test2.accepts("baz")  == ProtocolSupport.Accepts.REJECT

    }

    def "Without protocol support supresses both previously unknown and known protocols"() {
        setup:
        ProtocolSupport subtractFooAndBaz = new ProtocolSupport([barProtocol], ["foo", "baz"])
        ProtocolSupport noToAll = protocolSupport.combineBlacklists([subtractFooAndBaz])

        ProtocolSupport subtractBaz = new ProtocolSupport([barProtocol], ["baz"])
        ProtocolSupport fooNoBarBaz = protocolSupport.combineBlacklists([subtractBaz])

        expect:
        noToAll.accepts("foo")  == ProtocolSupport.Accepts.REJECT
        noToAll.accepts("bar")  == ProtocolSupport.Accepts.REJECT
        noToAll.accepts("baz")  == ProtocolSupport.Accepts.REJECT

        fooNoBarBaz.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        fooNoBarBaz.accepts("bar")  == ProtocolSupport.Accepts.REJECT
        fooNoBarBaz.accepts("baz")  == ProtocolSupport.Accepts.REJECT
    }


    def "Without protocols supresses both previously unknown and known protocols"() {
        setup:
        ProtocolSupport test1 = protocolSupport.withoutProtocols(["foo", "baz"])

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.REJECT
        test1.accepts("bar")  == ProtocolSupport.Accepts.REJECT
        test1.accepts("baz")  == ProtocolSupport.Accepts.REJECT
    }

    def "With protocols approves both previously unknown and known protocols"() {
        setup:
        ProtocolSupport test1 = protocolSupport.withProtocols([fooProtocol, barProtocol, bazProtocol])

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        test1.accepts("bar")  == ProtocolSupport.Accepts.TRUE
        test1.accepts("baz")  == ProtocolSupport.Accepts.TRUE
    }

    def "Combine blacklists combines blacklists"() {
        setup:
        ProtocolSupport protocolSupport2 = new ProtocolSupport([fooProtocol], [protocolName1])
        ProtocolSupport test = protocolSupport.combineBlacklists([protocolSupport2])

        expect:
        test.accepts(protocolName1)  == ProtocolSupport.Accepts.REJECT
        test.accepts(protocolName2)  == ProtocolSupport.Accepts.REJECT
        test.accepts(protocolName3)  == ProtocolSupport.Accepts.MAYBE

    }
}
