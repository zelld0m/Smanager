package com.search.thread;

import org.apache.log4j.Logger;

import com.search.manager.enums.RuleEntity;

public class LoadStoreRuleThread extends Thread{
	
	private static final Logger logger = Logger.getLogger(LoadStoreRuleThread.class);
	private String store;
	
	public LoadStoreRuleThread(String store){
		this.store=store;
	}
	
	@Override
	public void run() {
		try {	
			LoadRuleThread th1 = new LoadRuleThread(this.store, RuleEntity.ELEVATE);
			LoadRuleThread th2 = new LoadRuleThread(this.store, RuleEntity.EXCLUDE);
			LoadRuleThread th3 = new LoadRuleThread(this.store, RuleEntity.QUERY_CLEANING);
			LoadRuleThread th4 = new LoadRuleThread(this.store, RuleEntity.RANKING_RULE);
			LoadRuleThread th5 = new LoadRuleThread(this.store, RuleEntity.DEMOTE);
			LoadRuleThread th6 = new LoadRuleThread(this.store, RuleEntity.FACET_SORT);
			
			th1.start();
			th2.start();
			th3.start();
			th4.start();
			th5.start();
			th6.start();
			
			while(true){
				if(!th1.isAlive() && !th2.isAlive() && !th3.isAlive() && !th4.isAlive()){
					logger.info("########## Rules successfully loaded to cache for store "+this.store+"...");	
					break;
				}
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
	}
}
