package com.search.manager.dao;

import java.util.List;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RuleVersionXml;

public interface RuleVersionDaoService{
	public boolean createRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String notes);
	public boolean deleteRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, int version);
	public boolean restoreRuleVersion(RuleVersionXml xml);	
	public List<RuleVersionXml> getRuleVersions(String store, String ruleType, String ruleId);
}