package com.search.properties.manager;

import com.google.common.collect.Lists;
import com.search.properties.manager.exception.StoreNotFoundException;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import com.search.properties.manager.util.PropertiesManagerUtil;
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

    public List<StorePropertiesFile> readAllStorePropertiesFiles(String storeId)
            throws StoreNotFoundException {
        StoreProperties storeProperties = propertiesManager.getStoreProperties();

        Store store = PropertiesManagerUtil.getStoreById(storeId, storeProperties);
        List<Module> modules = store.getModules();
        List<StorePropertiesFile> storePropertiesFiles = Lists.newArrayList();

        for (Module module : modules) {
            String fileName = String.format("%s%s%s.%s.properties",
                    propertiesManager.getStorePropertiesSaveLocation(), File.separator,
                    storeId, module.getName());
            File file = new File(fileName);
            Properties propertiesObj = new Properties();

            if (file.exists()) {
                try {
                    propertiesObj.load(new FileReader(file));
                } catch (IOException e) {
                    logger.error(String.format("Unable to read properties file %s",
                            fileName), e);
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

            storePropertiesFiles.add(new StorePropertiesFile(storePropertyList));

        }

        return storePropertiesFiles;
    }
}
