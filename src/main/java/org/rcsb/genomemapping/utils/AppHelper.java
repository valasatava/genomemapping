package org.rcsb.genomemapping.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;

public class AppHelper {

	public static String getResponseMediaType(String format, HttpHeaders headers){
		//default is json
		String mediaType = MediaType.APPLICATION_JSON;
		//String acceptType = headers.getAcceptableMediaTypes().get(0).toString(); 
		
		if (!StringUtils.isBlank(format) && format.equalsIgnoreCase("xml")){
			mediaType = MediaType.APPLICATION_XML;						
		}
		return mediaType;
	}

	public static void nullAwareBeanCopy(Object dest, Object source) throws IllegalAccessException, InvocationTargetException {
		new BeanUtilsBean() {
			@Override
			public void copyProperty(Object dest, String name, Object value)
					throws IllegalAccessException, InvocationTargetException {
				if(value != null) {
					super.copyProperty(dest, name, value);
				}
			}
		}.copyProperties(dest, source);
	}
}
