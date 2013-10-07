package com.search.properties.manager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import com.search.properties.manager.util.Stores;
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
@ContextConfiguration("/spring-context-test.xml")
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
    }

    @Test
    public void testReadAllStorePropertiesFiles() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(1);

        String filePath = Stores.getFormattedSaveLocation(
                "src/test/resources/home/solr/conf", "pcmall", "settings");
        
        assertEquals(filePath, settingsPropertiesFile.getFilePath());

        assertEquals("settings", settingsPropertiesFile.getModuleName());

        StoreProperty siteDomainStoreProperty = Stores.getStorePropertyByName(
                "site_domain", settingsPropertiesFile);
        assertEquals("site_domain", siteDomainStoreProperty.getName());
        assertEquals("pcm.com", siteDomainStoreProperty.getValue());

        StoreProperty exportTargetStoreProperty = Stores.getStorePropertyByName(
                "export_target", settingsPropertiesFile);
        assertEquals("export_target", exportTargetStoreProperty.getName());
        assertEquals("pcmallcap,pcmgbd", exportTargetStoreProperty.getValue());
    }
}
