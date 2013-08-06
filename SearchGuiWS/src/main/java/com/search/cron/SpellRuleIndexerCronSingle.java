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


import com.search.manager.dao.DaoException;
import com.search.manager.model.Store;
import com.search.manager.solr.service.SolrService;
import com.search.manager.utility.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpellRuleIndexerCronSingle implements Runnable {

    private static final Logger logger =
            LoggerFactory.getLogger(SpellRuleIndexerCronSingle.class);
    
    private static final String BASE_RULE_DIR = PropertiesUtils
            .getValue("publishedfilepath");
    private static final String XML_FILE_TYPE = ".xml";
    private static final String DID_YOU_MEAN = "Did You Mean";
    private static final String FILE_PREFIX = "spell_rule_";
    private static final String DATA_INDEX = "data_index.txt";
    private SolrService solrService;
    private Map<String, String> storeSpellRule = new HashMap<String, String>();
    private List<String> stores = new ArrayList<String>();
    private boolean running = false;
    private boolean indexing = false;
    private long lastIndexedFile;

    public SpellRuleIndexerCronSingle(SolrService solrService,
            List<String> stores) {
        this.solrService = solrService;
        this.stores = stores;

        for (String store : stores) {
            init(store);
        }

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
                        && file.getName().endsWith(XML_FILE_TYPE)
                        && file.lastModified() > lastIndexedFile) {
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
                String[] temp = fileNames.get(0).split(" - ");
                String newFile = "";
                long newFileLastModified = 0;

                if (temp != null && temp.length > 1) {
                    newFile = temp[0];
                    newFileLastModified = Long.parseLong(temp[1]);
                }

                if (storeSpellRule.containsKey(store)) {
                    String[] temp1 = storeSpellRule.get(store).split(" - ");
                    String oldFile = "";
                    long oldFileLastModified = 0;

                    if (temp1 != null && temp1.length > 1) {
                        oldFile = temp1[0];
                        oldFileLastModified = Long.parseLong(temp1[1]);
                    }

                    if (!oldFile.equals(newFile)
                            && newFileLastModified > oldFileLastModified) {
                        logger.info("Old Doc: " + storeSpellRule.get(store));
                        logger.info("New Doc: " + fileNames.get(0));
                        logger.info("Start indexing spell rules for " + store
                                + ": " + fileNames.get(0));
                        indexing = true;
                        if (resetIndexData(store, newFile)) {
                            storeSpellRule.put(store, fileNames.get(0));
                            write(store, fileNames.get(0));
                            logger.info("Done indexing spell rules for "
                                    + store + ": " + fileNames.get(0));
                        }
                        indexing = false;
                    }
                } else {
                    logger.info("Initial spell rules for " + store + ": "
                            + fileNames.get(0));
                    indexing = true;
                    if (resetIndexData(store, newFile)) {
                        storeSpellRule.put(store, fileNames.get(0));
                        write(store, fileNames.get(0));
                        logger.info("Done indexing spell rules for " + store
                                + ": " + fileNames.get(0));
                    }
                    indexing = false;
                }
            }
        }
    }

    public void write(String store, String indexData) {
        BufferedWriter bufferWritter = null;
        FileWriter fileWriter = null;
        try {
            File file = new File(new StringBuilder().append(BASE_RULE_DIR)
                    .append(File.separator).append(store)
                    .append(File.separator).append(DID_YOU_MEAN)
                    .append(File.separator).append(DATA_INDEX).toString());

            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file);
            bufferWritter = new BufferedWriter(fileWriter);
            bufferWritter.write(indexData);
            bufferWritter.flush();

        } catch (IOException e) {
            logger.error("Error in write(String store, String indexData) : "
                    + e.getMessage(), e);
        } finally {
            if (bufferWritter != null) {
                try {
                    bufferWritter.close();
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    public void init(String store) {
        BufferedReader bufferedReader = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(
                    new StringBuilder().append(BASE_RULE_DIR)
                    .append(File.separator).append(store)
                    .append(File.separator).append(DID_YOU_MEAN)
                    .append(File.separator).append(DATA_INDEX)
                    .toString());

            if (fileInputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        new DataInputStream(fileInputStream)));
                String str = "";

                while ((str = bufferedReader.readLine()) != null) {
                    if (!str.trim().equals("")) {
                        storeSpellRule.put(store, str.trim());
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
                    logger.error("", e);
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
                logger.error("", e);
            }
        }

        logger.info("End!");
    }
}