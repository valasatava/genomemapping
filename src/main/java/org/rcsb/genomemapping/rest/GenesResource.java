package org.rcsb.genomemapping.rest;

import io.swagger.annotations.ApiOperation;
import org.rcsb.genomemapping.service.GenesService;
import org.rcsb.genomemapping.service.GenesServiceImpl;
import org.rcsb.genomemapping.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * Created by Yana Valasatava on 11/15/17.
 */

@Path(AppConstants.PATH_MAPPING)
public class GenesResource {

    private GenesService service;
    private static final Logger logger = LoggerFactory.getLogger(GenesResource.class);

    public GenesResource() {
        service = new GenesServiceImpl();
    }

    @GET
    @Path(AppConstants.PATH_GENES + AppConstants.PATH_PING)
    @Produces({MediaType.TEXT_PLAIN})
    @ApiOperation(value = "servicePing", hidden = true)
    public String servicePing() {
        logger.info("received ping on " + new Date().toString());
        return "RESTful Gene Mapping Service is running.";
    }
}