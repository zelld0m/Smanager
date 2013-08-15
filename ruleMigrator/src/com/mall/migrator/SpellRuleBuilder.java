package com.mall.migrator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.xml.sax.SAXException;

import com.mall.mail.MailNotifier;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.xml.file.RuleXmlUtil;

public class SpellRuleBuilder extends BaseRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(SpellRuleBuilder.class);

	private static final String BASE_RULE_DIR = "C:\\home\\solr\\utilities\\rules\\Did You Mean\\";
	private static final String SPELL_FILE = "spell.xml";
	private RuleXmlUtil ruleXmlUtil;
	private int fileCount;
	private int indexCount;

	@SuppressWarnings("unused")
	private SpellRuleBuilder() {
		// do nothing...
	}

	public SpellRuleBuilder(String storeId, String core)
			throws SolrServerException {
		this.storeId = storeId;
		solrServer = solrServerFactory.getCoreInstance(core);
		ruleXmlUtil = (RuleXmlUtil) context.getBean("ruleXmlUtil");
	}

	@Override
	public void run() {
		try {
			long timeStart = System.currentTimeMillis();

			@SuppressWarnings("static-access")
			SpellRules spellRules = (SpellRules) ruleXmlUtil
					.loadVersion(BASE_RULE_DIR + storeId + "\\" + SPELL_FILE);

			List<SpellRuleXml> spellRulesXml = null;

			long indexTime = System.currentTimeMillis();
			if (spellRules != null) {
				spellRulesXml = spellRules.getSpellRule();
				if (spellRulesXml != null && spellRulesXml.size() > 0) {
					fileCount = spellRulesXml.size();
					solrImport(spellRulesXml, spellRules.getStore());
				}
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
			info.append("\n Total Spell Rule fetched from database/file : "
					+ fileCount);
			info.append("\n Total Spell Rule indexed : " + indexCount);
			logger.info(info.toString());
			if (mailNotification.equals("true")) {
				MailNotifier mailNotifier = new MailNotifier(
						"Spell Rule Builder", info.toString(), properties);
				mailNotifier.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void solrImport(List<SpellRuleXml> spellRulesXml, String storeId)
			throws ParserConfigurationException, IOException, SAXException {
		List<SolrInputDocument> solrInputDocuments = new ArrayList<SolrInputDocument>();
		boolean hasError = false;

		try {
			if (spellRulesXml != null && spellRulesXml.size() > 0) {
				solrInputDocuments.addAll(SolrDocUtil.composeSolrDocsSpell(
						spellRulesXml, storeId));
				// Add rules to solr index.
				solrServer.addDocs(solrInputDocuments);
				solrServer.optimize();
				indexCount = solrInputDocuments.size();
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
							"spellRule");
				}
			} else {
				// Log error indexed data.
				if (logErrorIndex.equals("true")) {
					IndexBuilderUtil.saveIndexData(logPath + "error\\",
							solrInputDocuments, "spellRule");
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
			SpellRuleBuilder spellRuleBuilder = new SpellRuleBuilder(storeId,
					Constants.Core.SPELL_RULE_CORE.getCoreName());

			System.out.println("----------------------------------------");
			System.out.println("Store	  : " + storeId);
			System.out.println("Solr Url  : "
					+ ((LocalSolrServerRunner) context
							.getBean("localSolrServerRunner")).getSolrUrl());
			System.out.println("Database  : "
					+ ((BasicDataSource) context.getBean("dataSource_solr"))
							.getUrl());
			System.out.println("Path : " + BASE_RULE_DIR + storeId + "\\" + SPELL_FILE);
			System.out.println("----------------------------------------");
			String response = "";
			Scanner input = new Scanner(System.in);

			System.out.print("Are you sure you want to continue? (Y/N) : ");
			response = input.next();

			if (!response.toUpperCase().startsWith("Y")) {
				solrServerFactory.shutdown();
				return;
			}

			spellRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (solrServerFactory != null) {
				solrServerFactory.shutdown();
			}
		}
	}

}