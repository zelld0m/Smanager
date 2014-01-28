package com.search.ws;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.model.Relevancy;
import com.search.manager.model.Relevancy.Parameter;

public class EnterpriseConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseConfigManager.class);
    private static XMLConfiguration enterpriseXMLConfig;

    @Autowired
    public EnterpriseConfigManager(String enterpriseXMLFile) {
        super();
        try {
            enterpriseXMLConfig = new XMLConfiguration();
            enterpriseXMLConfig.setDelimiterParsingDisabled(true);
            enterpriseXMLConfig.setExpressionEngine(new XPathExpressionEngine());
            enterpriseXMLConfig.load(enterpriseXMLFile);
            enterpriseXMLConfig.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            logger.error("Failed initialization of enterprise config file: {}", enterpriseXMLFile);
        }
        
    }
    
    public List<String> getAllStoreNames(){
        return Arrays.asList(enterpriseXMLConfig.getStringArray("/store/@name"));
    }

    public List<String> getAllStoreIds(){
        return Arrays.asList(enterpriseXMLConfig.getStringArray("/store/@id"));
    }

    public List<String> getAllCores(){
        return Arrays.asList(enterpriseXMLConfig.getStringArray("/store/@core"));
    }
    
    public List<String> getAllParentStore(){
        return Arrays.asList(enterpriseXMLConfig.getStringArray("/store[@core and string-length(@core)!=0]/@id"));
    }

    public String getParentStoreIdByAlias(String storeId) {
        
        for(String sId: getAllParentStore()){
            String[] storeIdAliases = StringUtils.split(StringUtils.trim(StringUtils.defaultIfBlank(enterpriseXMLConfig.getString(String.format("/store[@id='%s']/store-id-aliases",sId)),StringUtils.EMPTY)).replaceAll("\\s+", " "),',');
            if(ArrayUtils.contains(storeIdAliases, storeId)){
                storeId = sId;
                break;
            }
        }
        
        return storeId;
    }
    
    public String[] getStoreParameterValues(String storeId, String xPathTag){
        String concatenatedParamValues = StringUtils.trim(getStoreParameterValue(storeId, xPathTag)).replaceAll("\\s+", " ");
        return StringUtils.split(concatenatedParamValues,",");
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

    public String getStoreFlag(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/store-flag");
    }

    public String getDismax(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/dismax");
    }

    public String getDefType(String storeId) {
        return getStoreParameterValue(storeId, "/store[@id='%s']/defType");
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

        return relevancy;
    }
}