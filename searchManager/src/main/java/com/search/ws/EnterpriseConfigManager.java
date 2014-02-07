package com.search.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.model.Relevancy;
import com.search.manager.model.Relevancy.Parameter;

public class EnterpriseConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseConfigManager.class);
    private static XMLConfiguration enterpriseXMLConfig;
    private static List<String> storeIdList = new ArrayList<String>();
    private static List<String> storeCoreList = new ArrayList<String>();
    private static List<String> parentStoreList = new ArrayList<String>();
    private static Map<String, String> storeNameMap = new HashMap<String, String>();
    private static Map<String, String> dismaxMap = new HashMap<String, String>();
    private static Map<String, String> defTypeMap = new HashMap<String, String>();
    private static Map<String, String> storeFlagMap = new HashMap<String, String>();
    private static Map<String, String> storeParentMap = new HashMap<String, String>();
    private static Map<String, Relevancy> relevancyMap = new HashMap<String, Relevancy>();

    @Autowired
    public EnterpriseConfigManager(String enterpriseXMLFile) {
        super();
        try {
            enterpriseXMLConfig = new XMLConfiguration();
            enterpriseXMLConfig.setDelimiterParsingDisabled(true);
            enterpriseXMLConfig.setExpressionEngine(new XPathExpressionEngine());
            enterpriseXMLConfig.load(enterpriseXMLFile);
            enterpriseXMLConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
            enterpriseXMLConfig.addConfigurationListener(new ConfigurationListener() {
                @Override
                public void configurationChanged(ConfigurationEvent event) {
                    if (!event.isBeforeUpdate()) {
                        initializeXmlConfigProperties();
                    }
                }
            });
            logger.info("Loaded file path: {}", enterpriseXMLConfig.getFile().getAbsolutePath());
            initializeXmlConfigProperties();
        } catch (ConfigurationException e) {
            logger.error("Failed initialization of enterprise config file: {}", enterpriseXMLFile);
        }
    }

    public String getStoreParameterValue(String storeId, String xPathTag){
        String parentStoreId = getParentStoreIdByAlias(storeId);
        String storeParam = StringUtils.defaultIfBlank(enterpriseXMLConfig.getString(String.format(xPathTag, storeId)), StringUtils.EMPTY);

        if(StringUtils.isNotBlank(parentStoreId) && !StringUtils.equalsIgnoreCase(parentStoreId, storeId)){
            String parentParam = StringUtils.defaultIfBlank(enterpriseXMLConfig.getString(String.format(xPathTag, parentStoreId)), StringUtils.EMPTY);
            return StringUtils.isEmpty(storeParam)? parentParam: storeParam;
        }

        return storeParam;
    }

    private void initializeXmlConfigProperties(){
        storeIdList = Arrays.asList(enterpriseXMLConfig.getStringArray("/store/@id"));
        storeCoreList = Arrays.asList(enterpriseXMLConfig.getStringArray("/store/@core"));
        parentStoreList = Arrays.asList(enterpriseXMLConfig.getStringArray("/store[@core and string-length(@core)!=0]/@id"));

        for(String storeId: parentStoreList){
            String[] storeIdAliases = StringUtils.split(StringUtils.trim(StringUtils.defaultIfBlank(enterpriseXMLConfig.getString(String.format("/store[@id='%s']/store-id-aliases",storeId)),StringUtils.EMPTY)).replaceAll("\\s+", " "),',');
            for(String aliases: storeIdAliases){
                storeParentMap.put(aliases, storeId);
            }
        }

        for(String storeId: getAllStoreIds()){
            Relevancy relevancy = new Relevancy();
            relevancy.setAlternateQuery(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_ALTERNATE_QUERY));
            relevancy.setBoostFunction(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_BOOST_FUNCTION));
            relevancy.setBoostQuery(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_BOOST_QUERY));
            relevancy.setMinimumToMatch(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_MIN_TO_MATCH));
            relevancy.setPhraseFields(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_PHRASE_FIELDS));
            relevancy.setPhraseSlop(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_PHRASE_SLOP));
            relevancy.setQueryFields(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_QUERY_FIELDS));
            relevancy.setQuerySlop(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_QUERY_SLOP));
            relevancy.setTieBreaker(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_TIE_BREAKER));
            relevancy.setTieBreaker(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_PHRASE_BIGRAM_FIELDS));
            relevancy.setTieBreaker(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_PHRASE_BIGRAM_SLOP));
            relevancy.setTieBreaker(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_PHRASE_TRIGRAM_FIELDS));
            relevancy.setTieBreaker(getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-default/" + Parameter.PARAM_PHRASE_TRIGRAM_SLOP));
            relevancyMap.put(storeId, relevancy);
            storeNameMap.put(storeId, getStoreParameterValue(storeId, "/store[@id='%s']/@name"));
            dismaxMap.put(storeId, getStoreParameterValue(storeId, "/store[@id='%s']/dismax"));
            storeFlagMap.put(storeId, getStoreParameterValue(storeId, "/store[@id='%s']/store-flag"));
            defTypeMap.put(storeId, getStoreParameterValue(storeId, "/store[@id='%s']/defType"));
        }
    }

    public List<String> getAllStoreNames(){
        return new ArrayList<String>(storeNameMap.values());
    }

    public List<String> getAllStoreIds(){
        return storeIdList;
    }

    public List<String> getAllCores(){
        return storeCoreList;
    }

    public List<String> getAllParentStore(){
        return parentStoreList;
    }

    public String getStoreName(String storeId){
        return StringUtils.defaultIfBlank(storeNameMap.get(storeId), StringUtils.EMPTY);
    }
    
    public String getParentStoreIdByAlias(String storeId) {
        return StringUtils.defaultIfBlank(storeParentMap.get(storeId), storeId);
    }

    public String getStoreFlag(String storeId) {
        return storeFlagMap.get(storeId);
    }

    public String getDismax(String storeId) {
        return dismaxMap.get(storeId);
    }

    public String getDefType(String storeId) {
        return defTypeMap.get(storeId);
    }

    public String getPopularity(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/popularity");
    }

    public String getFacetTemplate(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/facet-template");
    }

    public String getProductName(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/product-name");
    }

    public String getProductDescription(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/product-description");
    }

    public Relevancy getDefaultRelevancy(String storeId){

        if(MapUtils.isEmpty(relevancyMap)){
            initializeXmlConfigProperties();
        }

        Relevancy relevancy = relevancyMap.get(storeId);

        if(relevancy==null){
            logger.error("No defined default relevancy for {}", storeId);
        }

        return relevancy;
    }
}