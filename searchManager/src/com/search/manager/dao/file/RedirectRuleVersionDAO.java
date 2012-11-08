package com.search.manager.dao.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RedirectRule;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionXml;
import com.search.manager.utility.StringUtil;

@Repository(value="queryCleaningVersionDAO")
public class RedirectRuleVersionDAO extends RuleVersionDAO<RedirectRuleXml>{

	@Autowired private DaoService daoService;

	@Override
	public String getRuleVersionFilename(String store, String ruleId) {
		return RuleVersionUtil.getFileName(store, RuleEntity.QUERY_CLEANING, StringUtil.escapeKeyword(ruleId));
	}

	@Override
	@SuppressWarnings("unchecked")
	public RuleVersionListXml<RedirectRuleXml> getRuleVersionList(
			String store, String ruleId) {
		return (RuleVersionListXml<RedirectRuleXml>) RuleVersionUtil.getRuleVersionList(store, RuleEntity.QUERY_CLEANING, ruleId);
	}

	@Override
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes) {
		RuleVersionListXml<RedirectRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<RedirectRuleXml> queryCleaningRuleXmlList = ruleVersionListXml.getVersions();

			try {
				RedirectRule redirectRule = daoService.getRedirectRule(new RedirectRule(ruleId));
				
				queryCleaningRuleXmlList.add(new RedirectRuleXml(store, version, name, notes, username, redirectRule));

				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(redirectRule.getRuleName());
				ruleVersionListXml.setVersions(queryCleaningRuleXmlList);
			} catch (DaoException e) {
				return false;
			}	

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.QUERY_CLEANING, ruleId, ruleVersionListXml);
		}

		return false;
	}
}