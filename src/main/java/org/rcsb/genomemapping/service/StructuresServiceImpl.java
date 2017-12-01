package org.rcsb.genomemapping.service;

import org.rcsb.genomemapping.dao.StructuresDao;
import org.rcsb.genomemapping.dao.StructuresDaoMongoImpl;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public class StructuresServiceImpl implements StructuresService {

    private StructuresDao dao;

    public StructuresServiceImpl() {
        dao = new StructuresDaoMongoImpl();
    }

    @Override
    public Response getStructuresByGeneName(UriInfo uriInfo, Request request, String name, HttpHeaders headers) {

        dao.getStructuresByGeneName();

        return null;
    }
}
