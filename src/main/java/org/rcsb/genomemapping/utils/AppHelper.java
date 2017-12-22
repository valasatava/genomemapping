package org.rcsb.genomemapping.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

	/**
	 * This method makes a "deep clone" of any Java object it is given.
	 */
	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void nullAwareBeanCopy(Object dest, Object source) throws IllegalAccessException, InvocationTargetException {
		new BeanUtilsBean() {
			@Override
			public void copyProperty(Object dest, String name, Object value)
					throws IllegalAccessException, InvocationTargetException {
				if(value != null) {
					Object v = deepClone(value);
					super.copyProperty(dest, name, v);
				}
			}
		}.copyProperties(dest, source);
	}

	public static void nullAwareBeanCopy(Object dest, Object source, final List<String> props) throws IllegalAccessException, InvocationTargetException {
		new BeanUtilsBean() {
			@Override
			public void copyProperty(Object dest, String name, Object value)
					throws IllegalAccessException, InvocationTargetException {
				if(value != null && props.contains(name)) {
					Object v = deepClone(value);
					super.copyProperty(dest, name, v);
				}
			}
		}.copyProperties(dest, source);
	}
}
