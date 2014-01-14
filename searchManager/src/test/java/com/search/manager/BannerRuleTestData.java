package com.search.manager;

import com.search.manager.core.model.BannerRule;

public class BannerRuleTestData {

	public static final String DEFAULT_ID = "bannerrule1234";
	
	public static BannerRule getNewBannerRule() {
		BannerRule bannerRule = new BannerRule();
		bannerRule.setStoreId("ecost");
		bannerRule.setRuleName("ruleName");
		bannerRule.setCreatedBy("ecost_admin");
		bannerRule.setComment("comment");
		
		return bannerRule;
	}
	
	public static BannerRule getExistingBannerRule() {
		BannerRule bannerRule = getNewBannerRule();
		bannerRule.setRuleId(DEFAULT_ID);
		bannerRule.setRuleName("ruleName1");
		
		return bannerRule;
	}
	
}
