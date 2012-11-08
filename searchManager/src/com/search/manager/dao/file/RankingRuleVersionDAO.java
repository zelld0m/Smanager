package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
public class RankingRuleVersionDAO extends RuleVersionDAO<RankingRuleXml>{

	@Autowired private DaoService daoService;

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
		RuleVersionListXml<RankingRuleXml> ruleVersionListXml = getRuleVersionList(store, ruleId);

		if (ruleVersionListXml!=null){
			long version = ruleVersionListXml.getNextVersion();
			List<RankingRuleXml> rankingRuleXmlList = ruleVersionListXml.getVersions();

			try {
				Relevancy relevancy = daoService.getRelevancyDetails(new Relevancy(ruleId));
				List<RelevancyKeyword> relevancyKeywords = daoService.getRelevancyKeywords(relevancy).getList();
				List<String> keywords = new ArrayList<String>();

				if(CollectionUtils.isNotEmpty(relevancyKeywords)){
					for(RelevancyKeyword rk : relevancyKeywords){
						keywords.add(rk.getKeyword().getKeyword());
					}
				}

				relevancy.setKeywords(keywords);

				rankingRuleXmlList.add(new RankingRuleXml(store, version, name, notes, username, relevancy));

				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(relevancy.getRuleName());
				ruleVersionListXml.setVersions(rankingRuleXmlList);
			} catch (DaoException e) {
				return false;
			}	

			return RuleVersionUtil.addRuleVersion(store, RuleEntity.RANKING_RULE, ruleId, ruleVersionListXml);
		}

		return false;
	}
}