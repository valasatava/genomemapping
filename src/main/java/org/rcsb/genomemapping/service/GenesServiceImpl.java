package org.rcsb.genomemapping.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public class GenesServiceImpl implements GenesService {

    @Override
    public Response mapToStructuresById(UriInfo uriInfo, Request request, String id, HttpHeaders headers) {
        return null;
    }

    @Override
    public Response mapToStructuresByName(UriInfo uriInfo, Request request, String name, HttpHeaders headers) {
        return null;
    }
}
