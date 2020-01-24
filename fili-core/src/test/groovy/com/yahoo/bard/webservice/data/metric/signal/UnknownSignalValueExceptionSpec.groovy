// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal

import spock.lang.Specification

class UnknownSignalValueExceptionSpec extends Specification {

    def "Exception forms the correct message"() {
        String name = "name"
        Map<String, String> values = ["name": "bar", "otherName": "foo"]

        UnknownSignalValueException unknownSignalValueException = new UnknownSignalValueException(name, values)

        expect:
        unknownSignalValueException.signalName == name
        unknownSignalValueException.signalValues == values
        unknownSignalValueException.message.endsWith("bar")
    }
}
