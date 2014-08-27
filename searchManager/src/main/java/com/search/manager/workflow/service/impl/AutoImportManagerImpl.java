package com.search.manager.workflow.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.Store;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.workflow.service.AutoImportManager;


@Component(value="autoImportService")
public class AutoImportManagerImpl implements AutoImportManager{
	
	@Autowired
	private DaoService daoService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;

	public ExportRuleMap getExportRuleMap(RuleEntity ruleEntity, String sourceStoreId, String sourceRuleId, String targetStore, String importAsId) {
		ExportRuleMap searchExportRuleMap = new ExportRuleMap(sourceStoreId, sourceRuleId, null,
				targetStore, importAsId, null, ruleEntity);
		List<ExportRuleMap> rtList;
		try {
			rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(searchExportRuleMap), null).getList();
			if (CollectionUtils.isNotEmpty(rtList)) {
				return rtList.get(0);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String getImportAsId(RuleEntity ruleEntity, RuleStatus ruleStatus, String sourceStoreId, String sourceRuleId, String targetStore, String importAsId) {

		switch(ruleEntity) {
		case TYPEAHEAD:
			importAsId = ruleStatus.getRuleId();
			break;
		case FACET_SORT:
		case QUERY_CLEANING:
		case RANKING_RULE:
			ExportRuleMap existingMap = getExportRuleMap(ruleEntity, sourceStoreId, sourceRuleId, targetStore, null);

			if(existingMap.getRuleIdTarget() != null) {
				importAsId = existingMap.getRuleIdTarget();
			}

			break;
		default:
			break;
		}

		return importAsId;
	}
	
	public RuleStatus getTargetRuleStatus(RuleEntity ruleEntity, String storeId, String ruleId, String ruleName, String targetStoreId) throws CoreServiceException {
		switch(ruleEntity) {

		case TYPEAHEAD:
			TypeaheadRule typeaheadRule = new TypeaheadRule();

			typeaheadRule.setStoreId(storeId);
			typeaheadRule.setRuleName(ruleName);

			SearchResult<TypeaheadRule> result = typeaheadRuleService.search(typeaheadRule);

			if(result.getTotalSize() > 0) {
				return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), result.getList().get(0).getRuleId());
			}

			break;
		case QUERY_CLEANING:
			ExportRuleMap redirectMap = getExportRuleMap(ruleEntity, storeId, ruleId, targetStoreId, null);

			String redirectRuleId = null;

			if(redirectMap != null && redirectMap.getRuleIdTarget() != null){
				redirectRuleId = redirectMap.getRuleIdTarget();
			} else {
				RedirectRule redirectRule = getRedirectRule(targetStoreId, ruleName);
				if(redirectRule != null) {
					redirectRuleId = redirectRule.getRuleId();
				}
			}

			return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), redirectRuleId);

		case RANKING_RULE:
			ExportRuleMap relevancyMap = getExportRuleMap(ruleEntity, storeId, ruleId, targetStoreId, null);

			String relevancyRuleId = null;

			if(relevancyMap != null && relevancyMap.getRuleIdTarget() != null){
				relevancyRuleId = relevancyMap.getRuleIdTarget();
			} else {
				Relevancy relevancyRule = getRelevancyRule(targetStoreId, ruleName);

				if(relevancyRule != null) {
					relevancyRuleId = relevancyRule.getRuleId();
				}
			}
			
			return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), relevancyRuleId);

		case FACET_SORT:
			ExportRuleMap facetMap = getExportRuleMap(ruleEntity, storeId, ruleId, targetStoreId, null);

			String facetSortId = null;

			if(facetMap != null && facetMap.getRuleIdTarget() != null){
				facetSortId = facetMap.getRuleIdTarget();
			} else {
				FacetSort facetSort = getFacetSorRule(targetStoreId, ruleName);

				if(facetSort != null) {
					facetSortId = facetSort.getRuleId();
				}
			}
			
			return ruleStatusService.getRuleStatus(targetStoreId, ruleEntity.getName(), facetSortId);
		default:
			break;
		}
		return null;
	}
	
	private FacetSort getFacetSorRule(String targetStoreId, String ruleName) {
		FacetSort facetSort = new FacetSort();
		
		Store facetStore = new Store();
		facetStore.setStoreId(targetStoreId);

		facetSort.setStore(facetStore);
		facetSort.setRuleName(ruleName);
		
		SearchCriteria<FacetSort> facetSortCriteria = new SearchCriteria<FacetSort>(facetSort);
		
		try {
			RecordSet<FacetSort> facetSortResult = daoService.searchFacetSort(facetSortCriteria, MatchType.MATCH_NAME);
			
			facetSort = facetSortResult.getTotalSize() > 0 ? facetSortResult.getList().get(0) : null;
			return facetSort;

		} catch (DaoException e2) {
			e2.printStackTrace();
		}
		
		return null;
	}
	
	private RedirectRule getRedirectRule(String targetStoreId, String ruleName) {
		RedirectRule redirectRule = new RedirectRule();

		redirectRule.setStoreId(targetStoreId);
		redirectRule.setRuleName(ruleName);
				
		SearchCriteria<RedirectRule> redirectCriteria = new SearchCriteria<RedirectRule>(redirectRule);

		try {
			RecordSet<RedirectRule> redirectResult = daoService.searchRedirectRule(redirectCriteria, MatchType.MATCH_NAME);
			
			redirectRule = redirectResult.getTotalSize() > 0 ? redirectResult.getList().get(0) : null;
			return redirectRule;

		} catch (DaoException e2) {
			e2.printStackTrace();
		}

		return null;
	}

	private Relevancy getRelevancyRule(String targetStoreId, String ruleName) {
		Relevancy relevancyRule = new Relevancy();
		Store relevancyStore = new Store();
		relevancyStore.setStoreId(targetStoreId);

		relevancyRule.setStore(relevancyStore);
		relevancyRule.setRuleName(ruleName);
		SearchCriteria<Relevancy> relevancyCriteria = new SearchCriteria<Relevancy>(relevancyRule);

		try {
			RecordSet<Relevancy> relevancyResult = daoService.searchRelevancy(relevancyCriteria, MatchType.MATCH_NAME);
			relevancyRule = relevancyResult.getTotalSize() > 0 ? relevancyResult.getList().get(0) : null;
			return relevancyRule;

		} catch (DaoException e2) {
			e2.printStackTrace();
		}

		return null;
	}
}
