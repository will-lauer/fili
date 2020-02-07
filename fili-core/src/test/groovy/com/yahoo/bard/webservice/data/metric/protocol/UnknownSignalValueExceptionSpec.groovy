// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol

import spock.lang.Specification

class UnknownSignalValueExceptionSpec extends Specification {

    def "Exception forms the correct message"() {
        String name = "name"
        Map<String, String> values = ["name": "bar", "otherName": "foo"]

        UnknownProtocolValueException unknownSignalValueException = new UnknownProtocolValueException(name, values)

        expect:
        unknownSignalValueException.signalName == name
        unknownSignalValueException.protocolValues == values
        unknownSignalValueException.message.endsWith("bar")
    }
}
