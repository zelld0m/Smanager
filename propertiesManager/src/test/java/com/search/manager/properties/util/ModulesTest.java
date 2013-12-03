package com.search.manager.properties.util;

import com.search.manager.properties.PropertiesManager;
import com.search.manager.properties.exception.PropertyException;
import com.search.manager.properties.model.Module;
import com.search.manager.properties.model.Store;
import com.search.manager.properties.model.StoreProperties;
import com.search.manager.properties.service.PropertiesManagerService;
import com.search.manager.properties.service.PropertiesReaderService;
import com.search.manager.properties.util.Modules;
import com.search.manager.properties.util.Stores;

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
public class ModulesTest {

    @Autowired
    private PropertiesManagerService propertiesManagerService;
    @Autowired
    private PropertiesManager propertiesManager;
    @Autowired
    private PropertiesReaderService propertiesReaderService;
    private StoreProperties storeProperties;
    private Store pcmallStore;
    private Module settingsModuleOnPCMallStore;

    @Before
    public void setup() {
        assertNotNull(propertiesManagerService);
        assertNotNull(propertiesManager);

        storeProperties = propertiesManagerService.getStoreProperties();

        pcmallStore = Stores.getStoreById("pcmall", storeProperties);

        settingsModuleOnPCMallStore = Modules.getModuleByName("settings",
                pcmallStore);

        assertNotNull(propertiesReaderService);
    }

    @Test
    public void testGetModuleByName() {
        assertEquals("settings", settingsModuleOnPCMallStore.getName());
        assertEquals("Settings", settingsModuleOnPCMallStore.getTitle());
    }

    @Test(expected = PropertyException.class)
    public void testGetModuleByName_Module_Name_Does_Not_Exists_Throw_PropertyException() {
        Modules.getModuleByName("potato", pcmallStore);
    }
}
