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

public class RuleIndexBuilder extends BaseRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(RuleIndexBuilder.class);

	private String rule;
	private int dbCount;
	private int docCount;

	@SuppressWarnings("unused")
	private RuleIndexBuilder() {
		// do nothing...
	}

	public RuleIndexBuilder(String storeId, String core, String rule)
			throws SolrServerException {
		this.storeId = storeId;
		this.rule = rule;
		solrServer = solrServerFactory.getCoreInstance(core);
	}

	public RuleIndexBuilder(ApplicationContext context,
			SolrServerFactory solrServerFactory, Properties properties,
			String store, String core) {
		super(context, solrServerFactory, properties, store, core);

		if (core.equals(Constants.Core.DEMOTE_RULE_CORE.getCoreName())) {
			this.rule = Constants.Rule.DEMOTE.getRuleName();
		} else if (core.equals(Constants.Core.ELEVATE_RULE_CORE.getCoreName())) {
			this.rule = Constants.Rule.ELEVATE.getRuleName();
		} else if (core.equals(Constants.Core.EXCLUDE_RULE_CORE.getCoreName())) {
			this.rule = Constants.Rule.EXCLUDE.getRuleName();
		}
	}

	@Override
	public void run() {
		try {
			StoreKeyword storeKeyword = new StoreKeyword(storeId, null);
			long timeStart = System.currentTimeMillis();
			int page = 1;

			if (rule.equals(Constants.Rule.DEMOTE.getRuleName())) {
				DemoteResult demoteFilter = new DemoteResult();
				demoteFilter.setStoreKeyword(storeKeyword);
				while (true) {
					SearchCriteria<DemoteResult> criteria = new SearchCriteria<DemoteResult>(
							demoteFilter, page, MAX_IMPORT_DOC);
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
						if (page != 1) {
							solrServer.optimize();
						}
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
						if (page != 1) {
							solrServer.optimize();
						}
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
						if (page != 1) {
							solrServer.optimize();
						}
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
			logger.error("Error in solrImportExclude() : " + e, e);
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

		if (args.length > 0) {
			storeId = args[0];
		} else {
			System.out.println("Store is null.");
			return;
		}
		if (args.length > 1) {
			ruleType = args[1];
		} else {
			System.out.println("Rule Type not specified.");
			return;
		}

		try {
			RuleIndexBuilder indexBuilder = null;
			if (ruleType.equals(Constants.Rule.DEMOTE.getRuleName())) {
				indexBuilder = new RuleIndexBuilder(storeId,
						Constants.Core.DEMOTE_RULE_CORE.getCoreName(),
						Constants.Rule.DEMOTE.getRuleName());
			} else if (ruleType.equals(Constants.Rule.ELEVATE.getRuleName())) {
				indexBuilder = new RuleIndexBuilder(storeId,
						Constants.Core.ELEVATE_RULE_CORE.getCoreName(),
						Constants.Rule.ELEVATE.getRuleName());
			} else if (ruleType.equals(Constants.Rule.EXCLUDE.getRuleName())) {
				indexBuilder = new RuleIndexBuilder(storeId,
						Constants.Core.EXCLUDE_RULE_CORE.getCoreName(),
						Constants.Rule.EXCLUDE.getRuleName());
			}

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

			if (indexBuilder != null) {
				indexBuilder.run();
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (solrServerFactory != null) {
				solrServerFactory.shutdown();
			}
		}
	}

}