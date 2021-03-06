package org.rcsb.genomemapping.rest;

import io.swagger.annotations.ApiOperation;
import org.rcsb.genomemapping.service.CommonServiceImpl;
import org.rcsb.genomemapping.service.StructuresService;
import org.rcsb.genomemapping.service.StructuresServiceImpl;
import org.rcsb.genomemapping.utils.AppConstants;
import org.rcsb.genomemapping.utils.BooleanQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.util.Date;

/**
 * Created by Yana Valasatava on 11/15/17.
 */

@Path(AppConstants.PATH_STRUCTURES)
public class StructuresResource {

    private static final Logger logger = LoggerFactory.getLogger(StructuresResource.class);

    private CommonServiceImpl common;
    private StructuresService service;

    public StructuresResource() {
        common = new CommonServiceImpl();
        service = new StructuresServiceImpl();
    }

    @GET
    @Path(AppConstants.PATH_PING)
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "servicePing", hidden = true)
    public String servicePing() {
        logger.info("received ping on " + new Date().toString());
        return "RESTful Structure Mapping Service is running.";
    }

    @GET
    @Path(AppConstants.PATH_EXONS)
    @Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"})
    public Response mapToStructures(
            @Context UriInfo uriInfo,
            @Context Request request,
            @QueryParam(value = "taxonomyId") final int taxonomyId,
            @QueryParam(value = "id") final String id,
            @QueryParam(value = "name") final String name,
            @QueryParam(value = "canonical") final String canonical,
            @Context HttpHeaders headers) throws Exception
    {
        if (id != null)
            return service.getStructuresByGeneId(uriInfo, request, taxonomyId, id, BooleanQueryParam.valueOf(canonical), headers);

        else if (name != null)
            return service.getStructuresByGeneName(uriInfo, request, taxonomyId, name, BooleanQueryParam.valueOf(canonical), headers);

        else
            return common.invalidParameter(headers);
    }

    @GET
    @Path( AppConstants.PATH_GENOMIC )
    @Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"})
    public Response mapGeneticPosition(
            @Context UriInfo uriInfo,
            @Context Request request,
            @QueryParam(value = "taxonomyId") final int taxonomyId,
            @QueryParam(value = "chromosome") final String chromosome,
            @QueryParam(value = "position") final int position,
            @QueryParam(value = "canonical") final String canonical,
            @Context HttpHeaders headers) throws Exception
    {
        return service.getStructuresByGenomicPosition(uriInfo, request, taxonomyId, chromosome, position, BooleanQueryParam.valueOf(canonical), headers);
    }
}