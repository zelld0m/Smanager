package com.search.manager.utility;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.service.UtilityService;

public class RedirectUtility {
	
	private DaoCacheService daoCacheService;
	
	public void setDaoCacheService(DaoCacheService daoCacheService) {
		this.daoCacheService = daoCacheService;
	}

	public void updateRuleMap() {
		daoCacheService.updateRedirectRule(UtilityService.getStoreName());
	}

	public String getRedirectFQ(String keyword) {
		return daoCacheService.getRedirectRule(UtilityService.getStoreName(), keyword);
	}
}