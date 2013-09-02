package com.search.properties.manager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.StoreNotFoundException;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.service.PropertiesManagerService;
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

    @Before
    public void setup() {
        assertNotNull(propertiesManagerService);
        assertNotNull(propertiesManager);
        propertiesManager.setStorePropertiesLocation(
                "src/test/resources/home/solr/conf/store-properties.xml");
    }

    @Test
    public void testHasParent_Argument_Has_Parent() {
        StoreProperties storeProperties = propertiesManagerService.getStoreProperties();
        List<Store> stores = storeProperties.getStores();
        Store pcmallBDStore = stores.get(0);
        assertEquals(PropertiesManagerUtil.hasParent(pcmallBDStore), true);
    }

    @Test
    public void testHasParent_Argument_Has_No_Parent() {
        StoreProperties storeProperties = propertiesManagerService.getStoreProperties();
        List<Store> stores = storeProperties.getStores();
        Store pcmallStore = stores.get(1);
        assertEquals(PropertiesManagerUtil.hasParent(pcmallStore), false);
    }

    @Test(expected = NullPointerException.class)
    public void testHasParent_Argument_Null_Throw_NullPointerException() {
        PropertiesManagerUtil.hasParent(null);
    }

    @Test
    public void testGetParent_Store_Parent_Exists() {
        StoreProperties storeProperties = propertiesManagerService.getStoreProperties();
        List<Store> stores = storeProperties.getStores();
        Store pcmallBDStore = stores.get(0);
        Store pcmallStore = stores.get(1);

        assertEquals(PropertiesManagerUtil.getParent(storeProperties, pcmallBDStore),
                pcmallStore);
    }

    @Test(expected = StoreNotFoundException.class)
    public void testGetParent_Store_Parent_Does_Not_Exists_Throw_StoreNotFoundException() {
        StoreProperties storeProperties = propertiesManagerService.getStoreProperties();
        List<Store> stores = storeProperties.getStores();
        Store pcmallStore = stores.get(1);
        PropertiesManagerUtil.getParent(storeProperties, pcmallStore);
    }
}
