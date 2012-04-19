package com.search.manager.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.service.UtilityService;

@Component(value="redirectUtility")
public class RedirectUtility {
	
	@Autowired private DaoCacheService daoCacheService;
	
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
