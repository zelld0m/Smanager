package com.search.manager.report.statistics.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Loads the globalvar.properties file
 * @author PGutierr
 *
 */
public class PropertiesUtils {
    private static final Logger logger = Logger.getLogger(PropertiesUtils.class.getName());
	private static PropertiesConfiguration config;

	static {
		String filePath = "/home/solr/conf/globalvar.properties";

		try {
			config = new PropertiesConfiguration(filePath);
		} catch (ConfigurationException e) {
			logger.log(Level.SEVERE, String.format("Unable to load file %s", filePath), e);
		}
	}

	/**
	 * Returns the value of a key in the globalvar.properties file
	 * 
	 * @param key the key in the properties file
	 * @return the value of a key in the globalvar.properties file
	 */
	public static String getString(String key) {
		return config.getString(key);
	}
}
