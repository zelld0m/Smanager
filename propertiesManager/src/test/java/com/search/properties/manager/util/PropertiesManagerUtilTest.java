package com.search.properties.manager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.ModuleNotFoundException;
import com.search.properties.manager.exception.StoreNotFoundException;
import com.search.properties.manager.exception.StorePropertyNotFoundException;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import com.search.properties.manager.service.PropertiesManagerService;
import com.search.properties.manager.service.PropertiesReaderService;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for {@link PropertiesManagerUtil}
 *
 * @author Philip Mark Gutierrez
 * @since September 02, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context.xml")
public class PropertiesManagerUtilTest {

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
        
        propertiesManager.setStorePropertiesLocation(
                "src/test/resources/home/solr/conf/store-properties.xml");
        propertiesManager.setStorePropertiesSaveLocation(
                "src/test/resources/home/solr/conf");
        
        storeProperties = propertiesManagerService.getStoreProperties();
        
        pcmallCapStore = PropertiesManagerUtil.getStoreById("pcmallcap",
                storeProperties);
        pcmallStore = PropertiesManagerUtil.getStoreById("pcmall",
                storeProperties);

        assertNotNull(propertiesReaderService);
        storePropertiesFiles = propertiesReaderService.readAllStorePropertiesFiles(
                "pcmall");
    }

    @Test
    public void testHasParent_Argument_Has_Parent() {
        assertEquals(true, PropertiesManagerUtil.hasParent(pcmallCapStore));
    }

    @Test
    public void testHasParent_Argument_Has_No_Parent() {
        assertEquals(false, PropertiesManagerUtil.hasParent(pcmallStore));
    }

    @Test
    public void testGetParent_Store_Parent_Exists() {
        assertEquals(pcmallStore, PropertiesManagerUtil.getParent(pcmallCapStore, 
                storeProperties));
    }

    @Test(expected = StoreNotFoundException.class)
    public void testGetParent_Store_Parent_Does_Not_Exists_Throw_StoreNotFoundException() {
        PropertiesManagerUtil.getParent(pcmallStore, storeProperties);
    }

    @Test
    public void testGetStoreById_StoreId_Exists() {
        assertEquals("pcmall", pcmallStore.getId());
    }

    @Test(expected = StoreNotFoundException.class)
    public void testGetStoreById_StoreId_Does_Not_Exists_Throw_StoreNotFoundException() {
        PropertiesManagerUtil.getStoreById("macmall", storeProperties);
    }

    @Test
    public void testGetStorePropertyByName() {
        StorePropertiesFile pcmallSettingsStorePropertiesFile = storePropertiesFiles.get(
                1);
        StoreProperty siteDomainStoreProperty = PropertiesManagerUtil.
                getStorePropertyByName("site_domain", pcmallSettingsStorePropertiesFile);
        assertEquals("site_domain", siteDomainStoreProperty.getName());
        assertEquals("pcm.com", siteDomainStoreProperty.getValue());
    }

    @Test(expected = StorePropertyNotFoundException.class)
    public void testGetStorePropertyByName_StoreName_Does_Not_Exists_Throw_StorePropertyNotFoundException() {
        StorePropertiesFile pcmallSettingsStorePropertiesFile = storePropertiesFiles.get(
                1);
        PropertiesManagerUtil.getStorePropertyByName("hoopla", 
                pcmallSettingsStorePropertiesFile);
    }

    @Test
    public void testGetModuleByName() {
        Module settingsModule = PropertiesManagerUtil.getModuleByName("settings", 
                pcmallStore);
        assertEquals("settings", settingsModule.getName());
        assertEquals("Settings", settingsModule.getTitle());
    }
    
    @Test(expected = ModuleNotFoundException.class)
    public void testGetModuleByName_Module_Name_Does_Not_Exists_Throw_ModuleNotFoundException() {
        PropertiesManagerUtil.getModuleByName("potato", pcmallStore);
    }
}
