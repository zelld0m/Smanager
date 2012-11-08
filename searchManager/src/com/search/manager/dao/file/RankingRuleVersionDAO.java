package com.search.manager.dao.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.Store;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleVersionXml;

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

	@Override
	public boolean restoreRuleVersion(RuleVersionXml xml) {
		Relevancy restoreVersion = new Relevancy((RankingRuleXml) xml);
		String store = restoreVersion.getStore().toString();
		RelevancyField rf = new RelevancyField();
		boolean isRestored = true;

		try {
			Relevancy currentVersion = getRelevancy(store , restoreVersion.getRuleId());
			if (currentVersion == null && (isRestored &= daoService.addRelevancy(currentVersion) > 0)){

				//TODO: Check for optimization
				if (MapUtils.isNotEmpty(currentVersion.getParameters())) {
					for (Map.Entry<String, String> entry : currentVersion.getParameters().entrySet()) {
						rf = new RelevancyField(currentVersion,entry.getKey(), entry.getValue(), currentVersion.getCreatedBy(), currentVersion.getLastModifiedBy(),
								currentVersion.getCreatedDate(), currentVersion.getLastModifiedDate());
						isRestored &= daoService.addRelevancyField(rf) > 0;
					}
				}

				if (CollectionUtils.isNotEmpty(currentVersion.getRelKeyword())) {
					for (RelevancyKeyword keyword : currentVersion.getRelKeyword()) {
						keyword.setRelevancy(currentVersion);
						isRestored &= daoService.addRelevancyKeyword(keyword) > 0;
					}
				}

				//TODO: Rollback if isRestored value becomes false
				
				return isRestored;
			}

			Map<String, String> currParams = currentVersion.getParameters();
			Map<String, String> restParams = restoreVersion.getParameters();

			if (isRestored &= daoService.updateRelevancy(restoreVersion)>0){
			
				for (Map.Entry<String, String> entry : restParams.entrySet()) {
					
					if (currParams.get(entry.getKey()) == null) {
						rf = new RelevancyField(currentVersion,entry.getKey(), entry.getValue(), currentVersion.getCreatedBy(), currentVersion.getLastModifiedBy(),
								currentVersion.getCreatedDate(), currentVersion.getLastModifiedDate());
						
						isRestored &= daoService.addRelevancyField(rf) > 0;
						
					} else if (!currParams.get(entry.getKey()).equals(restParams.get(entry.getKey()))) {
					
						rf = new RelevancyField(currentVersion,entry.getKey(), entry.getValue(), currentVersion.getCreatedBy(), currentVersion.getLastModifiedBy(),
								currentVersion.getCreatedDate(), currentVersion.getLastModifiedDate());
						
//						
//						daoService.updateRelevancyField(new RelevancyField(relevancyVersion,entry.getKey(), entry.getValue(), relevancyVersion.getCreatedBy(), relevancyVersion.getLastModifiedBy(),
//								relevancyVersion.getCreatedDate(), relevancyVersion.getLastModifiedDate()));
					}
				}
				
				if (CollectionUtils.isNotEmpty(currentVersion.getRelKeyword())) {
					for (RelevancyKeyword keyword : currentVersion.getRelKeyword()) {
						keyword.setRelevancy(currentVersion);
						daoService.deleteRelevancyKeyword(keyword);
					}
				}
				
				if (CollectionUtils.isNotEmpty(restoreVersion.getRelKeyword())) {
					for (RelevancyKeyword keyword : restoreVersion.getRelKeyword()) {
						keyword.setRelevancy(restoreVersion);
						daoService.addRelevancyKeyword(keyword);
					}
				}
			}
			
		} catch (DaoException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	private Relevancy getRelevancy(String store, String ruleId) throws DaoException {
		Relevancy relevancy = new Relevancy();
		relevancy.setRelevancyId(ruleId);
		relevancy.setStore(new Store(store));
		relevancy = daoService.getRelevancyDetails(relevancy);
		if (relevancy != null) {
			List<RelevancyKeyword> relKWList = daoService.getRelevancyKeywords(relevancy).getList();
			relevancy.setRelKeyword(relKWList);
		}
		return relevancy;
	}
}