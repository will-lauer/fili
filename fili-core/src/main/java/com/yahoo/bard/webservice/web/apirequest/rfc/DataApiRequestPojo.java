// Copyright 2017 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest.rfc;

import com.yahoo.bard.webservice.data.dimension.Dimension;
import com.yahoo.bard.webservice.data.dimension.DimensionDictionary;
import com.yahoo.bard.webservice.data.dimension.DimensionField;
import com.yahoo.bard.webservice.data.filterbuilders.DruidFilterBuilder;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.time.Granularity;
import com.yahoo.bard.webservice.druid.model.filter.Filter;
import com.yahoo.bard.webservice.druid.model.having.Having;
import com.yahoo.bard.webservice.druid.model.orderby.OrderByColumn;
import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.web.ApiFilter;
import com.yahoo.bard.webservice.web.ApiHaving;
import com.yahoo.bard.webservice.web.ResponseFormatType;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequest;
import com.yahoo.bard.webservice.web.filters.ApiFilters;
import com.yahoo.bard.webservice.web.util.PaginationParameters;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

import javax.ws.rs.core.Response;

public class DataApiRequestPojo implements DataApiRequest {

    protected Map<String, Object> values;

    protected final String TABLE_KEY = "table";
    protected final String GRANULARITY_KEY = "granularity";
    protected final String GROUPING_DIMENSIONS_KEY = "groupingDimensions";
    protected final String DIMENSION_FIELDS_KEY = "dimensionFields";
    protected final String LOGICAL_METRICS_KEY = "logicalMetrics";
    protected final String INTERVALS_KEY = "intervals";
    protected final String API_FILTERS_KEY = "apiFilters";
    protected final String HAVINGS_KEY = "havings";
    protected final String SORTS_KEY = "sorts";
    protected final String DATE_TIME_SORT_KEY = "dateTimeSort";
    protected final String TIME_ZONE_KEY = "dateTimeZone";

    protected final String TOP_N_KEY = "topN";
    protected final String COUNT_KEY = "count";
    protected final String PAGINATION_PARAMS_KEY = "paginationParameters";
    protected final String FORMAT_KEY = "format";
    protected final String ASYNCH_AFTER_KEY = "aynchAfter";


    protected final String DRUID_FILTER_BUILDER_KEY = "druidFilterBuilder";

    protected final String DRUID_FILTER_KEY = "druidFilter";
    protected final String DRUID_HAVING_KEY = "druidHaving";

    public DataApiRequestPojo(
            LogicalTable table,
            Granularity granularity,
            Set<Dimension> groupingDimensions,
            LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> dimensionFields,
            Set<LogicalMetric> logicalMetrics,
            List<Interval> intervals,
            ApiFilters apiFilters,
            Map<LogicalMetric, Set<ApiHaving>> havings,
            LinkedHashSet<OrderByColumn> sorts,
            Optional<OrderByColumn> dateTimeSort,
            DateTimeZone dateTimeZone,

            OptionalInt topN,
            OptionalInt count,
            Optional<PaginationParameters> paginationParameters,
            ResponseFormatType responseFormatType,
            OptionalLong asynchAfter,

            Filter druidFilter,
            Having druidHaving,
            DruidFilterBuilder druidFilterBuilder
    )
    {
        this.values = new HashMap<>();
        values.put(TABLE_KEY, table);
        values.put(GRANULARITY_KEY, granularity);
        values.put(GROUPING_DIMENSIONS_KEY, groupingDimensions);
        values.put(DIMENSION_FIELDS_KEY, dimensionFields);
        values.put(LOGICAL_METRICS_KEY, logicalMetrics);
        values.put(INTERVALS_KEY, intervals);
        values.put(API_FILTERS_KEY, apiFilters);
        values.put(HAVINGS_KEY, havings);
        values.put(SORTS_KEY, sorts);
        values.put(DATE_TIME_SORT_KEY, dateTimeSort);
        values.put(TIME_ZONE_KEY, dateTimeZone);

        values.put(TOP_N_KEY, topN);
        values.put(COUNT_KEY, count);
        values.put(PAGINATION_PARAMS_KEY, paginationParameters);
        values.put(FORMAT_KEY, responseFormatType);
        values.put(ASYNCH_AFTER_KEY, asynchAfter);

        values.put(DRUID_FILTER_KEY, druidFilter);
        values.put(DRUID_HAVING_KEY, druidHaving);
        values.put(DRUID_FILTER_BUILDER_KEY, druidFilterBuilder);
    }


    protected DataApiRequestPojo(Map<String, Object> values, String withKey, Object withValue) {
        Map<String, Object> newValues = new HashMap<>(values);
        newValues.put(withKey, withValue);
        this.values = Collections.unmodifiableMap(newValues);
    }

    protected Object getValue(String key) {
        return values.get(key);
    }

    @Override
    public LogicalTable getTable() {
        return (LogicalTable) getValue(TABLE_KEY);
    }

    @Override
    public Granularity getGranularity() {
        return (Granularity) getValue(GRANULARITY_KEY);
    }

    @Override
    public Set<Dimension> getDimensions() {
        return (Set<Dimension>) getValue(GROUPING_DIMENSIONS_KEY);
    }

    @Override
    public LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> getDimensionFields() {
        return (LinkedHashMap<Dimension, LinkedHashSet<DimensionField>>) getValue(DIMENSION_FIELDS_KEY);
    }

    @Override
    public Set<LogicalMetric> getLogicalMetrics() {
        return (Set<LogicalMetric>) getValue(LOGICAL_METRICS_KEY);
    }

