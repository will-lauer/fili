package com.yahoo.bard.webservice.web.apirequest.rfc;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.PathSegment;

public class RequestParameters {

    public Optional<String> getTableName() {
        return Optional.ofNullable(tableName);
    }

    public Optional<String> getGranularity() {
        return Optional.ofNullable(granularity);
    }

    public List<PathSegment> getDimensions() {
        return dimensions;
    }

    public Optional<String> getLogicalMetrics() {
        return Optional.ofNullable(logicalMetrics);
    }

    public Optional<String> getIntervals() {
        return Optional.ofNullable(intervals);
    }

    public Optional<String> getApiFilters() {
        return Optional.ofNullable(apiFilters);
    }

    public Optional<String> getHavings() {
        return Optional.ofNullable(havings);
    }

    public Optional<String> getSorts() {
        return Optional.ofNullable(sorts);
    }

    public Optional<String> getCount() {
        return Optional.ofNullable(count);
    }

    public Optional<String> getTopN() {
        return Optional.ofNullable(topN);
    }

    public Optional<String> getFormat() {
        return Optional.ofNullable(format);
    }

    public Optional<String> getTimeZoneId() {
        return Optional.ofNullable(timeZoneId);
    }

    public Optional<String> getAsyncAfter() {
        return Optional.ofNullable(asyncAfter);
    }

    String tableName;
    String granularity;
    List<PathSegment> dimensions;
    String logicalMetrics;
    String intervals;
    String apiFilters;
    String havings;
    String sorts;
    String count;
    String topN;
    String format;
    String timeZoneId;
    String asyncAfter;
}
