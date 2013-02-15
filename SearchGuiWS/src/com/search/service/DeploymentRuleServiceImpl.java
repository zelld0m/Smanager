package com.search.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.exception.DataException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

public class DeploymentRuleServiceImpl implements DeploymentRuleService{

	private static Logger logger = Logger.getLogger(DeploymentRuleServiceImpl.class);
	
	private static DaoCacheService daoCacheService;
	private static DaoService daoService;
	private static DaoService daoServiceStg;
	
	public void setDaoCacheService(DaoCacheService daoCacheService_) {
		daoCacheService = daoCacheService_;
	}

	public void setDaoService(DaoService daoService_) {
		daoService = daoService_;
	}
	
	public void setDaoServiceStg(DaoService daoServiceStg_) {
		daoServiceStg = daoServiceStg_;
	}

	@Override
	public Map<String,Boolean> publishElevateRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		List<ElevateResult> elevatedList = null;
		ElevateResult elevateFilter = new ElevateResult();
		
			try {
				for(String key : list){
					try {
						ElevateResult delEl = new ElevateResult();
						StoreKeyword sk = new StoreKeyword(store, key);
						delEl.setStoreKeyword(sk);
						daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
						
						// retrieve staging data then push to prod
						daoService.addKeyword(sk);
						elevateFilter.setStoreKeyword(sk);
						SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
						elevatedList = daoServiceStg.getElevateResultList(criteria).getList();
						
						if(elevatedList != null && elevatedList.size() > 0){
							for(ElevateResult e : elevatedList){
								daoService.addElevateResult((ElevateResult) e);
							}
						}
						// clear cache data to force a reload
						daoCacheService.resetElevateRule(sk); // prod
						map.put(key, true);
					} catch (Exception e) {
						logger.error("Failed to publish rule: " + key, e);
						map.put(key, false);
					}
				}
				
				Store s = new Store(store);
				daoCacheService.reloadAllKeywords(s);
				daoCacheService.setForceReloadElevate(s);
				
			} catch (Exception e) {
				logger.error(e,e);
			}
			return map;
	}
	
	@Override
	public Map<String,Boolean> publishExcludeRulesMap(String store, List<String> list) {
		
		Map<String,Boolean> map = getKLMap(list);
		List<ExcludeResult> excludeList = null;
		ExcludeResult excludeFilter = new ExcludeResult();
		
			try {
				
				for(String key : list){
					try {
						ExcludeResult delEl = new ExcludeResult();
						StoreKeyword sk = new StoreKeyword(store, key);
						delEl.setStoreKeyword(sk);
						daoService.clearExcludeResult(sk); // prod
						
						// retrieve staging data then push to prod
						daoService.addKeyword(sk);
						excludeFilter.setStoreKeyword(sk);
						SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);
						
						excludeList = daoServiceStg.getExcludeResultList(criteria).getList();
						
						if(excludeList != null && excludeList.size() > 0){
							for(ExcludeResult e : excludeList){
								daoService.addExcludeResult(e);
							}
						}
						// clear cache data to force a reload
						daoCacheService.resetExcludeRule(sk); // prod
						map.put(key, true);
					} catch (Exception e) {
						logger.error("Failed to publish rule: " + key, e);
						map.put(key, false);
					}
				}
				
				Store s = new Store(store);
				daoCacheService.reloadAllKeywords(s);
				daoCacheService.setForceReloadExclude(s);
				
			} catch (Exception e) {
				logger.error(e,e);
			}
		return map;
	}
	
	@Override
	public Map<String,Boolean> publishRedirectRulesMap(String store, List<String> list) {
		
		Map<String,Boolean> map = getKLMap(list);
			try {
				for(String key : list){
					try {
						RedirectRule delRel = new RedirectRule();
						delRel.setRuleId(key);
						delRel.setStoreId(store);
						daoService.deleteRedirectRule(delRel); // prod
	
						// retrieve staging data then push to prod
						RedirectRule addRel = new RedirectRule();
						delRel.setRuleId(key);
						delRel.setStoreId(store);
						addRel = daoServiceStg.getRedirectRule(delRel);
						List<String> searchTerms = addRel.getSearchTerms();
						if (CollectionUtils.isNotEmpty(searchTerms)) {
							for (String keyword: searchTerms) {
								daoService.addKeyword(new StoreKeyword(store, keyword));
							}
						}
						
						if(addRel != null) {
							daoService.addRedirectRule(addRel); // prod 
							
							// add redirect keyword
							if (CollectionUtils.isNotEmpty(searchTerms)) {
								for (String keyword: searchTerms) {
									RedirectRule rule = new RedirectRule();
									rule.setRuleId(addRel.getRuleId());
									rule.setStoreId(addRel.getStoreId());
									rule.setSearchTerm(keyword);
									rule.setLastModifiedBy("SYSTEM");
									daoService.addRedirectKeyword(rule);							
								}
							}
							
							// add rule condition
							RedirectRule rule = new RedirectRule();
							rule.setRuleId(addRel.getRuleId());
							rule.setStoreId(addRel.getStoreId());
							RecordSet<RedirectRuleCondition> conditionSet = daoServiceStg.getRedirectConditions(
									new SearchCriteria<RedirectRule>(rule, null, null, 0, 0));
							if (conditionSet!= null && conditionSet.getTotalSize() > 0) {
								for (RedirectRuleCondition condition: conditionSet.getList()) {
									condition.setLastModifiedBy("SYSTEM");
									daoService.addRedirectCondition(condition);
								}
							}
						}
						map.put(key, true);
					} catch (Exception e) {
						map.put(key, false);
						logger.error("Failed to publish rule: " + key, e);
					}
				}
				
				Store s = new Store(store);
				daoCacheService.reloadAllKeywords(s);
				daoCacheService.setForceReloadRedirect(s); // invalidates all cached data to force a reload and prevent stale data
			} catch (Exception e) {
				logger.error(e,e);
			}
			return map;
	}
	
	@Override
	public Map<String,Boolean> publishRankingRulesMap(String store, List<String> list) {
	
		Map<String,Boolean> map = getKLMap(list);
		try {
			for(String key : list){
				try {
					Relevancy delRel = new Relevancy();
					delRel.setRelevancyId(key);
					delRel.setStore(new Store(store));
					daoService.deleteRelevancy(delRel); // prod

					// retrieve staging data then push to prod
					Relevancy addRel = new Relevancy();
					addRel.setRelevancyId(key);
					addRel.setStore(new Store(store));
					addRel = daoServiceStg.getRelevancyDetails(addRel);
					
					if(addRel != null) {
						// add relevancy
						daoService.addRelevancy(addRel);
						// add relevancy keywords
						RecordSet<RelevancyKeyword> relevancyKeywords = daoServiceStg.getRelevancyKeywords(addRel);
						if (relevancyKeywords.getTotalSize() > 0) {
							for (RelevancyKeyword rk: relevancyKeywords.getList()) {
								daoService.addKeyword(new StoreKeyword(store, rk.getKeyword().getKeywordId()));
								daoService.addRelevancyKeyword(rk);
							}								
						}
						// save relevancy fields
						RelevancyField rf = new RelevancyField();
						rf.setRelevancy(addRel);
						
						Map<String, String> relevancyFields = addRel.getParameters();
						if (relevancyFields != null) {
							for (String field: relevancyFields.keySet()) {
								String value = relevancyFields.get(field);
								if (StringUtils.isNotBlank(value)) {
									rf.setFieldName(field);
									rf.setFieldValue(value);
									daoService.addRelevancyField(rf);										
								}
							}
						}
					}
					
					Store s = new Store(store);
					daoCacheService.reloadAllKeywords(s);
					daoCacheService.setForceReloadRelevancy(s); // invalidates all cached data to force a reload and prevent stale data
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to publish rule: " + key, e);
					map.put(key, false);
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return map;
	}

	@Override
	public boolean loadElevateRules(String store) {
		try {
			daoCacheService.setForceReloadElevate(new Store(store));
			return daoCacheService.loadElevateRules(new Store(store));
		} catch (DataException e) {
			logger.error(e);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean loadExcludeRules(String store) {
		try {
			daoCacheService.setForceReloadExclude(new Store(store));
			return daoCacheService.loadExcludeRules(new Store(store));
		} catch (DataException e) {
			logger.error(e);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean loadRedirectRules(String store) {
		try {
			daoCacheService.setForceReloadRedirect(new Store(store));
			return daoCacheService.loadRedirectRules(new Store(store));
		} catch (DataException e) {
			logger.error(e);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public boolean loadRankingRules(String store) {
		try {
			daoCacheService.setForceReloadRelevancy(new Store(store));
			return daoCacheService.loadRelevancyRules(new Store(store));
		} catch (DataException e) {
			logger.error(e);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean loadFacetSortRules(String store) {
		try {
			daoCacheService.setForceReloadFacetSort(new Store(store));
			return daoCacheService.loadFacetSortRules(new Store(store));
		} catch (DataException e) {
			logger.error(e);
		} catch (DaoException e) {
			logger.error(e);
		}
		return false;
	}

	@Override
	public Map<String,Boolean> unpublishElevateRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		if(CollectionUtils.isNotEmpty(list)){
			for(String key : list){
				try {
					StoreKeyword sk = new StoreKeyword(store, key);
					ElevateResult addEl = new ElevateResult();
					addEl.setStoreKeyword(sk);
					daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
					daoCacheService.resetElevateRule(sk); // clear sk entry in cache
					daoCacheService.setForceReloadExclude(new Store(store));
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to unpublish rule: " + key, e);
					map.put(key, false);
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<String,Boolean> unpublishExcludeRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		if(CollectionUtils.isNotEmpty(list)){
			for(String key : list){
				try {
					StoreKeyword sk = new StoreKeyword(store, key);
					ExcludeResult addEx = new ExcludeResult();
					addEx.setStoreKeyword(sk);
					daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
					daoCacheService.resetExcludeRule(sk); // clear sk entry in cache
					daoCacheService.setForceReloadExclude(new Store(store));
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to unpublish rule: " + key, e);
					map.put(key, false);
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<String,Boolean> unpublishRedirectRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);

		if(CollectionUtils.isNotEmpty(list)){
			Store s = new Store(store);
			for(String key : list){
				try {
					RedirectRule delRel = new RedirectRule();
					delRel.setRuleId(key);
					delRel.setStoreId(store);
					// get list of keywords for ranking rule
					List<StoreKeyword> sks = new ArrayList<StoreKeyword>();
					RedirectRule rule = new RedirectRule();
					rule.setRuleId(key);
					rule.setStoreId(store);
					SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(rule, null, null,  0, 0);
					for (StoreKeyword keyword: daoService.getRedirectKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR).getList()) {
						sks.add(keyword);
					}
					daoService.deleteRedirectRule(delRel); // prod
					for (StoreKeyword sk: sks) {
						daoCacheService.resetRedirectRule(sk); // clear sk entry in cache
					}
					daoCacheService.setForceReloadRedirect(s); // invalidates all cached data to force a reload and prevent stale data
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to unpublish rule: " + key, e);
					map.put(key, false);
				}
			}
		}
		return map;
	}
	
	@Override
	public Map<String,Boolean> unpublishRankingRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		Store s = new Store(store);
		if(CollectionUtils.isNotEmpty(list)){
			for(String key : list){
				try {
					Relevancy delRel = new Relevancy();
					delRel.setRelevancyId(key);
					delRel.setStore(s);
					// get list of keywords for ranking rule
					List<StoreKeyword> sks = new ArrayList<StoreKeyword>();
					for (RelevancyKeyword keyword: daoService.getRelevancyKeywords(delRel).getList()) {
						sks.add(new StoreKeyword(store, keyword.getKeyword().getKeywordId()));
					}
					daoService.deleteRelevancy(delRel); // prod
					for (StoreKeyword sk: sks) {
						daoCacheService.resetRelevancyRule(sk); // clear sk entry in cache
					}
					daoCacheService.setForceReloadRelevancy(s); // invalidates all cached data to force a reload and prevent stale data
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to unpublish rule: " + key, e);
					map.put(key, false);
				}
			}
		}
		return map;
	}
	
	private Map<String,Boolean> getKLMap(List<String> list){	
		Map<String,Boolean> map = new HashMap<String, Boolean>();
		for(String key : list){
			map.put(key, false);
		}
		return map;
	}
	
	@Override
	public Map<String, Boolean> publishDemoteRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		List<DemoteResult> demotedList = null;
		DemoteResult demoteFilter = new DemoteResult();
		
		try {
			for(String key : list){
				try {
					DemoteResult delEl = new DemoteResult();
					StoreKeyword sk = new StoreKeyword(store, key);
					delEl.setStoreKeyword(sk);
					daoService.clearDemoteResult(new StoreKeyword(store, key)); // prod
					
					// retrieve staging data then push to prod
					daoService.addKeyword(sk);
					demoteFilter.setStoreKeyword(sk);
					SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(demoteFilter,null,null,0,0);
					demotedList = daoServiceStg.getDemoteResultList(criteria).getList();
					
					if(demotedList != null && demotedList.size() > 0){
						for(DemoteResult e : demotedList){
							daoService.addDemoteResult((DemoteResult) e);
						}
					}
					// clear cache data to force a reload
					daoCacheService.resetDemoteRule(sk); // prod
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to publish rule: " + key, e);
					map.put(key, false);
				}
			}
			
			Store s = new Store(store);
			daoCacheService.reloadAllKeywords(s);
			daoCacheService.setForceReloadDemote(s);
			
		} catch (Exception e) {
			logger.error(e,e);
		}
		return map;
	}

	@Override
	public boolean loadDemoteRules(String store) {
		try {
			daoCacheService.setForceReloadDemote(new Store(store));
			return daoCacheService.loadDemoteRules(new Store(store));
		} catch (DataException e) {
			logger.equals(e);
		} catch (DaoException e) {
			logger.equals(e);
		}
		return false;
	}

	@Override
	public Map<String, Boolean> unpublishDemoteRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		
		if(CollectionUtils.isNotEmpty(list)){
			for(String key : list){
				try {
					StoreKeyword sk = new StoreKeyword(store, key);
					DemoteResult addEl = new DemoteResult();
					addEl.setStoreKeyword(sk);
					daoService.clearDemoteResult(new StoreKeyword(store, key)); // prod
					daoCacheService.resetDemoteRule(sk); // clear sk entry in cache
					daoCacheService.setForceReloadDemote(new Store(store));
					map.put(key, true);
				} catch (Exception e) {
					logger.error("Failed to unpublish rule: " + key, e);
					map.put(key, false);
				}
			}
		}
		return map;
	}

	@Override
	public Map<String, Boolean> publishFacetSortRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		try {
			for(String key : list){
				int result = -1;
				FacetSort delFs = new FacetSort(key, store);
				daoService.deleteFacetSort(delFs); // prod

				// retrieve staging data then push to prod
				FacetSort addFs = new FacetSort(key, store);
				addFs = daoServiceStg.getFacetSort(delFs);
								
				if(addFs != null) {
					try{
						result += daoService.addFacetSort(addFs); // prod 
						
						//add facet groups
						FacetGroup facetGroup = new FacetGroup(key, "");
						SearchCriteria<FacetGroup> criteria = new SearchCriteria<FacetGroup>(facetGroup);
						RecordSet<FacetGroup> addFsGroups = daoServiceStg.searchFacetGroup(criteria, MatchType.MATCH_ID);
						
						if(addFsGroups != null){
							List<FacetGroup> addFsGs = addFsGroups.getList();
							
							for(FacetGroup fg : addFsGs){
								result += daoService.addFacetGroup(fg);	//prod
							}
						}
						
						//add facet group items
						FacetGroupItem facetGroupItem = new FacetGroupItem(key, "");
						SearchCriteria<FacetGroupItem> criteria2 = new SearchCriteria<FacetGroupItem>(facetGroupItem);
						RecordSet<FacetGroupItem> addFsGroupItems = daoServiceStg.searchFacetGroupItem(criteria2, MatchType.MATCH_ID);
						
						if(addFsGroupItems != null){
							result += daoService.addFacetGroupItems(addFsGroupItems.getList()); //prod
						}
						
						map.put(key, (result > 0));
					}
					catch(DaoException e){
						logger.error("Failed during addRule()",e);
						try {
							daoService.deleteFacetSort(new FacetSort(key, store));
						} catch (DaoException de) {
							logger.error("Unable to complete process, need to manually delete rule", de);
						}
					}
				}
			}
			
			Store s = new Store(store);
			daoCacheService.reloadAllKeywords(s);
			daoCacheService.setForceReloadFacetSort(s); // invalidates all cached data to force a reload and prevent stale data
		} catch (Exception e) {
			logger.error(e,e);
		}
		return map;
	}

	@Override
	public Map<String, Boolean> unpublishFacetSortRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		if(CollectionUtils.isNotEmpty(list)){
			for(String key : list){
				try {
					int result = -1;
				
					FacetSort delFs = new FacetSort();
					delFs.setRuleId(key);
					delFs.setStore(new Store(store));
					result = daoService.deleteFacetSort(delFs); // prod
					daoCacheService.setForceReloadFacetSort(new Store(store));
					map.put(key, (result > 0));
				} catch (Exception e) {
					logger.error("Failed to unpublish rule: " + key, e);
					map.put(key, false);
				}
			}
		}
		return map;
	}

}
