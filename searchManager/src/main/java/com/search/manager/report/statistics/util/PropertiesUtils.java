package com.search.manager.report.statistics.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the globalvar.properties file
 * @author PGutierr
 *
 */
public class PropertiesUtils {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
	private static PropertiesConfiguration config;

	static {
		String filePath = "C:\\home\\solr\\conf\\globalvar.properties";

		try {
			config = new PropertiesConfiguration(filePath);
		} catch (ConfigurationException e) {
			logger.error(String.format("Unable to load file %s", filePath), e);
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
