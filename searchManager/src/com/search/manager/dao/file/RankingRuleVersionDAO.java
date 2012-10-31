package com.search.manager.dao.file;

import org.springframework.stereotype.Repository;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="rankingRuleVersionDAO")
public class RankingRuleVersionDAO extends RuleVersionDAO<RankingRuleXml>{

	@Override
	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFileName(store, RuleEntity.RANKING_RULE, ruleId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<RankingRuleXml> getRuleVersionList(String store, String ruleId) {
		return (RuleVersionListXml<RankingRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.RANKING_RULE, ruleId);
	}

	@Override
	public boolean createRuleVersion(String store, String ruleId,
			String username, String name, String notes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean restoreRuleVersion(String store, String ruleId,
			String username, long version) {
		// TODO Auto-generated method stub
		return false;
	}

}