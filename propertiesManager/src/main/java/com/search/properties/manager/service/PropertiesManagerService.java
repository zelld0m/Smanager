package com.search.properties.manager.service;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.model.StoreProperties;
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
public class PropertiesManagerService {
    @Autowired
    private PropertiesManager propertiesManager;
    
    /**
     * @return the object representation of store-properties.xml
     */
    public StoreProperties getStoreProperties() {
        return propertiesManager.getStoreProperties();
    }
}
