package com.search.ws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTimeZone;

import com.search.manager.enums.RuleEntity;
import com.search.manager.utility.PropertiesUtils;
import org.slf4j.LoggerFactory;

public class ConfigManager {

    private static final org.slf4j.Logger logger =
            LoggerFactory.getLogger(ConfigManager.class);
    private XMLConfiguration xmlConfig;
    private static ConfigManager instance;
    //TODO: will eventually move out if settings is migrated to DB instead of file
    private Map<String, PropertiesConfiguration> serverSettingsMap = new HashMap<String, PropertiesConfiguration>();
    private Map<String, PropertiesConfiguration> linguisticSettingsMap = new HashMap<String, PropertiesConfiguration>();
    private Map<String, PropertiesConfiguration> mailSettingsMap = new HashMap<String, PropertiesConfiguration>();

    private ConfigManager() {
        // do nothing...
    }

    private ConfigManager(String configPath) {
        try {
            xmlConfig = new XMLConfiguration();
            xmlConfig.setDelimiterParsingDisabled(true);
            xmlConfig.setExpressionEngine(new XPathExpressionEngine());
            xmlConfig.load(configPath);
            xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            logger.debug("Search Config Folder: " + xmlConfig.getFile().getAbsolutePath());
            String configFolder = xmlConfig.getFile().getParent();

            // server settings
            for (String storeId : getStoreIds()) {
                File f = new File(String.format("%s%s%s.settings.properties", configFolder, File.separator, storeId));
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        logger.error("Unable to create settings file: " + f.getAbsolutePath(), e);
                    }
                }
                if (f.exists()) {
                    PropertiesConfiguration propConfig = new PropertiesConfiguration(f.getAbsolutePath());
                    propConfig.setAutoSave(true);
                    propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
                    serverSettingsMap.put(storeId, propConfig);
                    logger.info("Settings file for " + storeId + ": " + propConfig.getFileName());
                }

                // did you mean
                String fileName = PropertiesUtils.getValue("publishedfilepath");
                if (StringUtils.isNotBlank(fileName)) {
                    fileName += File.separator + storeId + File.separator + RuleEntity.getValue(RuleEntity.SPELL.getCode()) + File.separator + "spell.properties";
                    File spellFile = new File(fileName);
                    if (!spellFile.exists()) {
                        spellFile.getParentFile().mkdirs();
                        try {
                            spellFile.createNewFile();
                        } catch (IOException e) {
                            logger.error("No spell file detected and unable to create spell file", e);
                        }
                    }
                    PropertiesConfiguration propConfig = new PropertiesConfiguration(spellFile.getAbsolutePath());
                    propConfig.setAutoSave(true);
                    propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
                    linguisticSettingsMap.put(storeId, propConfig);
                }

                // mail properties
                File file = new File(String.format("%s%s%s.mail.properties", configFolder, File.separator, storeId));
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        logger.error("Unable to create mail property file: " + f.getAbsolutePath(), e);
                    }
                }
                if (file.exists()) {
                    PropertiesConfiguration propConfig = new PropertiesConfiguration(file.getAbsolutePath());
                    propConfig.setAutoSave(true);
                    propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
                    mailSettingsMap.put(storeId, propConfig);
                    logger.info("Mail property file for " + storeId + ": " + propConfig.getFileName());
                }
            }

            initTimezone();

        } catch (ConfigurationException ex) {
            ex.printStackTrace();
            logger.error(ex.getLocalizedMessage());
        }
    }

    private void initTimezone() {

        /* System timezone */
        String systemTimeZoneId = xmlConfig.getString("/system-timezone", "America/Los_Angeles");

        if (TimeZone.getDefault().getID().equalsIgnoreCase(systemTimeZoneId)) {
            logger.info(String.format("-DTZ- System timezone is already set to %s", systemTimeZoneId));
        } else {
            logger.info(String.format("-DTZ- Pre-Attempt: System timezone is %s", TimeZone.getDefault().getDisplayName()));
            logger.info(String.format("-DTZ- Attempted to set System timezone from %s to %s", TimeZone.getDefault().getID(), systemTimeZoneId));
            TimeZone.setDefault(TimeZone.getTimeZone(systemTimeZoneId));
            logger.info(String.format("-DTZ- Post-Attempt: System timezone is now %s", TimeZone.getDefault().getDisplayName()));
        }

        /* Joda timezone*/
        DateTimeZone defaultJodaTimeZone = DateTimeZone.getDefault();
        DateTimeZone jodaTimeZone = DateTimeZone.getDefault();

        if (defaultJodaTimeZone.getID().equalsIgnoreCase(systemTimeZoneId)) {
            logger.info(String.format("-DTZ- Joda timezone and system timezone are equals: %s", systemTimeZoneId));
        } else {
            try {
                jodaTimeZone = DateTimeZone.forID(systemTimeZoneId);

                try {
                    DateTimeZone.setDefault(jodaTimeZone);
                } catch (IllegalArgumentException iae) {
                    DateTimeZone.setDefault(defaultJodaTimeZone);
                    logger.error(String.format("-DTZ- Failed to set Joda Timezone from %s to %s, set default timezone to %s", defaultJodaTimeZone.getID(), systemTimeZoneId, DateTimeZone.getDefault().getID()));
                }
            } catch (IllegalArgumentException iae) {
                logger.error(String.format("-DTZ- Failed to convert System Timezone to Joda Timezone : %s", systemTimeZoneId));
            }
        }
    }

    public List<String> getStoreNames() {
        return getStoreAttributes("name", false);
    }

    public List<String> getStoreIds() {
        return getStoreAttributes("id", false);
    }

    public List<String> getCoreNames() {
        return getStoreAttributes("core", true);
    }

    @SuppressWarnings("unchecked")
    public List<String> getStoreAttributes(String attrName, boolean hasXmlTag) {
        List<String> storeAttrib = new ArrayList<String>();
        List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt(("/store"));
        for (HierarchicalConfiguration hc : hcList) {
            String attrib = "@" + attrName;
            if (hasXmlTag) {
                attrib = attrName;
            }
            String attrValue = hc.getString(attrib);
            if (StringUtils.isNotEmpty(attrValue)) {
                storeAttrib.add(attrValue);
            }
        }
        return storeAttrib;
    }

    public String getStoreName(String storeId) {
        return (xmlConfig.getString("/store[@id='" + getStoreIdByAliases(storeId) + "']/@name"));
    }

    public String getStoreParameter(String storeId, String param) {
        return (xmlConfig.getString("/store[@id='" + getStoreIdByAliases(storeId) + "']/" + param));
    }

    public String getSystemTimeZoneId() {
        return StringUtils.defaultIfBlank(getParameter("system-timezone"), "America/Los_Angeles");
    }

    @SuppressWarnings("unchecked")
    public List<String> getStoreParameterList(String storeId, String param) {
        return (xmlConfig.getList("/store[@id='" + getStoreIdByAliases(storeId) + "']/" + param));
    }

    public String getServerParameter(String server, String param) {
        return (xmlConfig.getString("/server[@name='" + server + "']/" + param));
    }

    @SuppressWarnings("unchecked")
    public String getStoreIdByAliases(String storeId) {
        String sId = xmlConfig.getString("/store[@id='" + storeId + "']/@id");
        List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt("/store");

        if (StringUtils.isBlank(sId) && CollectionUtils.isNotEmpty(hcList)) {
            for (HierarchicalConfiguration hc : hcList) {
                String[] storeIdAliases = StringUtils.stripAll(StringUtils.split(hc.getString("store-id-aliases"), ","));
                if (ArrayUtils.contains(storeIdAliases, storeId)) {
                    sId = hc.getString("@id");
                    break;
                }
            }
        }

        return sId;
    }

    @SuppressWarnings("unchecked")
    public List<NameValuePair> getDefaultSolrParameters(String storeId) {
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
        List<String> solrParamNames = (List<String>) xmlConfig.getList("/store[@id='" + storeId + "']/solr-param-name/param-name");
        List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt(("/store[@id='" + storeId + "']/solr-param-value"));

        for (HierarchicalConfiguration hc : hcList) {
            String solrParamName = hc.getString("@name");
            solrParamNames.contains(solrParamName);
            List<String> solrParamValues = hc.getList("param-value");
            for (String solrParamValue : solrParamValues) {
                nameValuePairList.add(new BasicNameValuePair(solrParamName, solrParamValue));
            }
        }

        return nameValuePairList;
    }

    public boolean isSharedCore() {
        return xmlConfig.getBoolean("/shared-core", false);
    }

    public String getSolrSelectorParam() {
        return xmlConfig.getString("/solr-selector-param");
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getServersByStoreId(String storeId) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt("/server");
        for (HierarchicalConfiguration hc : hcList) {
            String[] store = StringUtils.split(hc.getString("store"), ",");
            if (ArrayUtils.contains(store, getStoreIdByAliases(storeId))) {
                String serverName = hc.getString("@name");
                String serverUrl = hc.getString("url");
                map.put(serverName, serverUrl);
            }
        }
        // sort keys
        List<String> keys = new ArrayList<String>(map.keySet());
        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        for (String key : keys) {
            map.put(key, map.remove(key));
        }
        return map;
    }

    public String getParameter(String... keys) {
        StringBuilder str = new StringBuilder();
        for (String key : keys) {
            str.append("/").append(key);
        }
        return xmlConfig.getString(str.toString());
    }

    public synchronized static ConfigManager getInstance(String configPath) {
        if (instance == null) {
            instance = new ConfigManager(configPath);
        }
        return instance;
    }

    public void setInstance(String configPath) {
        getInstance(configPath);
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public boolean setStoreSetting(String storeId, String field, String value) {
        PropertiesConfiguration config = serverSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                config.setProperty(field, value);
                return StringUtils.equals(config.getString(field), value);
            }
        }
        return false;
    }

    /**
     * For a property that has multiple values, getString() will return the first value of
     * the list
	 *
     */
    public String getStoreSetting(String storeId, String field) {
        PropertiesConfiguration config = serverSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                return StringUtils.trimToEmpty(config.getString(field));
            }
        }
        return null;
    }

    public boolean setPublishedStoreLinguisticSetting(String storeId, String field, String value) {
        PropertiesConfiguration config = linguisticSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                config.setProperty(field, value);
                return StringUtils.equals(config.getString(field), value);
            }
        }
        return false;
    }

    /**
     * For a property that has multiple values, getString() will return the first value of
     * the list
	 *
     */
    public String getPublishedStoreLinguisticSetting(String storeId, String field) {
        PropertiesConfiguration config = linguisticSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                return config.getString(field);
            }
        }
        return null;
    }

    /**
     * For a property that has multiple values, getList() will return the complete list 
	 *
     */
    @SuppressWarnings("unchecked")
    public List<String> getStoreSettings(String storeId, String field) {
        PropertiesConfiguration config = serverSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                return config.getList(field);
            }
        }
        return null;
    }

    public boolean isMemberOf(String groupName, String storeId) {
        List<String> storeGroups = getStoreParameterList(storeId, "group-membership/group");

        if (CollectionUtils.isNotEmpty(storeGroups) && storeGroups.contains(groupName)) {
            return true;
        }

        return false;
    }

    public String getPublishedDidYouMeanPath(String storeId) {
        String fileName = PropertiesUtils.getValue("publishedfilepath");
        if (StringUtils.isNotBlank(fileName)) {
            fileName += File.separator + storeId + File.separator + RuleEntity.getValue(RuleEntity.SPELL.getCode()) + File.separator + "spell.csv";
        }
        return StringUtils.trimToNull(fileName);
    }

    public boolean isSolrImplOnly() {
        return "1".equals(PropertiesUtils.getValue("solrImplOnly"));
    }

