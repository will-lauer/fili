// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import com.yahoo.bard.webservice.data.metric.LogicalMetric

import spock.lang.Specification

import java.util.function.Function

class ProtocolSupportSpec extends Specification {

    MetricTransformer metricTransformer = Mock(MetricTransformer)
    LogicalMetric logicalMetric = Mock(LogicalMetric)

    Protocol fooProtocol
    Protocol barProtocol
    Protocol bazProtocol

    ProtocolSupport signalHandler

    String protocolName1 = "foo"
    String protocolName2 = "bar"
    String protocolName3 = "baz"


    def setup() {
        fooProtocol = new Protocol(protocolName1, metricTransformer)
        barProtocol = new Protocol(protocolName2, metricTransformer)
        bazProtocol = new Protocol(protocolName3, metricTransformer)
        signalHandler = new ProtocolSupport([fooProtocol], [protocolName2])
    }

    def "Accepts is true for whitelists false for blacklists or maybe for neither"() {
        expect:
        signalHandler.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        signalHandler.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        signalHandler.accepts("baz")  == ProtocolSupport.Accepts.MAYBE
    }

    def "Without protocol supresses both previously unknown and known protocols"() {
        setup:
        ProtocolSupport test1 = signalHandler.withoutProtocol("foo")
        ProtocolSupport test2 = signalHandler.withoutProtocol("baz")

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.FALSE
        test1.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        test1.accepts("baz")  == ProtocolSupport.Accepts.MAYBE

        test2.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        test2.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        test2.accepts("baz")  == ProtocolSupport.Accepts.FALSE

    }

    def "Without protocol support supresses both previously unknown and known protocols"() {
        setup:
        ProtocolSupport subtractFooAndBaz = new ProtocolSupport([barProtocol], ["foo", "baz"])
        ProtocolSupport noToAll = signalHandler.withoutProtocolSupport([subtractFooAndBaz])

        ProtocolSupport subtractBaz = new ProtocolSupport([barProtocol], ["baz"])
        ProtocolSupport fooNoBarBaz = signalHandler.withoutProtocolSupport([subtractBaz])

        expect:
        noToAll.accepts("foo")  == ProtocolSupport.Accepts.FALSE
        noToAll.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        noToAll.accepts("baz")  == ProtocolSupport.Accepts.FALSE

        fooNoBarBaz.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        fooNoBarBaz.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        fooNoBarBaz.accepts("baz")  == ProtocolSupport.Accepts.FALSE
    }


    def "Without protocols supresses both previously unknown and known protocols"() {
        setup:
        ProtocolSupport test1 = signalHandler.withoutProtocols(["foo", "baz"])

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.FALSE
        test1.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        test1.accepts("baz")  == ProtocolSupport.Accepts.FALSE
    }

    def "With protocols approves both previously unknown and known protocols"() {
        setup:
        ProtocolSupport test1 = signalHandler.withProtocols([fooProtocol, barProtocol, bazProtocol])

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        test1.accepts("bar")  == ProtocolSupport.Accepts.TRUE
        test1.accepts("baz")  == ProtocolSupport.Accepts.TRUE
    }
}
