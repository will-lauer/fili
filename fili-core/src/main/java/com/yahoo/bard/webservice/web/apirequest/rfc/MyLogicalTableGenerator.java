package com.yahoo.bard.webservice.web.apirequest.rfc;

import static com.yahoo.bard.webservice.web.ErrorMessageFormat.TABLE_UNDEFINED;

import com.yahoo.bard.webservice.data.time.DefaultTimeGrain;
import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.table.TableIdentifier;
import com.yahoo.bard.webservice.web.BadApiRequestException;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequestImpl;
import com.yahoo.bard.webservice.web.util.BardConfigResources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLogicalTableGenerator implements Generator<LogicalTable> {

    private static final Logger LOG = LoggerFactory.getLogger(DataApiRequestImpl.class);

    @Override
    public LogicalTable bind(
            final DataApiRequestBuilder request,
            final RequestParameters requestParameters,
            final BardConfigResources resources
    ) {
        return requestParameters.getTableName()
                .map(t -> new TableIdentifier(t, DefaultTimeGrain.DAY))
                .map(resources.getLogicalTableDictionary()::get)
                .orElse(null);
    }

    @Override
    public void validate(
            final LogicalTable table,
            final DataApiRequestBuilder builder,
            final RequestParameters requestParameters,
            final BardConfigResources resources
    ) {
        if (table == null) {
            String tableName = requestParameters.getTableName().orElse("");
            LOG.debug(TABLE_UNDEFINED.logFormat(tableName));
            throw new BadApiRequestException(TABLE_UNDEFINED.format(tableName));
        }
    }
}
