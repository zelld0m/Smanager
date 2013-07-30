package com.search.manager.cron;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.search.manager.solr.util.SolrServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrResetServerCronSingle extends QuartzJobBean {

    private static final Logger logger =
            LoggerFactory.getLogger(SolrResetServerCronSingle.class);

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
            logger.error("Error at SolrResetServerCronSingle.executeInternal()", e);
        }
    }
}
