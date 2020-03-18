// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils;

import java.util.Map;
import java.util.Objects;

/**
 * Metric detail represents a parsed apiName and set of parameters for a metric or other similar concept.
 */
public class MetricDetail {

    final String apiName;
    final String baseApiName;
    final Map<String, String> params;

    /**
     * Constructor.
     *
     * @param baseApiName  The name for the column
     * @param apiName  The api name for the base metric (the one in the metric dictionary)
     * @param params The list of key value params associated with it.
     */
    MetricDetail(String apiName, String baseApiName, Map<String, String> params) {
        this.baseApiName = baseApiName;
        this.apiName = apiName;
        this.params = params;
    }

    /**
     * Getter.
     *
     * @return The original column name
     */
    public String getBaseApiName() {
        return baseApiName;
    }

    /**
     * Getter.
     *
     * @return An api name
     */
    public String getApiName() {
        return apiName;
    }

    /**
     * Getter.
     *
     * @return A map of key value parameters.
     */
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return String.format("Metric details: %s: %s", apiName, params.toString());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        final MetricDetail that = (MetricDetail) o;
        return Objects.equals(baseApiName, that.baseApiName) &&
                Objects.equals(apiName, that.apiName) &&
                Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseApiName, apiName, params);
    }
}
