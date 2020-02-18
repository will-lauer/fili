// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol;

/**
 * Protocol describes a named type transformation on a group of metrics.
 *
 * Protocols should be immutable.
 *
 * Protocols have a contract name for which, under normal circumstances, there should be only one implementing version
 * active in a system.  If a client application wants to define it's own version of a built in protocol, it should
 * have the same contract name and replace that metric in the built in dictionaries.
 *
 * Protocols have a 'core' parameter, the parameter name from metric request terms
 * that indicate this protocol should be invoked.
 *
 * Finally, a Metric Transformer, a metric function which transforms metrics into other metrics.
 *
 * Typcically protocols are not idempotent and applying the same mapping twice is incorrect.
 */
public class Protocol {

    private final String contractName;
    private final String coreParameter;
    private final MetricTransformer metricTransformer;

    /**
     * Constructor.
     *
     * Use the protocol name as the default parameter name.
     *
     * @param contractName  The name of the protocol.
     * @param metricTransformer  The metric transformer implementing this protocol's transform
     */
    public Protocol(String contractName, MetricTransformer metricTransformer) {
        this(contractName, contractName, metricTransformer);
    }

    /**
     * Constructor.
     *
     * @param contractName  The name of the protocol.
     * @param coreParameter The name of the core parameter for this protocol.
     * @param metricTransformer  The transformer to process metrics under this protocol.
     */
    public Protocol(
            String contractName,
            String coreParameter,
            MetricTransformer metricTransformer
    ) {
        this.contractName = contractName;
        this.coreParameter = coreParameter;
        this.metricTransformer = metricTransformer;
    }

    /**
     * The name of the protocol.
     *
     * @return a name
     */
    public String getContractName() {
        return contractName;
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
