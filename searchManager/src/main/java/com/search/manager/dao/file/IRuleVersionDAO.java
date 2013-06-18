package com.search.manager.dao.file;

import java.util.List;

import com.search.manager.report.model.xml.RuleXml;

public interface IRuleVersionDAO<T extends RuleXml> {

	boolean createRuleVersion(String store, String ruleId, String username, String name, String notes);

	boolean createPublishedRuleVersion(String store, String ruleId, String username, String name, String notes);

	boolean restoreRuleVersion(RuleXml xml);

	boolean deleteRuleVersion(String store, String ruleId, String username, long version);

	List<RuleXml> getPublishedRuleVersions(String store, String ruleId);

	List<RuleXml> getRuleVersions(String store, String ruleId);

	int getRuleVersionsCount(String store, String ruleId);

}