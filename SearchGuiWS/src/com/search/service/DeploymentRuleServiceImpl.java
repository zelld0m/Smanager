package com.search.service;

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
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
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
						if(daoCacheService.hasExactMatchKey(new StoreKeyword(store, key))){
							ElevateResult delEl = new ElevateResult();
							delEl.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
							
							// retrieve staging data then push to prod
							StoreKeyword sk = new StoreKeyword(store, key);
							elevateFilter.setStoreKeyword(sk);
							SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
							
							elevatedList = daoServiceStg.getElevateResultList(criteria).getList();
							
							if(elevatedList != null && elevatedList.size() > 0){
								for(ElevateResult e : elevatedList){
									daoService.addElevateResult((ElevateResult) e);
								}
							}
							
							// update cache data
							daoCacheService.updateElevateRule(delEl); // prod
							success = true;
						}
					}
			} catch (Exception e) {
				logger.error(e);
				success = false;
			}
			return success;
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
						if(daoCacheService.hasExactMatchKey(new StoreKeyword(store, key))){
							ExcludeResult delEl = new ExcludeResult();
							delEl.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
							
							// retrieve staging data then push to prod
							StoreKeyword sk = new StoreKeyword(store, key);
							excludeFilter.setStoreKeyword(sk);
							SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);
							
							excludeList = daoServiceStg.getExcludeResultList(criteria).getList();
							
							if(excludeList != null && excludeList.size() > 0){
								for(ExcludeResult e : excludeList){
									daoService.addExcludeResult((ExcludeResult) e);
								}
							}
							
							daoCacheService.updateExcludeRules(delEl); // prod
							success = true;
						}
					}
				return true;
			} catch (Exception e) {
				logger.error(e);
				success = false;
			}
		return success;
	}
	
	@Override
	public boolean publishRedirectRules(String store, List<String> list) {
		// TODO Auto-generated method stub
		return false;
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
						
						daoCacheService.updateRelevancyRule(delRel); // prod
						success = true;
					}
			} catch (Exception e) {
				logger.error(e);
				success = false;
			}
			return success;
	}


	@Override
	public boolean recallElevateRules(String store, List<String> list) {
		
		boolean success = false;
		
			try {	
				Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.ELEVATE);
				
				if(CollectionUtils.isNotEmpty(list)){
					for(String key : list){
						if(daoCacheService.hasExactMatchKey(new StoreKeyword(store, key))){
							ElevateResult addEl = new ElevateResult();
							addEl.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
							fileService.removeBackup(store, key, RuleEntity.ELEVATE);
							success = true;
							
							try{
								for(Object e : backUp.get(key)){	
									daoService.addElevateResult((ElevateResult) e);
								}
							}catch (Exception e) {}
							
							daoCacheService.updateElevateRule(addEl); // prod
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
				success = false;
			}
			return success;
	}
	
	@Override
	public boolean recallExcludeRules(String store, List<String> list) {
		
		boolean success = false;
		
			try {	
				Map<String,List<Object>> backUp = fileService.readBackup(store, list, RuleEntity.EXCLUDE);
				
				if(CollectionUtils.isNotEmpty(list)){
					for(String key : list){
						if(daoCacheService.hasExactMatchKey(new StoreKeyword(store, key))){
							ExcludeResult addEx = new ExcludeResult();
							addEx.setStoreKeyword(new StoreKeyword(store, key));
							daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
							fileService.removeBackup(store, key, RuleEntity.EXCLUDE);
							success = true;
							
							try{
								for(Object e : backUp.get(key)){	
									daoService.addExcludeResult((ExcludeResult) e);
								}
							}catch (Exception e) {}
							
							daoCacheService.updateExcludeRules(addEx); // prod
						}
					}
				}
			} catch (Exception e) {
				logger.error(e);
				success = false;
			}
			return success;
	}
	
	@Override
	public boolean recallRedirectRules(String store, List<String> list) {
		return false;
	}

	@Override
	public boolean recallRankingRules(String store, List<String> list) {
	
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

					}catch (Exception e) {}
					
					daoCacheService.updateRelevancyRule(delRel); // prod
				}
			}
		} catch (Exception e) {
			logger.error(e);
			success = false;
		}
		return success;
	}

	@Override
	public boolean loadElevateRules(String store) {
		try {
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
					if(daoCacheService.hasExactMatchKey(new StoreKeyword(store, key))){
						ElevateResult addEl = new ElevateResult();
						addEl.setStoreKeyword(new StoreKeyword(store, key));
						daoService.clearElevateResult(new StoreKeyword(store, key)); // prod
						daoCacheService.updateElevateRule(addEl); // prod
						success = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			success = false;
		}
		return success;
	}

	@Override
	public boolean unpublishExcludeRules(String store, List<String> list) {
		boolean success = false;
		
		try {	
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
					if(daoCacheService.hasExactMatchKey(new StoreKeyword(store, key))){
						ExcludeResult addEx = new ExcludeResult();
						addEx.setStoreKeyword(new StoreKeyword(store, key));
						daoService.clearExcludeResult(new StoreKeyword(store, key)); // prod
						daoCacheService.updateExcludeRules(addEx); // prod
						success = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			success = false;
		}
		return success;
	}
	
	@Override
	public boolean unpublishRedirectRules(String store, List<String> list) {
		return false;
	}

	@Override
	public boolean unpublishRankingRules(String store, List<String> list) {
		boolean success = false;

		try {	
			if(CollectionUtils.isNotEmpty(list)){
				for(String key : list){
					Relevancy delRel = new Relevancy();
					delRel.setRelevancyId(key);
					delRel.setStore(new Store(store));
					daoService.deleteRelevancy(delRel); // prod
					daoCacheService.updateRelevancyRule(delRel); // prod
					success = true;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			success = false;
		}
		return success;
	}
}
