package com.search.ws;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.search.manager.enums.RuleEntity;
import com.search.manager.utility.PropertiesUtils;

@Component
public class ConfigManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	private XMLConfiguration solrXMLConfig;
	private XMLConfiguration storeXMLConfig;
	private Map<String, PropertiesConfiguration> storeSettingsMap = new HashMap<String, PropertiesConfiguration>();

	@SuppressWarnings("unused")
	private ConfigManager() {
		// Exists only to defeat instantiation.
	}

	public ConfigManager(String solrXMLFile, String storeXMLFile) {
		solrXMLConfig = getXMLConfiguration(solrXMLFile);
		storeXMLConfig = getXMLConfiguration(storeXMLFile);
		initStoreSettingsMap();

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

				//				PropertiesConfiguration propConfig = new PropertiesConfiguration(spellFile.getAbsolutePath());
				//				propConfig.setAutoSave(true);
				//				propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
				//				linguisticSettingsMap.put(storeId, propConfig);
			}
		}

		// initialize the timezone
		initTimezone();

	}

	private void initStoreSettingsMap() {
		for (String storeId : getStoreIds()) {
			for (String moduleName: getModuleNames(storeId)) {
				String basePath = StringUtils.remove(storeXMLConfig.getBasePath(), storeXMLConfig.getFile().getName());
				String module = String.format("%s.%s.properties", storeId, moduleName);
				String filePath = String.format("%s%s/%s", basePath, storeId, module);
				String mapKey = String.format("%s-%s", storeId, moduleName);
				try {
					PropertiesConfiguration propConfig = new PropertiesConfiguration(filePath);
					propConfig.setAutoSave(true);
					propConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
					storeSettingsMap.put(mapKey, propConfig);
					logger.info(String.format("%s property file for %s: %s", moduleName, storeId, propConfig.getFileName()));
				} catch (ConfigurationException e) {
					logger.error(String.format("Unable to load the store configuration for %s", moduleName), e);
				}
			}
		}
	}

	private void initTimezone() {

		/* System timezone */
		String systemTimeZoneId = solrXMLConfig.getString("/system-timezone", "America/Los_Angeles");

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

	public List<String> getModuleNames(String storeId) {
		return Arrays.asList(storeXMLConfig.getStringArray(String.format("/store[@id='%s']/module/@name", storeId)));
	}

	public String getSystemTimeZoneId() {
		return StringUtils.defaultIfBlank(getParameter("system-timezone"), "America/Los_Angeles");
	}

	private List<String> getStoreIds() {
		return Arrays.asList(solrXMLConfig.getStringArray("/store/@id"));
	}

	public List<String> getStoreNames() {
		return Arrays.asList(solrXMLConfig.getStringArray("/store/@name"));
	}

	public List<String> getCoreNames() {
		return getStoreAttributes("core", true);
	}

	@SuppressWarnings("unchecked")
	public List<String> getStoreAttributes(String attrName, boolean hasXmlTag) {
		List<String> storeAttrib = new ArrayList<String>();
		List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) solrXMLConfig.configurationsAt(("/store"));
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

	private XMLConfiguration getXMLConfiguration(String filePath) {
		XMLConfiguration xmlConfiguration = new XMLConfiguration();

		try {
			xmlConfiguration.setDelimiterParsingDisabled(true);
			xmlConfiguration.setExpressionEngine(new XPathExpressionEngine());
			xmlConfiguration.load(filePath);
			xmlConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
			logger.info("Loaded file path: {}", xmlConfiguration.getFile().getAbsolutePath());
		} catch (ConfigurationException e) {
			logger.error(e.getMessage());
		}

		return xmlConfiguration;
	}

	@SuppressWarnings("unchecked")
	public String getStoreIdByAliases(String storeId) {
		String sId = solrXMLConfig.getString(String.format("/store[@id='%s']/@id", storeId));
		List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) solrXMLConfig.configurationsAt("/store");

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
		List<String> solrParamNames = (List<String>) solrXMLConfig.getList(String.format("/store[@id='%s']/solr-param-name/param-name", storeId));
		List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) solrXMLConfig.configurationsAt(("/store[@id='" + storeId + "']/solr-param-value"));

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

	public String getPublishedDidYouMeanPath(String storeId) {
		String fileName = PropertiesUtils.getValue("publishedfilepath");
		if (StringUtils.isNotBlank(fileName)) {
			fileName += File.separator + storeId + File.separator + RuleEntity.getValue(RuleEntity.SPELL.getCode()) + File.separator + "spell.csv";
		}
		return StringUtils.trimToNull(fileName);
	}


	public String getSolrSelectorParam() {
		return solrXMLConfig.getString("/solr-selector-param");
	}

	public String getStoreParameter(String storeId, String param) {
		return solrXMLConfig.getString(String.format("/store[@id='%s']/%s", getStoreIdByAliases(storeId), param));
	}

	public String getParameter(String... keys) {
		StringBuilder str = new StringBuilder();
		for (String key : keys) {
			str.append("/").append(key);
		}
		return solrXMLConfig.getString(str.toString());
	}

	public String getServerParameter(String server, String param) {
		return solrXMLConfig.getString(String.format("/server[@name='%s']/%s", server, param));
	}

	public String getStoreName(String storeId) {
		return solrXMLConfig.getString(String.format("/store[@id='%s']/@name", getStoreIdByAliases(storeId)));
	}

	@SuppressWarnings("unchecked")
	public List<String> getStoreParameterList(String storeId, String param) {
		return (List<String>) solrXMLConfig.getList(String.format("/store[@id='%s']/%s", getStoreIdByAliases(storeId), param));
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getServersByStoreId(String storeId) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) solrXMLConfig.configurationsAt("/server");
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

	public boolean isSharedCore() {
		return solrXMLConfig.getBoolean("/shared-core", false);
	}

	public boolean isSolrImplOnly() {
		return "1".equals(PropertiesUtils.getValue("solrImplOnly"));
	}

	public boolean isMemberOf(String groupName, String storeId) {
		List<String> storeGroups = getStoreParameterList(storeId, "group-membership/group");

		if (CollectionUtils.isNotEmpty(storeGroups) && storeGroups.contains(groupName)) {
			return true;
		}

		return false;
	}

	public String getProperty(String moduleName, String storeId, String field) {
		String mapKey = String.format("%s-%s", storeId, moduleName);
		PropertiesConfiguration config = storeSettingsMap.get(mapKey);

		if (config != null) {
			synchronized (config) {
				return StringUtils.trimToEmpty(config.getString(field));
			}
		}

		return StringUtils.EMPTY;
	}

	@SuppressWarnings("unchecked")
	public List<String> getPropertyList(String moduleName, String storeId, String field) {
		String mapKey = String.format("%s-%s", storeId, moduleName);
		PropertiesConfiguration config = storeSettingsMap.get(mapKey);

		if (config != null) {
			synchronized (config) {
				return config.getList(field);
			}
		}

		return new ArrayList<String>();
	}


	/**
	 * For a property that has multiple values, getString() will return the first value of
	 * the list
	 *
	 */
	public String getPublishedStoreLinguisticSetting(String storeId, String field) {
		//		PropertiesConfiguration config = linguisticSettingsMap.get(storeId);
		//		if (config != null) {
		//			synchronized (config) {
		//				return config.getString(field);
		//			}
		//		}
		return null;
	}

	public boolean setStoreSetting(String storeId, String field, String value) {
		//		PropertiesConfiguration config = serverSettingsMap.get(storeId);
		//		if (config != null) {
		//			synchronized (config) {
		//				config.setProperty(field, value);
		//				return StringUtils.equals(config.getString(field), value);
		//			}
		//		}
		return false;
	}

	public boolean setPublishedStoreLinguisticSetting(String storeId, String field, String value) {
		//		PropertiesConfiguration config = linguisticSettingsMap.get(storeId);
		//		if (config != null) {
		//			synchronized (config) {
		//				config.setProperty(field, value);
		//				return StringUtils.equals(config.getString(field), value);
		//			}
		//		}
		return false;
	}

	public List<String> getStoreGroupMembership(String storeId) {
		return getStoreParameterList(storeId, "group-membership/group");
	}

	/* Facet-related */
	public String getStoreFacetTemplate(String storeId) {
		return StringUtils.defaultIfBlank(getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE), StringUtils.EMPTY);
	}

	public String getStoreFacetPrefix(String storeId) {
		return StringUtils.defaultIfBlank(getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_NAME), StringUtils.EMPTY);
	}

	public String getStoreFacetName(String storeId) {
		return StringUtils.defaultIfBlank(getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_NAME), StringUtils.EMPTY);
	}
	
	public String getStoreFacetTemplateName(String storeId) {
		return StringUtils.defaultIfBlank(getStoreParameter(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME), StringUtils.EMPTY);
	}
}