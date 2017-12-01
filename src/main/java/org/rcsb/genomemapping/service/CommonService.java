package org.rcsb.genomemapping.service;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Created by Yana Valasatava on 11/30/17.
 */
public interface CommonService {

    Response invalidParameter(HttpHeaders headers);
}
