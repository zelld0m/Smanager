package com.search.webservice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import com.search.manager.model.BackupInfo;
import com.search.manager.utility.PropsUtils;
import com.search.service.DeploymentRuleService;
import com.search.service.FileService;
import com.search.webservice.model.TransportList;

public class SearchGuiServiceImpl implements SearchGuiService{

	private static Logger logger = Logger.getLogger(SearchGuiServiceImpl.class);
	private static String token;
	private static final String RESOURCE_MAP = "token";
	
	private static DeploymentRuleService deploymentRuleService;
	private static FileService fileService;
	
	public void setDeploymentRuleService(DeploymentRuleService deploymentRuleService_) {
		deploymentRuleService = deploymentRuleService_;
	}
	
	public void setFileService(FileService fileService_) {
		fileService = fileService_;
	}

	static{
		try {
			token = PropsUtils.getValue(RESOURCE_MAP);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

	/*@Override
	public boolean deployRules(TransportList list) {
		
		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList)){
					
					switch (list.getRuleEntity()) {
					case ELEVATE:
						return deploymentRuleService.publishElevateRules(list.getStore(), ruleList);
					case EXCLUDE:
						return deploymentRuleService.publishExcludeRules(list.getStore(), ruleList);
					case DEMOTE:
						return deploymentRuleService.publishDemoteRules(list.getStore(), ruleList);
					case FACET_SORT:
						return deploymentRuleService.publishFacetSortRules(list.getStore(), ruleList);
					case KEYWORD: 
						break;
					case STORE_KEYWORD: 
						break;
					case CAMPAIGN:
						break;
					case BANNER:
						break;
					case QUERY_CLEANING:
						return deploymentRuleService.publishRedirectRules(list.getStore(), ruleList);
					case RANKING_RULE:
						return deploymentRuleService.publishRankingRules(list.getStore(), ruleList);
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return false;
	}
	
	@Override
	public boolean recallRules(TransportList list) {
		
		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList)){
					
					switch (list.getRuleEntity()) {
					case ELEVATE:
						return deploymentRuleService.recallElevateRules(list.getStore(), ruleList);
					case EXCLUDE:
						return deploymentRuleService.recallExcludeRules(list.getStore(), ruleList);
					case DEMOTE:
						return deploymentRuleService.recallDemoteRules(list.getStore(), ruleList);
					case FACET_SORT:
						return deploymentRuleService.recallFacetSortRules(list.getStore(), ruleList);
					case KEYWORD: 
						break;
					case STORE_KEYWORD: 
						break;
					case CAMPAIGN:
						break;
					case BANNER:
						break;
					case QUERY_CLEANING:
						return deploymentRuleService.recallRedirectRules(list.getStore(), ruleList);
					case RANKING_RULE:
						return deploymentRuleService.recallRankingRules(list.getStore(), ruleList);
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return false;
	}
	
	@Override
	public boolean unDeployRules(TransportList list) {
		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList)){
					
					switch (list.getRuleEntity()) {
					case ELEVATE:
						return deploymentRuleService.unpublishElevateRules(list.getStore(), ruleList);
					case EXCLUDE:
						return deploymentRuleService.unpublishExcludeRules(list.getStore(), ruleList);
					case DEMOTE:
						return deploymentRuleService.unpublishDemoteRules(list.getStore(), ruleList);
					case FACET_SORT:
						return deploymentRuleService.unpublishFacetSortRules(list.getStore(), ruleList);
					case KEYWORD: 
						break;
					case STORE_KEYWORD: 
						break;
					case CAMPAIGN:
						break;
					case BANNER:
						break;
					case QUERY_CLEANING:
						return deploymentRuleService.unpublishRedirectRules(list.getStore(), ruleList);
					case RANKING_RULE:
						return deploymentRuleService.unpublishRankingRules(list.getStore(), ruleList);
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return false;
	}*/
	
	@Override
	public Map<String,Boolean> deployRulesMap(TransportList list) {

		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList)){
					
					switch (list.getRuleEntity()) {
					case ELEVATE:
						return deploymentRuleService.publishElevateRulesMap(list.getStore(), ruleList);
					case EXCLUDE:
						return deploymentRuleService.publishExcludeRulesMap(list.getStore(), ruleList);
					case DEMOTE:
						return deploymentRuleService.publishDemoteRulesMap(list.getStore(), ruleList);
					case FACET_SORT:
						return deploymentRuleService.publishFacetSortRulesMap(list.getStore(), ruleList);
					case KEYWORD: 
						break;
					case STORE_KEYWORD: 
						break;
					case CAMPAIGN:
						break;
					case BANNER:
						break;
					case QUERY_CLEANING:
						return deploymentRuleService.publishRedirectRulesMap(list.getStore(), ruleList);
					case RANKING_RULE:
						return deploymentRuleService.publishRankingRulesMap(list.getStore(), ruleList);
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.EMPTY_MAP;
	}
	
	@Override
	public Map<String,Boolean> recallRulesMap(TransportList list) {
		
		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList)){
					
					switch (list.getRuleEntity()) {
					case ELEVATE:
						return deploymentRuleService.recallElevateRulesMap(list.getStore(), ruleList);
					case EXCLUDE:
						return deploymentRuleService.recallExcludeRulesMap(list.getStore(), ruleList);
					case DEMOTE:
						return deploymentRuleService.recallDemoteRulesMap(list.getStore(), ruleList);
					case FACET_SORT:
						return deploymentRuleService.recallFacetSortRulesMap(list.getStore(), ruleList);
					case KEYWORD: 
						break;
					case STORE_KEYWORD: 
						break;
					case CAMPAIGN:
						break;
					case BANNER:
						break;
					case QUERY_CLEANING:
						return deploymentRuleService.recallRedirectRulesMap(list.getStore(), ruleList);
					case RANKING_RULE:
						return deploymentRuleService.recallRankingRulesMap(list.getStore(), ruleList);
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.EMPTY_MAP;
	}
	
	@Override
	public Map<String,Boolean> unDeployRulesMap(TransportList list) {
		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList)){
					
					switch (list.getRuleEntity()) {
					case ELEVATE:
						return deploymentRuleService.unpublishElevateRulesMap(list.getStore(), ruleList);
					case EXCLUDE:
						return deploymentRuleService.unpublishExcludeRulesMap(list.getStore(), ruleList);
					case DEMOTE:
						return deploymentRuleService.unpublishDemoteRulesMap(list.getStore(), ruleList);
					case FACET_SORT:
						return deploymentRuleService.unpublishFacetSortRulesMap(list.getStore(), ruleList);
					case KEYWORD: 
						break;
					case STORE_KEYWORD: 
						break;
					case CAMPAIGN:
						break;
					case BANNER:
						break;
					case QUERY_CLEANING:
						return deploymentRuleService.unpublishRedirectRulesMap(list.getStore(), ruleList);
					case RANKING_RULE:
						return deploymentRuleService.unpublishRankingRulesMap(list.getStore(), ruleList);
					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.EMPTY_MAP;
	}
	
	@Override
	public List<BackupInfo> getBackupInfo(TransportList list){
		
		try {
			if(isValidToken(list.getToken())){
				List<String> ruleList = list.getList();
				
				if(CollectionUtils.isNotEmpty(ruleList))
					return fileService.getBackupInfo(list.getStore(), list.getList(), list.getRuleEntity());
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.EMPTY_LIST;
	}
	
	private boolean isValidToken(String token_){	
		try{	
			if(token.equals(token_)){
				logger.info("User has valid token ... ");
				return true;
			}else{
				logger.info("User has invalid token ... ");
				return false;
			}
		}catch(Exception e){
			logger.error(e,e);
		}
		return false;
	}
}
