package com.search.manager.dao.file;

import java.util.List;

import com.search.manager.model.RuleVersionInfo;

public interface RuleVersionDAO{
	public List<RuleVersionInfo> getRuleVersions(String store, String ruleId);
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes);
	public boolean restoreRuleVersion(String store, String ruleId, String username, long version);
}