package com.mall.migrator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.xml.sax.SAXException;

import com.mall.mail.MailNotifier;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

public class RuleIndexBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RuleIndexBuilder.class);

	private static SolrServerFactory solrServerFactory;

	private ApplicationContext context;
	private LocalSolrServerRunner solrServer;
	private Properties properties;
	private DaoService daoService;
	private String storeId;
	private String rule;

	private String logPath;
	private String logIndex;
	private String logErrorIndex;
	private String mailNotification;

	private int count;
	private int ruleCount;

	RuleIndexBuilder(String storeId, LocalSolrServerRunner solrServer,
			Properties properties, ApplicationContext context, String rule) {
		this.storeId = storeId;
		this.solrServer = solrServer;
		this.properties = properties;
		this.context = context;
		this.rule = rule;
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
			e.printStackTrace();
			return;
		}

		if (storeId == null) {
			logger.debug("storeId is null.");
			return;
		}

		if (daoService == null) {
			logger.debug("daoService is null.");
			return;
		}

		if (solrServer == null) {
			logger.debug("SolrServer is null.");
			return;
		}

		try {
			long timeStart = System.currentTimeMillis();

			List<String> keywords = new ArrayList<String>();
			List<Keyword> keywordList = null;

			if (rule.equals(Constants.Rule.DEMOTE.getRuleName())) {
				keywordList = (List<Keyword>) daoService.getAllKeywords(
						storeId, RuleEntity.DEMOTE);
			} else if (rule.equals(Constants.Rule.ELEVATE.getRuleName())) {
				keywordList = (List<Keyword>) daoService.getAllKeywords(
						storeId, RuleEntity.ELEVATE);
			} else if (rule.equals(Constants.Rule.EXCLUDE.getRuleName())) {
				keywordList = (List<Keyword>) daoService.getAllKeywords(
						storeId, RuleEntity.EXCLUDE);
			}

			if (CollectionUtils.isNotEmpty(keywordList)) {
				for (Keyword key : keywordList) {
					keywords.add(key.getKeywordId());
				}
			}

			if (keywords != null) {
				logger.info("Keyword size : " + keywords.size());

				for (String keyword : keywords) {
					solrImport(storeId, keyword, rule);
				}
			}

			solrServer.optimize();

			long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
			StringBuffer info = new StringBuffer();
			info.append(" Indexing completed!");
			info.append("\n Time Completed : " + (elapsedTimeMillis / (1000F))
					+ " secs.");
			info.append("\n Time Completed : "
					+ (elapsedTimeMillis / (60 * 1000F)) + " mins.");
			info.append("\n Total " + rule + " rule indexed : " + ruleCount);
			info.append("\n Total file fetched from database : " + count);
			logger.info(info.toString());
			if (mailNotification.equals("true")) {
				MailNotifier mailNotifier = new MailNotifier(
						"Rule Index Builder - [" + storeId + "]",
						info.toString(), properties);
				mailNotifier.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void solrImport(String store, String keyword, String rule)
			throws ParserConfigurationException, IOException, SAXException {
		List<SolrInputDocument> solrInputDocuments = null;
		List<DemoteResult> demoteResults = null;
		List<ElevateResult> elevateResults = null;
		List<ExcludeResult> excludeResults = null;
		StoreKeyword storeKeyword = new StoreKeyword(store, keyword);
		boolean hasError = false;

		logger.info(rule + " = store [" + store + "] " + "keyword[" + keyword
				+ "]");

		try {
			if (rule.equals(Constants.Rule.DEMOTE.getRuleName())) {
				// Get demote rules from database.
				DemoteResult demoteFilter = new DemoteResult();
				demoteFilter.setStoreKeyword(storeKeyword);

				SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
						demoteFilter, null, null, 0, 0);
				logger.info("Search Criteria = " + criteria.toString());
				demoteResults = daoService.getDemoteResultList(criteria)
						.getList();

				if (demoteResults != null && demoteResults.size() > 0) {
					count += demoteResults.size();
					solrInputDocuments = SolrDocUtil
							.composeSolrDocs(demoteResults);
					solrServer.addDocs(solrInputDocuments);
					solrServer.softCommit();
					ruleCount += solrInputDocuments.size();
				}
			} else if (rule.equals(Constants.Rule.ELEVATE.getRuleName())) {
				// Get elevate rules from database.
				ElevateResult elevateFilter = new ElevateResult();
				elevateFilter.setStoreKeyword(storeKeyword);

				SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
						elevateFilter, null, null, 0, 0);
				logger.info("Search Criteria = " + criteria.toString());
				elevateResults = daoService.getElevateResultList(criteria)
						.getList();

				if (elevateResults != null && elevateResults.size() > 0) {
					count += elevateResults.size();
					solrInputDocuments = SolrDocUtil
							.composeSolrDocs(elevateResults);
					solrServer.addDocs(solrInputDocuments);
					solrServer.softCommit();
					ruleCount += solrInputDocuments.size();
				}
			} else if (rule.equals(Constants.Rule.EXCLUDE.getRuleName())) {
				// Get exclude rules from database.
				ExcludeResult excludeFilter = new ExcludeResult();
				excludeFilter.setStoreKeyword(storeKeyword);

				SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
						excludeFilter, null, null, 0, 0);
				logger.info("Search Criteria = " + criteria.toString());
				excludeResults = daoService.getExcludeResultList(criteria)
						.getList();

				if (excludeResults != null && excludeResults.size() > 0) {
					count += excludeResults.size();
					solrInputDocuments = SolrDocUtil
							.composeSolrDocs(excludeResults);
					solrServer.addDocs(solrInputDocuments);
					solrServer.softCommit();
					ruleCount += solrInputDocuments.size();
				}
			}
		} catch (Exception e) {
			hasError = true;
			e.printStackTrace();
			logger.error(e);
		}

		if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
			if (!hasError) {
				if (logIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath, solrInputDocuments,
							rule + "_");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, rule + "_");
				}
			}
		}
	}

	public static void main(String[] args) {
		String storeId;
		String ruleType;
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
		if (args.length > 1) {
			ruleType = args[1];
		} else {
			logger.debug("Rule Type not specified.");
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
			if (ruleType.equals(Constants.Rule.DEMOTE.getRuleName())) {
				solrServer = solrServerFactory
						.getCoreInstance(Constants.Core.DEMOTE_RULE_CORE
								.getCoreName());
				RuleIndexBuilder demoteIndexBuilder = new RuleIndexBuilder(
						storeId, solrServer, properties, context,
						Constants.Rule.DEMOTE.getRuleName());
				demoteIndexBuilder.run();
			} else if (ruleType.equals(Constants.Rule.ELEVATE.getRuleName())) {
				solrServer = solrServerFactory
						.getCoreInstance(Constants.Core.ELEVATE_RULE_CORE
								.getCoreName());
				RuleIndexBuilder elevateIndexBuilder = new RuleIndexBuilder(
						storeId, solrServer, properties, context,
						Constants.Rule.ELEVATE.getRuleName());
				elevateIndexBuilder.run();
			} else if (ruleType.equals(Constants.Rule.EXCLUDE.getRuleName())) {
				solrServer = solrServerFactory
						.getCoreInstance(Constants.Core.EXCLUDE_RULE_CORE
								.getCoreName());
				RuleIndexBuilder excludeIndexBuilder = new RuleIndexBuilder(
						storeId, solrServer, properties, context,
						Constants.Rule.EXCLUDE.getRuleName());
				excludeIndexBuilder.run();
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			solrServerFactory.shutdown();
		}
	}

}