// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils

import spock.lang.Specification

class MetricTokenIteratorSpec extends Specification {

    def "Iterator processes correctly"() {
        setup:
        def metrics = "one(bar=baz),two(  ),three(),four(bar=baz, one=two)";
        def actual = []
        MetricTokenIterator iterator = new MetricTokenIterator(metrics)
        while (iterator.hasNext()) {
            actual.add(iterator.next())
        }

        expect:
        actual == ["one(bar=baz)", "two(  )", "three()", "four(bar=baz, one=two)"]
    }

    def "Unbalanced paranthesis create exceptions"() {
        setup:
        def metrics = "one(bar=baz),two(  ),three(,four(bar=baz, one=two)";

        when:
        MetricTokenIterator iterator = new MetricTokenIterator(metrics)
        def actual = []
        while (iterator.hasNext()) {
            actual.add(iterator.next())
        }

        then:
        thrown(MetricTokenIterator.UnbalancedMetricExpressionException)
    }
}
