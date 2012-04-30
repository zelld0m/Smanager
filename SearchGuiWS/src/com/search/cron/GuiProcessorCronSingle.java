package com.search.cron;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.search.manager.enums.RuleEntity;
import com.search.manager.utility.PropsUtils;
import com.search.thread.LoadRuleThread;

public class GuiProcessorCronSingle extends QuartzJobBean{

	private static final Logger logger = Logger.getLogger(GuiProcessorCronSingle.class);
	private static String store;
	
	static{
		try {
			store = PropsUtils.getValue("store");
		} catch (Exception e) {
			logger.error(e,e);
		}
	}

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		try {
			LoadRuleThread th1 = new LoadRuleThread(store, RuleEntity.ELEVATE);
			LoadRuleThread th2 = new LoadRuleThread(store, RuleEntity.EXCLUDE);
			LoadRuleThread th3 = new LoadRuleThread(store, RuleEntity.QUERY_CLEANING);
			LoadRuleThread th4 = new LoadRuleThread(store, RuleEntity.RANKING_RULE);
			
			th1.start();
			th2.start();
			th3.start();
			th4.start();
			
			while(true){
				if(!th1.isAlive() && !th2.isAlive() && !th3.isAlive() && !th4.isAlive()){
					logger.info("########## Rules successfully loaded to cache ...");	
					break;
				}
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
}
