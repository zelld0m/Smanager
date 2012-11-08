package com.search.manager.xml.file;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleVersionXml;

public class RuleRestoreUtil {
	
	@Autowired DaoService daoService;
	
	private boolean restoreElevate(RuleVersionXml xml){
		ElevateRuleXml elevateRuleXml = (ElevateRuleXml) xml;
		StoreKeyword storeKeyword = new StoreKeyword(xml.getStore(), xml.getRuleId());
		ElevateResult elevateResult = new ElevateResult(storeKeyword);

		try {
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean restoreExclude(RuleVersionXml xml){
		return false;
	}

	private boolean restoreDemote(RuleVersionXml xml){
		return false;
	}
	
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
	
	public static boolean restoreRule(RuleVersionXml xml) {
	
		if(xml==null){
			
		}else if (xml instanceof ElevateRuleXml){
			
		}else if(xml instanceof DemoteRuleXml){
			
		}else if(xml instanceof ExcludeRuleXml){
			
		}else if(xml instanceof FacetSortRuleXml){
			
		}else if(xml instanceof RedirectRuleXml){
			
		}else if(xml instanceof RankingRuleXml){
			
		}
		
		return false;
	}
}