//	public boolean getPendingNotification() {
//		return "1".equals(PropsUtils.getValue("pendingNotification"));
//	}
//	
//	public boolean getApprovalNotification() {
//		return "1".equals(PropsUtils.getValue("approvalNotification"));
//	}
//	
//	public boolean getPushToProdNotification() {
//		return "1".equals(PropsUtils.getValue("pushToProdNotification"));
//	}
    public String getMailProperty(String storeId, String field) {
        PropertiesConfiguration config = mailSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                return config.getString(field);
            }
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public List<String> getListMailProperty(String storeId, String field) {
        PropertiesConfiguration config = mailSettingsMap.get(storeId);
        if (config != null) {
            synchronized (config) {
                return config.getList(field);
            }
        }
        return new ArrayList<String>();
    }

    public static void main(String[] args) {
        final ConfigManager configManager = new ConfigManager("C:\\home\\solr\\conf\\solr.xml");
        logger.info(String.format("qt: %s", configManager.getSolrSelectorParam()));
//		System.out.println("qt: " + configManager.getStoreParameter("pcmall", "core"));
        logger.info(String.format("qt: %s", configManager.getStoreIdByAliases("pcm")));
//		System.out.println("query: " + configManager.getParameter("big-bets", "fields"));
//		System.out.println("query: " + configManager.getParameter("big-bets", "query"));
//		System.out.println("macmall deafault solr param: " + configManager.getDefaultSolrParameters("macmall"));
//		System.out.println("bd default solr param: " + configManager.getDefaultSolrParameters("pcmallcap"));
//		System.out.println("query: " + configManager.getStoreName("macmall"));
//		
//		Map<String, String> map = configManager.getServersByCore("macmall");
//		System.out.println("macmall");
//		for (String key: map.keySet()) {
//			System.out.println(key + "\t" + map.get(key));
//		}
//
//		map = configManager.getServersByCore("pcmallcap");
//		System.out.println("pcmallcap");
//		for (String key: map.keySet()) {
//			System.out.println(key + "\t" + map.get(key));
//		}
//
//		for (String key: configManager.getCoreNames()) {
//			System.out.println("core: " + key);
//		}
//		
//		for (int i =0; i < 20; i++) {
//			(new Thread() {
//				public void run() {
//					for (int i = 1; i < 50; i++) {
//						try {
//							configManager.setStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT, "true");
//							System.out.println(configManager.getStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT));
//							configManager.setStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT, "false");
//							System.out.println( configManager.getStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT));
//							Thread.sleep(100);
//						} catch (InterruptedException e) {
//						}
//					}
//				}
//			}).start();
//		}

    }
}
