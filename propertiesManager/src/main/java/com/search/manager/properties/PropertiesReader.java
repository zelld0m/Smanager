package com.search.manager.properties;

import com.google.common.collect.Lists;
import com.search.manager.properties.exception.PropertyException;
import com.search.manager.properties.model.Module;
import com.search.manager.properties.model.Property;
import com.search.manager.properties.model.Store;
import com.search.manager.properties.model.StoreProperties;
import com.search.manager.properties.model.StorePropertiesFile;
import com.search.manager.properties.model.StoreProperty;
import com.search.manager.properties.util.Stores;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reads the properties file of a specific mall
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
@Component
public class PropertiesReader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesReader.class);
    @Autowired
    private PropertiesManager propertiesManager;

    /**
     * Reads all store properties files
     * @param storeId the store id
     * @return a {@link List} of {@link StorePropertiesFile} object
     * @throws PropertyException
     */
    public List<StorePropertiesFile> readAllStorePropertiesFiles(String storeId)
            throws PropertyException {
        StoreProperties storeProperties = propertiesManager.getStoreProperties();

        Store store = Stores.getStoreById(storeId, storeProperties);
        List<Module> modules = store.getModules();
        List<StorePropertiesFile> storePropertiesFiles = Lists.newArrayList();

        for (Module module : modules) {
            String moduleName = module.getName();
            String filePath = Stores.getFormattedSaveLocation(
                    propertiesManager.getStorePropertiesFolder(), storeId, 
                    moduleName);
            File file = new File(filePath);
            Properties propertiesObj = new Properties();

            if (file.exists()) {
                try {
                    propertiesObj.load(new FileReader(file));
                } catch (IOException e) {
                    logger.error(String.format("Unable to read properties file %s",
                            filePath), e);
                }
            }

            List<StoreProperty> storePropertyList = Lists.newArrayList();
            List<Property> moduleProperties = module.getProperties();

            for (Property moduleProperty : moduleProperties) {
                String name = moduleProperty.getId();
                String defaultValue = moduleProperty.getDefaultValue();
                String propertyValue = propertiesObj.getProperty(name, defaultValue);
                
                storePropertyList.add(new StoreProperty(name, propertyValue));
            }

            storePropertiesFiles.add(new StorePropertiesFile(moduleName, filePath, 
                    storePropertyList));
        }

        return storePropertiesFiles;
    }
}
