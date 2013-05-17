package com.mall.migrator;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.mall.mail.MailNotifier;
import com.search.manager.dao.DaoService;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RecordSet;
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

	private final int MAX_IMPORT_DOC = 1000;
	private int dbCount;
	private int docCount;

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
			dbCount = 0;
			docCount = 0;
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
			StoreKeyword storeKeyword = new StoreKeyword(storeId, null);
			int page = 1;

			if (rule.equals(Constants.Rule.DEMOTE.getRuleName())) {
				DemoteResult demoteFilter = new DemoteResult();
				demoteFilter.setStoreKeyword(storeKeyword);
				while (true) {
					SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
							demoteFilter, null, null, page, MAX_IMPORT_DOC);
					RecordSet<DemoteResult> recordSet = daoService
							.getDemoteResultListNew(criteria);
					if (recordSet != null && recordSet.getTotalSize() > 0) {
						List<DemoteResult> demoteResults = recordSet.getList();

						solrImportDemote(demoteResults);
						if (demoteResults.size() < MAX_IMPORT_DOC) {
							solrServer.optimize();
							break;
						}
						page++;
					} else {
						break;
					}
				}
			} else if (rule.equals(Constants.Rule.ELEVATE.getRuleName())) {
				ElevateResult elevateFilter = new ElevateResult();
				elevateFilter.setStoreKeyword(storeKeyword);
				while (true) {
					SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(
							elevateFilter, null, null, page, MAX_IMPORT_DOC);
					RecordSet<ElevateResult> recordSet = daoService
							.getElevateResultListNew(criteria);
					if (recordSet != null && recordSet.getTotalSize() > 0) {
						List<ElevateResult> elevateResults = recordSet
								.getList();
						solrImportElevate(elevateResults);
						if (elevateResults.size() < MAX_IMPORT_DOC) {
							solrServer.optimize();
							break;
						}
						page++;
					} else {
						break;
					}
				}
			} else if (rule.equals(Constants.Rule.EXCLUDE.getRuleName())) {
				ExcludeResult excludeFilter = new ExcludeResult();
				excludeFilter.setStoreKeyword(storeKeyword);
				while (true) {
					SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(
							excludeFilter, null, null, 0, 0);
					RecordSet<ExcludeResult> recordSet = daoService
							.getExcludeResultListNew(criteria);
					if (recordSet != null && recordSet.getTotalSize() > 0) {
						List<ExcludeResult> excludeResults = recordSet
								.getList();
						solrImportExclude(excludeResults);
						if (excludeResults.size() < MAX_IMPORT_DOC) {
							solrServer.optimize();
							break;
						}
						page++;
					} else {
						break;
					}
				}
			}

			long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
			StringBuffer info = new StringBuffer();
			info.append(" Indexing completed!");
			info.append("\n Time Completed : " + (elapsedTimeMillis / (1000F))
					+ " secs./");
			info.append((elapsedTimeMillis / (60 * 1000F)) + " mins.");
			info.append("\n Total " + rule + " rule indexed : " + docCount);
			info.append("\n Total " + rule + " rule fetched from database : "
					+ dbCount);
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

	private void solrImportDemote(List<DemoteResult> demoteResults) {
		List<SolrInputDocument> solrInputDocuments = null;
		boolean hasError = false;

		try {
			dbCount += demoteResults.size();
			solrInputDocuments = SolrDocUtil.composeSolrDocs(demoteResults);
			solrServer.addDocs(solrInputDocuments);
			solrServer.softCommit();
			docCount += solrInputDocuments.size();
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
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, rule + "_");
				}
			}
		}
	}

	private void solrImportElevate(List<ElevateResult> elevateResults) {
		List<SolrInputDocument> solrInputDocuments = null;
		boolean hasError = false;

		try {
			dbCount += elevateResults.size();
			solrInputDocuments = SolrDocUtil.composeSolrDocs(elevateResults);
			solrServer.addDocs(solrInputDocuments);
			solrServer.softCommit();
			docCount += solrInputDocuments.size();
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
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, rule + "_");
				}
			}
		}
	}

	private void solrImportExclude(List<ExcludeResult> excludeResults) {
		List<SolrInputDocument> solrInputDocuments = null;
		boolean hasError = false;

		try {
			dbCount += excludeResults.size();
			solrInputDocuments = SolrDocUtil.composeSolrDocs(excludeResults);
			solrServer.addDocs(solrInputDocuments);
			solrServer.softCommit();
			docCount += solrInputDocuments.size();
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
			System.out.println("----------------------------------------");
			System.out.println("Store	  : " + storeId);
			System.out.println("Rule type : " + ruleType);
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