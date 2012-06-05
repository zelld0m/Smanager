package com.search.manager.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class MessagesConfig {

	private final static Logger logger = Logger.getLogger(MessagesConfig.class);
	
	private static Map<String, String> msgMap = new HashMap<String, String>();

	private XMLConfiguration xmlConfig;
	
	private static MessagesConfig instance;

	public static synchronized MessagesConfig getInstance() {
		return instance;
	}
	
	public static synchronized MessagesConfig getInstance(String configPath) {
		if (instance == null) {
			instance = new MessagesConfig(configPath);
		}
		return instance;
	}
	
	private MessagesConfig(String configPath) {
		try {
			xmlConfig = new XMLConfiguration();
			xmlConfig.setDelimiterParsingDisabled(false);
			xmlConfig.setExpressionEngine(new XPathExpressionEngine());
			xmlConfig.load(configPath);
			xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			xmlConfig.addConfigurationListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					if (!event.isBeforeUpdate()) {
						reloadConfig();
					}
				}
			});
			logger.debug("Message Config Folder: " + xmlConfig.getFile().getAbsolutePath());
			reloadConfig();
		} catch (ConfigurationException ex) {
			logger.error(ex.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void reloadConfig() {
		synchronized (this) {
			logger.info("Beginning to load messages config file");
			for (Configuration c: (List<Configuration>)xmlConfig.configurationsAt("/message")) {
				msgMap.put(c.getString("@name"), c.getString("description"));
			}
			logger.info("done loading messages config file");
		}
	}
	
	public String getMessage(String name, Object...params){
		String message = "";
		if (StringUtils.isNotEmpty(msgMap.get(name))) {
			message = String.format(msgMap.get(name), params);
		}
		return message;
	}
}
