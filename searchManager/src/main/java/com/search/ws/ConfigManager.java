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
import com.search.manager.properties.PropertiesManager;
import com.search.manager.properties.exception.NotDirectoryException;
import com.search.manager.properties.model.Module;
import com.search.manager.properties.model.Store;
import com.search.manager.properties.model.StoreProperties;
import com.search.manager.properties.util.Stores;
import com.search.manager.utility.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
	private static PropertiesManager propertiesManager;
	private XMLConfiguration xmlConfig;
	private static ConfigManager instance;
	//TODO: will eventually move out if settings is migrated to DB instead of file
	private Map<String, PropertiesConfiguration> serverSettingsMap = new HashMap<String, PropertiesConfiguration>();
	private Map<String, PropertiesConfiguration> linguisticSettingsMap = new HashMap<String, PropertiesConfiguration>();
	private Map<String, PropertiesConfiguration> mailSettingsMap = new HashMap<String, PropertiesConfiguration>();
	private Map<String, PropertiesConfiguration> searchWithinSettingsMap = new HashMap<String, PropertiesConfiguration>();
	private Map<String, PropertiesConfiguration> facetSortSettingsMap = new HashMap<String, PropertiesConfiguration>();
	private Map<String, PropertiesConfiguration> workflowSettingsMap = new HashMap<String, PropertiesConfiguration>();

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

			// create the store specific properties if not existing yet
			propertiesManager.saveStoreProperties();

			// loads the store settings to their respective map
			loadStoreSettingsToMapFromStoreProperties(propertiesManager.getStoreProperties());

			// server settings
			for (String storeId : getStoreIds()) {
				// did you mean
				String fileName = PropertiesUtils.getValue("publishedfilepath");
				if (StringUtils.isNotBlank(fileName)) {
					fileName += File.separator + storeId + File.separator
							+ RuleEntity.getValue(RuleEntity.SPELL.getCode())
							+ File.separator + "spell.properties";
					File spellFile = new File(fileName);
					if (!spellFile.exists()) {
						spellFile.getParentFile().mkdirs();
						try {
							spellFile.createNewFile();
						} catch (IOException e) {
							logger.error("No spell file detected and unable to create "
									+ "spell file", e);
						}
					}

					PropertiesConfiguration propConfig = new PropertiesConfiguration(spellFile.getAbsolutePath());
					propConfig.setAutoSave(true);
					propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
					linguisticSettingsMap.put(storeId, propConfig);
				}
			}

			// initialize the timezone
			initTimezone();

		} catch (ConfigurationException ex) {
			ex.printStackTrace(System.err);
			logger.error(ex.getLocalizedMessage());
		}
	}

	/**
	 * loads the store settings to their respective map
	 *
	 * @param storeProperties the {@link StoreProperties} object
	 */
	private void loadStoreSettingsToMapFromStoreProperties(
			StoreProperties storeProperties) {
		List<Store> stores = storeProperties.getStores();

		for (Store store : stores) {
			String storeId = store.getId();
			List<Module> modules = store.getModules();

			for (Module module : modules) {
				String moduleName = module.getName();
				try {
					String filePath = Stores.getFormattedSaveLocation(
							propertiesManager.getStorePropertiesFolder(), storeId,
							moduleName);

					if (moduleName.equals("settings")) {
						// load the store settings
						loadStoreSettingsToMap("settings", serverSettingsMap, storeId,
								filePath);
					} else if (moduleName.equals("mail")) {
						// load the mail settings
						loadStoreSettingsToMap("mail", mailSettingsMap, storeId, filePath);
					} else if (moduleName.equals("searchwithin")) {
						// load the searchwithin settings
						loadStoreSettingsToMap("searchwithin", searchWithinSettingsMap,
								storeId, filePath);
					} else if (moduleName.equals("facetsort")) {
						// load the searchwithin settings
						loadStoreSettingsToMap("facetsort", facetSortSettingsMap,
								storeId, filePath);
					} else if (moduleName.equals("workflow")) {
						// load the workflow settings
						loadStoreSettingsToMap("workflow", workflowSettingsMap,
								storeId, filePath);
					}	
				} catch (NotDirectoryException e) {
					logger.error(String.format("%s is not a valid directory",
							e.getFile().getPath()), e);
				}
			}
		}
	}

	/**
	 * Helper method for loading store settings to a {@link Map}
	 *
	 * @param moduleName the module name (used for logging purposes only)
	 * @param storeSettingsMap the {@link Map} to store the store settings
	 * @param storeId the store id
	 * @param filePath the file path of the store settings
	 */
	private void loadStoreSettingsToMap(String moduleName,
			Map<String, PropertiesConfiguration> storeSettingsMap, String storeId,
			String filePath) {
		try {
			PropertiesConfiguration propConfig = new PropertiesConfiguration(filePath);
			propConfig.setAutoSave(true);
			propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			storeSettingsMap.put(storeId, propConfig);
			logger.info(String.format("%s property file for %s: %s", moduleName, storeId,
					propConfig.getFileName()));
		} catch (ConfigurationException e) {
			logger.error(String.format("Unable to load the store configuration for %s",
					moduleName), e);
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

		if (defaultJodaTimeZone.getID().equalsIgnoreCase(systemTimeZoneId)) {
			logger.info(String.format("-DTZ- Joda timezone and system timezone are equals: %s", systemTimeZoneId));
		} else {
			try {
				DateTimeZone jodaTimeZone = DateTimeZone.forID(systemTimeZoneId);

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

	private List<String> getStoreIds() {
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

	public synchronized static ConfigManager getInstance(String configPath,
			PropertiesManager pm) {
		propertiesManager = pm;
		return getInstance(configPath);
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

	public enum PropertyFileType{
		MAIL,
		SETTINGS,
		SEARCHWITHIN,
		FACETSORT,
		WORKFLOW
	}

	private PropertiesConfiguration getPropertiesConfiguration(PropertyFileType pft, String storeId){
		PropertiesConfiguration config = null;

		switch(pft){
		case MAIL: config = mailSettingsMap.get(storeId); break;
		case SETTINGS: config = serverSettingsMap.get(storeId); break;
		case SEARCHWITHIN: config = searchWithinSettingsMap.get(storeId); break;
		case FACETSORT: config = facetSortSettingsMap.get(storeId); break;
		case WORKFLOW: config = workflowSettingsMap.get(storeId); break;
		}
		
		return config;
	}
	
	public String getProperty(PropertyFileType pft, String storeId, String field) {
		PropertiesConfiguration config = getPropertiesConfiguration(pft, storeId);

		if (config != null) {
			synchronized (config) {
				return StringUtils.trimToEmpty(config.getString(field));
			}
		}
		
		return StringUtils.EMPTY;
	}

	@SuppressWarnings("unchecked")
	public List<String> getPropertyList(PropertyFileType pft, String storeId, String field) {
		PropertiesConfiguration config = getPropertiesConfiguration(pft, storeId);

		if (config != null) {
			synchronized (config) {
				return config.getList(field);
			}
		}

		return new ArrayList<String>();
	}
}