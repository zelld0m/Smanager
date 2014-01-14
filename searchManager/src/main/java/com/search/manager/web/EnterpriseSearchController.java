package com.search.manager.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Relevancy.Parameter;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.ParameterUtils;
import com.search.ws.EnterpriseSearchConfigManager;
import com.search.ws.SolrConstants;
import com.search.ws.SolrResponseParser;

@Controller
public class EnterpriseSearchController extends AbstractSearchController {

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseSearchController.class);

    private EnterpriseSearchConfigManager enterpriseSearchConfigManager;

    // TODO: transfer to config file
    private final static String[] supportedCores = {
        "pcmall",
        "macmall",
        "ecost",
        "pcmallgov",
        "enterpriseSearch"
    };

    public void afterPropertiesSet() throws Exception {
    	super.afterPropertiesSet();
        enterpriseSearchConfigManager = EnterpriseSearchConfigManager.getInstance();
    }

    @RequestMapping("/enterpriseSearch/**")
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	super.handleRequest(request, response);
    }
    @SuppressWarnings("unchecked")
    @Override
    public String applyValueOverride(String value, HttpServletRequest request, RuleEntity ruleEntity) {
        if (StringUtils.isNotBlank(value)) {
            Map<String, String> overrideMap = (Map<String, String>) request.getAttribute(getOverrideMapAttributeName(ruleEntity));
            if (overrideMap != null) {
                value = StringUtils.replaceEach(value, overrideMap.keySet().toArray(new String[0]),
                        overrideMap.values().toArray(new String[0]));
            }
        }
        return value;
    }

    @Override
    protected void addDefaultParameters(String storeId, List<NameValuePair> nameValuePairs, Map<String, List<NameValuePair>> paramMap) {
        String storeFlag = enterpriseSearchConfigManager.getStoreFlag(storeId);
        if (StringUtils.isNotBlank(storeFlag)) {
            NameValuePair nvp = new BasicNameValuePair("fq", String.format("%s:true", storeFlag));
            if (ParameterUtils.addNameValuePairToMap(paramMap, "fq", nvp, uniqueFields)) {
                nameValuePairs.add(nvp);
            }
        }
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
            logger.debug("Server name: " + serverName);
            logger.debug("Solr version: " + solr);
            logger.debug("Solr core: " + solrCore);
            logger.debug("Store id: " + storeId);
        }

        if (enterpriseSearchConfigManager.getSearchConfiguration(storeId) == null) {
            throw new HttpException("Invalid request: Invalid store " + storeId);
        } else if (!ArrayUtils.contains(supportedCores, solrCore)) {
            throw new HttpException("Invalid request: Invalid core " + solrCore);
        }

        return storeId;
    }

    @Override
    protected boolean generateSearchNav(HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean isActiveSearchRule(String storeId, RuleEntity ruleEntity) {
        return enterpriseSearchConfigManager.isActiveSearchRule(storeId, ruleEntity);
    }

    @Override
    protected StoreKeyword getStoreKeywordOverride(RuleEntity entity, String storeId, String keyword) {
        String storeOverride = enterpriseSearchConfigManager.getSearchRuleCore(storeId, entity);
        return new StoreKeyword(storeOverride, keyword);
    }

    @Override
    protected void initFieldOverrideMaps(HttpServletRequest request, SolrResponseParser solrHelper, String storeId) {

        Map<String, String> elevateFieldOverrides = enterpriseSearchConfigManager.getFieldOverrideMap(storeId,
                enterpriseSearchConfigManager.getSearchRuleCore(storeId, RuleEntity.ELEVATE));
        Map<String, String> demoteFieldOverrides = enterpriseSearchConfigManager.getFieldOverrideMap(storeId,
                enterpriseSearchConfigManager.getSearchRuleCore(storeId, RuleEntity.DEMOTE));
        request.setAttribute(SolrConstants.REQUEST_ATTRIB_ELEVATE_OVERRIDE_MAP, elevateFieldOverrides);
        request.setAttribute(SolrConstants.REQUEST_ATTRIB_DEMOTE_OVERRIDE_MAP, demoteFieldOverrides);
        solrHelper.setElevateFieldOverrides(elevateFieldOverrides);
        solrHelper.setDemoteFieldOverrides(demoteFieldOverrides);

        request.setAttribute(SolrConstants.REQUEST_ATTRIB_EXCLUDE_OVERRIDE_MAP, enterpriseSearchConfigManager.getFieldOverrideMap(storeId,
                enterpriseSearchConfigManager.getSearchRuleCore(storeId, RuleEntity.EXCLUDE)));
        request.setAttribute(SolrConstants.REQUEST_ATTRIB_REDIRECT_OVERRIDE_MAP, enterpriseSearchConfigManager.getFieldOverrideMap(storeId,
                enterpriseSearchConfigManager.getSearchRuleCore(storeId, RuleEntity.QUERY_CLEANING)));
        request.setAttribute(SolrConstants.REQUEST_ATTRIB_RELEVANCY_OVERRIDE_MAP, enterpriseSearchConfigManager.getFieldOverrideMap(storeId,
                enterpriseSearchConfigManager.getSearchRuleCore(storeId, RuleEntity.RANKING_RULE)));
    }

    @Override
    protected String applyRelevancyOverrides(HttpServletRequest request, String paramName, String paramValue) {
        if (StringUtils.equals(Parameter.PARAM_QUERY_FIELDS.toString(), paramName)) {
            // insert VW into possible matches
            // TODO: put in config file?
            paramValue += " VW_DPNo_Index^0 VW_PN_Index^0";
        }
        paramValue = applyValueOverride(paramValue, request, RuleEntity.RANKING_RULE);
        return paramValue;
    }

    @Override
    protected void setDefaultQueryType(HttpServletRequest request, List<NameValuePair> nameValuePairs, String storeId) {
        if (StringUtils.isBlank(request.getParameter(SolrConstants.SOLR_PARAM_QUERY_TYPE))) {
            nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_QUERY_TYPE, enterpriseSearchConfigManager.getDismax(storeId)));
        }
    }

    @Override
    protected List<BannerRuleItem> getActiveBannerRuleItems(Store store, String keyword, boolean fromSearchGui, DateTime currentDate) throws CoreServiceException {
        // Enterprise Search does not need banners
        return null;
    }
    
    @Override
    protected String getDefType(String storeId) throws DaoException {
    	 return enterpriseSearchConfigManager.getDefType(storeId);
    }
    
    protected Relevancy getDefaultRelevancy(String storeId) {
        return enterpriseSearchConfigManager.getRelevancy(storeId);
    }

    protected Relevancy getDefaultRelevancyRule(Store store, boolean fromSearchGui) throws DaoException {
        Relevancy relevancy = getRelevancyRule(store, store.getStoreId() + "_default", fromSearchGui);
        if (relevancy == null) {
            relevancy = getDefaultRelevancy(store.getStoreId());
        }
        return relevancy;
    }

	protected String getRequestPath(HttpServletRequest request) {
		String start = request.getContextPath() + "/enterpriseSearch";
		int idx = request.getRequestURI().indexOf(start);
		return "http:/" + request.getRequestURI().substring(start.length() + idx);
	}
}
