package com.search.manager.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.search.manager.schema.analyzer.model.AnalyzerComponent;
import com.search.manager.schema.analyzer.model.AnalyzerComponent.AnalyzerComponentType;
import com.search.manager.schema.model.bf.Function;
import com.search.manager.schema.model.bf.Function.ArgumentConstraint;
import com.search.manager.schema.model.bq.FilterOperator;
import com.search.manager.schema.model.bq.LogicalOperator;
import com.search.manager.schema.model.bq.Operator;

public class RelevancyConfig {

	private final static Logger logger = Logger.getLogger(RelevancyConfig.class);
	
	// TODO: integrate ehCache
	private static Map<String, Function> functionMap = new HashMap<String, Function>();
	private static Map<String, AnalyzerComponent> filterMap = new HashMap<String, AnalyzerComponent>();
	private static Map<String, AnalyzerComponent> tokenizerMap = new HashMap<String, AnalyzerComponent>();
	private static Map<String, LogicalOperator> logicalOperatorMap = new HashMap<String, LogicalOperator>();
	private static Map<String, FilterOperator> filterOperatorMap = new HashMap<String, FilterOperator>();
	private static List<Operator> logicalOperators;
	private static List<Operator> filterOperators;

	private XMLConfiguration xmlConfig;
	
	private static RelevancyConfig instance;

	public static synchronized RelevancyConfig getInstance() {
		return instance;
	}
	
	public static synchronized RelevancyConfig getInstance(String configPath) {
		if (instance == null) {
			instance = new RelevancyConfig(configPath);
		}
		return instance;
	}
	
	// TODO: inject this into ConfigServlet like ConfigManager
	private RelevancyConfig(String configPath) {
		try {
			xmlConfig = new XMLConfiguration();
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setExpressionEngine(new XPathExpressionEngine());
			xmlConfig.load(configPath);
			xmlConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
			xmlConfig.addConfigurationListener(new ConfigurationListener() {
				@Override
				public void configurationChanged(ConfigurationEvent event) {
					if (!event.isBeforeUpdate()) {
						reloadSchema();
					}
				}
			});
			logger.debug("Search Config Folder: " + xmlConfig.getFile().getAbsolutePath());
			reloadSchema();
		} catch (ConfigurationException ex) {
			logger.error(ex.getLocalizedMessage());
		}
	}
	
	public List<String> getIgnoredFields() {
		List<String> list = new ArrayList<String>();
		for (String str : xmlConfig.getString("/ignore-schema-fields").split(",")) {
			list.add(StringUtils.trim(str));
		}
		return list;
	}
	
	public List<Function> getAllFunctions() {
		return new ArrayList<Function>(functionMap.values());
	}

	public Function getFunction(String name) {
		return functionMap.get(StringUtils.lowerCase(name));
	}

	public AnalyzerComponent getTokenizer(String name) {
		return tokenizerMap.get(StringUtils.lowerCase(name));
	}

	public AnalyzerComponent getFilter(String name) {
		return filterMap.get(StringUtils.lowerCase(name));
	}

	
	public List<Operator> getAllLogicalOperators() {
		return new ArrayList<Operator>(logicalOperators);
	}

	public LogicalOperator getLogicalOperator(String name) {
		return logicalOperatorMap.get(StringUtils.lowerCase(name));
	}
	
	public List<Operator> getAllFilterOperators() {
		return new ArrayList<Operator>(filterOperators);
	}
	
	public FilterOperator getFilterOperator(String name) {
		return filterOperatorMap.get(StringUtils.lowerCase(name));
	}
	
