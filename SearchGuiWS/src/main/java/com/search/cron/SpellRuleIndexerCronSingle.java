package com.search.cron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.search.manager.dao.DaoException;
import com.search.manager.model.Store;
import com.search.manager.solr.service.SolrService;
import com.search.manager.utility.PropertiesUtils;

public class SpellRuleIndexerCronSingle extends TimerTask {

	private final static Logger logger = Logger.getLogger(SpellRuleIndexerCronSingle.class);

	private static final String BASE_RULE_DIR = PropertiesUtils.getValue("publishedfilepath");
	private static final String XML_FILE_TYPE = PropertiesUtils.getValue("spellfileextension");
	private static final String DID_YOU_MEAN = PropertiesUtils.getValue("didyoumeanfolder");
	private static final String FILE_PREFIX = PropertiesUtils.getValue("spellfileprefix");
	private static final String DATA_INDEX = PropertiesUtils.getValue("spelldataindex");
	private static Integer CHECK_INTERVAL = 20000;

	private SolrService solrService;
	private Map<String, String> storeSpellRule = new HashMap<String, String>();
	private Map<String, Long> storeIndexedDate = new HashMap<String, Long>();
	private List<String> stores = new ArrayList<String>();

	private Timer timer = new Timer();

	public SpellRuleIndexerCronSingle(SolrService solrService, List<String> stores) {
		this.solrService = solrService;
		this.stores = stores;
	}

	public List<String> checkFiles(String storeId) {
		File folder = new File(new StringBuilder().append(BASE_RULE_DIR).append(File.separator).append(storeId)
		        .append(File.separator).append(DID_YOU_MEAN).append(File.separator).toString());
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();

		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.getName().startsWith(FILE_PREFIX) && file.getName().endsWith(XML_FILE_TYPE)) {
					fileNames.add(file.getName() + " - " + file.lastModified());
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
			String path = new StringBuilder().append(BASE_RULE_DIR).append(File.separator).append(storeId)
			        .append(File.separator).append(DID_YOU_MEAN).append(File.separator).toString();

			if (solrService.deleteSpellRules(store)) {
				return solrService.loadSpellRules(store, path, fileName);
			}
		} catch (DaoException e) {
			logger.error("Error in resetIndexData(String storeId, String fileName) : ", e);
			return false;
		}

		return false;
	}

	public void checkForNewDoc() {
		for (String store : stores) {
			logger.debug("Checking new doc for: " + store);
			List<String> fileNames = checkFiles(store);
			if (fileNames != null && fileNames.size() > 0) {
				String[] temp = fileNames.get(0).split(" - ");
				String newFile = "";
				long newFileLastModified = 0;

				if (temp != null && temp.length > 1) {
					newFile = temp[0];
					newFileLastModified = Long.parseLong(temp[1]);

					if (storeSpellRule.containsKey(store)) {
						String[] temp1 = storeSpellRule.get(store).split(" - ");
						String oldFile = "";
						long oldFileLastModified = 0;

						if (temp1 != null && temp1.length > 1) {
							oldFile = temp1[0];
							oldFileLastModified = Long.parseLong(temp1[1]);

							if (!oldFile.equals(newFile)
							        || (oldFile.equals(newFile) && newFileLastModified > oldFileLastModified)) {
								logger.info("Old Doc: " + storeSpellRule.get(store));
								logger.info("New Doc: " + fileNames.get(0));
								logger.info("Start indexing spell rules for " + store + ": " + fileNames.get(0));
								if (resetIndexData(store, newFile)) {
									storeSpellRule.put(store, fileNames.get(0));
									storeIndexedDate.put(store, newFileLastModified);
									write(store, fileNames.get(0));
									logger.info("Done indexing spell rules for " + store + ": " + fileNames.get(0));
								}
							}
						}
					} else {
						logger.info("Initial spell rules for " + store + ": " + fileNames.get(0));
						if (resetIndexData(store, newFile)) {
							storeSpellRule.put(store, fileNames.get(0));
							storeIndexedDate.put(store, newFileLastModified);
							write(store, fileNames.get(0));
							logger.info("Done indexing spell rules for " + store + ": " + fileNames.get(0));
						}
					}
				}
			}
		}
	}

	public void write(String store, String indexData) {
		BufferedWriter bufferWritter = null;
		FileWriter fileWriter = null;
		try {
			File file = new File(new StringBuilder().append(BASE_RULE_DIR).append(File.separator).append(store)
			        .append(File.separator).append(DID_YOU_MEAN).append(File.separator).append(DATA_INDEX).toString());

			if (!file.exists()) {
				file.createNewFile();
			}
			fileWriter = new FileWriter(file);
			bufferWritter = new BufferedWriter(fileWriter);
			bufferWritter.write(indexData);
			bufferWritter.flush();

		} catch (IOException e) {
			logger.error("Error in write(String store, String indexData) : " + e.getMessage(), e);
		} finally {
			if (bufferWritter != null) {
				try {
					bufferWritter.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}

	public void init(String store) {
		BufferedReader bufferedReader = null;
		try {
			storeIndexedDate.put(store, 0L);
			FileInputStream fileInputStream = new FileInputStream(new StringBuilder().append(BASE_RULE_DIR)
			        .append(File.separator).append(store).append(File.separator).append(DID_YOU_MEAN)
			        .append(File.separator).append(DATA_INDEX).toString());

			if (fileInputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(new DataInputStream(fileInputStream)));
				String str = "";

				while ((str = bufferedReader.readLine()) != null) {
					if (!str.trim().equals("")) {
						storeSpellRule.put(store, str.trim());
						String[] temp1 = storeSpellRule.get(store).split(" - ");
						if (temp1 != null && temp1.length > 1) {
							storeIndexedDate.put(store, Long.parseLong(temp1[1]));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in init(String store) : " + e.getMessage(), e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	@Override
	public void run() {
		logger.debug("Checking for new doc...");
		checkForNewDoc();
	}

	public void start() {
		CHECK_INTERVAL = NumberUtils.toInt(PropertiesUtils.getValue("spellcheckinterval"), 20000);

		for (String store : stores) {
			init(store);
		}

		logger.info("Starting Spell Rule Indexer");
		timer.schedule(this, 500, CHECK_INTERVAL);
	}

	public void shutdown() {
		logger.info("Shutting down Spell Rule Indexer.");
		timer.cancel();
		logger.info("Spell Rule Indexed stopped.");
	}

}