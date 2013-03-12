package com.mall.migrator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.xml.sax.SAXException;

import com.mall.mail.MailNotifier;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

public class RedirectRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RedirectRuleBuilder.class);

	private static SolrServerFactory solrServerFactory;

	private ApplicationContext context;
	private LocalSolrServerRunner solrServer;
	private Properties properties;
	private DaoService daoService;
	private String storeId;

	private String logPath;
	private String logIndex;
	private String logErrorIndex;
	private String mailNotification;

	private int count;
	private int redirectRuleCount;

	RedirectRuleBuilder(String storeId, LocalSolrServerRunner solrServer,
			Properties properties, ApplicationContext context) {
		this.storeId = storeId;
		this.solrServer = solrServer;
		this.properties = properties;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			logPath = properties.getProperty("logPath");
			logIndex = properties.getProperty("logIndex");
			logErrorIndex = properties.getProperty("logErrorIndex");
			mailNotification = properties.getProperty("mail.notification");
			PropertyConfigurator.configure("config/log4j.properties");
			daoService = (DaoService) context.getBean("daoService");
		} catch (Exception e) {
			logger.error(e);
			return;
		}

		if (storeId == null) {
			logger.debug("storeId is null.");
			return;
		}

		if (solrServer == null) {
			logger.debug("SolrServer is null.");
			return;
		}

		try {
			long timeStart = System.currentTimeMillis();
			List<RedirectRule> redirectRules = null;

			RedirectRule redirectRuleFilter = new RedirectRule();
			redirectRuleFilter.setStoreId(storeId);
			redirectRuleFilter.setRuleName(""); // ALL
			SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
					redirectRuleFilter, null, null, 0, 0);

			RecordSet<RedirectRule> recordSet = daoService.searchRedirectRule(
					criteria, MatchType.LIKE_NAME);

			if (recordSet != null) {
				redirectRules = recordSet.getList();
			}

			long indexTime = System.currentTimeMillis();

			if (redirectRules != null) {
				count = redirectRules.size();
				solrImport(redirectRules);
			}

			long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
			long elapsedIndexTime = System.currentTimeMillis() - indexTime;

			StringBuffer info = new StringBuffer();
			info.append(" Indexing completed!");
			info.append("\n Time Completed (Sec): "
					+ (elapsedTimeMillis / (1000F)) + " secs.");
			info.append("\n Time Completed (Min): "
					+ (elapsedTimeMillis / (60 * 1000F)) + " mins.");
			info.append("\n Index Time (Sec): " + (elapsedIndexTime / (1000F))
					+ " secs.");
			info.append("\n Index Time (Min): "
					+ (elapsedIndexTime / (60 * 1000F)) + " mins.");
			info.append("\n Total Redirect rule indexed : " + redirectRuleCount);
			info.append("\n Total file fetched from database : " + count);
			logger.info(info.toString());
			if (mailNotification.equals("true")) {
				MailNotifier mailNotifier = new MailNotifier(
						"Redirect Rule Builder -[" + storeId + "]",
						info.toString(), properties);
				mailNotifier.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void solrImport(List<RedirectRule> redirectRules)
			throws ParserConfigurationException, IOException, SAXException {
		List<SolrInputDocument> solrInputDocuments = null;
		boolean hasError = false;

		try {
			if (redirectRules != null && redirectRules.size() > 0) {
				solrInputDocuments = SolrDocUtil
						.composeSolrDocsRedirectRule(redirectRules);
				// Add rules to solr index.
				solrServer.addDocs(solrInputDocuments);
				solrServer.optimize();
				redirectRuleCount = solrInputDocuments.size();
			}
		} catch (Exception e) {
			hasError = true;
			logger.error(e);
		}

		if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
			if (!hasError) {
				if (logIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath, solrInputDocuments,
							"redirect_");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, "redirect_");
				}
			}
		}
	}

	public static void main(String[] args) {
		String storeId;
		LocalSolrServerRunner solrServer;
		Properties properties;
		ApplicationContext context;

		try {
			FileInputStream inStream = new FileInputStream(
					"./config/dataImport.properties");
			properties = new Properties(System.getProperties());
			properties.load(inStream);
			PropertyConfigurator.configure("config/log4j.properties");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
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

		try {
			solrServer = solrServerFactory
					.getCoreInstance(Constants.Core.REDIRECT_RULE_CORE
							.getCoreName());
			RedirectRuleBuilder redirectRuleBuilder = new RedirectRuleBuilder(
					storeId, solrServer, properties, context);
			redirectRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			solrServerFactory.shutdown();
		}
	}

}
