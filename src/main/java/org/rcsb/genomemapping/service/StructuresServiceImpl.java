package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.dao.StructuresDao;
import org.rcsb.genomemapping.dao.StructuresDaoMongoImpl;
import org.rcsb.genomemapping.response.ResponseMessageFeatures;
import org.rcsb.genomemapping.response.ResponseMessagePositions;
import org.rcsb.genomemapping.utils.AppHelper;
import org.rcsb.genomemapping.utils.BooleanQueryParam;
import org.rcsb.mojave.genomemapping.MultipleFeaturesMap;
import org.rcsb.mojave.genomemapping.PositionPropertyMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public class StructuresServiceImpl implements StructuresService {

    private StructuresDao dao;

    public StructuresServiceImpl() {
        dao = new StructuresDaoMongoImpl();
    }

    @Override
    public Response getStructuresByGeneName(UriInfo uriInfo, Request request, int taxonomyId, String name, BooleanQueryParam canonical, HttpHeaders headers) {

        List<MultipleFeaturesMap> results = dao.getStructuresByGeneName(taxonomyId, name, canonical.getValue());

        ResponseMessageFeatures responseMsg = new ResponseMessageFeatures();
        responseMsg.setResults(results);
        responseMsg.setCount(results.size());

        Response.ResponseBuilder responseBuilder =  Response
                .status(Response.Status.OK)
                .type(AppHelper.getResponseMediaType("json", headers))
                .entity(responseMsg);

        return responseBuilder.build();
    }

    @Override
    public Response getStructuresByGeneId(UriInfo uriInfo, Request request, int taxonomyId, String id, BooleanQueryParam canonical, HttpHeaders headers) {

        List<MultipleFeaturesMap> results = dao.getStructuresByGeneId(taxonomyId, id, canonical.getValue());

        ResponseMessageFeatures responseMsg = new ResponseMessageFeatures();
        responseMsg.setResults(results);
        responseMsg.setCount(results.size());

        Response.ResponseBuilder responseBuilder =  Response
                .status(Response.Status.OK)
                .type(AppHelper.getResponseMediaType("json", headers))
                .entity(responseMsg);

        return responseBuilder.build();
    }

    @Override
    public Response getStructuresByGenomicPosition(UriInfo uriInfo, Request request, int taxonomyId, String chromosome, int position, BooleanQueryParam canonical, HttpHeaders headers) {

        List<PositionPropertyMap> results = dao.getStructuresByGeneticPosition(taxonomyId, chromosome, position, canonical.getValue());

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
