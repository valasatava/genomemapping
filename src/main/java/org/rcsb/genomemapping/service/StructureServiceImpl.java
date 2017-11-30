package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.dao.StructureDao;
import org.rcsb.genomemapping.dao.StructureDaoMongoImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public class StructureServiceImpl implements StructureService {

    private StructureDao dao;

    public StructureServiceImpl() {
        dao = new StructureDaoMongoImpl();
    }

    @Override
    public Response mapStructureToIsoforms(UriInfo uriInfo, Request request, String entryId, HttpHeaders headers) {



        return null;
    }
}
