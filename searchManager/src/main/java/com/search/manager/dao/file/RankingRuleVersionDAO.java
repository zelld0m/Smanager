package com.search.manager.dao.file;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;

@Repository(value="rankingRuleVersionDAO")
public class RankingRuleVersionDAO extends AbstractRuleVersionDAO<RankingRuleXml>{

	@Autowired private DaoService daoService;

	protected RuleEntity getRuleEntity() {
		return RuleEntity.RANKING_RULE;
	}

	@Override
	protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId, String username, String name, String notes, boolean isVersion) {
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<RankingRuleXml> eRuleXmlList = ((RuleVersionListXml<RankingRuleXml>)ruleVersionListXml).getVersions();
			long version = ruleVersionListXml.getNextVersion();
			try {
				Relevancy relevancy = daoService.getRelevancyDetails(new Relevancy(ruleId));
				List<RelevancyKeyword> relevancyKeywords = daoService.getRelevancyKeywords(relevancy).getList();
				relevancy.setRelKeyword(relevancyKeywords);
				eRuleXmlList.add(new RankingRuleXml(store, version, name, notes, username, relevancy));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(relevancy.getRuleName());
				return true;
			} catch (DaoException e) {
			}	
		}
		return false;
	}

}