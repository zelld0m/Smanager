package com.search.properties.manager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context.xml")
public class PropertiesReaderServiceTest {

    @Autowired
    private PropertiesReaderService propertiesReaderService;
    @Autowired
    private PropertiesManager propertiesManager;
    private List<StorePropertiesFile> storePropertiesFiles;
    
    @Before
    public void setup() {
        assertNotNull(propertiesReaderService);
        assertNotNull(propertiesManager);
        storePropertiesFiles = propertiesReaderService.readAllStorePropertiesFiles(
                "pcmall");
        propertiesManager.setStorePropertiesLocation(
                "src/test/resources/home/solr/conf/store-properties.xml");
        propertiesManager.setStorePropertiesSaveLocation(
                "src/test/resources/home/solr/conf");
    }

    @Test
    public void testReadAllStorePropertiesFiles() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(1);
        List<StoreProperty> storeProperties = settingsPropertiesFile.getStoreProperties();
        
        StoreProperty siteDomainStoreProperty = storeProperties.get(0);
        assertEquals(siteDomainStoreProperty.getName(), "site_domain");
        assertEquals(siteDomainStoreProperty.getValue(), "pcm.com");
    }
    
    @Test
    public void testReadAllStorePropertiesFile_Property_Does_Not_Exist() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(1);
        List<StoreProperty> storeProperties = settingsPropertiesFile.getStoreProperties();
        StoreProperty autoExportStoreProperty = storeProperties.get(1);
        assertEquals(autoExportStoreProperty.getName(), "default_banner_linkpath_protocol");
        assertEquals(autoExportStoreProperty.getValue(), "http");
    }
    
    @Test
    public void testReadAllStorePropertiesFiles_Properties_File_Does_Not_Exist() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(0);
        List<StoreProperty> storeProperties = settingsPropertiesFile.getStoreProperties();
        StoreProperty pendingNotification = storeProperties.get(0);
        assertEquals(pendingNotification.getName(), "pendingNotification");
        assertEquals(pendingNotification.getValue(), "true");
    }
}
