package com.search.manager.properties.service;

import com.search.manager.properties.PropertiesReader;
import com.search.manager.properties.model.StorePropertiesFile;

import java.util.List;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for {@link PropertiesReader}
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
@Service
@RemoteProxy(
        name = "PropertiesReaderServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "PropertiesReaderService"))
public class PropertiesReaderService {
    @Autowired
    private PropertiesReader propertiesReader;
    
    @RemoteMethod
    public List<StorePropertiesFile> readAllStorePropertiesFiles(String storeId) {
        return propertiesReader.readAllStorePropertiesFiles(storeId);
    }
}
