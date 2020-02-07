// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import com.yahoo.bard.webservice.data.metric.LogicalMetric

import spock.lang.Specification

import java.util.function.Function

class ProtocolSupportSpec extends Specification {

    Function accepter = Mock(Function)
    MetricTransformer metricTransformer = Mock(MetricTransformer)
    LogicalMetric logicalMetric = Mock(LogicalMetric)
    ProtocolSupport signalHandler = new ProtocolSupport(["foo"], ["bar"], accepter)
    String signalName = "foo"


    def setup() {
    }

    def "Accepts is true for whitelists false for blacklists or maybe for neither"() {
        expect:
        signalHandler.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        signalHandler.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        signalHandler.accepts("baz")  == ProtocolSupport.Accepts.MAYBE
    }

    def "Without signal supresses both previously unknown and known signals"() {
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

    def "Without signals supresses both previously unknown and known signals"() {
        setup:
        ProtocolSupport test1 = signalHandler.withoutProtocols(["foo", "baz"])

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.FALSE
        test1.accepts("bar")  == ProtocolSupport.Accepts.FALSE
        test1.accepts("baz")  == ProtocolSupport.Accepts.FALSE
    }

    def "With signals approves both previously unknown and known signals"() {
        setup:
        ProtocolSupport test1 = signalHandler.withSignals(["foo", "bar", "baz"])

        expect:
        test1.accepts("foo")  == ProtocolSupport.Accepts.TRUE
        test1.accepts("bar")  == ProtocolSupport.Accepts.TRUE
        test1.accepts("baz")  == ProtocolSupport.Accepts.TRUE
    }

    def "AcceptSignal returns a transformed logical metric"() {
        setup:
        accepter.apply(signalName) >> metricTransformer
        metricTransformer.apply(logicalMetric, signalName, _ as Map) >> logicalMetric

        expect:
        signalHandler.acceptSignal(logicalMetric, signalName, [:]) == logicalMetric
    }

    def "Accept Signal throws exception"() {
        setup:
        Map signalValues = [:]
        accepter.apply(signalName) >> metricTransformer
        metricTransformer.apply(logicalMetric, signalName, _ as Map) >> {
            throw new UnknownProtocolValueException(signalName, signalValues)
        }

        when:
        signalHandler.acceptSignal(logicalMetric, signalName, [:])

        then:
        thrown(UnknownProtocolValueException)
    }
}
