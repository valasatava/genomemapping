package org.rcsb.genomemapping.utils;

import org.rcsb.common.config.ConfigProfileManager;

import java.util.Properties;

/**
 * A class to hold the properties singleton for the application.
 *
 */
public class Parameters {

    private static final Properties props = ConfigProfileManager.getRedwoodAppProperties();

    public static final String workDirectoryName = "spark";
    
    public static final String userHome = System.getProperty("user.home");

    public static String getWorkDirectory(){
        return userHome +"/" + workDirectoryName;
    }

    public static Properties getProperties() {
    	return props;
    }
}