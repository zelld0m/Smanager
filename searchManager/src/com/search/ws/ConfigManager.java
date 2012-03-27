package com.search.ws;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

public class ConfigManager {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private XMLConfiguration xmlConfig;
    
    private static ConfigManager instance;
    
    private ConfigManager(String configPath) {
		try {
			xmlConfig = new XMLConfiguration();
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setExpressionEngine(new XPathExpressionEngine());
			xmlConfig.load(configPath);
			xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			logger.debug("Search Config Folder: " + xmlConfig.getFile().getAbsolutePath());
		} catch (ConfigurationException ex) {
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
    
    public String getStoreParameter(String coreName, String param) {
    	return (xmlConfig.getString("/store[core='" + StringUtils.lowerCase(coreName) + "']/" + param));
    }
    
    public String getStoreName(String coreName) {
    	return (xmlConfig.getString("/store[core='" + coreName + "']/@name"));
    }
    
    public String getServerParameter(String server, String param) {
    	return (xmlConfig.getString("/server[@name='" + server + "']/" +param));
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
    
    public static void main(String[] args) {
    	ConfigManager configManager = new ConfigManager("C:\\home\\widgetti\\conf\\solr.xml");
		System.out.println("qt: " + configManager.getStoreParameter(configManager.getStoreName("macmall"), "qt"));
//		System.out.println("query: " + configManager.getParameter("big-bets", "fields"));
//		System.out.println("query: " + configManager.getParameter("big-bets", "query"));
		System.out.println("query: " + configManager.getDefaultSolrParameters("macmall"));
		System.out.println("query: " + configManager.getStoreName("macmall"));
    }
    
}