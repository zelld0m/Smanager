package com.mall.migrator;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.context.ApplicationContext;

import com.mall.mail.MailNotifier;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

public class BannerRuleBuilder extends BaseRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(BannerRuleBuilder.class);

	private String storeId;
	private int dbCount;
	private int indexCount;

	@SuppressWarnings("unused")
	private BannerRuleBuilder() {
		// do nothing...
	}

	public BannerRuleBuilder(String storeId, String core)
			throws SolrServerException {
		this.storeId = storeId;
		solrServer = solrServerFactory.getCoreInstance(core);
	}

	public BannerRuleBuilder(ApplicationContext context,
			SolrServerFactory solrServerFactory, Properties properties,
			String storeId, String core) {
		super(context, solrServerFactory, properties, storeId, core);
	}

	@Override
	public void run() {
		try {
			BannerRuleItem bannerRuleItemFilter = new BannerRuleItem();
			BannerRule bannerRule = new BannerRule();
			bannerRule.setStoreId(storeId);
			bannerRuleItemFilter.setRule(bannerRule);
			long timeStart = System.currentTimeMillis();
			long indexTime = 0;
			int page = 1;

			while (true) {
				SearchCriteria<BannerRuleItem> searchCriteria = new SearchCriteria<BannerRuleItem>(
						bannerRuleItemFilter, page, MAX_IMPORT_DOC);
				RecordSet<BannerRuleItem> recordSet = daoService
						.searchBannerRuleItem(searchCriteria);
				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<BannerRuleItem> bannerRuleItems = recordSet.getList();
					logger.info("Indexing Page: [" + page + "] Size: ["
							+ bannerRuleItems.size() + "]");
					dbCount += bannerRuleItems.size();
					solrImport(bannerRuleItems);
					if (bannerRuleItems.size() < MAX_IMPORT_DOC) {
						indexTime = System.currentTimeMillis();
						solrServer.optimize();
						break;
					}
					page++;
				} else {
					if (page != 1) {
						indexTime = System.currentTimeMillis();
						solrServer.optimize();
					}
					break;
				}
			}

			long elapsedTimeMillis = System.currentTimeMillis() - timeStart;
			long elapsedIndexTime = System.currentTimeMillis() - indexTime;

			StringBuffer info = new StringBuffer();
			info.append("Indexing completed!");
			info.append("\n Time Completed (Sec): "
					+ (elapsedTimeMillis / (1000F)) + " secs./");
			info.append((elapsedTimeMillis / (60 * 1000F)) + " mins.");
			info.append("\n Index Time (Sec): " + (elapsedIndexTime / (1000F))
					+ " secs./");
			info.append((elapsedIndexTime / (60 * 1000F)) + " mins.");
			info.append("\n Total Banner Rule Item rule indexed : "
					+ indexCount);
			info.append("\n Total Banner Rule Item fetched from database : "
					+ dbCount);
			logger.info(info.toString());
			if (mailNotification.equals("true")) {
				MailNotifier mailNotifier = new MailNotifier(
						"Redirect Rule Builder -[" + storeId + "]",
						info.toString(), properties);
				mailNotifier.send();
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void solrImport(List<BannerRuleItem> bannerRuleItems) {
		List<SolrInputDocument> solrInputDocuments = null;
		boolean hasError = false;

		try {
			if (bannerRuleItems != null && bannerRuleItems.size() > 0) {
				solrInputDocuments = SolrDocUtil
						.composeSolrDocsBannerRuleItem(bannerRuleItems);
				// Add rules to solr index.
				solrServer.addDocs(solrInputDocuments);
				solrServer.commit();
				indexCount += solrInputDocuments.size();
			}
		} catch (Exception e) {
			hasError = true;
		}

		if (solrInputDocuments != null && solrInputDocuments.size() > 0) {
			if (!hasError) {
				if (logIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath, solrInputDocuments,
							"banner_");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, "banner_");
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
			BannerRuleBuilder bannerRuleBuilder = new BannerRuleBuilder(
					storeId, Constants.Core.BANNER_RULE_CORE.getCoreName());
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

			bannerRuleBuilder.run();
		} catch (Exception e) {
			System.out.println(e);
			logger.error(e);
		} finally {
			if (solrServerFactory != null) {
				solrServerFactory.shutdown();
			}
		}
	}

}