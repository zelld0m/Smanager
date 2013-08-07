package com.search.manager.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
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
    private static PropertiesConfiguration config;

    public static void initPropertiesConfig(String path) {
        try {
            config = new PropertiesConfiguration(path);
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            logger.log(Level.SEVERE, String.format("Unable to load file %s", path), e);
        }
    }

    @Deprecated
    public static Properties load(String propsName) throws Exception {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(new File(propsName));
        props.load(fis);
        fis.close();
        return props;
    }

    @Deprecated
    public static Properties load(File propsFile) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propsFile);
        props.load(fis);
        fis.close();

        return props;
    }

    @Deprecated
    public static Properties getProperties(String propsName) {
        try {
            return load(getValue(propsName));
        } catch (Exception e) {
        }
        return null;
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
