// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils

import spock.lang.Specification
import spock.lang.Unroll

class SimpleMetricApiProtocolParserSpec extends Specification {

    @Unroll
    def "Parser produces correct value for single metric"() {
        expect:
        new MetricDetailParser(text).metricDetails == [new MetricDetail(metric, params)]
        where:
        text                         | metric  | params
        "one(bar=baz)"               | "one"   | ["bar": "baz"]
        "two(  )"                    | "two"   | [:]
        "three()"                    | "three" | [:]
        "four(bar=baz, one=two)"     | "four"  | ["bar": "baz", "one": "two"]
        "five( bar=baz, one=three )" | "five"  | ["bar": "baz", "one": "three"]
        "six(bar=baz, 1=2)"          | "six"   | ["bar": "baz", "1": "2"]
        "seven(bar=baz , 1=7)"       | "seven" | ["bar": "baz", "1": "7"]
    }

    def "Parser produces correct value for list of metrics"() {
        setup:
        def metrics = "one(bar=baz),two(  ),three(),four(bar=baz, one=two)";
        List expected = [];
        expected.add(new MetricDetail("one", ["bar": "baz"]))
        expected.add(new MetricDetail("two", [:]))
        expected.add(new MetricDetail("three", [:]))
        expected.add(new MetricDetail("four", ["bar": "baz", "one": "two"]))

        expect:
        new MetricDetailParser(metrics).metricDetails == expected
    }
}
