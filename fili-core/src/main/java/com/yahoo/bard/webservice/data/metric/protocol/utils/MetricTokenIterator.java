// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator to pull comma delimited metrics off a string without splitting on paranthetical commas.
 */
public class MetricTokenIterator implements Iterator<String> {

    String text;

    int paranDepth;

    int position;

    /**
     * Constructor.
     *
     * @param metricText  Text describing an expression for a list of metrics.
     */
    MetricTokenIterator(String metricText) {
        text = metricText.trim();
        position = 0;
    }

    @Override
    public boolean hasNext() {
        return position < text.length();
    }

    @Override
    public String next() {
        if (position >= text.length()) {
            throw new NoSuchElementException(String.format("No element found after position %d", position));
        }
        if (paranDepth != 0) {
            throw new IllegalStateException(text.toString());
        }
        int tempPosition = position;
        while (tempPosition < text.length() && !isSplittableComma(tempPosition)) {
            if (text.charAt(tempPosition) == '(') {
                paranDepth++;
            }
            if (text.charAt(tempPosition) == ')') {
                paranDepth--;
            }
            tempPosition++;
        }
        String result;

        if (tempPosition < text.length()) {
            // We have a splittable comma in here, so don't capture it
            result = text.substring(position, tempPosition);
            position = tempPosition + 1;
        } else {
            // we're at the end of the string
            result = text.substring(position);
            position = tempPosition;
        }
        if (paranDepth != 0) {
            throw new UnbalancedMetricExpressionException(text.toString());
        }
        return result;
    }

    private boolean isSplittableComma(int position) {
        return (text.charAt(position) == ',' && paranDepth == 0);
    }

    @Override
    public String toString() {
        return "Text " + text + " position " + position;
    }

    static class UnbalancedMetricExpressionException extends IllegalStateException {

        public static String messageFormat = "Unbalanced parantheses while parsing expression: %s";

        UnbalancedMetricExpressionException(String expression) {
            super(String.format(messageFormat, expression));
        }
    }
}
