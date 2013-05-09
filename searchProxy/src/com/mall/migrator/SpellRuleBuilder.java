package com.mall.migrator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import com.search.manager.report.model.xml.RuleFileXml;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.util.IndexBuilderUtil;
import com.search.manager.solr.util.LocalSolrServerRunner;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrServerFactory;
import com.search.manager.xml.file.RuleXmlUtil;

public class SpellRuleBuilder implements Runnable {

	private static final Logger logger = Logger
			.getLogger(SpellRuleBuilder.class);

	private static final String BASE_RULE_DIR = "C:\\home\\solr\\utilities\\rules\\Did You Mean\\";
	private static final String SPELL_FILE = "spell.xml";

	private static SolrServerFactory solrServerFactory;

	private ApplicationContext context;
	private LocalSolrServerRunner solrServer;
	private Properties properties;
	private RuleXmlUtil ruleXmlUtil;
	private String storeId;

	private String logPath;
	private String logIndex;
	private String logErrorIndex;
	private String mailNotification;

	private int count;
	private int spellCount;

	SpellRuleBuilder(String storeId, LocalSolrServerRunner solrServer,
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
			ruleXmlUtil = (RuleXmlUtil) context.getBean("ruleXmlUtil");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (storeId == null) {
			logger.debug("storeId is null");
			return;
		}

		if (solrServer == null) {
			logger.debug("SolrServer is null.");
			return;
		}

		try {
			long timeStart = System.currentTimeMillis();

			RuleFileXml xml = new RuleFileXml();
			xml.setPath(BASE_RULE_DIR + storeId + "\\" + SPELL_FILE);

			@SuppressWarnings("static-access")
			SpellRules spellRules = (SpellRules) ruleXmlUtil.loadVersion(xml);

			List<SpellRuleXml> spellRulesXml = null;

			long indexTime = System.currentTimeMillis();
			if (spellRules != null) {
				spellRulesXml = spellRules.getSpellRule();
				if (spellRulesXml != null && spellRulesXml.size() > 0) {
					count = spellRulesXml.size();
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
					+ count);
			info.append("\n Total Spell Rule indexed : " + spellCount);
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
				spellCount = solrInputDocuments.size();
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
					.getCoreInstance(Constants.Core.SPELL_RULE_CORE
							.getCoreName());
			SpellRuleBuilder spellRuleBuilder = new SpellRuleBuilder(storeId,
					solrServer, properties, context);
			spellRuleBuilder.run();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			solrServerFactory.shutdown();
		}
	}
	
}