package com.search.manager.workflow.service;

import com.search.manager.model.RuleStatus;

public interface RuleStatusService {

	public RuleStatus getRuleStatus(String storeId, String ruleType, String ruleRefId);
	public RuleStatus createRuleStatus(String storeId, String userName) ;
}
