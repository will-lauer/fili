package com.yahoo.bard.webservice.web.apirequest.rfc;

import com.yahoo.bard.webservice.data.time.Granularity;
import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequest;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequestFactory;
import com.yahoo.bard.webservice.web.util.BardConfigResources;

import java.util.List;
import java.util.TimeZone;

import javax.inject.Named;
import javax.ws.rs.core.PathSegment;

public class GeneratorDataApiRequestFactory implements DataApiRequestFactory {

    Generator<LogicalTable> logicalTableGenerator;
    Generator<Granularity> granularityGenerator;

    public GeneratorDataApiRequestFactory(
            @Named("logicalTableGenerator") Generator<LogicalTable> logicalTableGenerator,
            @Named("granularityyGenerator") Generator<Granularity> granularityGenerator
    ) {
        this.logicalTableGenerator = logicalTableGenerator;
        this.granularityGenerator = granularityGenerator;
    }

    @Override
    public DataApiRequest buildApiRequest(
            final String tableName,
            final String granularity,
            final List<PathSegment> dimensions,
            final String logicalMetrics,
            final String intervals,
            final String apiFilters,
            final String havings,
            final String sorts,
            final String count,
            final String topN,
            final String format,
            final String downloadFilename,
            final String timeZoneId,
            final String asyncAfter,
            final String perPage,
            final String page,
            final BardConfigResources bardConfigResources
    ) {
        RequestParameters requestParameters = new RequestParameters();
        DataApiRequestBuilder builder = new DataApiRequestBuilder(bardConfigResources);

        builder = builder.granularity(requestParameters, granularityGenerator)
                        .logicalTable(requestParameters, logicalTableGenerator);

        return builder.build();

    }
}
