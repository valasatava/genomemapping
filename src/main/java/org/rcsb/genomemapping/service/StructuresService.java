package org.rcsb.genomemapping.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public interface StructuresService {

    Response getStructuresByGeneName(UriInfo uriInfo, Request request, String name, HttpHeaders headers);

    Response getStructuresByGeneId(UriInfo uriInfo, Request request, String id, HttpHeaders headers);
}