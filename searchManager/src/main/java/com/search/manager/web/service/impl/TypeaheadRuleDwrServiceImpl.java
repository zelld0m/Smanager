package com.search.manager.web.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.enums.RuleType;
import com.search.manager.service.UtilityService;
import com.search.manager.web.service.TypeaheadRuleDwrService;

public class TypeaheadRuleDwrServiceImpl implements TypeaheadRuleDwrService{

	@Autowired
	private TypeaheadRuleService typeaheadRuleService;
	@Autowired
	private UtilityService utilityService;
	
	public void addTypeahead(String storeId, String keyword) {
		TypeaheadRule typeaheadRule = new TypeaheadRule();
		
		typeaheadRule.setRuleName(keyword);
		typeaheadRule.setStoreId(storeId);
		typeaheadRule.setCreatedBy(utilityService.getUsername());
		typeaheadRule.setCreatedDate(new DateTime());
		typeaheadRule.setRuleType(RuleType.KEYWORD);
		
		try {
			typeaheadRuleService.add(typeaheadRule);
		} catch (CoreServiceException e) {
			e.printStackTrace();
		}
	}
}
