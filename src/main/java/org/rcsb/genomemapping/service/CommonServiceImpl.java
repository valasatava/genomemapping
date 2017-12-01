package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.response.ResponseMessage;
import org.rcsb.genomemapping.utils.AppHelper;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public class CommonServiceImpl implements CommonService {

    @Override
    public Response invalidParameter(HttpHeaders headers) {

        ResponseMessage responseMsg = new ResponseMessage();
        responseMsg.setCode(400);
        responseMsg.setStatus(400);
        responseMsg.setMessage("Bad Request: Invalid query parameter");

        Response.ResponseBuilder responseBuilder =  Response
                .status(Response.Status.OK)
                .type(AppHelper.getResponseMediaType("json", headers))
                .entity(responseMsg);

        return responseBuilder.build();
    }
}
