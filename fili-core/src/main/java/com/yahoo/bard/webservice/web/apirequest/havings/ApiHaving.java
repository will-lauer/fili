// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest.havings;

import com.yahoo.bard.webservice.web.apirequest.metrics.ApiMetric;

import java.util.List;
import java.util.Map;

public class ApiHaving extends ApiMetric {

    private final String operation;
    private final List<String> values;

    /**
     * Constructor.
     *  @param rawName  The name of this metric as it appears in the request
     * @param baseApiMetricId  The name of the base metric from the metric dictionary.
     * @param parameters  The key value pairs of used to modify the metric, if any.
     */
    public ApiHaving(
            final String rawName,
            final String baseApiMetricId,
            final Map<String, String> parameters,
            String operation,
            List<String> values
    ) {
        super(rawName, baseApiMetricId, parameters);
        this.operation = operation;
        this.values = values;
    }

    public String getOperation() {
        return operation;
    }

    public List<String> getValues() {
        return values;
    }
}
