package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.dao.CoordinatesDao;
import org.rcsb.genomemapping.dao.CoordinatesDaoMongoImpl;
import org.rcsb.genomemapping.response.ResponseMessagePositions;
import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.genomemapping.utils.BooleanQueryParam;
import org.rcsb.mojave.genomemapping.PositionPropertyMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


/**
 * Created by Yana Valasatava on 11/28/17.
 */
public class CoordinatesServiceImpl implements CoordinatesService {

    private CoordinatesDao dao;

    public static final String ETAG_PREFIX = "coordinates-";

    public CoordinatesServiceImpl() {

        dao = new CoordinatesDaoMongoImpl();
    }

    @Override
    public Response mapGenomicPosition(UriInfo uriInfo, Request request, int taxonomyId, String chromosome, int position, BooleanQueryParam canonical, HttpHeaders headers) throws Exception {

        List<PositionPropertyMap> results = dao.mapGenomicPosition(taxonomyId, chromosome, position, canonical.getValue());

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