// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest.generator.orderBy;

import static com.yahoo.bard.webservice.web.ErrorMessageFormat.DATE_TIME_SORT_VALUE_INVALID;
import static com.yahoo.bard.webservice.web.ErrorMessageFormat.SORT_DIRECTION_INVALID;
import static com.yahoo.bard.webservice.web.ErrorMessageFormat.SORT_METRICS_NOT_IN_QUERY_FORMAT;
import static com.yahoo.bard.webservice.web.ErrorMessageFormat.SORT_METRICS_NOT_SORTABLE_FORMAT;
import static com.yahoo.bard.webservice.web.apirequest.DataApiRequest.DATE_TIME_STRING;

import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.druid.model.orderby.OrderByColumn;
import com.yahoo.bard.webservice.druid.model.orderby.SortDirection;
import com.yahoo.bard.webservice.logging.RequestLog;
import com.yahoo.bard.webservice.logging.TimedPhase;
import com.yahoo.bard.webservice.util.StreamUtils;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequestBuilder;
import com.yahoo.bard.webservice.web.apirequest.RequestParameters;
import com.yahoo.bard.webservice.web.apirequest.exceptions.BadApiRequestException;
import com.yahoo.bard.webservice.web.apirequest.generator.Generator;
import com.yahoo.bard.webservice.web.util.BardConfigResources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default generator implementation for binding logical metrics. Binding logical metrics is dependent on the logical
 * table being queried. Ensure the logical table has been bound before using this class to generate logical metrics.
 */
public class DefaultOrderByGenerator implements Generator<List<OrderByColumn>> {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOrderByGenerator.class);

    @Override
    public List<OrderByColumn> bind(
            DataApiRequestBuilder builder,
            RequestParameters params,
            BardConfigResources resources
    ) {
        return generateOrderByColumns(
                params.getSorts().orElse(null),
                builder.getLogicalMetricsIfInitialized()
        );
    }

    @Override
    public void validate(
            final List<OrderByColumn> entity,
            final DataApiRequestBuilder builder,
            final RequestParameters params,
            final BardConfigResources resources
    ) {
        if (entity.stream().skip(1).anyMatch(column -> column.getDimension().equals(DATE_TIME_STRING)) ){
            LOG.debug(DATE_TIME_SORT_VALUE_INVALID.logFormat());
            throw new BadApiRequestException(DATE_TIME_SORT_VALUE_INVALID.format());
        };
    }

    /**
     * Extract valid sort direction.
     *
     * @param columnWithDirection  Column and its sorting direction
     *
     * @return Sorting direction. If no direction provided then the default one will be DESC
     */
    protected SortDirection getSortDirection(List<String> columnWithDirection) {
        try {
            return columnWithDirection.size() == 2 ?
                    SortDirection.valueOf(columnWithDirection.get(1).toUpperCase(Locale.ENGLISH)) :
                    SortDirection.DESC;
        } catch (IllegalArgumentException ignored) {
            String sortDirectionName = columnWithDirection.get(1);
            LOG.debug(SORT_DIRECTION_INVALID.logFormat(sortDirectionName));
            throw new BadApiRequestException(SORT_DIRECTION_INVALID.format(sortDirectionName));
        }
    }

    /**
     * Method to convert sort list to column and direction map.
     *
     * @param sortsRequest  String of sort columns
     *
     * @return LinkedHashMap of columns and their direction. Using LinkedHashMap to preserve the order
     */
    protected List<OrderByColumn> parseLogicalOrderByColumns(String sortsRequest) {
        LinkedHashMap<String, OrderByColumn> logicalColumns = new LinkedHashMap<>();

        if (sortsRequest == null || sortsRequest.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderByColumn> result = Arrays.stream(sortsRequest.split(","))
                .map(e -> Arrays.asList(e.split("\\|")))
                .map(e -> new OrderByColumn(e.get(0), getSortDirection(e)))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Generates a Set of OrderByColumn.
     *
     * @param sortsRequest  api request string for the sort clause
     * @param selectedMetrics  Set of LogicalMetrics selected in the api request
     *
     * @return a List of OrderByColumn whose names are physical column names or "dateTime"
     * @throws BadApiRequestException if the sort clause is invalid due to unresolvable logical column names.
     */
    protected List<OrderByColumn> generateOrderByColumns(
            String sortsRequest,
            Set<LogicalMetric> selectedMetrics
    ) throws BadApiRequestException {
        try (TimedPhase timer = RequestLog.startTiming("GeneratingSortColumns")) {

            LinkedHashMap<String, OrderByColumn> logicalColumnByName = parseLogicalOrderByColumns(sortsRequest).stream()
                    .collect(StreamUtils.toLinkedMap(OrderByColumn::getDimension, f -> f));

            List<String> unmatchedMetrics = new ArrayList<>();
            List<String> unsortableMetrics = new ArrayList<>();
            List<OrderByColumn> physicalOrderByColumns = new ArrayList<>();

            OrderByColumn dateTime = logicalColumnByName.remove(DATE_TIME_STRING);

            if (dateTime != null) {
                physicalOrderByColumns.add(dateTime);
            }

            for (String key : logicalColumnByName.keySet())  {

                OrderByColumn orderByColumn = logicalToPhysicalOrderByColumn(
                        logicalColumnByName.get(key),
                        selectedMetrics,
                        unsortableMetrics,
                        unmatchedMetrics
                );
                if (orderByColumn != null) {
                    physicalOrderByColumns.add(orderByColumn);
                }
            }
            if (!unmatchedMetrics.isEmpty()) {
                LOG.debug(SORT_METRICS_NOT_IN_QUERY_FORMAT.logFormat(unmatchedMetrics.toString()));
                throw new BadApiRequestException(SORT_METRICS_NOT_IN_QUERY_FORMAT.format(unmatchedMetrics.toString()));
            }
            if (!unsortableMetrics.isEmpty()) {
                LOG.debug(SORT_METRICS_NOT_SORTABLE_FORMAT.logFormat(unsortableMetrics.toString()));
                throw new BadApiRequestException(SORT_METRICS_NOT_SORTABLE_FORMAT.format(unsortableMetrics.toString()));
            }

            return physicalOrderByColumns;
        }
    }

    private OrderByColumn logicalToPhysicalOrderByColumn(
            final OrderByColumn logicalColumn,
            final Set<LogicalMetric> selectedMetrics,
            final List<String> unsortableMetrics,
            final List<String> unmatchedMetrics
    ) {
        String logicalColumnName = logicalColumn.getDimension();

        final Map<String, LogicalMetric> metricsByName = new LinkedHashMap<>();
        if (selectedMetrics != null) {
            selectedMetrics.forEach(metric -> metricsByName.put(metric.getName(), metric));
        }

        if (!metricsByName.containsKey(logicalColumnName)) {
            unmatchedMetrics.add(logicalColumnName);
            return null;
        }
        LogicalMetric logicalMetric = metricsByName.get(logicalColumnName);

        if (logicalMetric.getTemplateDruidQuery() == null) {
            unsortableMetrics.add(logicalColumnName);
            return null;
        }
        OrderByColumn physicalOrderByColumn = new OrderByColumn(
                logicalMetric.getMetricField().getName(),
                logicalColumn.getDirection()
        );
        return physicalOrderByColumn;
    }
}
