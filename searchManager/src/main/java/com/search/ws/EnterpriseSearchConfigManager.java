package com.search.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Relevancy;
import com.search.ws.EnterpriseSearchConfigManager.SearchConfiguration.SearchRuleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterpriseSearchConfigManager {

    public static class SearchConfiguration {

        public enum FieldOverride {

            facetTemplate,
            productName,
            productDescription,
            popularity
        }

        public static class SearchRuleConfiguration {

            boolean isActive;
            String overrideWith;
        }
        private String catalog;
        private String storeFlag;

        public String getCatalog() {
            return catalog;
        }

        public String getStoreFlag() {
            return storeFlag;
        }

        public String getDismax() {
            return dismax;
        }
        
        public String getDefType() {
			return defType;
		}

		public Relevancy getDefaultRelevancy() {
            return defaultRelevancy;
        }

        public String getSearchRuleOverride() {
            return searchRuleOverride;
        }

        public Map<RuleEntity, SearchRuleConfiguration> getActiveRules() {
            return activeRules;
        }

        public String[] getFieldOverrides() {
            return fieldOverrides;
        }
        private String dismax;
        private String defType;
        private Relevancy defaultRelevancy;
        private String searchRuleOverride;
        private String storeSpecificFieldsOverride;
        private Map<RuleEntity, SearchRuleConfiguration> activeRules = new HashMap<RuleEntity, SearchRuleConfiguration>();
        private String[] fieldOverrides = new String[4];

        public boolean isRuleActive(RuleEntity searchRule) {
            return (activeRules.get(searchRule) != null && activeRules.get(searchRule).isActive);
        }

        public String getStoreOverride(RuleEntity searchRule) {
            return (activeRules.get(searchRule) != null ? activeRules.get(searchRule).overrideWith : "");
        }

        public SearchConfiguration(String catalog, String storeFlag, String dismax, String defType, String facetTemplate, String productName,
                String productDescription, String popularity, String searchRuleOverride, String storeSpecificFieldsOverride) {
            this.catalog = catalog;
            this.dismax = dismax;
            this.defType = defType;
            this.storeFlag = storeFlag;
            fieldOverrides[0] = facetTemplate;
            fieldOverrides[1] = productName;
            fieldOverrides[2] = productDescription;
            fieldOverrides[3] = popularity;
            this.searchRuleOverride = searchRuleOverride;
            this.storeSpecificFieldsOverride = storeSpecificFieldsOverride;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("catalog: %s\nstoreFlag: %s\ndismax: %s\ndefType: %s\nfacetTemplate: %s\nproductName: %s\nproductDescription: %s\npopularity: %s\nsearchRuleOverride: %s\nstoreSpecificFieldsOverride: %s\n",
                    catalog, storeFlag, dismax, defType,
                    fieldOverrides[0], fieldOverrides[1], fieldOverrides[2], fieldOverrides[3], searchRuleOverride, storeSpecificFieldsOverride));
            if (defaultRelevancy != null) {
                builder.append("default relevancy: \n");
                for (String key : defaultRelevancy.getParameters().keySet()) {
                    builder.append("\t" + key + ": " + defaultRelevancy.getParameter(key) + "\n");
                }
            }
            return builder.toString();
        }

        public String getStoreSpecificFieldsOverride() {
            return storeSpecificFieldsOverride;
        }
    }
    private static final Logger logger =
            LoggerFactory.getLogger(EnterpriseSearchConfigManager.class);
    private XMLConfiguration xmlConfig;
    private static EnterpriseSearchConfigManager instance;
    private static Map<String, SearchConfiguration> rules = new HashMap<String, SearchConfiguration>();

    private EnterpriseSearchConfigManager(String configPath) {
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
                        reloadConfiguration();
                    }
                }
            });
            reloadConfiguration();
            logger.debug("Search Config Folder: " + xmlConfig.getFile().getAbsolutePath());
        } catch (ConfigurationException ex) {
            ex.printStackTrace();
            logger.error(ex.getLocalizedMessage());
        }
    }

    public SearchConfiguration getSearchConfiguration(String storeName) {
        checkReloadNeeded();
        return rules.get(storeName);
    }

    private String[] getStoreSpecificFields(String storeName) {
        checkReloadNeeded();
        SearchConfiguration sc = getSearchConfiguration(storeName);
        while (StringUtils.isNotBlank(sc.storeSpecificFieldsOverride)) {
            // TODO: lookout for circular loop
            sc = getSearchConfiguration(sc.storeSpecificFieldsOverride);
        }
        return sc.fieldOverrides;
    }

    public boolean isActiveSearchRule(String storeName, RuleEntity searchRule) {
        checkReloadNeeded();
        SearchConfiguration sc = getSearchConfiguration(storeName);
        while (sc != null && StringUtils.isNotBlank(sc.getSearchRuleOverride())) {
            // TODO: lookout for circular loop
            sc = getSearchConfiguration(sc.getSearchRuleOverride());
        }
        return (sc == null) ? false : sc.isRuleActive(searchRule);
    }

    public String getSearchRuleCore(String storeName, RuleEntity searchRule) {
        checkReloadNeeded();
        if (!isActiveSearchRule(storeName, searchRule)) {
            return null;
        }

        SearchConfiguration sc = getSearchConfiguration(storeName);
        String core = storeName;
        String newCore = null;
        while (sc != null && StringUtils.isNotBlank(sc.getSearchRuleOverride())) {
            // TODO: lookout for circular loop
            newCore = sc.getSearchRuleOverride();
            if (StringUtils.isNotBlank(newCore)) {
                core = newCore;
            }
            sc = getSearchConfiguration(sc.getSearchRuleOverride());
        }

        if (sc != null) {
            SearchRuleConfiguration src = sc.activeRules.get(searchRule);
            if (src != null && StringUtils.isNotEmpty(src.overrideWith)) {
                core = src.overrideWith;
            }
        }
        return core;
    }

    public String getStoreFlag(String storeName) {
        checkReloadNeeded();
        SearchConfiguration sc = getSearchConfiguration(storeName);
        return (sc != null) ? sc.getStoreFlag() : null;
    }
    
    public String getDefType(String storeName) {
        checkReloadNeeded();
        SearchConfiguration sc = getSearchConfiguration(storeName);
        return (sc != null) ? sc.getDefType() : null;
    }

    public Relevancy getRelevancy(String storeName) {
        checkReloadNeeded();
        SearchConfiguration sc = getSearchConfiguration(storeName);
        while (sc != null && StringUtils.isNotBlank(sc.getSearchRuleOverride())) {
            // TODO: lookout for circular loop
            sc = getSearchConfiguration(sc.getSearchRuleOverride());
        }
        return (sc != null) ? sc.defaultRelevancy : null;
    }

    public String getDismax(String storeName) {
        checkReloadNeeded();
        SearchConfiguration sc = getSearchConfiguration(storeName);
        while (sc != null && StringUtils.isNotBlank(sc.getSearchRuleOverride())) {
            // TODO: lookout for circular loop
            sc = getSearchConfiguration(sc.getSearchRuleOverride());
        }
        return (sc != null) ? sc.getDismax() : null;
    }

    private String getString(Configuration config, String key) {
        return StringUtils.trim(config.getString(key));
    }

    private void reloadConfiguration() {
        synchronized (this) {
            Map<String, SearchConfiguration> newRules = new HashMap<String, SearchConfiguration>();
            for (String storeName : getStoreNames()) {

                SubnodeConfiguration config = xmlConfig.configurationAt(String.format("/store[@name='%s']", storeName));
                SearchConfiguration sc = new SearchConfiguration(
                        config.getString("catalog"),
                        config.getString("store-flag"),
                        config.getString("dismax"),
                        config.getString("defType"),
                        config.getString("store-specific-fields/facet-template"),
                        config.getString("store-specific-fields/product-name"),
                        config.getString("store-specific-fields/product-description"),
                        config.getString("store-specific-fields/popularity"),
                        config.getString("search-rule/@override"),
                        config.getString("store-specific-fields/@override"));

                // relevancy
                // workaround for checking if node exists
                if (StringUtils.isNotBlank(config.getString("default-relevancy/qf"))) {
                    SubnodeConfiguration relevancyConfiguration = xmlConfig.configurationAt(String.format("/store[@name='%s']/default-relevancy", storeName));
                    Relevancy relevancy = new Relevancy();
                    relevancy.setAlternateQuery(getString(relevancyConfiguration, "q.alt"));
                    relevancy.setBoostFunction(getString(relevancyConfiguration, "bf"));
                    relevancy.setBoostQuery(getString(relevancyConfiguration, "bq"));
                    relevancy.setMinimumToMatch(getString(relevancyConfiguration, "mm"));
                    relevancy.setPhraseFields(getString(relevancyConfiguration, "pf"));
                    relevancy.setPhraseSlop(getString(relevancyConfiguration, "ps"));
                    relevancy.setQueryFields(getString(relevancyConfiguration, "qf"));
                    relevancy.setQuerySlop(getString(relevancyConfiguration, "qs"));
                    relevancy.setTieBreaker(getString(relevancyConfiguration, "tie"));
                    relevancy.setRuleName("dismax" + storeName + "relevancy");
                    sc.defaultRelevancy = relevancy;
                }
                sc.activeRules.put(RuleEntity.ELEVATE, generateSearchRuleConfiguration(getString(config, "search-rule/elevate"),
                        getString(config, "search-rule/elevate/@override")));
                sc.activeRules.put(RuleEntity.EXCLUDE, generateSearchRuleConfiguration(getString(config, "search-rule/exclude"),
                        getString(config, "search-rule/exclude/@override")));
                sc.activeRules.put(RuleEntity.DEMOTE, generateSearchRuleConfiguration(getString(config, "search-rule/demote"),
                        getString(config, "search-rule/demote/@override")));
                sc.activeRules.put(RuleEntity.FACET_SORT, generateSearchRuleConfiguration(getString(config, "search-rule/facet-sort"),
                        getString(config, "search-rule/facet-sort/@override")));
                sc.activeRules.put(RuleEntity.QUERY_CLEANING, generateSearchRuleConfiguration(getString(config, "search-rule/redirect"),
                        getString(config, "search-rule/redirect/@override")));
                sc.activeRules.put(RuleEntity.RANKING_RULE, generateSearchRuleConfiguration(getString(config, "search-rule/relevancy"),
                        getString(config, "search-rule/relevancy/@override")));

                newRules.put(storeName, sc);
            }
            rules = newRules;
        }
    }

    private void checkReloadNeeded() {
        xmlConfig.getString("/store/name");
    }

    private SearchRuleConfiguration generateSearchRuleConfiguration(String value, String value2) {
        SearchRuleConfiguration src = null;
        if (value != null || value2 != null) {
            src = new SearchRuleConfiguration();
            src.isActive = true;
            if (StringUtils.isNotEmpty(value2)) {
                src.overrideWith = value2;
            }
        }
        return src;
    }

    @SuppressWarnings("unchecked")
    private List<String> getStoreNames() {
        List<String> coreNames = new ArrayList<String>();
        List<HierarchicalConfiguration> hcList = (List<HierarchicalConfiguration>) xmlConfig.configurationsAt(("/store"));
        for (HierarchicalConfiguration hc : hcList) {
            String name = hc.getString("@name");
            if (!StringUtils.isEmpty(name)) {
                coreNames.add(name);
            }
        }
        return coreNames;
    }

    public Map<String, String> getFieldOverrideMap(String storeName, String storeOverrideName) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotBlank(storeOverrideName) && StringUtils.isNotBlank(storeName)) {
            String[] originalList = getStoreSpecificFields(storeOverrideName);
            String[] replacementList = getStoreSpecificFields(storeName);
            for (int i = 0; i < originalList.length; i++) {
                if (StringUtils.isNotBlank(originalList[i]) && StringUtils.isNotBlank(replacementList[i]) && !originalList[i].equals(replacementList[i])) {
                    map.put(originalList[i], replacementList[i]);
                }
            }
        }
        return map;
    }

    public synchronized static EnterpriseSearchConfigManager getInstance(String configPath) {
        if (instance == null) {
            instance = new EnterpriseSearchConfigManager(configPath);
        }
        return instance;
    }

    public static EnterpriseSearchConfigManager getInstance() {
        return instance;
    }

    public static void main(String[] args) {

        //http://afs-pl-schpd01.afservice.org:8080/solr14/enterpriseSearch/?
//    	String url = "http://afs-pl-schpd01.afservice.org:8080/solr14/enterpriseSearch/select?store=pcmall";
//		Pattern pathPattern1 = Pattern.compile("http://(.*):.*/(.*)/(.*)/select.*");

        EnterpriseSearchConfigManager configManager = new EnterpriseSearchConfigManager("C:\\home\\solr\\conf\\enterpriseSearch.xml");
//		for (String key: configManager.getStoreNames()) {
//			System.out.println(configManager.getSearchConfiguration(key));
//			for (RuleEntity rule: RuleEntity.values()) {
//				logger.info(key + " " + rule + " " + configManager.isActiveSearchRule(key, rule) + " " + configManager.getSearchRuleCore(key, rule));
//			}
//			for (String fieldOverride: configManager.getStoreSpecificFields(key)) {
//				logger.info(fieldOverride);
//			}
//			logger.info(configManager.getStoreFlag(key));
//			System.out.println();
//		}

        String key = "macmall";
        RuleEntity rule = RuleEntity.QUERY_CLEANING;
        logger.info(key + " " + rule + " " + configManager.isActiveSearchRule(key, rule) + " " + configManager.getSearchRuleCore(key, rule));

        Map<String, String> map = configManager.getFieldOverrideMap("ecost", "ecost");
        logger.info("ecost" + " " + RuleEntity.EXCLUDE + " " + configManager.isActiveSearchRule("ecost", RuleEntity.EXCLUDE) + " " + configManager.getSearchRuleCore("ecost", RuleEntity.EXCLUDE));

        for (String str : map.keySet()) {
            logger.info(str);
        }
        for (String str : map.values()) {
            logger.info(str);
        }

        String strCondition = StringUtils.replaceEach("EDP2^0 DPNo2^0 Description^0 UPC2^0 RelevantIDKey^4 NameIndex^2 MfrPN2^0 MfrPN^1 SubCategoryIndex^3 PCMall_FacetTemplateNameIndex^8 ManufacturerIndex^2 CategoryIndex^6 UPC^2",
                map.keySet().toArray(new String[0]), map.values().toArray(new String[0]));
        logger.info(strCondition);


        logger.info(configManager.getDefType("pcoe"));
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace(); 
//		}
//		logger.info(key + " " + rule + " " + configManager.isActiveSearchRule(key, rule) + " " + configManager.getSearchRuleCore(key, rule));
    }
}