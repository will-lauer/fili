// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * Protocol describes a supported transformation on for some group of metrics.
 *
 * The protocol should have implicit parameter(s) which are read for metric transformation.
 */
public class Protocol {

    private final String name;
    private final String coreParameter;
    private final MetricTransformer metricTransformer;

    /**
     * Constructor.
     *
     * Use the protocol name as the default parameter name.
     *
     * @param name  The name of the protocol.
     */
    public Protocol(String name, MetricTransformer metricTransformer) {
        this(name, name, metricTransformer);
    }

    /**
     * Constructor.
     *
     * @param name  The name of the protocol.
     * @param coreParameter The name of the core parameter for this protocol.
     * @param metricTransformer  The transformer to process metrics under this protocol.
     */
    public Protocol(
            String name,
            String coreParameter,
            MetricTransformer metricTransformer
    ) {
        this.name = name;
        this.coreParameter = coreParameter;
        this.metricTransformer = metricTransformer;
    }

    /**
     * The name of the protocol.
     *
     * @return a name
     */
    public String getName() {
        return name;
    }

    /**
     * The parameter whose presence triggers this protocol.
     *
     * @return a parameter name
     */
    public String getCoreParameter() {
        return coreParameter;
    }

    /**
     * The function to transform a metric under this protocol.
     *
     * @return the transformer for a given protocol.
     */
    public MetricTransformer getMetricTransformer() {
        return metricTransformer;
    }
}
