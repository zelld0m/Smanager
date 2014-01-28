package com.search.manager.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Relevancy.Parameter;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.ParameterUtils;
import com.search.ws.EnterpriseConfigManager;
import com.search.ws.SolrConstants;

@Controller
public class EnterpriseSearchController extends AbstractSearchController {

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseSearchController.class);
    @Autowired
    private EnterpriseConfigManager enterpriseConfigManager;

    @RequestMapping("/enterpriseSearch/**")
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.handleRequest(request, response);
    }
    
    @Override
    public String applyValueOverride(String value, HttpServletRequest request, RuleEntity ruleEntity) {
        return value;
    }
   
    @Override
    protected void addDefaultParameters(String storeId, List<NameValuePair> nameValuePairs, Map<String, List<NameValuePair>> paramMap) {
        String storeFlag = enterpriseConfigManager.getStoreFlag(storeId);
        if (StringUtils.isNotBlank(storeFlag)) {
            NameValuePair nvp = new BasicNameValuePair("fq", String.format("%s:true", storeFlag));
            if (ParameterUtils.addNameValuePairToMap(paramMap, "fq", nvp, uniqueFields)) {
                nameValuePairs.add(nvp);
            }
        }
    }

    @Override
    protected StoreKeyword getStoreKeywordOverride(RuleEntity entity, String storeId, String keyword) {
        String storeIdOverride = enterpriseConfigManager.getParentStoreIdByAlias(storeId);
        return new StoreKeyword(storeIdOverride, keyword);
    }
    
    /**
     * Return Error Message if invalid else return null;
     */
    @Override
    protected String getStoreId(HttpServletRequest request) throws HttpException {
        // get the server name, solr path, core name and do mapping for the store name to use for the search
        Pattern pathPattern = Pattern.compile("http://(.*):.*/(.*)/(.*)/select.*");
        String requestPath = getRequestPath(request);
        if (StringUtils.isEmpty(requestPath)) {
            throw new HttpException("Invalid request: Invalid URL");
        }
        Matcher matcher = pathPattern.matcher(requestPath);
        if (!matcher.matches()) {
            throw new HttpException("Invalid request: Invalid URL");
        } else if (StringUtils.isEmpty(request.getParameter("store"))) {
            throw new HttpException("Invalid request: No store parameter");
        }

        String serverName = matcher.group(1);
        String solr = matcher.group(2);
        String solrCore = matcher.group(3);
        String storeId = StringUtils.lowerCase(request.getParameter("store"));

        if (logger.isDebugEnabled()) {
            logger.debug("Server name: {}", serverName);
            logger.debug("Solr version: {}", solr);
            logger.debug("Solr core: {}", solrCore);
            logger.debug("Store id: {}", storeId);
        }

        if(!ArrayUtils.contains(enterpriseConfigManager.getAllCores().toArray(), solrCore)){
            throw new HttpException(String.format("Invalid request: Invalid core %s",solrCore));
        }
       
        if (!ArrayUtils.contains(enterpriseConfigManager.getAllStoreIds().toArray(), storeId)) {
            throw new HttpException(String.format("Invalid request: Invalid store %s",storeId));
        } 

        return storeId;
    }

    @Override
    protected boolean generateSearchNav(HttpServletRequest request) {
        return false;
    }
    
    @Override
    protected String applyRelevancyOverrides(HttpServletRequest request, String paramName, String paramValue) {
        try {
            String storeId = getStoreId(request);
            if(StringUtils.equalsIgnoreCase(Parameter.PARAM_QUERY_FIELDS.toString(), paramName)){
                String[] arrParamValue = StringUtils.split(paramValue);
                String[] arrParamValueReplace = StringUtils.split(enterpriseConfigManager.getStoreParameterValue(storeId, "/store[@id='%s']/relevancy-override-replace/" + paramName));
                List<String> listParamValue = new ArrayList<String>();
                listParamValue.addAll(Arrays.asList(arrParamValue));
                listParamValue.addAll(ArrayUtils.getLength(arrParamValue), Arrays.asList(arrParamValueReplace));
                Map<String, String> mapParamValue = new HashMap<String, String>();
                
                for(String value: listParamValue){
                    String[] boostFactor = StringUtils.split(value, '^');
                    mapParamValue.put(boostFactor[0], boostFactor[1]);
                }
                
                StringBuilder sb = new StringBuilder();
                for(Entry<String, String> entry: mapParamValue.entrySet()){
                    sb.append(String.format("%s^%s ", entry.getKey(), entry.getValue()));
                }
                
                paramValue = StringUtils.trimToEmpty(sb.toString());
            }
        } catch (HttpException e) {

        }finally{
            paramValue = applyValueOverride(paramValue, request, RuleEntity.RANKING_RULE);
        }
        return paramValue;
    }

    @Override
    protected void setDefaultQueryType(HttpServletRequest request, List<NameValuePair> nameValuePairs, String storeId) {
        if (StringUtils.isBlank(request.getParameter(SolrConstants.SOLR_PARAM_QUERY_TYPE))) {
            nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_QUERY_TYPE, enterpriseConfigManager.getDismax(storeId)));
        }
    }

    protected Relevancy getDefaultRelevancyRule(Store store, boolean fromSearchGui) throws DaoException {
        Relevancy relevancy = getRelevancyRule(store, store.getStoreId() + "_default", fromSearchGui);
        if (relevancy == null) {
            relevancy = enterpriseConfigManager.getDefaultRelevancy(store.getStoreId());
        }
        return relevancy;
    }
    
    @Override
    protected List<BannerRuleItem> getActiveBannerRuleItems(Store store, String keyword, boolean fromSearchGui, DateTime currentDate) throws DaoException {
        // Enterprise Search does not need banners
        return null;
    }
    
    @Override
    protected String getDefType(String storeId) throws DaoException {
    	 return enterpriseConfigManager.getDefType(storeId);
    }
    
	protected String getRequestPath(HttpServletRequest request) {
		String start = request.getContextPath() + "/enterpriseSearch";
		int idx = request.getRequestURI().indexOf(start);
		return "http:/" + request.getRequestURI().substring(start.length() + idx);
	}
}