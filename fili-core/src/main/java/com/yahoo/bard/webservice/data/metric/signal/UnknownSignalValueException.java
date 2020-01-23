// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

/**
 * A checked exception for errors emitted when a signal value cannot be processed.
 */
public class UnknownSignalValueException extends Exception {

    public static final String MESSAGE_FORMAT = "Uknown value for signal %s: %s";

    public UnknownSignalValueException(String signalName, String signalValue) {
        super(String.format(MESSAGE_FORMAT, signalName, signalValue));
    }
}
