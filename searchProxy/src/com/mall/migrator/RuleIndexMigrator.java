package com.mall.migrator;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrServerFactory;

public class RuleIndexMigrator {

	private static final Logger logger = Logger
			.getLogger(RuleIndexMigrator.class);

	public static void main(String[] args) {
		SolrServerFactory solrServerFactory;
		ApplicationContext context;
		Properties properties;
		String storeId;

		try {
			FileInputStream inStream = new FileInputStream(
					"config/dataImport.properties");
			properties = new Properties(System.getProperties());
			properties.load(inStream);
			PropertyConfigurator.configure("config/log4j.properties");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (args.length > 0) {
			storeId = args[0];
		} else {
			logger.debug("Store is null.");
			return;
		}

		context = new FileSystemXmlApplicationContext(
				"/WebContent/WEB-INF/spring/search-proxy-context.xml");

		solrServerFactory = (SolrServerFactory) context
				.getBean("solrServerFactory");

		if (solrServerFactory == null) {
			logger.debug("SolrServerFactory is null.");
			return;
		}

		List<String> cores = solrServerFactory.getCores();

		if (cores.size() == 0) {
			logger.debug("No core define.");
			return;
		} else {
			logger.info("cores: ");
			for (String core : cores) {
				logger.info(core);
			}
		}

		LocalSolrServerRunner redirectSolrServer;
		LocalSolrServerRunner relevancySolrServer;
		LocalSolrServerRunner facetSortSolrServer;
		LocalSolrServerRunner demoteSolrServer;
		LocalSolrServerRunner elevateSolrServer;
		LocalSolrServerRunner excludeSolrServer;

		try {
			redirectSolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.REDIRECT_RULE_CORE
							.getCoreName());
			relevancySolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.RELEVANCY_RULE_CORE
							.getCoreName());
			facetSortSolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.FACET_SORT_RULE_CORE
							.getCoreName());
			demoteSolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.DEMOTE_RULE_CORE
							.getCoreName());
			elevateSolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.ELEVATE_RULE_CORE
							.getCoreName());
			excludeSolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.EXCLUDE_RULE_CORE
							.getCoreName());
		} catch (SolrServerException e) {
			logger.error(e);
			return;
		}

		// Check Solr servers
		try {
			boolean pingable = true;
			if (redirectSolrServer.ping() == 1) {
				logger.debug("redirectSolrServer is not pingable.");
				pingable = false;
			}
			if (relevancySolrServer.ping() == 1) {
				logger.debug("relevancySolrServer is not pingable.");
				pingable = false;
			}
			if (facetSortSolrServer.ping() == 1) {
				logger.debug("facetSortSolrServer is not pingable.");
				pingable = false;
			}
			if (demoteSolrServer.ping() == 1) {
				logger.debug("demoteSolrServer is not pingable.");
				pingable = false;
			}
			if (elevateSolrServer.ping() == 1) {
				logger.debug("elevateSolrServer is not pingable.");
				pingable = false;
			}
			if (excludeSolrServer.ping() == 1) {
				logger.debug("excludeSolrServer is not pingable.");
				pingable = false;
			}
			if (!pingable) {
				return;
			}
		} catch (Exception e) {
			logger.error(e);
			return;
		}

		RedirectRuleBuilder redirectRuleBuilder = new RedirectRuleBuilder(
				storeId, redirectSolrServer, properties, context);
		RelevancyRuleBuilder relevancyRuleBuilder = new RelevancyRuleBuilder(
				storeId, relevancySolrServer, properties, context);
		FacetSortRuleBuilder facetSortRuleBuilder = new FacetSortRuleBuilder(
				storeId, facetSortSolrServer, properties, context);
		RuleIndexBuilder demoteRuleIndexBuilder = new RuleIndexBuilder(storeId,
				demoteSolrServer, properties, context,
				Constants.Rule.DEMOTE.getRuleName());
		RuleIndexBuilder elevateRuleIndexBuilder = new RuleIndexBuilder(
				storeId, elevateSolrServer, properties, context,
				Constants.Rule.ELEVATE.getRuleName());
		RuleIndexBuilder excludeRuleIndexBuilder = new RuleIndexBuilder(
				storeId, excludeSolrServer, properties, context,
				Constants.Rule.EXCLUDE.getRuleName());

		Thread redirectRuleBuilderThread = new Thread(redirectRuleBuilder);
		Thread relevancyRuleBuilderThread = new Thread(relevancyRuleBuilder);
		Thread facetSortRuleBuilderThread = new Thread(facetSortRuleBuilder);
		Thread demoteRuleBuilderThread = new Thread(demoteRuleIndexBuilder);
		Thread elevateRuleBuilderThread = new Thread(elevateRuleIndexBuilder);
		Thread excludeRuleBuilderThread = new Thread(excludeRuleIndexBuilder);

		long timeStart = System.currentTimeMillis();
		try {
			redirectRuleBuilderThread.start();
			relevancyRuleBuilderThread.start();
			facetSortRuleBuilderThread.start();
			demoteRuleBuilderThread.start();
			elevateRuleBuilderThread.start();
			excludeRuleBuilderThread.start();

			redirectRuleBuilderThread.join();
			relevancyRuleBuilderThread.join();
			facetSortRuleBuilderThread.join();
			demoteRuleBuilderThread.join();
			elevateRuleBuilderThread.join();
			excludeRuleBuilderThread.join();
		} catch (InterruptedException e) {
			logger.error(e);
		} finally {
			logger.info("Shutting down solr servers...");
			solrServerFactory.shutdown();
		}
		long elapsedTimeMillis = System.currentTimeMillis() - timeStart;

		logger.info("=================================================");
		logger.info(" Done indexing " + storeId + " rules in "
				+ (elapsedTimeMillis / (1000F)) + " sec./"
				+ (elapsedTimeMillis / (60 * 1000F)) + " mins.");
		logger.info("=================================================");
	}

}
