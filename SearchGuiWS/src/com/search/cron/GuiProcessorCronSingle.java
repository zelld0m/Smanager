package com.search.cron;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.search.manager.utility.PropsUtils;
import com.search.webservice.SearchGuiService;
import com.search.webservice.SearchGuiServiceImpl;

public class GuiProcessorCronSingle extends QuartzJobBean{

	protected final transient Logger logger = Logger.getLogger(this.getClass());

	private static SearchGuiService searchGuiService;
	
	private static String token;
	private static String store;
	
	static{
		try {
			token = PropsUtils.getValue("token");
			store = PropsUtils.getValue("store");
			searchGuiService = new SearchGuiServiceImpl();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		try {
			synchronized (this) {	
				logger.info("########### Start loading to cache");
				
//				if(searchGuiService.loadElevateList(store, token)){
//					if(searchGuiService.loadExcludeList(store, token)){
//						if(searchGuiService.loadRelevancyList(store, token)){
//							searchGuiService.loadRelevancyDetails(store, token);
//						}
//					}
//				}

				logger.info("########### Done loading to cache");
			}
		}catch (Exception e) {
			logger.error(e);
		}
	}
}
