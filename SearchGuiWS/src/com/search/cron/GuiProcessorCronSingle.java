package com.search.cron;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.search.manager.utility.PropsUtils;
import com.search.service.DeploymentRuleService;
import com.search.service.DeploymentRuleServiceImpl;

public class GuiProcessorCronSingle extends QuartzJobBean{

	private static final Logger logger = Logger.getLogger(GuiProcessorCronSingle.class);

	private static DeploymentRuleService deploymentRuleService;

	private static String store;
	
	static{
		try {
			store = PropsUtils.getValue("store");
			deploymentRuleService = new DeploymentRuleServiceImpl();
		} catch (Exception e) {
			logger.error(e,e);
		}
	}

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		try {
			synchronized (this) {	
				processElevateRules();
				processExcludeRules();
				//processRedirectRules();
				processRankingRules();
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
	
	public Integer processElevateRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to elevated rules ...");
					deploymentRuleService.loadElevateRules(store);
					logger.info("########### Done loading to elevated rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
	
	public Integer processExcludeRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to excluded rules ...");
					deploymentRuleService.loadExcludeRules(store);
					logger.info("########### Done loading to excluded rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
	
	public Integer processRedirectRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to redirect rules ...");
					deploymentRuleService.loadRedirectRules(store);
					logger.info("########### Done loading to redirect rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
	
	public Integer processRankingRules(){
		new Thread() {
			@Override
			public void run() {
				try {
					logger.info("########### Start loading to ranking rules ...");
					deploymentRuleService.loadRankingRules(store);
					logger.info("########### Done loading to ranking rules ...");
				} catch (Exception e) {
					logger.error(e,e);
				}
			}
		}.start();
		
		return 0;
	}
}