	private void reloadSchema() {
		synchronized (SolrSchemaUtility.class) {
			logger.info("Beginning to load relevancy config file");
			getFunctions();
			getFilters();
			getTokenizers();
			getLogicalOperators();
			getFilterOperators();
			logger.info("done loading relevancy config file");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getFunctions() {
		try {
			Map<String, Function> tmpMap = new HashMap<String, Function>();
	    	for (Configuration c: (List<Configuration>)xmlConfig.configurationsAt("/functions/function")) {
	    		Function function = new Function();
	    		function.setName(c.getString("@name"));
	    		function.setDescription(c.getString("description"));
	    		function.setDisplayText(c.getString("display"));
	    		function.setMinArgs(c.getInt("min-arg"));
	    		String maxArg = c.getString("max-arg");
	    		if (StringUtils.isNotEmpty(maxArg) && StringUtils.isNumeric(maxArg)) {
		    		function.setMaxArgs(Integer.parseInt(maxArg));
	    		}
	    		
	    		int i = 0;
	    		while (true) {
		    		i++;
		    		if (c.containsKey("constraints/arg[" + i + "]/@position")) {
			    		String position = c.getString("constraints/arg[" + i + "]/@position");
			    		String type = c.getString("constraints/arg[" + i + "]/type");
			    		ArgumentConstraint constraint = ("NumericConstant".equalsIgnoreCase(type)) ? ArgumentConstraint.NUMERIC_CONSTANT :
			    							("Date".equalsIgnoreCase(type)) ? ArgumentConstraint.DATE : ArgumentConstraint.NORMAL;
			    		for (String pos: position.split(",")) {
			    			logger.debug("Constraint for argument #" + pos + " is "  + constraint);
			    			function.setArgumentConstraint(Integer.parseInt(pos), constraint);
			    		}
		    		}
		    		else {
		    			break;
		    		}
	    		}
	    		logger.debug("Added function: " + function.getName());
	    		tmpMap.put(StringUtils.lowerCase(function.getName()), function);
	    		functionMap = tmpMap;
	    	}
		}
		catch (Exception e) {
			logger.error("Error reading configuration file: " + xmlConfig.getFileName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void getFilters() {
		try {
			Map<String, AnalyzerComponent> tmpMap = new HashMap<String, AnalyzerComponent>();
			for (Configuration c: (List<Configuration>)xmlConfig.configurationsAt("/tokenizers/tokenizer")) {
				AnalyzerComponent link = new AnalyzerComponent(c.getString("@class"), c.getString("@name"), c.getString("description"), AnalyzerComponentType.Tokenizer);
				tmpMap.put(StringUtils.lowerCase(link.getName()), link);
	    		logger.debug("Added filter: " + link.getName());
				filterMap = tmpMap;
	    	}
		}
		catch (Exception e) {
			logger.error("Error reading configuration file: " + xmlConfig.getFileName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void getLogicalOperators() {
		try {
			Map<String, LogicalOperator> tmpMap = new HashMap<String, LogicalOperator>();
			List<Operator> tmpList = new ArrayList<Operator>();
			for (Configuration c: (List<Configuration>)xmlConfig.configurationsAt("/expressions/expression")) {
				LogicalOperator expression = new LogicalOperator(c.getString("@name"), c.getString("text"), c.getString("regexp"),
						c.getBoolean("lvalue"), c.getBoolean("rvalue"), c.getInt("priority"));
				tmpMap.put(StringUtils.lowerCase(expression.getName()), expression);
				tmpList.add(expression);
	    		logger.debug("Added boolean operator: " + expression.getName());
	    	}
			logicalOperatorMap = tmpMap;
			Collections.sort(tmpList, new Comparator<Operator>(){
				@Override
				public int compare(Operator p1, Operator p2) {
					int cmp = p1.getPriority() - p2.getPriority();
					if (cmp == 0) {
						cmp = StringUtils.length(p2.getRegExp()) - StringUtils.length(p1.getRegExp());
					}
					return cmp;
				}
			});
			logicalOperators = tmpList;
		}
		catch (Exception e) {
			logger.error("Error reading configuration file: " + xmlConfig.getFileName(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getFilterOperators() {
		try {
			Map<String, FilterOperator> tmpMap = new HashMap<String, FilterOperator>();
			List<Operator> tmpList = new ArrayList<Operator>();
			for (Configuration c: (List<Configuration>)xmlConfig.configurationsAt("/filter_expressions/filter_expression")) {
				FilterOperator expression = new FilterOperator(c.getString("@name"), c.getString("text"), c.getString("regexp"),
						c.getBoolean("lvalue"), c.getBoolean("rvalue"), c.getInt("priority"));
				tmpMap.put(StringUtils.lowerCase(expression.getName()), expression);
				tmpList.add(expression);
	    		logger.debug("Added logical operator: " + expression.getName());
	    	}
			filterOperatorMap = tmpMap;
			tmpList.addAll(logicalOperators);
			Collections.sort(tmpList, new Comparator<Operator>(){
				@Override
				public int compare(Operator p1, Operator p2) {
					int cmp = p1.getPriority() - p2.getPriority();
					if (cmp == 0) {
						cmp = StringUtils.length(p2.getRegExp()) - StringUtils.length(p1.getRegExp());
					}
					return cmp;
				}
			});
			filterOperators = tmpList;
		}
		catch (Exception e) {
			logger.error("Error reading configuration file: " + xmlConfig.getFileName(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getTokenizers() {
		try {
			Map<String, AnalyzerComponent> tmpMap = new HashMap<String, AnalyzerComponent>();
			for (Configuration c: (List<Configuration>)xmlConfig.configurationsAt("/filters/filter")) {
				AnalyzerComponent link = new AnalyzerComponent(c.getString("@class"), c.getString("@name"), c.getString("description"), AnalyzerComponentType.Tokenizer);
				tmpMap.put(StringUtils.lowerCase(link.getName()), link);
	    		logger.debug("Added tokenizer: " + link.getName());
				tokenizerMap = tmpMap;
	    	}
		}
		catch (Exception e) {
			logger.error("Error reading configuration file: " + xmlConfig.getFileName(), e);
		}
	}

}
