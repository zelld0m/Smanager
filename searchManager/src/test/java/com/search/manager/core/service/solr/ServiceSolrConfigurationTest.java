package com.search.manager.core.service.solr;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.dao.BannerRuleItemDao;

public class ServiceSolrConfigurationTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("bannerRuleItemDaoSolr")
	private BannerRuleItemDao bannerRuleItemDao;

	@Test
	public void testWiring() {
		assertNotNull(bannerRuleItemDao);
	}

}
