package com.search.properties.manager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.NotDirectoryException;
import com.search.properties.manager.exception.PropertyException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
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
    private Module settingsModuleOnPCMallStore;
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

        settingsModuleOnPCMallStore = PropertiesManagerUtil.getModuleByName("settings",
                pcmallStore);

        assertNotNull(propertiesReaderService);
        storePropertiesFiles = propertiesReaderService.readAllStorePropertiesFiles(
                "pcmall");
    }

    @Test
    public void testGetFormattedSaveLocation() {
        String formattedSaveLocation = PropertiesManagerUtil.getFormattedSaveLocation(
                "/home/solr/conf", "pcmall", "settings");
        assertEquals("/home/solr/conf/pcmall/pcmall.settings.properties",
                formattedSaveLocation);
    }

    @Test(expected = NotDirectoryException.class)
    public void testGetFormattedSaveLocation_StoreSaveLocation_Not_A_Valid_Directory_Thrown_NotDirectoryException() {
        PropertiesManagerUtil.getFormattedSaveLocation(
                "/home/solr/conf/store-properties.xml", "pcmall", "settings");
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

    @Test(expected = PropertyException.class)
    public void testGetParent_Store_Parent_Does_Not_Exists_Throw_PropertyException() {
        PropertiesManagerUtil.getParent(pcmallStore, storeProperties);
    }

    @Test
    public void testGetStoreById_StoreId_Exists() {
        assertEquals("pcmall", pcmallStore.getId());
    }

    @Test(expected = PropertyException.class)
    public void testGetStoreById_StoreId_Does_Not_Exists_Throw_PropertyException() {
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

    @Test(expected = PropertyException.class)
    public void testGetStorePropertyByName_StoreName_Does_Not_Exists_Throw_PropertyException() {
        StorePropertiesFile pcmallSettingsStorePropertiesFile = storePropertiesFiles.get(
                1);
        PropertiesManagerUtil.getStorePropertyByName("hoopla",
                pcmallSettingsStorePropertiesFile);
    }

    @Test
    public void testGetModuleByName() {
        assertEquals("settings", settingsModuleOnPCMallStore.getName());
        assertEquals("Settings", settingsModuleOnPCMallStore.getTitle());
    }

    @Test(expected = PropertyException.class)
    public void testGetModuleByName_Module_Name_Does_Not_Exists_Throw_PropertyException() {
        PropertiesManagerUtil.getModuleByName("potato", pcmallStore);
    }

    @Test
    public void testGetGroupByName() {
        Group bannerAdsFeatureGroup = PropertiesManagerUtil.getGroupByName(
                "Banner Ads Feature", settingsModuleOnPCMallStore);
        assertEquals("Banner Ads Feature", bannerAdsFeatureGroup.getName());
    }

    @Test(expected = PropertyException.class)
    public void testGroupGroupByName_Group_Name_Does_Not_Exists_Throw_PropertyException() {
        PropertiesManagerUtil.getGroupByName("Avalanche", settingsModuleOnPCMallStore);
    }

    @Test
    public void testGetAllGroupsWithoutAName() {
        List<Group> allGroupsWithoutAName = PropertiesManagerUtil.
                getAllGroupsWithoutAName(settingsModuleOnPCMallStore);
        assertEquals("auto_export", allGroupsWithoutAName.get(0).getMembers().
                get(0).getPropertyId());
    }

    @Test
    public void testGroupAllGroupsWithoutAName_No_Empty_Group_Return_Empty_ArrayList() {
        Module mailModule = PropertiesManagerUtil.getModuleByName("mail", pcmallStore);
        List<Group> allGroupsWithoutAName = PropertiesManagerUtil.
                getAllGroupsWithoutAName(mailModule);
        assertEquals(0, allGroupsWithoutAName.size());
        assertEquals(java.util.ArrayList.class, allGroupsWithoutAName.getClass());
    }

    @Test
    public void testGetPropertyById() {
        Property siteDomainProperty =
                PropertiesManagerUtil.getPropertyById("site_domain", settingsModuleOnPCMallStore);

        assertEquals("site_domain", siteDomainProperty.getId());
        assertEquals("pcm.com", siteDomainProperty.getDefaultValue());
    }

    @Test(expected = PropertyException.class)
    public void testGetPropertyById_Property_Id_Does_Not_Exists_Throw_PropertyException() {
        PropertiesManagerUtil.getPropertyById("witch_please", settingsModuleOnPCMallStore);
    }

    @Test
    public void testContainsModule() {
        assertEquals(true, PropertiesManagerUtil.containsModule(settingsModuleOnPCMallStore,
                pcmallCapStore));
    }

    @Test
    public void testContainsGroup() {
        assertEquals(true, PropertiesManagerUtil.containsGroup("Export Feature",
                settingsModuleOnPCMallStore));
    }

    @Test
    public void testContainsGroup_Group_Name_Does_Not_Exists_Return_False() {
        assertEquals(false, PropertiesManagerUtil.containsGroup("Avalanche",
                settingsModuleOnPCMallStore));
    }

    @Test
    public void testContainsAtLeastAGroupWithoutAName() {
        assertEquals(true, PropertiesManagerUtil.containsAtLeastAGroupWithoutAName(
                settingsModuleOnPCMallStore));
    }

    @Test
    public void testContainsAtLeastAGroupWithoutAName_Return_False() {
        Module mailModule = PropertiesManagerUtil.getModuleByName("mail", pcmallStore);
        assertEquals(false, PropertiesManagerUtil.containsAtLeastAGroupWithoutAName(
                mailModule));
    }

    @Test
    public void testContainsProperty() {
        assertEquals(true, PropertiesManagerUtil.containsProperty("site_domain",
                settingsModuleOnPCMallStore));
    }

    @Test
    public void testContainsProperty_Return_False() {
        assertEquals(false, PropertiesManagerUtil.containsProperty("witch_please",
                settingsModuleOnPCMallStore));
    }
}
