package com.search.manager.service;

import java.util.List;

import org.apache.log4j.Logger;
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

@Service("ruleVersionService")
@RemoteProxy(
		name = "RuleVersionServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "ruleVersionService")
)
public class RuleVersionService{

	private static final Logger logger = Logger.getLogger(RuleVersionService.class);

	@Autowired private DaoService daoService;
	@Autowired private DeploymentService deploymentService;

	@RemoteMethod
	public boolean createRuleVersion(String ruleType, String ruleId, String name, String reason) {
		return daoService.createRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, UtilityService.getUsername(), name, reason);
	}

	@RemoteMethod
	public boolean deleteRuleVersion(String ruleType, String ruleId, int version) {
		boolean success = false;
		try {
			success = daoService.deleteRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, UtilityService.getUsername(), version);
		} catch (Exception e) {
			logger.error("Failed during deleteRuleVersion()",e);
		}
		return success;	
	}

	@RemoteMethod
	public List<RuleXml> getRuleVersions(String ruleType, String ruleId) {
		List<RuleXml> versionList = null;
		try {
			versionList = daoService.getRuleVersions(UtilityService.getStoreName(), ruleType, ruleId);
		} catch (Exception e) {
			logger.error("Failed during getRuleVersions()",e);
		}
		return versionList;
	}
	
	@RemoteMethod
	public int getRuleVersionsCount(String ruleType, String ruleId) {
		int count = 0;
		try {
			count = daoService.getRuleVersionsCount(UtilityService.getStoreName(), ruleType, ruleId);
		} catch (Exception e) {
			logger.error("Failed during getRuleVersionsCount()",e);
		}
		return count;
	}

	@RemoteMethod
	public boolean restoreRuleVersion(String ruleType, String ruleId, int version) {
		boolean success = false;
		RuleXml rule = RuleVersionUtil.getRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, version);
		if (rule != null) {
			rule.setCreatedBy(UtilityService.getUsername());
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
				RuleStatus ruleStatus = deploymentService.getRuleStatus("Ranking Rule", ruleId);
				if ("DELETE".equals(ruleStatus.getUpdateStatus())) {
					deploymentService.processRuleStatus("Ranking Rule", ruleId, null, false);
				}
				break;
			}
		}
		return success;
	}	

	@RemoteMethod
	public RuleXml getCurrentRuleXml(String ruleType, String ruleId) {
		String store = UtilityService.getStoreName();
		RuleXml rXml = RuleXmlUtil.currentRuleToXml(store, ruleType, ruleId);

		if (rXml instanceof ElevateRuleXml){
			((ElevateRuleXml) rXml).setProducts(RuleXmlUtil.getProductDetails(rXml, store));
		}else if (rXml instanceof ExcludeRuleXml){
			((ExcludeRuleXml) rXml).setProducts(RuleXmlUtil.getProductDetails(rXml, store));
		}else if (rXml instanceof DemoteRuleXml){
			((DemoteRuleXml) rXml).setProducts(RuleXmlUtil.getProductDetails(rXml, store));
		}

		return rXml;
	}
}