package com.yahoo.bard.webservice.web.apirequest.rfc;

import com.yahoo.bard.webservice.table.LogicalTable;
import com.yahoo.bard.webservice.web.apirequest.DataApiRequest;
import com.yahoo.bard.webservice.web.util.BardConfigResources;

public interface Generator<T> {
    /**
     *
     * @param request
     * @param requestParameters
     * @param resources
     * @return
     */
    T bind(DataApiRequestBuilder request, RequestParameters requestParameters, BardConfigResources resources);

    /**
     *
     * @param requestParameters
     * @param resources
     */
    void validate(T entity, DataApiRequestBuilder builder, RequestParameters requestParameters, BardConfigResources resources);
}
