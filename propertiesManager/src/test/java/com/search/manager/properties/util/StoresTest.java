package com.search.manager.properties.util;

import com.search.manager.properties.PropertiesManager;
import com.search.manager.properties.exception.NotDirectoryException;
import com.search.manager.properties.exception.PropertyException;
import com.search.manager.properties.model.Store;
import com.search.manager.properties.model.StoreProperties;
import com.search.manager.properties.model.StorePropertiesFile;
import com.search.manager.properties.model.StoreProperty;
import com.search.manager.properties.service.PropertiesManagerService;
import com.search.manager.properties.service.PropertiesReaderService;
import com.search.manager.properties.util.Stores;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 7, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class StoresTest {

    @Autowired
    private PropertiesManagerService propertiesManagerService;
    @Autowired
    private PropertiesManager propertiesManager;
    @Autowired
    private PropertiesReaderService propertiesReaderService;
    private StoreProperties storeProperties;
    private Store pcmallCapStore;
    private Store pcmallStore;
    private List<StorePropertiesFile> storePropertiesFiles;

    @Before
    public void setup() {
        assertNotNull(propertiesManagerService);
        assertNotNull(propertiesManager);

        storeProperties = propertiesManagerService.getStoreProperties();

        pcmallCapStore = Stores.getStoreById("pcmallcap", storeProperties);
        pcmallStore = Stores.getStoreById("pcmall", storeProperties);

        assertNotNull(propertiesReaderService);
        storePropertiesFiles = propertiesReaderService.readAllStorePropertiesFiles(
                "pcmall");
    }

    @Test
    public void testGetFormattedSaveLocation() {
        String formattedSaveLocation = Stores.getFormattedSaveLocation(
                "/home/solr/conf", "pcmall", "settings");
        assertEquals("/home/solr/conf/pcmall/pcmall.settings.properties",
                formattedSaveLocation);
    }

    @Test(expected = NotDirectoryException.class)
    public void testGetFormattedSaveLocation_StoreSaveLocation_Not_A_Valid_Directory_Thrown_NotDirectoryException() {
        Stores.getFormattedSaveLocation("/home/solr/conf/store-properties.xml", "pcmall", 
                "settings");
    }

    @Test
    public void testHasParent_Argument_Has_Parent() {
        assertEquals(true, Stores.hasParent(pcmallCapStore));
    }

    @Test
    public void testHasParent_Argument_Has_No_Parent() {
        assertEquals(false, Stores.hasParent(pcmallStore));
    }

    @Test
    public void testGetParent_Store_Parent_Exists() {
        assertEquals(pcmallStore, Stores.getParent(pcmallCapStore,
                storeProperties));
    }

    @Test(expected = PropertyException.class)
    public void testGetParent_Store_Parent_Does_Not_Exists_Throw_PropertyException() {
        Stores.getParent(pcmallStore, storeProperties);
    }

    @Test
    public void testGetStoreById_StoreId_Exists() {
        assertEquals("pcmall", pcmallStore.getId());
    }

    @Test(expected = PropertyException.class)
    public void testGetStoreById_StoreId_Does_Not_Exists_Throw_PropertyException() {
        Stores.getStoreById("macmall", storeProperties);
    }

    @Test
    public void testGetStorePropertyByName() {
        StorePropertiesFile pcmallSettingsStorePropertiesFile = storePropertiesFiles.get(
                1);
        StoreProperty siteDomainStoreProperty = Stores.getStorePropertyByName(
                "site_domain", pcmallSettingsStorePropertiesFile);
        assertEquals("site_domain", siteDomainStoreProperty.getName());
        assertEquals("pcm.com", siteDomainStoreProperty.getValue());
    }

    @Test(expected = PropertyException.class)
    public void testGetStorePropertyByName_StoreName_Does_Not_Exists_Throw_PropertyException() {
        StorePropertiesFile pcmallSettingsStorePropertiesFile = 
                storePropertiesFiles.get(0);
        Stores.getStorePropertyByName("hoopla", pcmallSettingsStorePropertiesFile);
    }
}
