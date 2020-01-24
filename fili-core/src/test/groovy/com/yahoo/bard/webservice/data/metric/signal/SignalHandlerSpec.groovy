// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal

import com.yahoo.bard.webservice.data.metric.LogicalMetric

import spock.lang.Specification

import java.util.function.Function

class SignalHandlerSpec extends Specification {

    Function accepter = Mock(Function)
    MetricTransformer metricTransformer = Mock(MetricTransformer)
    LogicalMetric logicalMetric = Mock(LogicalMetric)
    SignalHandler signalHandler = new SignalHandler(["foo"], ["bar"], accepter)
    String signalName = "foo"


    def setup() {
    }

    def "Accepts is true for whitelists false for blacklists or maybe for neither"() {
        expect:
        signalHandler.accepts("foo")  == SignalHandler.Accepts.TRUE
        signalHandler.accepts("bar")  == SignalHandler.Accepts.FALSE
        signalHandler.accepts("baz")  == SignalHandler.Accepts.MAYBE
    }

    def "Without signal supresses both previously unknown and known signals"() {
        setup:
        SignalHandler test1 = signalHandler.withoutSignal("foo")
        SignalHandler test2 = signalHandler.withoutSignal("baz")

        expect:
        test1.accepts("foo")  == SignalHandler.Accepts.FALSE
        test1.accepts("bar")  == SignalHandler.Accepts.FALSE
        test1.accepts("baz")  == SignalHandler.Accepts.MAYBE

        test2.accepts("foo")  == SignalHandler.Accepts.TRUE
        test2.accepts("bar")  == SignalHandler.Accepts.FALSE
        test2.accepts("baz")  == SignalHandler.Accepts.FALSE

    }

    def "Without signals supresses both previously unknown and known signals"() {
        setup:
        SignalHandler test1 = signalHandler.withoutSignals(["foo", "baz"])

        expect:
        test1.accepts("foo")  == SignalHandler.Accepts.FALSE
        test1.accepts("bar")  == SignalHandler.Accepts.FALSE
        test1.accepts("baz")  == SignalHandler.Accepts.FALSE
    }

    def "With signals approves both previously unknown and known signals"() {
        setup:
        SignalHandler test1 = signalHandler.withSignals(["foo", "bar", "baz"])

        expect:
        test1.accepts("foo")  == SignalHandler.Accepts.TRUE
        test1.accepts("bar")  == SignalHandler.Accepts.TRUE
        test1.accepts("baz")  == SignalHandler.Accepts.TRUE
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
            throw new UnknownSignalValueException(signalName, signalValues)
        }

        when:
        signalHandler.acceptSignal(logicalMetric, signalName, [:])

        then:
        thrown(UnknownSignalValueException)
    }
}
