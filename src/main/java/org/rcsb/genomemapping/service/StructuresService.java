package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.utils.BooleanQueryParam;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public interface StructuresService {

    Response getStructuresByGeneName(UriInfo uriInfo, Request request, int taxonomyId, String name, BooleanQueryParam canonical, HttpHeaders headers);

    Response getStructuresByGeneId(UriInfo uriInfo, Request request, int taxonomyId, String id, BooleanQueryParam canonical, HttpHeaders headers);

    Response mapGenomicPositionToStructures(UriInfo uriInfo, Request request, int taxonomyId, String chromosome, int position, HttpHeaders headers);
}