package com.search.cron;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Store;
import com.search.manager.solr.service.SolrService;
import com.search.manager.utility.PropsUtils;

public class SolrProcessorCronSingle implements Runnable {

	private final static Logger logger = Logger
			.getLogger(SolrProcessorCronSingle.class);

	private static final String BASE_RULE_DIR = PropsUtils
			.getValue("publishedfilepath");
	private static final String XML_FILE_TYPE = ".xml";
	private static final String DID_YOU_MEAN = "Did You Mean";
	private static final String FILE_PREFIX = "spell_rule";

	private SolrService solrService;
	private Map<String, String> storeSpellRule = new HashMap<String, String>();
	private List<String> stores = new ArrayList<String>();
	private boolean running = false;
	private boolean indexing = false;

	public SolrProcessorCronSingle(SolrService solrService, List<String> stores) {
		this.solrService = solrService;
		this.stores = stores;
		new Thread(this).start();
	}

	public List<String> checkFiles(String storeId) {
		File folder = new File(new StringBuilder().append(BASE_RULE_DIR)
				.append(File.separator).append(storeId).append(File.separator)
				.append(DID_YOU_MEAN).append(File.separator).toString());
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();

		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.getName().startsWith(FILE_PREFIX)
						&& file.getName().endsWith(XML_FILE_TYPE)) {
					fileNames.add(file.getName());
				}
			}
			if (fileNames.size() > 0) {
				Collections.sort(fileNames, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o2.compareTo(o1);
					}
				});
			}
		}

		return fileNames;
	}

	public boolean resetIndexData(String storeId, String fileName) {
		try {
			Store store = new Store(storeId);
			String path = new StringBuilder().append(BASE_RULE_DIR)
					.append(File.separator).append(storeId)
					.append(File.separator).append(DID_YOU_MEAN)
					.append(File.separator).toString();

			if (solrService.deleteSpellRules(store)) {
				return solrService.loadSpellRules(store, path, fileName);
			}
		} catch (DaoException e) {
			logger.error(
					"Error in resetIndexData(String storeId, String fileName) : ",
					e);
			return false;
		}

		return false;
	}

	public void checkForNewDoc() {
		for (String store : stores) {
			logger.debug("Checking new doc for: " + store);
			List<String> fileNames = checkFiles(store);
			if (fileNames != null && fileNames.size() > 0) {
				if (storeSpellRule.containsKey(store)) {
					if (!storeSpellRule.get(store).equals(fileNames.get(0))) {
						logger.info("Old Doc: " + storeSpellRule.get(store));
						logger.info("New Doc: " + fileNames.get(0));
						logger.info("Start indexing spell rules for " + store
								+ ": " + fileNames.get(0));
						indexing = true;
						if (resetIndexData(store, fileNames.get(0))) {
							storeSpellRule.put(store, fileNames.get(0));
							logger.info("Done indexing spell rules for "
									+ store + ": " + fileNames.get(0));
						}
						indexing = false;
					}
				} else {
					logger.info("Initial spell rules for " + store + ": "
							+ fileNames.get(0));
					indexing = true;
					if (resetIndexData(store, fileNames.get(0))) {
						storeSpellRule.put(store, fileNames.get(0));
						logger.info("Done indexing spell rules for " + store
								+ ": " + fileNames.get(0));
					}
					indexing = false;
				}
			}
		}
	}

	@Override
	public void run() {
		logger.info("Running...");
		running = true;

		while (running) {
			if (!indexing) {
				logger.debug("Checking for new doc...");
				checkForNewDoc();
			} else {
				logger.debug("indexing...");
			}

			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}

		logger.info("End!");
	}

}