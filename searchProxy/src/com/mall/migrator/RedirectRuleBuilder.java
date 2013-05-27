package com.mall.migrator;

import java.io.IOException;
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
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;

public class RedirectRuleBuilder extends BaseRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RedirectRuleBuilder.class);

	private int dbCount;
	private int indexCount;

	@SuppressWarnings("unused")
	private RedirectRuleBuilder() {
		// do nothing...
	}

	public RedirectRuleBuilder(String storeId, String core)
			throws SolrServerException {
		this.storeId = storeId;
		solrServer = solrServerFactory.getCoreInstance(core);
	}

	public RedirectRuleBuilder(ApplicationContext context,
			SolrServerFactory solrServerFactory, Properties properties,
			String store, String core) {
		super(context, solrServerFactory, properties, store, core);
	}

	@Override
	public void run() {
		try {
			RedirectRule redirectRuleFilter = new RedirectRule();
			redirectRuleFilter.setStoreId(storeId);
			redirectRuleFilter.setRuleName(""); // ALL
			long timeStart = System.currentTimeMillis();
			long indexTime = 0;
			int page = 1;

			while (true) {
				SearchCriteria<RedirectRule> criteria = new SearchCriteria<RedirectRule>(
						redirectRuleFilter, page, MAX_IMPORT_DOC);
				RecordSet<RedirectRule> recordSet = daoService
						.searchRedirectRule(criteria, MatchType.LIKE_NAME);
				if (recordSet != null && recordSet.getTotalSize() > 0) {
					List<RedirectRule> redirectRules = recordSet.getList();
					logger.info("Indexing Page: [" + page + "] Size: ["
							+ redirectRules.size() + "]");
					dbCount += redirectRules.size();
					solrImport(redirectRules);
					if (redirectRules.size() < MAX_IMPORT_DOC) {
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
			info.append("\n Total Redirect rule indexed : " + indexCount);
			info.append("\n Total Redirect rule fetched from database : "
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
				solrServer.commit();
				indexCount += solrInputDocuments.size();
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

		if (args.length > 0) {
			storeId = args[0];
		} else {
			logger.info("Store is null.");
			return;
		}

		try {
			RedirectRuleBuilder redirectRuleBuilder = new RedirectRuleBuilder(
					storeId, Constants.Core.REDIRECT_RULE_CORE.getCoreName());
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

			redirectRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (solrServerFactory != null) {
				solrServerFactory.shutdown();
			}
		}
	}

}