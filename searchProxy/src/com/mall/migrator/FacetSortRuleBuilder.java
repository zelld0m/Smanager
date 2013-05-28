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
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

public class FacetSortRuleBuilder extends BaseRuleBuilder implements Runnable {
	private static final Logger logger = Logger
			.getLogger(FacetSortRuleBuilder.class);

	private int dbCount;
	private int indexCount;

	@SuppressWarnings("unused")
	private FacetSortRuleBuilder() {
		// do nothing...
	}

	public FacetSortRuleBuilder(String storeId, String core)
			throws SolrServerException {
		this.storeId = storeId;
		solrServer = solrServerFactory.getCoreInstance(core);
	}

	public FacetSortRuleBuilder(ApplicationContext context,
			SolrServerFactory solrServerFactory, Properties properties,
			String storeId, String core) {
		super(context, solrServerFactory, properties, storeId, core);
	}

	@Override
	public void run() {
		try {
			Store store = new Store(storeId);
			FacetSort facetSortFilter = new FacetSort();
			facetSortFilter.setStore(store);
			long timeStart = System.currentTimeMillis();
			long indexTime = 0;
			int page = 1;

			while (true) {
				SearchCriteria<FacetSort> criteria = new SearchCriteria<FacetSort>(
						facetSortFilter, page, MAX_IMPORT_DOC);
				RecordSet<FacetSort> recordSet = daoService.searchFacetSort(
						criteria, MatchType.LIKE_NAME);
				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<FacetSort> facetSorts = recordSet.getList();
					logger.info("Indexing Page: [" + page + "] Size: ["
							+ facetSorts.size() + "]");
					dbCount += facetSorts.size();
					solrImport(facetSorts);
					if (facetSorts.size() < MAX_IMPORT_DOC) {
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
			info.append("\n Total Facet Sort Rule indexed : " + indexCount);
			info.append("\n Total Facet Sort fetched from database : "
					+ dbCount);
			logger.info(info.toString());
			if (mailNotification.equals("true")) {
				MailNotifier mailNotifier = new MailNotifier(
						"FacetSort Rule Builder", info.toString(), properties);
				mailNotifier.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void solrImport(List<FacetSort> facetSorts)
			throws ParserConfigurationException, IOException, SAXException {
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
		boolean hasError = false;

		try {
			if (facetSorts != null && facetSorts.size() > 0) {
				solrInputDocuments.addAll(SolrDocUtil
						.composeSolrDocsFacetSort(facetSorts));
				// Add rules to solr index.
				solrServer.addDocs(solrInputDocuments);
				solrServer.optimize();
				indexCount += solrInputDocuments.size();
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
							"facetSort");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, "facetSort");
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
			FacetSortRuleBuilder facetSortRuleBuilder = new FacetSortRuleBuilder(
					storeId, Constants.Core.FACET_SORT_RULE_CORE.getCoreName());
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

			facetSortRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (solrServerFactory != null) {
				solrServerFactory.shutdown();
			}
		}
	}

}
