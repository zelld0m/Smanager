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
import com.search.manager.enums.RuleEntity;
import com.search.manager.exception.DataException;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
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
	private static FileService fileService;
	
	public void setDaoCacheService(DaoCacheService daoCacheService_) {
		daoCacheService = daoCacheService_;
	}

	public void setDaoService(DaoService daoService_) {
		daoService = daoService_;
	}
	
	public void setDaoServiceStg(DaoService daoServiceStg_) {
		daoServiceStg = daoServiceStg_;
	}
	
	public void setFileService(FileService fileService_) {
		fileService = fileService_;
	}

	@Override
	public boolean publishElevateRules(String store, List<String> list) {
		
		List<ElevateResult> elevatedList = null;
		ElevateResult elevateFilter = new ElevateResult();
		boolean success = false;
		
			try {
				// Create backup
				fileService.createBackup(store,list,RuleEntity.ELEVATE);		
				
				for(String key : list){
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
				}
				
				Store s = new Store(store);
				daoCacheService.reloadAllKeywords(s);
				daoCacheService.setForceReloadElevate(s);
				success = true;
				
			} catch (Exception e) {
				logger.error(e,e);
				success = false;
			}
			return success;
	}
	
	@Override
	public Map<String,Boolean> publishElevateRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		List<ElevateResult> elevatedList = null;
		ElevateResult elevateFilter = new ElevateResult();
		
			try {
				// Create backup
				fileService.createBackup(store,list,RuleEntity.ELEVATE);		
				
				for(String key : list){
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
	public boolean publishExcludeRules(String store, List<String> list) {
		
		List<ExcludeResult> excludeList = null;
		ExcludeResult excludeFilter = new ExcludeResult();
		boolean success = false;
		
			try {	
				// Create backup
				fileService.createBackup(store,list,RuleEntity.EXCLUDE);	
				
				for(String key : list){
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
				}
				
				Store s = new Store(store);
				daoCacheService.reloadAllKeywords(s);
				daoCacheService.setForceReloadExclude(s);
				success = true;	
				
			} catch (Exception e) {
				logger.error(e,e);
				success = false;
			}
		return success;
	}
	
	@Override
	public Map<String,Boolean> publishExcludeRulesMap(String store, List<String> list) {
		
		Map<String,Boolean> map = getKLMap(list);
		List<ExcludeResult> excludeList = null;
		ExcludeResult excludeFilter = new ExcludeResult();
		
			try {	
				// Create backup
				fileService.createBackup(store,list,RuleEntity.EXCLUDE);	
				
				for(String key : list){
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
	public boolean publishRedirectRules(String store, List<String> list) {
		
		boolean success = false;
			try {
				// Create backup
				fileService.createBackup(store,list,RuleEntity.QUERY_CLEANING);		
				
				for(String key : list){

					RedirectRule delRel = new RedirectRule();
					delRel.setRuleId(key);
					delRel.setStoreId(store);
					daoService.deleteRedirectRule(delRel); // prod

					// retrieve staging data then push to prod
					RedirectRule addRel = daoServiceStg.getRedirectRule(delRel);
					for (String keyword: addRel.getSearchTerms()) {
						daoService.addKeyword(new StoreKeyword(store, keyword));
					}
					
					if(addRel != null) {
						daoService.addRedirectRule(addRel); // prod 
					}					
					success = true;
				}
				
				Store s = new Store(store);
				daoCacheService.reloadAllKeywords(s);
				daoCacheService.setForceReloadRedirect(s); // invalidates all cached data to force a reload and prevent stale data
				success = true;	
				
			} catch (Exception e) {
				logger.error(e,e);
				success = false;
			}
			return success;
	}
	
	@Override
	public Map<String,Boolean> publishRedirectRulesMap(String store, List<String> list) {
		
		Map<String,Boolean> map = getKLMap(list);
			try {
				// Create backup
				fileService.createBackup(store,list,RuleEntity.QUERY_CLEANING);		
				
				for(String key : list){

					RedirectRule delRel = new RedirectRule();
					delRel.setRuleId(key);
					delRel.setStoreId(store);
					daoService.deleteRedirectRule(delRel); // prod

					// retrieve staging data then push to prod
					RedirectRule addRel = new RedirectRule();
					delRel.setRuleId(key);
					delRel.setStoreId(store);
					addRel = daoServiceStg.getRedirectRule(delRel);
					for (String keyword: addRel.getSearchTerms()) {
						daoService.addKeyword(new StoreKeyword(store, keyword));
					}
					
					if(addRel != null) {
						daoService.addRedirectRule(addRel); // prod 
					}					
					map.put(key, true);
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
	public boolean publishRankingRules(String store, List<String> list) {
	
		boolean success = false;
		
			try {
				// Create backup
				fileService.createBackup(store,list,RuleEntity.RANKING_RULE);		
				
					for(String key : list){

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
						success = true;
					}
			} catch (Exception e) {
				logger.error(e,e);
				success = false;
			}
			return success;
	}
	
	@Override
	public Map<String,Boolean> publishRankingRulesMap(String store, List<String> list) {
	
		Map<String,Boolean> map = getKLMap(list);
		
			try {
				// Create backup
				fileService.createBackup(store,list,RuleEntity.RANKING_RULE);		
				
					for(String key : list){

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
					}
			} catch (Exception e) {
				logger.error(e,e);
			}
			return map;
	}


	@Override
	public boolean recallElevateRules(String store, List<String> list) {
		
		boolean success = false;
		
			try {	
				Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.ELEVATE);
				
				if(CollectionUtils.isNotEmpty(list)){
					for(String key : list){
							ElevateResult addEl = new ElevateResult();
							addEl.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
							fileService.removeBackup(store, key, RuleEntity.ELEVATE);
							success = true;
							
							try{
								for(Object e : backUp.get(key)){	
									daoService.addElevateResult((ElevateResult) e);
								}
							}catch (Exception e) {logger.error(e,e);}
							
							daoCacheService.updateElevateRule(addEl); // prod
					}
				}
			} catch (Exception e) {
				logger.error(e,e);
				success = false;
			}
			return success;
	}
	
	@Override
	public Map<String,Boolean> recallElevateRulesMap(String store, List<String> list) {
		
		Map<String,Boolean> map = getKLMap(list);
		
			try {	
				Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.ELEVATE);
				
				if(CollectionUtils.isNotEmpty(list)){
					for(String key : list){
							ElevateResult addEl = new ElevateResult();
							addEl.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
							fileService.removeBackup(store, key, RuleEntity.ELEVATE);
							
							try{
								for(Object e : backUp.get(key)){	
									daoService.addElevateResult((ElevateResult) e);
								}
							}catch (Exception e) {logger.error(e,e);}
							
							daoCacheService.updateElevateRule(addEl); // prod
							map.put(key, true);
					}
				}
			} catch (Exception e) {
				logger.error(e,e);
			}
			return map;
	}
	
	@Override
	public boolean recallExcludeRules(String store, List<String> list) {
		
		boolean success = false;
		
			try {	
				Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.EXCLUDE);
				
				if(CollectionUtils.isNotEmpty(list)){
					for(String key : list){
							ExcludeResult addEx = new ExcludeResult();
							addEx.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
							fileService.removeBackup(store, key, RuleEntity.EXCLUDE);
							success = true;
							
							try{
								for(Object e : backUp.get(key)){	
									daoService.addExcludeResult((ExcludeResult) e);
								}
							}catch (Exception e) {logger.error(e,e);}
							
							daoCacheService.updateExcludeRules(addEx); // prod
					}
				}
			} catch (Exception e) {
				logger.error(e,e);
				success = false;
			}
			return success;
	}
	
	@Override
	public Map<String,Boolean> recallExcludeRulesMap(String store, List<String> list) {
		
		Map<String,Boolean> map = getKLMap(list);
		
			try {	
				Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.EXCLUDE);
				
				if(CollectionUtils.isNotEmpty(list)){
					for(String key : list){
							ExcludeResult addEx = new ExcludeResult();
							addEx.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
							fileService.removeBackup(store, key, RuleEntity.EXCLUDE);
							
							try{
								for(Object e : backUp.get(key)){	
									daoService.addExcludeResult((ExcludeResult) e);
								}
							}catch (Exception e) {}
							
							daoCacheService.updateExcludeRules(addEx); // prod
							map.put(key, true);
					}
				}
			} catch (Exception e) {
				logger.error(e,e);
			}
			return map;
	}
	
	@Override
	public boolean recallRedirectRules(String store, List<String> list) {
		// TODO: implement
		return false;
	}
	
	@Override
	public Map<String,Boolean> recallRedirectRulesMap(String store, List<String> list) {
		return null;
	}

	@Override
	public boolean recallRankingRules(String store, List<String> list) {
		// TODO: fix
		boolean success = false;
		Relevancy relevancy = null;
		List<RelevancyKeyword> kwList = null;
		
		try {	
			Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.RANKING_RULE);
			
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
					Relevancy delRel = new Relevancy();
					delRel.setRelevancyId(key);
					delRel.setStore(new Store(store));
					daoService.deleteRelevancy(delRel); // prod
					fileService.removeBackup(store, key, RuleEntity.RANKING_RULE);
					success = true;
						
					try{
						for(Object e : backUp.get(key)){		
							Map<String,Object> bck = (Map<String,Object>)e;
							
							if(bck != null && bck.size() > 0){
								
								relevancy = (Relevancy)bck.get("relevancy");
								
								if(relevancy != null && StringUtils.isNotEmpty(relevancy.getRelevancyId())){
									// add relevancy
									daoService.addRelevancy(relevancy);
									// add relevancy keywords
									kwList = (List<RelevancyKeyword>)bck.get("keywords");				
									if(CollectionUtils.isNotEmpty(kwList)){
										for (RelevancyKeyword rk: kwList) {
											daoService.addRelevancyKeyword(rk);
										}
									}
									// save relevancy fields
									RelevancyField rf = new RelevancyField();
									rf.setRelevancy(relevancy);
									Map<String, String> relevancyFields = relevancy.getParameters();
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
							}
						}

					}catch (Exception e) {logger.error(e,e);}
					
					daoCacheService.updateRelevancyRule(delRel); // prod
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
			success = false;
		}
		return success;
	}
	
	@Override
	public Map<String,Boolean> recallRankingRulesMap(String store, List<String> list) {

		Map<String,Boolean> map = getKLMap(list);
		Relevancy relevancy = null;
		List<RelevancyKeyword> kwList = null;
		
		try {	
			Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.RANKING_RULE);
			
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
					Relevancy delRel = new Relevancy();
					delRel.setRelevancyId(key);
					delRel.setStore(new Store(store));
					daoService.deleteRelevancy(delRel); // prod
					fileService.removeBackup(store, key, RuleEntity.RANKING_RULE);
					map.put(key, true);
						
					try{
						for(Object e : backUp.get(key)){		
							Map<String,Object> bck = (Map<String,Object>)e;
							
							if(bck != null && bck.size() > 0){
								
								relevancy = (Relevancy)bck.get("relevancy");
								
								if(relevancy != null && StringUtils.isNotEmpty(relevancy.getRelevancyId())){
									// add relevancy
									daoService.addRelevancy(relevancy);
									// add relevancy keywords
									kwList = (List<RelevancyKeyword>)bck.get("keywords");				
									if(CollectionUtils.isNotEmpty(kwList)){
										for (RelevancyKeyword rk: kwList) {
											daoService.addRelevancyKeyword(rk);
										}
									}
									// save relevancy fields
									RelevancyField rf = new RelevancyField();
									rf.setRelevancy(relevancy);
									Map<String, String> relevancyFields = relevancy.getParameters();
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
							}
						}

					}catch (Exception e) {logger.error(e,e);}
					
					daoCacheService.updateRelevancyRule(delRel); // prod
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
			logger.equals(e);
		} catch (DaoException e) {
			logger.equals(e);
		}
		return false;
	}

	@Override
	public boolean loadExcludeRules(String store) {
		try {
			daoCacheService.setForceReloadExclude(new Store(store));
			return daoCacheService.loadExcludeRules(new Store(store));
		} catch (DataException e) {
			logger.equals(e);
		} catch (DaoException e) {
			logger.equals(e);
		}
		return false;
	}

	@Override
	public boolean loadRedirectRules(String store) {
		try {
			daoCacheService.setForceReloadRedirect(new Store(store));
			return daoCacheService.loadRedirectRules(new Store(store));
		} catch (DataException e) {
			logger.equals(e);
		} catch (DaoException e) {
			logger.equals(e);
		}
		return false;
	}

	@Override
	public boolean loadRankingRules(String store) {
		try {
			daoCacheService.setForceReloadRelevancy(new Store(store));
			return daoCacheService.loadRelevancyRules(new Store(store));
		} catch (DataException e) {
			logger.equals(e);
		} catch (DaoException e) {
			logger.equals(e);
		}
		return false;
	}

	@Override
	public boolean unpublishElevateRules(String store, List<String> list) {
		boolean success = false;
		
		try {	
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
						StoreKeyword sk = new StoreKeyword(store, key);
						ElevateResult addEl = new ElevateResult();
						addEl.setStoreKeyword(sk);
						daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
						daoCacheService.resetElevateRule(sk); // clear sk entry in cache
						daoCacheService.setForceReloadExclude(new Store(store));
						success = true;
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
			success = false;
		}
		return success;
	}

	@Override
	public Map<String,Boolean> unpublishElevateRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		
		try {	
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
						StoreKeyword sk = new StoreKeyword(store, key);
						ElevateResult addEl = new ElevateResult();
						addEl.setStoreKeyword(sk);
						daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
						daoCacheService.resetElevateRule(sk); // clear sk entry in cache
						daoCacheService.setForceReloadExclude(new Store(store));
						map.put(key, true);
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return map;
	}
	
	@Override
	public boolean unpublishExcludeRules(String store, List<String> list) {
		boolean success = false;
		
		try {	
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
						StoreKeyword sk = new StoreKeyword(store, key);
						ExcludeResult addEx = new ExcludeResult();
						addEx.setStoreKeyword(sk);
						daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
						daoCacheService.resetExcludeRule(sk); // clear sk entry in cache
						daoCacheService.setForceReloadExclude(new Store(store));
						success = true;
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
			success = false;
		}
		return success;
	}
	
	@Override
	public Map<String,Boolean> unpublishExcludeRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		
		try {	
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
						StoreKeyword sk = new StoreKeyword(store, key);
						ExcludeResult addEx = new ExcludeResult();
						addEx.setStoreKeyword(sk);
						daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
						daoCacheService.resetExcludeRule(sk); // clear sk entry in cache
						daoCacheService.setForceReloadExclude(new Store(store));
						map.put(key, true);
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return map;
	}
	
	@Override
	public boolean unpublishRedirectRules(String store, List<String> list) {
		boolean success = false;

		try {	
			if(CollectionUtils.isNotEmpty(list)){
				Store s = new Store(store);
				for(String key : list){
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
					success = true;
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
			success = false;
		}
		return success;
	}
	
	@Override
	public Map<String,Boolean> unpublishRedirectRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);

		try {	
			if(CollectionUtils.isNotEmpty(list)){
				Store s = new Store(store);
				for(String key : list){
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
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return map;
	}

	@Override
	public boolean unpublishRankingRules(String store, List<String> list) {
		boolean success = false;
		try {	
			Store s = new Store(store);
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
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
					success = true;
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
			success = false;
		}
		return success;
	}
	
	@Override
	public Map<String,Boolean> unpublishRankingRulesMap(String store, List<String> list) {
		Map<String,Boolean> map = getKLMap(list);
		try {	
			Store s = new Store(store);
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
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
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
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
}
