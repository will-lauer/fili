// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.signal;

import java.util.Map;

/**
 * A checked exception for errors emitted when a signal value cannot be processed.
 */
public class UnknownSignalValueException extends Exception {

    public static final String MESSAGE_FORMAT = "Unknown value for signal %s: %s";

    public String signalName;
    Map<String, String> signalValues;

    /**
     * Constructor.
     *
     * @param signalName  The name of the signal being processed.
     * @param signalValues  The values for the signal.
     */
    public UnknownSignalValueException(String signalName, Map<String, String> signalValues) {
        super(String.format(MESSAGE_FORMAT, signalName, signalValues.get(signalName)));
        this.signalName = signalName;
        this.signalValues = signalValues;
    }

    /**
     * The name of the signal.
     *
     * @return A signal name.
     */
    public String getSignalName() {
        return signalName;
    }

    /**
     * The signal values.
     *
     * @return A map of values.
     */
    public Map<String, String> getSignalValues() {
        return signalValues;
    }
}
