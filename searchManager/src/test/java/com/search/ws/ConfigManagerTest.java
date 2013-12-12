package com.search.ws;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 1, 2013
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context-test.xml")
public class ConfigManagerTest {

    @Autowired
    private ConfigManager configManager;

    @Test
    public void testGetCoreNames() {
        assertEquals(Arrays.asList("pcmall", "macmall", "pcmall", "pcmallgov", "macmall",
                "ecost"), configManager.getCoreNames());
    }

    @Test
    public void testGetDefaultSolrParameters() {
        assertEquals(Arrays.asList(new BasicNameValuePair("fq", "PcMall_StoreFlag:true")),
                configManager.getDefaultSolrParameters("pcmall"));
    }

    @Test
    public void testGetListMailProperty() {
        assertEquals(Arrays.asList("true"), configManager.getPropertyList("mail","pcmall",
                "pushToProdNotification"));
    }

    @Test
    public void testGetSearchWithinProperty() {
        assertEquals("true", configManager.getProperty("searchwithin", "pcmall", "searchwithin.enable"));
    }

    @Test
    public void testGetMailProperty() {
        assertEquals("true", configManager.getProperty("mail","pcmall", "approvalNotification"));
    }

    @Test
    public void testGetParameter() {
        assertEquals("America/Los_Angeles", configManager.getParameter(
                "system-timezone"));
    }

//    @Test
//    public void testGetPublishedDidYouMeanPath() {
//        // TODO add test here
//    }
//    
//    @Test
//    public void testGetPublishedStoreLinguisticSetting() {
//        // TODO add test here
//    }

//    @Test
//    public void testGetServerParameterName() {
//        // TODO add test here
//    }
    @Test
    public void testGetServersByStoreId() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("afs-pl-schpd07",
                "http://afs-pl-schpd07.afservice.org:8080/solr4/(core)/");
        map.put("afs-pl-schpd08",
                "http://afs-pl-schpd08.afservice.org:8080/solr4/(core)/");
        map.put("btorschprod02",
                "http://btorschprod02:8080/solr4/(core)/");
        map.put("btorschprod03",
                "http://btorschprod03:8080/solr4/(core)/");
        map.put("search",
                "http://10.17.35.36:8080/solr4/(core)/");
        map.put("search.pcmall.com",
                "http://search.pcmall.com:8080/solr4/(core)/");

        assertEquals(map, configManager.getServersByStoreId("pcmall"));

    }

    @Test
    public void getSolrSelectorParam() {
        assertEquals("storeAlias", configManager.getSolrSelectorParam());
    }

    @Test
    public void getStoreAttributes() {
        assertEquals(Arrays.asList("pcmall", "macmall", "pcmall", "pcmallgov", "macmall",
                "ecost"), configManager.getStoreAttributes("core", true));
    }

    @Test
    public void getStoreAttributes_hasXmlTag_False() {
        assertEquals(Arrays.asList("pcmall", "macmall", "pcmallcap", "pcmallgov",
                "macmallbd", "ecost"), configManager.getStoreAttributes("id", false));
    }

    @Test
    public void testGetStoreIdByAliases() {
        assertEquals("pcmall", configManager.getStoreIdByAliases("pcmall"));
    }

    @Test
    public void testGetStoreName() {
        assertEquals("PCM", configManager.getStoreName("pcmall"));
    }

    @Test
    public void testGetStoreNames() {
        assertEquals(Arrays.asList("PCM", "MacMall", "PCMall BD", "PCMG BD", "MacMall BD", "eCOST"), configManager.getStoreNames());
    }

    @Test
    public void testGetStoreParameter() {
        assertEquals("MM/dd/yyyy hh:mm aa", configManager.getStoreParameter("pcmall",
                "datetime-format"));
    }

    @Test
    public void testGetStoreParameterList() {
        assertEquals(Arrays.asList("Store", "PCM"), configManager.getStoreParameterList(
                "pcmall", "group-membership/group"));
    }
    
    @Test
    public void testGetStoreSetting() {
        assertEquals("pcm.com", configManager.getProperty("settings", "pcmall", "site_domain"));
        assertEquals("true", configManager.getProperty("settings", "pcmall", "auto_export"));
    }

    @Test
    public void testGetStoreSettings() {
        assertEquals(Arrays.asList("180x150", "728x90", "300x250", "728x150"), configManager.getPropertyList("settings", "pcmall", "allowed_banner_sizes"));
    }
    
    @Test
    public void testGetSystemTimeZoneId() {
        assertEquals("America/Los_Angeles", configManager.getSystemTimeZoneId());
    }
    
    @Test
    public void testIsMemberOf() {
        assertEquals(true, configManager.isMemberOf("PCM", "pcmall"));
    }
    
    @Test
    public void testIsMemberOf_Return_False() {
        assertEquals(false, configManager.isMemberOf("PCM", "macmall"));
    }
    
    @Test
    public void testIsSharedCore() {
        assertEquals(true, configManager.isSharedCore());
    }
    
    @Test
    public void testGetModuleNames() {
    	assertEquals(Arrays.asList("mail", "settings", "searchwithin", "facetsort"), configManager.getModuleNames("pcmall"));
    }
    
    //concurrency
//	for (int i =0; i < 20; i++) {
	//			(new Thread() {
	//				public void run() {
	//					for (int i = 1; i < 50; i++) {
	//						try {
	//							configManager.setStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT, "true");
	//							System.out.println(configManager.getStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT));
	//							configManager.setStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT, "false");
	//							System.out.println( configManager.getStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT));
	//							Thread.sleep(100);
	//						} catch (InterruptedException e) {
	//						}
	//					}
	//				}
	//			}).start();
	//		}
    
//    @Test
//    public void testIsSolrImplOnly() {
//        // TODO add test code here
//    }
}