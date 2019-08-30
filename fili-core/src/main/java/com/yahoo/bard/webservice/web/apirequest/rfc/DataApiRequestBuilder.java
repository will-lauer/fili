package com.yahoo.bard.webservice.web.apirequest.rfc;

import com.yahoo.bard.webservice.data.dimension.Dimension;
import com.yahoo.bard.webservice.data.dimension.DimensionDictionary;
import com.yahoo.bard.webservice.data.dimension.DimensionField;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.time.Granularity;
import com.yahoo.bard.webservice.druid.model.builders.DruidFilterBuilder;
import com.yahoo.bard.webservice.druid.model.filter.Filter;
import com.yahoo.bard.webservice.druid.model.having.Having;
import com.yahoo.bard.webservice.druid.model.orderby.OrderByColumn;
import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.web.ApiFilter;
import com.yahoo.bard.webservice.web.ApiHaving;
import com.yahoo.bard.webservice.web.ResponseFormatType;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequest;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequestImpl;
import com.yahoo.bard.webservice.web.filters.ApiFilters;
import com.yahoo.bard.webservice.web.util.BardConfigResources;
import com.yahoo.bard.webservice.web.util.PaginationParameters;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import javax.ws.rs.core.Response;

// TODO Look at extendable subclassing options for new fields (to avoid casting)
public class DataApiRequestBuilder {

    BardConfigResources resources;

    public DataApiRequestBuilder(BardConfigResources resources) {
        this.resources = resources;
    }


    public static final String LOGICAL_TABLE = "logicalTable";
    public static final String GRANULARITY = "logicalTable";

    LogicalTable logicalTable;
    Granularity granularity;

    <T> T bindAndValidate(RequestParameters parameters, Generator<T> generator) {
        T bound = generator.bind(this, parameters, resources);
        generator.validate(bound, this, parameters, resources);
        return bound;
    }

    public DataApiRequestBuilder logicalTable(RequestParameters parameters, Generator<LogicalTable> generator) {
        this.logicalTable = bindAndValidate(parameters, generator);
        return this;
    }

    public DataApiRequestBuilder granularity(RequestParameters parameters, Generator<Granularity> generator) {
        this.granularity = bindAndValidate(parameters, generator);
        return this;
    }

    DataApiRequest build() {
        new DataApiRequestPojoImpl(...)
    }
}
