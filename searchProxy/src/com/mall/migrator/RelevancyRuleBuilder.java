package com.mall.migrator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.xml.sax.SAXException;

import com.mall.mail.MailNotifier;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

public class RelevancyRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RelevancyRuleBuilder.class);

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

	private final int MAX_IMPORT_DOC = 1000;
	private int dbCount;
	private int relevancyRuleCount;
	private int relevancyRuleCountDefault;

	RelevancyRuleBuilder(String storeId, LocalSolrServerRunner solrServer,
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
			Store store = new Store(storeId);
			long timeStart = System.currentTimeMillis();

			Relevancy relevancyFilter = new Relevancy();
			relevancyFilter.setStore(store);
			relevancyFilter.setRelevancyName(""); // ALL

			long indexTime = System.currentTimeMillis();

			int page = 1;

			while (true) {
				SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(
						relevancyFilter, null, null, page, MAX_IMPORT_DOC);

				RecordSet<Relevancy> recordSet = daoService.searchRelevancy(
						criteria, MatchType.LIKE_NAME);

				if (recordSet != null) {
					List<Relevancy> relevancies = new ArrayList<Relevancy>();
					for (Relevancy relevancy : recordSet.getList()) {
						relevancy = daoService.getRelevancyDetails(relevancy);
						// setRelKeyword
						relevancy.setRelKeyword(daoService
								.getRelevancyKeywords(relevancy).getList());
						relevancies.add(relevancy);
					}
					dbCount += relevancies.size();
					solrImport(relevancies);

					if (relevancies.size() < MAX_IMPORT_DOC) {
						solrServer.commit();
						break;
					}
					page++;
				} else {
					break;
				}
			}

			// import default Relevancy
			solrImport(store);
			long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
			long elapsedIndexTime = System.currentTimeMillis() - indexTime;

			StringBuffer info = new StringBuffer();
			info.append(" Indexing completed!");
			info.append("\n Time Completed (Sec): "
					+ (elapsedTimeMillis / (1000F)) + " secs./");
			info.append((elapsedTimeMillis / (60 * 1000F)) + " mins.");
			info.append("\n Index Time (Sec): " + (elapsedIndexTime / (1000F))
					+ " secs./");
			info.append((elapsedIndexTime / (60 * 1000F)) + " mins.");
			info.append("\n Total Relevancy rule indexed : ");
			info.append(relevancyRuleCount);
			info.append("\n Total Relevancy fetched from database : ");
			info.append(dbCount);
			info.append("\n Total Default Relevancy rule indexed : ");
			info.append(relevancyRuleCountDefault);
			logger.info(info.toString());
			if (mailNotification.equals("true")) {
				MailNotifier mailNotifier = new MailNotifier(
						"Relevancy Rule Builder -[" + storeId + "]",
						info.toString(), properties);
				mailNotifier.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void solrImport(List<Relevancy> relevancies)
			throws ParserConfigurationException, IOException, SAXException {
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
		boolean hasError = false;

		try {
			solrInputDocuments = SolrDocUtil
					.composeSolrDocsRelevancy(relevancies);

			if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
				// Add rules to solr index.
				solrServer.addDocs(solrInputDocuments);
				solrServer.optimize();
				relevancyRuleCount += solrInputDocuments.size();
			}

		} catch (Exception e) {
			hasError = true;
			logger.error(e);
		}

		if (solrInputDocuments.size() > 0) {

			if (!hasError) {
				if (logIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath, solrInputDocuments,
							"relevancyRule_");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, "relevancyRule_");
				}
			}
		}

	}

	private void solrImport(Store store) throws ParserConfigurationException,
			IOException, SAXException {
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
		Relevancy relevancyDefault = null;
		boolean hasError = false;

		logger.info("store [" + store + "]");

		try {
			relevancyDefault = new Relevancy();
			relevancyDefault.setRelevancyId(store.getStoreId() + "_default");
			relevancyDefault = daoService.getRelevancyDetails(relevancyDefault);

			if (relevancyDefault != null
					&& relevancyDefault.getRelevancyId() != null) {
				solrInputDocuments.add(SolrDocUtil
						.composeSolrDoc(relevancyDefault));
			}

			if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
				// Add rules to solr index.
				solrServer.addDocs(solrInputDocuments);
				solrServer.optimize();
				relevancyRuleCountDefault += solrInputDocuments.size();
			}

		} catch (Exception e) {
			hasError = true;
			e.printStackTrace();
			logger.error(e);
		}

		if (solrInputDocuments.size() > 0) {
			if (!hasError) {
				if (logIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath, solrInputDocuments,
							"defaultRelevancyRule_");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, "defaultRelevancyRule_");
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

			solrServer = solrServerFactory
					.getCoreInstance(Constants.Core.RELEVANCY_RULE_CORE
							.getCoreName());
			RelevancyRuleBuilder relevancyRuleBuilder = new RelevancyRuleBuilder(
					storeId, solrServer, properties, context);
			relevancyRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			solrServerFactory.shutdown();
		}
	}

}
