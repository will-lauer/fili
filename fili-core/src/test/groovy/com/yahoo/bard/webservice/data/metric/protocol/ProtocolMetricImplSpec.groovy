// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import com.yahoo.bard.webservice.data.metric.LogicalMetric
import com.yahoo.bard.webservice.data.metric.LogicalMetricInfo
import com.yahoo.bard.webservice.data.metric.TemplateDruidQuery
import com.yahoo.bard.webservice.data.metric.mappers.NoOpResultSetMapper
import com.yahoo.bard.webservice.data.metric.mappers.ResultSetMapper

import spock.lang.Specification

class ProtocolMetricImplSpec extends Specification {

    LogicalMetricInfo logicalMetricInfo = new LogicalMetricInfo("name")
    TemplateDruidQuery templateDruidQuery = Mock(TemplateDruidQuery)
    ResultSetMapper resultSetMapper = new NoOpResultSetMapper()
    ProtocolSupport signalHandler = Mock(ProtocolSupport)
    ProtocolMetricImpl signalMetric;

    String signalName = "foo"

    def setup() {
        signalMetric = new ProtocolMetricImpl(logicalMetricInfo, templateDruidQuery, resultSetMapper, signalHandler)
    }

    def "Accepts is true if the underlying signal handlers is true only"() {
        when:
        boolean accepts = signalMetric.accepts(signalName)

        then:
        1 * signalHandler.accepts(signalName) >> ProtocolSupport.Accepts.MAYBE
        ! accepts

        when:
        accepts = signalMetric.accepts(signalName)

        then:
        1 * signalHandler.accepts(signalName) >> ProtocolSupport.Accepts.FALSE
        ! accepts

        when:
        accepts = signalMetric.accepts(signalName)

        then:
        1 * signalHandler.accepts(signalName) >> ProtocolSupport.Accepts.TRUE
        accepts
    }

    def "Accept invokes the signal handler passing itself as an argument"() {
        setup:
        LogicalMetric logicalMetric = Mock(LogicalMetric)
        Map map = [:]

        when:
        LogicalMetric result = signalMetric.accept(signalName, map)

        then:
        signalHandler.acceptSignal(signalMetric, signalName, map) >> logicalMetric
        result == logicalMetric
    }
}
