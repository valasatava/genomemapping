package org.rcsb.genomemapping.rest;

import io.swagger.annotations.ApiOperation;
import org.rcsb.genomemapping.service.CoordinatesService;
import org.rcsb.genomemapping.service.CoordinatesServiceImpl;
import org.rcsb.genomemapping.utils.AppConstants;
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

@Path(AppConstants.PATH_MAPPING+AppConstants.PATH_COORDINATES)
public class CoordinatesResource {

    private CoordinatesService coordinatesService;
    private static final Logger logger = LoggerFactory.getLogger(CoordinatesResource.class);

    public CoordinatesResource() {
        coordinatesService = new CoordinatesServiceImpl();
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
    @Path(AppConstants.PATH_GENOMIC+ AppConstants.PATH_SEPARATOR )
    @Produces({MediaType.APPLICATION_JSON + "; charset=utf-8"})
    public Response showIt(
            @Context UriInfo uriInfo,
            @Context Request request,
            @QueryParam(value = "taxonomyId") final int taxonomyId,
            @QueryParam(value = "chromosome") final String chromosome,
            @QueryParam(value = "position") final int position,
            @Context HttpHeaders headers) throws Exception
    {
        return coordinatesService.getResidueByGeneticPosition(uriInfo, request, taxonomyId, chromosome, position, headers);
    }
}