package org.rcsb.genomemapping.config;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.message.filtering.SelectableEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class CustomApplication extends ResourceConfig 
{
	private String SELECTABLE_ENTITY_FILTER = "select";
	
    public CustomApplication() throws Exception
    {
    	super(MultiPartFeature.class);

    	packages(true, "org.rcsb.genomemapping.rest");

        register(SelectableEntityFilteringFeature.class);
        property(SelectableEntityFilteringFeature.QUERY_PARAM_NAME, SELECTABLE_ENTITY_FILTER);
    }
}