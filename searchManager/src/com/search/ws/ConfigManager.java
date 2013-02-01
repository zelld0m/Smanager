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
import org.apache.log4j.Logger;

import com.search.manager.dao.sp.DAOConstants;

public class ConfigManager {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private XMLConfiguration xmlConfig;
    
    private static ConfigManager instance;
    
    //TODO: will eventually move out if settings is migrated to DB instead of file
    private Map<String, PropertiesConfiguration> serverSettingsMap = new HashMap<String, PropertiesConfiguration>();
    
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
			for (String coreName: getCoreNames()) {
				File f = new File(String.format("%s%s%s.settings.properties", configFolder, File.separator, coreName));
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
					serverSettingsMap.put(coreName, propConfig);
					logger.info("Settings file for " + coreName + ": " + propConfig.getFileName());
				}
			}
			
		} catch (ConfigurationException ex) {
			ex.printStackTrace();
			logger.error(ex.getLocalizedMessage());
		}
    }
    
	@SuppressWarnings("unchecked")
	public List<String> getStoreNames() {
    	List<String> storeNames = new ArrayList<String>();
    	List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt(("/store"));
    	for (HierarchicalConfiguration hc: hcList) {
    		String name = hc.getString("@name");
    		if (!StringUtils.isEmpty(name)) {
        		storeNames.add(name);
    		}
    	}
    	return storeNames;
    }
	
	@SuppressWarnings("unchecked")
	public List<String> getCoreNames() {
    	List<String> coreNames = new ArrayList<String>();
    	List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt(("/store"));
    	for (HierarchicalConfiguration hc: hcList) {
    		String name = hc.getString("core");
    		if (!StringUtils.isEmpty(name)) {
        		coreNames.add(name);
    		}
    	}
    	return coreNames;
    }
    
    public String getStoreParameter(String coreName, String param) {
    	return (xmlConfig.getString("/store[core='" + StringUtils.lowerCase(coreName) + "']/" + param));
    }
    
    public String getStoreName(String coreName) {
    	return (xmlConfig.getString("/store[core='" + coreName + "']/@name"));
    }
    
    public String getServerParameter(String server, String param) {
    	return (xmlConfig.getString("/server[@name='" + server + "']/" +param));
    }
    
    public String getParameterByStore(String storeName, String param) {
    	return (xmlConfig.getString("/store[@name='" + storeName + "']/" + param));
    }
    
    public String getParameterByCore(String coreName, String param) {
    	return (xmlConfig.getString("/store[core='" + StringUtils.lowerCase(coreName) + "']/" + param));
    }
    
    @SuppressWarnings("unchecked")
	public List<NameValuePair> getDefaultSolrParameters(String core) {
    	List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
    	List<String> solrParamNames = (List<String>) xmlConfig.getList("/store[@name='" + getStoreName(core) + "']/solr-param-name/param-name");
    	List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>)xmlConfig.configurationsAt(("/store[@name='" + getStoreName(core) + "']/solr-param-value"));
    	
    	for (HierarchicalConfiguration hc: hcList) {
    		String solrParamName = hc.getString("@name");
    		solrParamNames.contains(solrParamName);
    		List<String> solrParamValues = hc.getList("param-value");
    		for (String solrParamValue: solrParamValues) {
    			nameValuePairList.add(new BasicNameValuePair(solrParamName, solrParamValue));
    		}
    	}
    	
    	return nameValuePairList;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, String> getServersByCore(String coreName) {
    	Map<String, String> map = new LinkedHashMap<String, String>();
    	List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>)xmlConfig.configurationsAt("/server");
    	for (HierarchicalConfiguration hc: hcList) {
    		String[] store = StringUtils.split(hc.getString("store"), ",");
    		if (ArrayUtils.contains(store, coreName)) {
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
		for (String key: keys) {
			map.put(key, map.remove(key));
		}
    	return map;
    }
    
    public String getParameter(String ... keys) {
    	StringBuilder str = new StringBuilder();
    	for (String key: keys) {
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
    
    public static ConfigManager getInstance() {
    	return instance;
    }
    
	public boolean setStoreSetting(String coreName, String field, String value) {
		PropertiesConfiguration config = serverSettingsMap.get(coreName);
		if (config != null) {
			config.setProperty(field, value);
			return StringUtils.equals(config.getString(field), value);
		}
		return false;
	}
	
	/** 
	 * For a property that has multiple values, getString() will return the first value of the list
	 * */
	public String getStoreSetting(String coreName, String field) {
		PropertiesConfiguration config = serverSettingsMap.get(coreName);
		if (config != null) {
			return config.getString(field);
		}
		return null;
	}
	
	/** 
	 * For a property that has multiple values, getList() will return the complete list 
	 * */
	@SuppressWarnings("unchecked")
	public List<String> getStoreSettings(String coreName, String field){
		PropertiesConfiguration config = serverSettingsMap.get(coreName);
		if (config != null) {
			return config.getList(field);
		}
		return null;
	}
	
    public static void main(String[] args) {
    	ConfigManager configManager = new ConfigManager("C:\\home\\solr\\conf\\solr.xml");
		System.out.println("qt: " + configManager.getStoreParameter(configManager.getStoreName("macmall"), "qt"));
//		System.out.println("query: " + configManager.getParameter("big-bets", "fields"));
//		System.out.println("query: " + configManager.getParameter("big-bets", "query"));
		System.out.println("macmall deafault solr param: " + configManager.getDefaultSolrParameters("macmall"));
		System.out.println("bd default solr param: " + configManager.getDefaultSolrParameters("pcmallcap"));
		System.out.println("query: " + configManager.getStoreName("macmall"));
		
		Map<String, String> map = configManager.getServersByCore("macmall");
		System.out.println("macmall");
		for (String key: map.keySet()) {
			System.out.println(key + "\t" + map.get(key));
		}

		map = configManager.getServersByCore("pcmallcap");
		System.out.println("pcmallcap");
		for (String key: map.keySet()) {
			System.out.println(key + "\t" + map.get(key));
		}

		for (String key: configManager.getCoreNames()) {
			System.out.println("core: " + key);
		}
		
//		configManager.setStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT, "true");
		System.out.println(configManager.getStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT));
//		configManager.setStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT, "false");
		System.out.println(configManager.getStoreSetting("pcmall", DAOConstants.SETTINGS_AUTO_EXPORT));
		
    }
    
 }