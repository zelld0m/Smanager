package com.search.manager.core.service.sp;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.service.AuditTrailService;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.core.service.ImagePathService;
import com.search.manager.core.service.RuleStatusService;

public class ServiceSpConfigurationTest extends BaseIntegrationTest {

	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	@Qualifier("auditTrailServiceSp")
	private AuditTrailService auditTrailService;
	// rule
	@Autowired
	@Qualifier("bannerRuleServiceSp")
	private BannerRuleService bannerRuleService;
	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;
	@Autowired
	@Qualifier("imagePathServiceSp")
	private ImagePathService imagePathService;

	@Test
	public void testWiring() {
		assertNotNull(ruleStatusService);
		assertNotNull(auditTrailService);
		// rule
		assertNotNull(bannerRuleService);
		assertNotNull(bannerRuleItemService);
		assertNotNull(imagePathService);
	}

}
