package org.rcsb.genomemapping.rest;

import io.swagger.annotations.ApiOperation;
import org.rcsb.genomemapping.service.CoordinatesService;
import org.rcsb.genomemapping.service.CoordinatesServiceImpl;
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

@Path(AppConstants.PATH_COORDINATES)
public class CoordinatesResource {

    private CoordinatesService service;
    private static final Logger logger = LoggerFactory.getLogger(CoordinatesResource.class);

    public CoordinatesResource() {
        service = new CoordinatesServiceImpl();
    }

    @GET
    @Path(AppConstants.PATH_PING)
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "servicePing", hidden = true)
    public String servicePing() {
        logger.info("received ping on " + new Date().toString());
        return "RESTful Coordinates Mapping Service is running.";
    }

    @GET
    @Path(AppConstants.PATH_GENOMIC + AppConstants.PATH_SEPARATOR )
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
        return service.mapGenomicPosition(uriInfo, request, taxonomyId, chromosome, position, BooleanQueryParam.valueOf(canonical), headers);
    }

    @GET
    @Path(AppConstants.PATH_STRUCTURE + AppConstants.PATH_SEPARATOR )
    @Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"})
    public Response mapPdbSeqPosition(
            @Context UriInfo uriInfo,
            @Context Request request,
            @QueryParam(value = "entryId") final String entryId,
            @QueryParam(value = "entityId") final String entityId,
            @QueryParam(value = "position") final int position,
            @QueryParam(value = "canonical") final String canonical,
            @Context HttpHeaders headers) throws Exception
    {
        return service.mapPdbSeqPosition(uriInfo, request, entryId, entityId, position, BooleanQueryParam.valueOf(canonical), headers);
    }
}