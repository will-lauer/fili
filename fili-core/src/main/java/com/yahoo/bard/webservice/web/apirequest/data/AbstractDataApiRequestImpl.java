// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest.data;

import com.yahoo.bard.webservice.data.dimension.Dimension;
import com.yahoo.bard.webservice.data.dimension.DimensionDictionary;
import com.yahoo.bard.webservice.data.dimension.DimensionField;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;
import com.yahoo.bard.webservice.data.time.Granularity;
import com.yahoo.bard.webservice.data.time.GranularityParser;
import com.yahoo.bard.webservice.druid.model.builders.DruidFilterBuilder;
import com.yahoo.bard.webservice.druid.model.orderby.OrderByColumn;
import com.yahoo.bard.webservice.druid.model.orderby.SortDirection;
import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.table.LogicalTableDictionary;
import com.yahoo.bard.webservice.web.ApiHaving;
import com.yahoo.bard.webservice.web.ResponseFormatType;
import com.yahoo.bard.webservice.web.apirequest.ApiRequestImpl;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequest;
import com.yahoo.bard.webservice.web.apirequest.exceptions.BadApiRequestException;
import com.yahoo.bard.webservice.web.apirequest.generator.having.HavingGenerator;
import com.yahoo.bard.webservice.web.filters.ApiFilters;
import com.yahoo.bard.webservice.web.util.PaginationParameters;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.PathSegment;

/**
 * Data API Request Implementation binds, validates, and models the parts of a request to the data endpoint.
 */
public abstract class AbstractDataApiRequestImpl extends ApiRequestImpl implements DataApiRequest {
    private static  Logger LOG = LoggerFactory.getLogger(DataApiRequestImpl.class);

    protected LogicalTable table;

    protected Granularity granularity;

    protected LinkedHashSet<Dimension> dimensions;
    protected LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> perDimensionFields;
    protected LinkedHashSet<LogicalMetric> logicalMetrics;
    protected List<Interval> intervals;
    protected ApiFilters apiFilters;
    protected Map<LogicalMetric, Set<ApiHaving>> havings;
    protected LinkedHashSet<OrderByColumn> sorts;
    protected OrderByColumn dateTimeSort;

    protected int count;
    protected int topN;
    protected DateTimeZone timeZone;
    protected boolean optimizable;

    @Deprecated
    protected DruidFilterBuilder filterBuilder;