    @Override
    public List<Interval> getIntervals() {
        return (List<Interval>) getValue(INTERVALS_KEY);
    }

    @Override
    public ApiFilters getApiFilters() {
        return (ApiFilters) getValue(API_FILTERS_KEY);
    }

    @Override
    public Map<LogicalMetric, Set<ApiHaving>> getHavings() {
        return (Map<LogicalMetric, Set<ApiHaving>>) getValue(HAVINGS_KEY);
    }

    @Override
    public LinkedHashSet<OrderByColumn> getSorts() {
        return (LinkedHashSet<OrderByColumn>) getValue(SORTS_KEY);
    }

    @Override
    public Optional<OrderByColumn> getDateTimeSort() {
        return (Optional<OrderByColumn>) getValue(DATE_TIME_SORT_KEY);
    }

    @Override
    public DateTimeZone getTimeZone() {
        return (DateTimeZone) getValue(TIME_ZONE_KEY);
    }

    @Override
    public OptionalInt getTopN() {
        return (OptionalInt) getValue(TOP_N_KEY);
    }

    @Override
    public OptionalInt getCount() {
        return (OptionalInt) getValue(COUNT_KEY);
    }

    @Override
    public Optional<PaginationParameters> getPaginationParameters() {
        return (Optional<PaginationParameters>) getValue(PAGINATION_PARAMS_KEY);
    }

    @Override
    public ResponseFormatType getFormat() {
        return (ResponseFormatType) getValue(FORMAT_KEY);
    }

    @Override
    public Long getAsyncAfter() {
        return (Long) getValue(ASYNCH_AFTER_KEY);
    }

    @Override
    public Filter getDruidFilter() {
        return (Filter) getValue(DRUID_FILTER_KEY);
    }

    @Override
    public Having getDruidHaving() {
        return (Having) getValue(DRUID_HAVING_KEY);
    }

    @Override
    public Map<Dimension, Set<ApiFilter>> generateFilters(
            final String filterQuery, final LogicalTable table, final DimensionDictionary dimensionDictionary
    ) {
        return null;
    }

    @Override
    public DruidFilterBuilder getFilterBuilder() {
        return (DruidFilterBuilder) getValue(DRUID_FILTER_BUILDER_KEY);
    }

    // CHECKSTYLE:OFF
    @Override
    public DataApiRequest withTable(LogicalTable table) {
        return new DataApiRequestPojo(values, TABLE_KEY, table);
    }

    @Override
    public DataApiRequest withGranularity(Granularity granularity) {
        return new DataApiRequestPojo(values, GRANULARITY_KEY, granularity);
    }

    @Override
    public DataApiRequest withDimensions(LinkedHashSet<Dimension> dimensions) {
        return new DataApiRequestPojo(values, DIMENSION_FIELDS_KEY, dimensions);
    }

    @Override
    public DataApiRequest withPerDimensionFields(
            LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> perDimensionFields)
    {
        return new DataApiRequestPojo(values, DIMENSION_FIELDS_KEY, perDimensionFields);
    }

    @Override
    public DataApiRequest withLogicalMetrics(LinkedHashSet<LogicalMetric> logicalMetrics) {
        return new DataApiRequestPojo(values, LOGICAL_METRICS_KEY, logicalMetrics);
    }

    @Override
    public DataApiRequest withIntervals(final List<Interval> intervals) {
        return new DataApiRequestPojo(values, INTERVALS_KEY, intervals);
    }

    @Override
    public DataApiRequest withIntervals(Set<Interval> intervals) {
        return new DataApiRequestPojo(values, INTERVALS_KEY, intervals);
    }

    @Override
    public DataApiRequest withFilters(ApiFilters filters) {
        return new DataApiRequestPojo(values, API_FILTERS_KEY, filters);
    }

    @Override
    public DataApiRequest withHavings(Map<LogicalMetric, Set<ApiHaving>> havings) {
        return new DataApiRequestPojo(values, HAVINGS_KEY, havings);
    }

    @Override
    public DataApiRequest withSorts(LinkedHashSet<OrderByColumn> sorts) {
        return new DataApiRequestPojo(values, SORTS_KEY, sorts);
    }

    @Override
    public DataApiRequest withTimeSort(Optional<OrderByColumn> sort) {
        return new DataApiRequestPojo(values, DATE_TIME_SORT_KEY, sort);
    }

    @Override
    public DataApiRequest withTimeZone(DateTimeZone timeZone) {
        return new DataApiRequestPojo(values, TIME_ZONE_KEY, timeZone);
    }

    @Override
    public DataApiRequest withTopN(int topN) {
        return new DataApiRequestPojo(values, TOP_N_KEY, topN);
    }

    @Override
    public DataApiRequest withFormat(ResponseFormatType format) {
        return new DataApiRequestPojo(values, FORMAT_KEY, format) ;
    }

    @Override
    public DataApiRequest withPaginationParameters(Optional<PaginationParameters> paginationParameters) {
        return null;
    }

    @Override
    public DataApiRequest withBuilder(Response.ResponseBuilder builder) {
        return null;
    }

    @Override
    public DataApiRequest withCount(int count) {
        return null;
    }

    @Override
    public DataApiRequest withAsyncAfter(long asyncAfter) {
        return null;
    }

    @Override
    public DataApiRequest withFilterBuilder(DruidFilterBuilder filterBuilder) {
        return null;
    }

    @Override
    public DruidFilterBuilder getDruidFilterBuilder() {
        return null;
    }
    // CHECKSTYLE:ON
}
