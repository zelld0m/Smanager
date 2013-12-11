package com.search.manager.service;

import java.util.List;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoService;
import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleXmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("ruleVersionService")
@RemoteProxy(
        name = "RuleVersionServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "ruleVersionService"))
public class RuleVersionService {

    private static final Logger logger =
            LoggerFactory.getLogger(RuleVersionService.class);
    @Autowired
    private DaoService daoService;
    @Autowired
    private DeploymentService deploymentService;
    @Autowired
    private UtilityService utilityService;
    @Autowired
    private RuleVersionUtil ruleVersionUtil;
    @Autowired
    private RuleXmlUtil ruleXmlUtil;
    
    @RemoteMethod
    public boolean createRuleVersion(String ruleType, String ruleId, String name, String reason) {
        return daoService.createRuleVersion(utilityService.getStoreId(), RuleEntity.find(ruleType), ruleId, utilityService.getUsername(), name, reason);
    }

    @RemoteMethod
    public boolean deleteRuleVersion(String ruleType, String ruleId, int version) {
        boolean success = false;
        try {
            success = daoService.deleteRuleVersion(utilityService.getStoreId(), RuleEntity.find(ruleType), ruleId, utilityService.getUsername(), version);
        } catch (Exception e) {
            logger.error("Failed during deleteRuleVersion()", e);
        }
        return success;
    }

    @RemoteMethod
    public boolean deleteRuleVersionPhysically(String ruleType, String ruleId, int version) {
        return deleteRuleVersion(ruleType, ruleId, version);
    }

    @RemoteMethod
    public List<RuleXml> getRuleVersions(String ruleType, String ruleId) {
        List<RuleXml> versionList = null;
        try {
            versionList = daoService.getRuleVersions(utilityService.getStoreId(), ruleType, ruleId);
        } catch (Exception e) {
            logger.error("Failed during getRuleVersions()", e);
        }
        return versionList;
    }

    @RemoteMethod
    public int getRuleVersionsCount(String ruleType, String ruleId) {
        int count = 0;
        try {
            count = daoService.getRuleVersionsCount(utilityService.getStoreId(), ruleType, ruleId);
        } catch (Exception e) {
            logger.error("Failed during getRuleVersionsCount()", e);
        }
        return count;
    }

    @RemoteMethod
    public boolean restoreRuleVersion(String ruleType, String ruleId, int version) {
        boolean success = false;
        String storeId = utilityService.getStoreId();
        RuleXml rule = ruleVersionUtil.getRuleVersion(storeId, RuleEntity.find(ruleType), ruleId, version);

        if (rule != null) {
            rule.setCreatedBy(utilityService.getUsername());
            success = daoService.restoreRuleVersion(rule);
            switch (RuleEntity.find(ruleType)) {
                case ELEVATE:
                case EXCLUDE:
                case DEMOTE:
                case FACET_SORT:
                case QUERY_CLEANING:
                default:
                    break;
                case RANKING_RULE:
                    // what is this for?
                    RuleStatus ruleStatus = deploymentService.getRuleStatus(storeId, "Ranking Rule", ruleId);
                    if ("DELETE".equals(ruleStatus.getUpdateStatus())) {
                        deploymentService.processRuleStatus(utilityService.getStoreId(), "Ranking Rule", ruleId, null, false);
                    }
                    break;
            }
        }
        return success;
    }

    @RemoteMethod
    public RuleXml getCurrentRuleXml(String ruleType, String ruleId) {
        String store = utilityService.getStoreId();
        RuleXml rXml = ruleXmlUtil.currentRuleToXml(store, ruleType, ruleId);

        if (rXml instanceof ElevateRuleXml) {
            ((ElevateRuleXml) rXml).setProducts(ruleXmlUtil.getProductDetails(rXml, store));
        } else if (rXml instanceof ExcludeRuleXml) {
            ((ExcludeRuleXml) rXml).setProducts(ruleXmlUtil.getProductDetails(rXml, store));
        } else if (rXml instanceof DemoteRuleXml) {
            ((DemoteRuleXml) rXml).setProducts(ruleXmlUtil.getProductDetails(rXml, store));
        }

        return rXml;
    }
}
