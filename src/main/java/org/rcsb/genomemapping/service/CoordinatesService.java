package org.rcsb.genomemapping.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/28/17.
 */
public interface CoordinatesService {

    public Response mapGeneticPosition(UriInfo uriInfo, Request request, int taxonomyId, String chromosome, int position, HttpHeaders headers) throws Exception;
}
