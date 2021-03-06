package com.search.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.manager.utility.PropertiesUtils;
import com.search.service.DeploymentRuleService;
import com.search.webservice.model.TransportList;

public class SearchGuiServiceImpl implements SearchGuiService {

    private static final Logger logger = LoggerFactory.getLogger(SearchGuiServiceImpl.class);
    private static String token;
    private static final String RESOURCE_MAP = "token";
    private static DeploymentRuleService deploymentRuleService;

    public void setDeploymentRuleService(DeploymentRuleService deploymentRuleService_) {
        deploymentRuleService = deploymentRuleService_;
    }

    static {
        try {
            token = PropertiesUtils.getValue(RESOURCE_MAP);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public Map<String, Boolean> deployRulesMap(TransportList list) {

        try {
            if (isValidToken(list.getToken())) {
                List<String> ruleList = list.getList();

                if (CollectionUtils.isNotEmpty(ruleList)) {

                    switch (list.getRuleEntity()) {
                        case ELEVATE:
                            return deploymentRuleService.publishElevateRulesMap(list.getStore(), ruleList);
                        case EXCLUDE:
                            return deploymentRuleService.publishExcludeRulesMap(list.getStore(), ruleList);
                        case DEMOTE:
                            return deploymentRuleService.publishDemoteRulesMap(list.getStore(), ruleList);
                        case FACET_SORT:
                            return deploymentRuleService.publishFacetSortRulesMap(list.getStore(), ruleList);
                        case KEYWORD:
                            break;
                        case STORE_KEYWORD:
                            break;
                        case CAMPAIGN:
                            break;
                        case BANNER:
                            return deploymentRuleService.publishBannerRulesMap(list.getStore(), ruleList);
                        case QUERY_CLEANING:
                            return deploymentRuleService.publishRedirectRulesMap(list.getStore(), ruleList);
                        case RANKING_RULE:
                            return deploymentRuleService.publishRankingRulesMap(list.getStore(), ruleList);
                        case SPELL:
                            return deploymentRuleService.publishDidYouMeanRulesMap(list.getStore(), ruleList);
                        case TYPEAHEAD: 
                        	return deploymentRuleService.publishTypeaheadRule(list.getStore(), ruleList);
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return new HashMap<String, Boolean>();
    }

    @Override
    public Map<String, Boolean> unDeployRulesMap(TransportList list) {
        try {
            if (isValidToken(list.getToken())) {
                List<String> ruleList = list.getList();

                if (CollectionUtils.isNotEmpty(ruleList)) {

                    switch (list.getRuleEntity()) {
                        case ELEVATE:
                            return deploymentRuleService.unpublishElevateRulesMap(list.getStore(), ruleList);
                        case EXCLUDE:
                            return deploymentRuleService.unpublishExcludeRulesMap(list.getStore(), ruleList);
                        case DEMOTE:
                            return deploymentRuleService.unpublishDemoteRulesMap(list.getStore(), ruleList);
                        case FACET_SORT:
                            return deploymentRuleService.unpublishFacetSortRulesMap(list.getStore(), ruleList);
                        case KEYWORD:
                            break;
                        case STORE_KEYWORD:
                            break;
                        case CAMPAIGN:
                            break;
                        case BANNER:
                            return deploymentRuleService.unpublishBannerRulesMap(list.getStore(), ruleList);
                        case QUERY_CLEANING:
                            return deploymentRuleService.unpublishRedirectRulesMap(list.getStore(), ruleList);
                        case RANKING_RULE:
                            return deploymentRuleService.unpublishRankingRulesMap(list.getStore(), ruleList);
                        case TYPEAHEAD: 
                        	return deploymentRuleService.unpublishTypeaheadRulesMap(list.getStore(), ruleList);
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return new HashMap<String, Boolean>();
    }

    private boolean isValidToken(String token_) {
        try {
            if (token.equals(token_)) {
                logger.info("User has valid token ... ");
                return true;
            } else {
                logger.info("User has invalid token ... ");
                return false;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }
}
