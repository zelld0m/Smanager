package com.search.manager.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service(value = "typeaheadValidationService")
public class TypeaheadValidationService {

	private static final Logger logger =
            LoggerFactory.getLogger(TypeaheadValidationService.class);
	private TypeaheadBlackList blackList;
	private XMLConfiguration xmlConfig;
	private final String path = "/home/solr/conf/typeaheadblacklist.xml";

	public TypeaheadValidationService() {

		try {
			xmlConfig = new XMLConfiguration();
			xmlConfig.setDelimiterParsingDisabled(false);
			xmlConfig.setExpressionEngine(new XPathExpressionEngine());
			xmlConfig.load(path);
			xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			xmlConfig.addConfigurationListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					if (!event.isBeforeUpdate()) {
						reloadConfig();
					}
				}
			});
			logger.debug("Typeahead Blacklist Config Folder: " + xmlConfig.getFile().getAbsolutePath());
			reloadConfig();
		} catch (ConfigurationException ex) {
			logger.error(ex.getLocalizedMessage());
		}
	}

	public boolean validateKeyword(String store, String keyword) {

		//To invoke the configurationChanged method
		xmlConfig.configurationsAt("/blacklistMap");
		
		List<String> expressionList = blackList.getExpressions().get(store) != null ? blackList.getExpressions().get(store).getList() : null;

		if(expressionList == null) return true;

		for(String regex : expressionList) {
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(keyword);
			if(matcher.matches()) {
				return false;
			}
		}

		return true;
	}

	private void reloadConfig() {
		logger.debug("reloading Typeahead Blacklist Config Folder: " + xmlConfig.getFile().getAbsolutePath());
		File file = new File(path);

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(TypeaheadBlackList.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			blackList = (TypeaheadBlackList) jaxbUnmarshaller.unmarshal(file);
			logger.debug("done reloading Typeahead Blacklist Config Folder: " + xmlConfig.getFile().getAbsolutePath());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@XmlRootElement(name = "blacklist")
	private static class TypeaheadBlackList {

		private HashMap<String, ListWrapper> expressions;

		@XmlElementWrapper(name = "blacklistMap")
		public HashMap<String, ListWrapper> getExpressions() {
			return expressions;
		}

		@SuppressWarnings("unused")
		public void setExpressions(HashMap<String, ListWrapper> expressions) {
			this.expressions = expressions;
		}

	}

	private static class ListWrapper {

		private List<String> list;

		@XmlElementWrapper(name = "expressions")
		public List<String> getList() {
			return list;
		}

		@SuppressWarnings("unused")
		public void setList(List<String> list) {
			this.list = list;
		}

	}

	public static void main(String[] args) {
		TypeaheadValidationService service = new TypeaheadValidationService();

		for(int i=0; i< 5; i++)
			System.out.println(service.validateKeyword("pcmall", "1111111"));
	}

}
