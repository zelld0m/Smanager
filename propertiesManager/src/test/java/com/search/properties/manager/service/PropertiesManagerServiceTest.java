package com.search.properties.manager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.PropertyException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Member;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
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
 * @since August 30, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-context-test.xml")
public class PropertiesManagerServiceTest {

    @Autowired
    private PropertiesManagerService propertiesManagerService;
    @Autowired
    private PropertiesManager propertiesManager;
    private StoreProperties storeProperties;

    @Before
    public void setup() {
        assertNotNull(propertiesManagerService);
        assertNotNull(propertiesManager);

        storeProperties = propertiesManagerService.getStoreProperties();
    }

    @Test
    public void testGetStoreProperties() {

        Store pcmallBDStore = Stores.getStoreById("pcmallcap", storeProperties);
        assertEquals("pcmallcap", pcmallBDStore.getId());

        Store pcmallStore = Stores.getStoreById("pcmall", storeProperties);
        assertEquals("pcmall", pcmallStore.getId());

        List<Module> pcmallModules = pcmallStore.getModules();
        Module storeSettingsModule = pcmallModules.get(1);
        assertEquals("settings", storeSettingsModule.getName());

        List<Group> storeSettingsGroups = storeSettingsModule.getGroups();
        Group bannerAdsFeatureGroup = storeSettingsGroups.get(0);
        assertEquals("Banner Ads Feature", bannerAdsFeatureGroup.getName());

        List<Member> bannerAdsFeatureMembers = bannerAdsFeatureGroup.getMembers();
        Member siteDomainMember = bannerAdsFeatureMembers.get(0);
        assertEquals("site_domain", siteDomainMember.getPropertyId());

        List<Property> storeSettingsProperties = storeSettingsModule.getProperties();
        Property siteDomainProperty = storeSettingsProperties.get(0);
        assertEquals("Site Domain", siteDomainProperty.getLabel());
        assertEquals("Domain of managed site.", siteDomainProperty.getDescription());
        assertEquals("pcm.com", siteDomainProperty.getDefaultValue());
    }

    @Test(expected = PropertyException.class)
    public void testGetStoreProperties_No_Macmall_Store_Throw_PropertyException() {
        Stores.getStoreById("macmall", storeProperties);
    }
}
