// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest.generator.having.antlr

import com.yahoo.bard.webservice.web.apirequest.generator.having.DefaultHavingApiGenerator
import com.yahoo.bard.webservice.web.apirequest.generator.metric.antlr.ProtocolAntlrApiMetricParser
import com.yahoo.bard.webservice.web.apirequest.metrics.ApiMetric

import spock.lang.Specification
import spock.lang.Unroll

class ProtocolAntlrHavingGeneratorSpec extends Specification {

    DefaultHavingApiGenerator generator = new DefaultHavingApiGenerator()

    @Unroll
    def "Parser produces correct value for single metric"() {
        expect:
        generator.apply(text) == [new ApiMetric(text.replace(" ", ""), metric, params)]
        where:
        text                         | metric  | params
        "two ( )-gt[1]"                    | "two"   | [:]
        "one(bar=baz)-gt[1]"               | "one"   | ["bar": "baz"]
        "three-gt[1]"                      | "three" | [:]
        "four(bar=baz, one=two)-gt[1]"     | "four"  | ["bar": "baz", "one": "two"]
        "five( bar=baz, one=three )-gt[1]" | "five"  | ["bar": "baz", "one": "three"]
        "six(bar=baz, 1=2)-gt[1]"          | "six"   | ["bar": "baz", "1": "2"]
        "seven(bar=baz , 1=7)-gt[1]"       | "seven" | ["bar": "baz", "1": "7"]
    }

    def "Parser produces correct value for list of metrics"() {
        setup:
        def expected = []
        String key
        key = "one(bar=baz)"
        ApiMetric expectedMetric = new ApiMetric(key.replace(" ", ""), "one", ["bar": "baz"])
        expected.add(expectedMetric)
        key = "two(  )"
        expectedMetric = new ApiMetric(key.replace(" ", ""), "two", [:])
        expected.add(expectedMetric)
        key = " three "
        expectedMetric = new ApiMetric(key.replace(" ", ""), "three", [:])
        expected.add(expectedMetric)
        key = "four(bar=baz, one = two)"
        expectedMetric = new ApiMetric(key.replace(" ", ""), "four", ["bar": "baz", "one": "two"])
        expected.add(expectedMetric)
        def query = expected.collect {it.rawName}.join(",")

        expect:
        generator.apply(query) == expected
    }
}
