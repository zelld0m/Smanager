package com.search.properties.manager.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Member;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
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
@ContextConfiguration("/spring-context.xml")
public class PropertiesManagerServiceGetStorePropertiesTest {

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
    public void testGetStoreProperties() {
        StoreProperties storeProperties = propertiesManagerService.getStoreProperties();
        List<Store> stores = storeProperties.getStores();

        Store pcmallBDStore = stores.get(0);
        assertEquals("pcmallcap", pcmallBDStore.getId());

        Store pcmallStore = stores.get(1);
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
}
