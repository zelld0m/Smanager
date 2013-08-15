package com.mall.migrator;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.search.manager.dao.DaoService;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrServerFactory;
import com.search.ws.ConfigManager;

public class BaseRuleBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(BaseRuleBuilder.class);

	protected static ApplicationContext context;
	protected static SolrServerFactory solrServerFactory;
	protected LocalSolrServerRunner solrServer;
	protected Properties properties;
	protected DaoService daoService;
	protected String storeId;
	protected String logPath;
	protected String logIndex;
	protected String logErrorIndex;
	protected String mailNotification;
	protected final int MAX_IMPORT_DOC = 1000;

	public BaseRuleBuilder() {
		try {
			FileInputStream inStream = new FileInputStream(
					"resources/dataImport.properties");
			properties = new Properties(System.getProperties());
			properties.load(inStream);
			context = new FileSystemXmlApplicationContext(
					"resources/spring/search-proxy-context.xml");

			solrServerFactory = (SolrServerFactory) context
					.getBean("solrServerFactory");

			if (solrServerFactory == null) {
				logger.info("SolrServerFactory is null.");
				return;
			}

			daoService = (DaoService) context.getBean("daoService");

			if (daoService == null) {
				logger.info("DaoService is null.");
				return;
			}

			init();
		} catch (Exception e) {
			logger.error("Error in BaseRuleBuiler() : " + e.getMessage(), e);
			return;
		}
	}

	public BaseRuleBuilder(ApplicationContext context_,
			SolrServerFactory solrServerFactory_, Properties properties_,
			String storeId_, String core_) {
		context = context_;
		solrServerFactory = solrServerFactory_;
		properties = properties_;
		storeId = storeId_;
		daoService = (DaoService) context.getBean("daoService");

		if (daoService == null) {
			logger.info("DaoService is null.");
			return;
		}

		try {
			solrServer = solrServerFactory.getCoreInstance(core_);
		} catch (SolrServerException e) {
			logger.error(e.getMessage(), e);
		}

		if (solrServer == null) {
			logger.info("SolrServer is null.");
			return;
		}

		init();
	}

	public void init() {
		ConfigManager.getInstance(properties.getProperty("solrXml"));
		logPath = properties.getProperty("logPath");
		logIndex = properties.getProperty("logIndex");
		logErrorIndex = properties.getProperty("logErrorIndex");
		mailNotification = properties.getProperty("mail.notification");
	}

	public SolrServerFactory solrServerFactory() {
		return solrServerFactory;
	}

}