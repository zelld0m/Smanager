package com.search.properties.manager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import com.search.properties.manager.util.PropertiesManagerUtil;
import java.io.File;
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

        propertiesManager.setStorePropertiesLocation(
                "src/test/resources/home/solr/conf/store-properties.xml");
        propertiesManager.setStorePropertiesSaveLocation(
                "src/test/resources/home/solr/conf");

        storePropertiesFiles = propertiesReaderService.readAllStorePropertiesFiles(
                "pcmall");
    }

    @Test
    public void testReadAllStorePropertiesFiles() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(1);

        String filePath = PropertiesManagerUtil.getFormattedSaveLocation(
                "src/test/resources/home/solr/conf", "pcmall", "settings");
        
        assertEquals(filePath, settingsPropertiesFile.getFilePath());

        assertEquals("settings", settingsPropertiesFile.getModuleName());

        StoreProperty siteDomainStoreProperty = PropertiesManagerUtil.
                getStorePropertyByName("site_domain", settingsPropertiesFile);
        assertEquals("site_domain", siteDomainStoreProperty.getName());
        assertEquals("pcm.com", siteDomainStoreProperty.getValue());

        StoreProperty exportTargetStoreProperty = PropertiesManagerUtil.
                getStorePropertyByName("export_target", settingsPropertiesFile);
        assertEquals("export_target", exportTargetStoreProperty.getName());
        assertEquals("pcmallcap,pcmgbd", exportTargetStoreProperty.getValue());
    }

    @Test
    public void testReadAllStorePropertiesFile_Property_Does_Not_Exist() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(1);
        StoreProperty forTestingStoreProperty = PropertiesManagerUtil.
                getStorePropertyByName("for_testing", settingsPropertiesFile);
        assertEquals("for_testing", forTestingStoreProperty.getName());
        assertEquals("This is a test", forTestingStoreProperty.getValue());
    }

    @Test
    public void testReadAllStorePropertiesFiles_Properties_File_Does_Not_Exist() {
        StorePropertiesFile settingsPropertiesFile = storePropertiesFiles.get(0);
        StoreProperty pendingNotification = PropertiesManagerUtil.getStorePropertyByName(
                "pendingNotification", settingsPropertiesFile);
        assertEquals("pendingNotification", pendingNotification.getName());
        assertEquals("true", pendingNotification.getValue());
    }
}
