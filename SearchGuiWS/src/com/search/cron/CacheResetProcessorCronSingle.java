package com.search.cron;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.search.manager.cache.ehcache.CacheClient;

public class CacheResetProcessorCronSingle extends QuartzJobBean {

	private static final Logger logger = Logger
			.getLogger(CacheResetProcessorCronSingle.class);

	protected void executeInternal(JobExecutionContext ctx)
			throws JobExecutionException {
		try {
			CacheClient cacheClient = CacheClient.getInstance();
			cacheClient.resetAllNodeStatus();
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
}
