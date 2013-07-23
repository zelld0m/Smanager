package com.search.manager.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * Loads the globalvar.properties file
 *
 * @author PGutierr
 *
 */
public class PropertiesUtils {
    private static final Logger logger = Logger.getLogger(PropertiesUtils.class.getName());
    private static PropertiesConfiguration config = initPropertiesConfig();
    private static final String FILE_PATH = "/home/solr/conf/globalvar.properties";

    private PropertiesUtils() {
        // NOTHING
    }
    
    private static PropertiesConfiguration initPropertiesConfig() {
        try {
            config = new PropertiesConfiguration(FILE_PATH);
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            logger.log(Level.SEVERE, String.format("Unable to load file %s", FILE_PATH), e);
        }

        return config;
    }

    /**
     * Returns the value of a key in the globalvar.properties file
     *
     * @param key the key in the properties file
     * @return the value of a key in the globalvar.properties file
     */
    public static String getValue(String key) {
        return config.getString(key);
    }
}
