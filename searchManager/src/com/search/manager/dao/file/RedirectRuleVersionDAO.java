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

@Repository(value="queryCleaningVersionDAO")
public class RedirectRuleVersionDAO extends AbstractRuleVersionDAO<RedirectRuleXml>{

	@Autowired private DaoService daoService;

	protected RuleEntity getRuleEntity() {
		return RuleEntity.QUERY_CLEANING;
	}

	@Override
	protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes, boolean isVersion) {
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<RedirectRuleXml> eRuleXmlList = ((RuleVersionListXml<RedirectRuleXml>)ruleVersionListXml).getVersions();
			long version = ruleVersionListXml.getNextVersion();
			try {
				RedirectRule redirectRule = daoService.getRedirectRule(new RedirectRule(ruleId));
				eRuleXmlList.add(new RedirectRuleXml(store, version, name, notes, username, redirectRule));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(redirectRule.getRuleName());
				return true;
			} catch (DaoException e) {
			}	
		}
		return false;
	}

}