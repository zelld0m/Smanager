package com.search.cron;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.search.manager.utility.PropsUtils;
import com.search.thread.LoadStoreRuleThread;

public class GuiProcessorCronSingle extends QuartzJobBean{

	private static String[] stores;
	
	static{
		stores = PropsUtils.getValue("store").split(",");
	}

	protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
		for(String store : stores){
			new LoadStoreRuleThread(store).start();
		}
	}
}
