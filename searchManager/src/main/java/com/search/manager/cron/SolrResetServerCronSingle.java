package com.search.manager.cron;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.search.manager.solr.util.SolrServerFactory;

public class SolrResetServerCronSingle extends QuartzJobBean {

	private static final Logger logger = Logger
			.getLogger(SolrResetServerCronSingle.class);

	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		try {
			SchedulerContext skedCtx = arg0.getScheduler().getContext();
			SolrServerFactory solrServerFactory = (SolrServerFactory) skedCtx
					.get("solrServerFactory");
			if (solrServerFactory != null) {
				solrServerFactory.clearConnections();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