    public AbstractDataApiRequestImpl(
            String tableName,
            String granularityRequest,
            List<PathSegment> dimensionsRequest,
            String logicalMetricsRequest,
            String intervalsRequest,
            String apiFiltersRequest,
            String havingsRequest,
            String sortsRequest,
            String countRequest,
            String topNRequest,
            String formatRequest,
            String downloadFilename,
            String timeZoneId,
            String asyncAfterRequest,
            @NotNull String perPage,
            @NotNull String page,
            DimensionDictionary dimensionDictionary,
            MetricDictionary metricDictionary,
            LogicalTableDictionary logicalTableDictionary,
            DateTimeZone systemTimeZone,
            GranularityParser granularityParser,
            DruidFilterBuilder druidFilterBuilder,
            HavingGenerator havingGenerator
    ) throws BadApiRequestException {
        super(formatRequest, downloadFilename, asyncAfterRequest, perPage, page);

        timeZone = generateTimeZone(timeZoneId, systemTimeZone);

        // Time grain must be from allowed interval keywords
        this.granularity = generateGranularity(granularityRequest, timeZone, granularityParser);

        this.table = bindLogicalTable(tableName, granularity, logicalTableDictionary);
        validateLogicalTable(tableName, table, granularity, logicalTableDictionary);

        // Zero or more grouping dimensions may be specified
        this.dimensions = bindGroupingDimensions(dimensionsRequest, table, dimensionDictionary);
        validateGroupingDimensions(dimensionsRequest, dimensions, table, dimensionDictionary);

        // Map of dimension to its fields specified using show clause (matrix params)
        this.perDimensionFields = bindDimensionFields(dimensionsRequest, dimensions, table, dimensionDictionary);
        validateDimensionFields(dimensionsRequest, perDimensionFields, dimensions, table, dimensionDictionary);

        // At least one logical metric is required
        this.filterBuilder = druidFilterBuilder;  // required for intersection metrics to work

        this.logicalMetrics = bindLogicalMetrics(logicalMetricsRequest, table, metricDictionary, dimensionDictionary);
        validateLogicalMetrics(logicalMetricsRequest, logicalMetrics, table, metricDictionary);

        this.intervals = bindIntervals(intervalsRequest, granularity, timeZone);
        validateIntervals(intervalsRequest, intervals, granularity, timeZone);

        // Zero or more filtering dimensions may be referenced
        this.apiFilters = bindApiFilters(apiFiltersRequest, table, dimensionDictionary);
        validateApiFilters(apiFiltersRequest, apiFilters, table, dimensionDictionary);
        validateRequestDimensions(apiFilters.keySet(), table);
        validateAggregatability(dimensions, apiFilters);

        // Zero or more having queries may be referenced
        this.havings = bindApiHavings(havingsRequest, havingGenerator, logicalMetrics);
        validateApiHavings(havingsRequest, havings);

        //Using the LinkedHashMap to preserve the sort order
        LinkedHashMap<String, SortDirection> sortColumnDirection = bindToColumnDirectionMap(sortsRequest);

        //Requested sort on dateTime column
        this.dateTimeSort = bindDateTimeSortColumn(sortColumnDirection).orElse(null);

        // Requested sort on metrics - optional, can be empty Set
        this.sorts = bindToColumnDirectionMap(
                removeDateTimeSortColumn(sortColumnDirection),
                logicalMetrics,
                metricDictionary
        );
        validateSortColumns(sorts, dateTimeSort, sortsRequest, logicalMetrics, metricDictionary);


        // Overall requested number of rows in the response. Ignores grouping in time buckets.
        this.count = bindCount(countRequest);
        validateCount(countRequest, count);

        // Requested number of rows per time bucket in the response
        this.topN = bindTopN(topNRequest);
        validateTopN(topNRequest, topN, sorts);

        this.optimizable = true;

        LOG.debug(
                "Api request: TimeGrain: {}," +
                        " Table: {}," +
                        " Dimensions: {}," +
                        " Dimension Fields: {}," +
                        " Filters: {},\n" +
                        " Havings: {},\n" +
                        " Logical metrics: {},\n\n" +
                        " Sorts: {}," +
                        " Count: {}," +
                        " TopN: {}," +
                        " AsyncAfter: {}" +
                        " Format: {}" +
                        " Pagination: {}",
                granularity,
                table.getName(),
                dimensions,
                perDimensionFields,
                apiFilters,
                havings,
                logicalMetrics,
                sorts,
                count,
                topN,
                asyncAfter,
                format,
                paginationParameters
        );
    }

    public AbstractDataApiRequestImpl(
            LogicalTable table,
            Granularity granularity,
            LinkedHashSet<Dimension> groupingDimensions,
            LinkedHashMap<Dimension, LinkedHashSet<DimensionField>> perDimensionFields,
            LinkedHashSet<LogicalMetric> logicalMetrics,
            List<Interval> intervals,
            ApiFilters apiFilters,
            Map<LogicalMetric, Set<ApiHaving>> havings,
            LinkedHashSet<OrderByColumn> sorts,
            OrderByColumn dateTimeSort,
            DateTimeZone timeZone,
            Integer topN,
            Integer count,
            PaginationParameters paginationParameters,
            ResponseFormatType format,
            String downloadFilename,
            Long asyncAfter,
            boolean optimizable
    ) {
        super(format, downloadFilename, asyncAfter, paginationParameters);
        this.table = table;
        this.granularity = granularity;
        this.dimensions = groupingDimensions;
        this.perDimensionFields = perDimensionFields;
        this.logicalMetrics = logicalMetrics;
        this.intervals = intervals;
        this.apiFilters = apiFilters;
        this.havings = havings;
        this.sorts = sorts;
        this.dateTimeSort = dateTimeSort;
        this.count = count;
        this.topN = topN;
        this.timeZone = timeZone;
        this.optimizable = optimizable;
    }
}
