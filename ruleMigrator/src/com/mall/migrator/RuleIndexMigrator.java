package com.mall.migrator;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrServerFactory;

public class RuleIndexMigrator {

	private static final Logger logger = LoggerFactory
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
			// PropertyConfigurator.configure("config/log4j.properties");
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
				"resources/spring/search-proxy-context.xml");

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
		LocalSolrServerRunner bannerSolrServer;

		try {
			System.out.println("----------------------------------------");
			System.out.println("Store	  : " + storeId);
			System.out.println("Solr Url  : "
					+ ((LocalSolrServerRunner) context
							.getBean("localSolrServerRunner")).getSolrUrl());
			System.out.println("Database  : "
					+ ((BasicDataSource) context.getBean("dataSource_solr"))
							.getUrl());
			System.out.println("----------------------------------------");
			String response = "";
			Scanner input = new Scanner(System.in);

			System.out.print("Are you sure you want to continue? (Y/N) : ");
			response = input.next();

			if (!response.toUpperCase().startsWith("Y")) {
				solrServerFactory.shutdown();
				return;
			}

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
			bannerSolrServer = solrServerFactory
					.getCoreInstance(Constants.Core.BANNER_RULE_CORE
							.getCoreName());
		} catch (SolrServerException e) {
			logger.error(e.getMessage(), e);
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
			if (bannerSolrServer.ping() == 1) {
				logger.debug("bannerSolrServer is not pingable.");
				pingable = false;
			}
			if (!pingable) {
				return;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		}

		RedirectRuleBuilder redirectRuleBuilder;
		FacetSortRuleBuilder facetSortRuleBuilder;
		RuleIndexBuilder demoteRuleIndexBuilder;
		RuleIndexBuilder elevateRuleIndexBuilder;
		RuleIndexBuilder excludeRuleIndexBuilder;
		RelevancyRuleBuilder relevancyRuleBuilder;
		BannerRuleBuilder bannerRuleBuilder;

		try {
			redirectRuleBuilder = new RedirectRuleBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.REDIRECT_RULE_CORE.getCoreName());
			facetSortRuleBuilder = new FacetSortRuleBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.FACET_SORT_RULE_CORE.getCoreName());
			relevancyRuleBuilder = new RelevancyRuleBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.RELEVANCY_RULE_CORE.getCoreName());
			demoteRuleIndexBuilder = new RuleIndexBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.DEMOTE_RULE_CORE.getCoreName());
			elevateRuleIndexBuilder = new RuleIndexBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.ELEVATE_RULE_CORE.getCoreName());
			excludeRuleIndexBuilder = new RuleIndexBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.EXCLUDE_RULE_CORE.getCoreName());
			bannerRuleBuilder = new BannerRuleBuilder(context,
					solrServerFactory, properties, storeId,
					Constants.Core.BANNER_RULE_CORE.getCoreName());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return;
		}

		Thread redirectRuleBuilderThread = new Thread(redirectRuleBuilder);
		Thread relevancyRuleBuilderThread = new Thread(relevancyRuleBuilder);
		Thread facetSortRuleBuilderThread = new Thread(facetSortRuleBuilder);
		Thread demoteRuleBuilderThread = new Thread(demoteRuleIndexBuilder);
		Thread elevateRuleBuilderThread = new Thread(elevateRuleIndexBuilder);
		Thread excludeRuleBuilderThread = new Thread(excludeRuleIndexBuilder);
		Thread bannerRuleBuilderThread = new Thread(bannerRuleBuilder);

		long timeStart = System.currentTimeMillis();
		try {
			redirectRuleBuilderThread.start();
			relevancyRuleBuilderThread.start();
			facetSortRuleBuilderThread.start();
			demoteRuleBuilderThread.start();
			elevateRuleBuilderThread.start();
			excludeRuleBuilderThread.start();
			bannerRuleBuilderThread.start();

			redirectRuleBuilderThread.join();
			relevancyRuleBuilderThread.join();
			facetSortRuleBuilderThread.join();
			demoteRuleBuilderThread.join();
			elevateRuleBuilderThread.join();
			excludeRuleBuilderThread.join();
			bannerRuleBuilderThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
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
