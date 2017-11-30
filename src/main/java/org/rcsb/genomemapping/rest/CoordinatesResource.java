package org.rcsb.genomemapping.rest;

import org.rcsb.genomemapping.service.CoordinatesService;
import org.rcsb.genomemapping.service.CoordinatesServiceImpl;
import org.rcsb.genomemapping.utils.AppConstants;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;

/**
 * Created by Yana Valasatava on 11/15/17.
 */

@Path(AppConstants.PATH_MAPPING+AppConstants.PATH_COORDINATES)
public class CoordinatesResource {

    private CoordinatesService coordinatesService;

    public CoordinatesResource() {
        coordinatesService = new CoordinatesServiceImpl();
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