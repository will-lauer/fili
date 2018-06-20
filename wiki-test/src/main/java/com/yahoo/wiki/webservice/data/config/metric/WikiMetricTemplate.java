// Copyright 2018 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.wiki.webservice.data.config.metric;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yahoo.bard.webservice.data.time.TimeGrain;
import com.yahoo.bard.webservice.util.EnumUtils;
import com.yahoo.wiki.webservice.data.config.Template;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wiki metric template.
 *
 * An example:
 *
 *       {
 *         "apiName" : "ADDED",
 *         "longName" : "ADDED",
 *         "description" : "Description for added",
 *         "maker" : "DoubleSum",
 *         "dependencyMetricNames" : ["ADDED"]
 *       }
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikiMetricTemplate extends Template implements MetricConfigAPI {

    @JsonProperty("apiName")
    private String apiName;

    @JsonProperty("longName")
    private String longName;

    @JsonProperty("maker")
    private String makerName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("dependencyMetricNames")
    private List<String> dependencyMetricNames;

    private List<TimeGrain> satisfyingGrains;

    /**
     * Constructor used by json parser.
     *
     * @param apiName               json property apiName
     * @param longName              json property longName
     * @param makerName             json property makerName
     * @param description           json property description
     * @param dependencyMetricNames json property dependencyMetricNames
     */
    WikiMetricTemplate(
            @JsonProperty("apiName") String apiName,
            @JsonProperty("longName") String longName,
            @JsonProperty("maker") String makerName,
            @JsonProperty("description") String description,
            @JsonProperty("dependencyMetricNames") List<String> dependencyMetricNames
    ) {
        setApiName(apiName);
        setLongName(longName);
        setMakerName(makerName);
        setDescription(description);
        setDependencyMetricNames(dependencyMetricNames);
    }

    /**
     * Create a Wiki Metric descriptor with a fixed set of satisfying grains.
     *
     * @param apiName          The api name for the metric.
     * @param satisfyingGrains The grains that satisfy this metric.
     */
    WikiMetricTemplate(String apiName, TimeGrain... satisfyingGrains) {
        // to camelCase
        this.apiName = (apiName == null ? EnumUtils.camelCase(this.getApiName()) : apiName);
        this.satisfyingGrains = Arrays.asList(satisfyingGrains);
    }

    /**
     * Set metrics info.
     */
    @Override
    public void setApiName(String apiName) {
        this.apiName = (apiName == null ? EnumUtils.camelCase(this.apiName) : apiName);
    }

    @Override
    public void setLongName(String longName) {
        this.longName = longName;
    }

    @Override
    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setDependencyMetricNames(List<String> dependencyMetricNames) {
        this.dependencyMetricNames = dependencyMetricNames;
    }

    /**
     * Get metrics info.
     */
    @Override
    public String getApiName() {
        return EnumUtils.camelCase(this.apiName);
    }

    @Override
    public String getLongName() {
        if (Objects.isNull(longName)) {
            return getApiName();
        }
        return EnumUtils.camelCase(longName);
    }

    @Override
    public String getMakerName() {
        return makerName.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<String> getDependencyMetricNames() {
        if (dependencyMetricNames == null) {
            return Collections.emptyList();
        }
        return dependencyMetricNames.stream().map(name -> EnumUtils.camelCase(name)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return this.getApiName().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String asName() {
        return getApiName();
    }

    @Override
    public boolean isValidFor(TimeGrain grain) {
        // As long as the satisfying grains of this metric satisfy the requested grain
        return satisfyingGrains.stream().anyMatch(grain::satisfiedBy);
    }
}