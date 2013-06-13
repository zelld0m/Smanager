package com.mall.migrator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;
import org.xml.sax.SAXException;

import com.mall.mail.MailNotifier;
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

public class RelevancyRuleBuilder extends BaseRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RelevancyRuleBuilder.class);

	private int dbCount;
	private int relevancyRuleCount;
	private int relevancyRuleCountDefault;

	@SuppressWarnings("unused")
	private RelevancyRuleBuilder() {
		// do nothing...
	}

	public RelevancyRuleBuilder(String storeId, String core)
			throws SolrServerException {
		this.storeId = storeId;
		solrServer = solrServerFactory.getCoreInstance(core);
	}

	public RelevancyRuleBuilder(ApplicationContext context,
			SolrServerFactory solrServerFactory, Properties properties,
			String store, String core) {
		super(context, solrServerFactory, properties, store, core);
	}

	@Override
	public void run() {
		try {
			Store store = new Store(storeId);
			Relevancy relevancyFilter = new Relevancy();
			relevancyFilter.setStore(store);
			relevancyFilter.setRelevancyName(""); // ALL
			long timeStart = System.currentTimeMillis();
			long indexTime = 0;
			int page = 1;

			while (true) {
				SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(
						relevancyFilter, page, MAX_IMPORT_DOC);

				RecordSet<Relevancy> recordSet = daoService.searchRelevancy(
						criteria, MatchType.LIKE_NAME);

				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<Relevancy> relevancies = new ArrayList<Relevancy>();
					logger.info("Indexing Page: [" + page + "] Size: ["
							+ relevancies.size() + "]");
					for (Relevancy relevancy : recordSet.getList()) {
						logger.info("Getting relevancy details for ["
								+ relevancy.getRuleName() + "]");
						relevancy = daoService.getRelevancyDetails(relevancy);
						// setRelKeyword
						relevancy.setRelKeyword(daoService
								.getRelevancyKeywords(relevancy).getList());
						relevancies.add(relevancy);
					}
					dbCount += relevancies.size();
					solrImport(relevancies);

					if (relevancies.size() < MAX_IMPORT_DOC) {
						indexTime = System.currentTimeMillis();
						solrServer.commit();
						break;
					}
					page++;
				} else {
					if (page != 1) {
						indexTime = System.currentTimeMillis();
						solrServer.commit();
					}
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
			solrInputDocuments = SolrDocUtil.composeSolrDocs(relevancies);

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
		if (args.length > 0) {
			storeId = args[0];
		} else {
			System.out.println("Store is null.");
			return;
		}

		try {
			RelevancyRuleBuilder relevancyRuleBuilder = new RelevancyRuleBuilder(
					storeId, Constants.Core.RELEVANCY_RULE_CORE.getCoreName());
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

			relevancyRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			solrServerFactory.shutdown();
		}
	}

}
