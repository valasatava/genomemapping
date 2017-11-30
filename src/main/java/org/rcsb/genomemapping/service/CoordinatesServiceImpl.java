package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.dao.CoordinatesDao;
import org.rcsb.genomemapping.dao.CoordinatesDaoMongoImpl;
import org.rcsb.genomemapping.response.ResponseMessagePositions;
import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.mojave.genomemapping.models.Position;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


/**
 * Created by Yana Valasatava on 11/28/17.
 */
public class CoordinatesServiceImpl implements CoordinatesService {

    private CoordinatesDao coordinatesDao;

    public static final String ETAG_PREFIX = "coordinates-";

    public CoordinatesServiceImpl() {

        super();
        coordinatesDao = new CoordinatesDaoMongoImpl();
    }

    @Override
    public Response getResidueByGeneticPosition(UriInfo uriInfo, Request request, int taxonomyId, String chromosome, int position, HttpHeaders headers) throws Exception {

        List<Position> results = coordinatesDao.getResidueByGeneticPosition(taxonomyId, chromosome, position);

        ResponseMessagePositions responseMsg = new ResponseMessagePositions();
        responseMsg.setResults(results);
        responseMsg.setCount(results.size());

        Response.ResponseBuilder responseBuilder =  Response
                .status(Response.Status.OK)
                .type(AppHelper.getResponseMediaType("json", headers))
                .entity(responseMsg);

        return responseBuilder.build();
    }
}