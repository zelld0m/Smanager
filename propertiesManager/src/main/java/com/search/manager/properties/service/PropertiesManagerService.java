package com.search.manager.properties.service;

import com.search.manager.properties.PropertiesManager;
import com.search.manager.properties.model.StoreProperties;
import com.search.manager.properties.model.StorePropertiesFile;

import java.util.List;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for {@link PropertiesManager}
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@Service
@RemoteProxy(
        name = "PropertiesManagerServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "PropertiesManagerService"))
public class PropertiesManagerService {

    @Autowired
    private PropertiesManager propertiesManager;

    /**
     * @return the object representation of store-properties.xml
     */
    public StoreProperties getStoreProperties() {
        return propertiesManager.getStoreProperties();
    }
    
    /**
     * Creates the appropriate properties files if not existing yet with initial
     * properties from store-properties.xml
     */
    public void saveStoreProperties() {
        propertiesManager.saveStoreProperties();
    }
    
    /**
     * Stores the properties in their specific properties file
     * @param storePropertiesFile the properties file to save
     */
    public void saveStoreProperties(List<StorePropertiesFile> storePropertiesFile) {
        propertiesManager.saveStoreProperties(storePropertiesFile);
    }
}
