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
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.model.xml.RuleVersionXml;

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
	public List<RuleVersionXml> getRuleVersions(String ruleType, String ruleId) {
		List<RuleVersionXml> versionList = null;
		try {
			versionList = daoService.getRuleVersions(UtilityService.getStoreName(), ruleType, ruleId);
		} catch (Exception e) {
			logger.error("Failed during getRuleVersions()",e);
		}
		return versionList;
	}

	@RemoteMethod
	public boolean restoreRuleVersion(String ruleType, String ruleId, int version) {
		//boolean success = ruleVersionDaoService.restoreRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, UtilityService.getUsername(), version);
		switch (RuleEntity.find(ruleType)) {
		case ELEVATE:
			break;
		case EXCLUDE:
			break;
		case DEMOTE:
			break;
		case FACET_SORT:
			break;
		case QUERY_CLEANING:
			break;
		case RANKING_RULE:
			RuleStatus ruleStatus = deploymentService.getRuleStatus("Ranking Rule", ruleId);
			if ("DELETE".equals(ruleStatus.getUpdateStatus())) {
				deploymentService.processRuleStatus("Ranking Rule", ruleId, null, false);
			}
			break;
		default: break;
		}

		return true;
	}	
}