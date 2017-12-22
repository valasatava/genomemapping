package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.utils.BooleanQueryParam;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public interface CoordinatesService {

    Response mapGenomicPosition(UriInfo uriInfo, Request request, int taxonomyId, String chromosome, int position, BooleanQueryParam canonical, HttpHeaders headers) throws Exception;
    Response mapPdbSeqPosition(UriInfo uriInfo, Request request, String entryId, String entityId, int position, BooleanQueryParam canonical, HttpHeaders headers) throws Exception;
}
