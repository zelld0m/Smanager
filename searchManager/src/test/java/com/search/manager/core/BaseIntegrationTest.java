package com.search.manager.core;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.search.manager.utility.PropertiesUtils;
import com.search.ws.ConfigManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-context-test.xml",
		"classpath:spring/spring-solr-context-test.xml",
		"classpath:spring/spring-datasource-test.xml" })
public class BaseIntegrationTest {
	
	public BaseIntegrationTest() {
		PropertiesUtils.initPropertiesConfig("C:\\home\\solr\\conf\\globalvar.properties");
		ConfigManager.getInstance("C:\\home\\solr\\conf\\solr.xml");
	}
}
