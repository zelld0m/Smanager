package com.search.thread;

import org.apache.log4j.Logger;
import com.search.manager.enums.RuleEntity;
import com.search.service.DeploymentRuleService;
import com.search.service.DeploymentRuleServiceImpl;

public class LoadRuleThread extends Thread{
	
	private static final Logger logger = Logger.getLogger(LoadRuleThread.class);
	private String store;
	private RuleEntity entity;
	private static DeploymentRuleService deploymentRuleService;
	
	static{
		try {
			deploymentRuleService = new DeploymentRuleServiceImpl();
		} catch (Exception e) {
			logger.error(e,e);
		}
	}
	
	public LoadRuleThread(String store, RuleEntity entity){
		this.store=store;
		this.entity=entity;
	}
	
	@Override
	public void run() {
		try {	
			switch (this.entity) {
			case ELEVATE:	
				deploymentRuleService.loadElevateRules(store);
				break;
			case EXCLUDE:	
				deploymentRuleService.loadExcludeRules(store);
				break;
			case KEYWORD: 
				break;
			case STORE_KEYWORD: 
				break;
			case CAMPAIGN:
				break;
			case BANNER:
				break;
			case QUERY_CLEANING:
				deploymentRuleService.loadRedirectRules(store);
				break;
			case RANKING_RULE:
				deploymentRuleService.loadRankingRules(store);
				break;
			default:
				break;
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
}
