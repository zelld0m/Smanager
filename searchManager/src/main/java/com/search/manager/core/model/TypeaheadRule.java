package com.search.manager.core.model;

import com.search.manager.enums.RuleType;

public class TypeaheadRule extends ModelBean{

	private static final long serialVersionUID = 7840812498092574024L;

	private String ruleId;
	private RuleType ruleType;
	
	private String storeId;
	private String ruleName;
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	
}
