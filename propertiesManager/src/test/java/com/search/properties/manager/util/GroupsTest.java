package com.search.properties.manager.util;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.PropertyException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.service.PropertiesManagerService;
import com.search.properties.manager.service.PropertiesReaderService;
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
public class GroupsTest {

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
    public void testContainsAGroupWithoutAName() {
        assertEquals(true, Groups.containsAGroupWithoutAName(
                settingsModuleOnPCMallStore));
    }

    @Test
    public void testGetGroupByName() {
        Group bannerAdsFeatureGroup = Groups.getGroupByName(
                "Banner Ads Feature", settingsModuleOnPCMallStore);
        assertEquals("Banner Ads Feature", bannerAdsFeatureGroup.getName());
    }

    @Test(expected = PropertyException.class)
    public void testGetGroupByName_Group_Name_Does_Not_Exists_Throw_PropertyException() {
        Groups.getGroupByName("Avalanche", settingsModuleOnPCMallStore);
    }

    @Test
    public void testGetAllGroupsWithoutAName() {
        List<Group> allGroupsWithoutAName = Groups.getAllGroupsWithoutAName(
                settingsModuleOnPCMallStore);
        assertEquals("auto_export", allGroupsWithoutAName.get(0).getMembers().
                get(0).getPropertyId());
    }

    @Test
    public void testGroupAllGroupsWithoutAName_No_Empty_Group_Return_Empty_ArrayList() {
        Module mailModule = Modules.getModuleByName("mail", pcmallStore);
        List<Group> allGroupsWithoutAName = Groups.getAllGroupsWithoutAName(mailModule);
        assertEquals(2, allGroupsWithoutAName.size());
        assertEquals(java.util.ArrayList.class, allGroupsWithoutAName.getClass());
    }
}
