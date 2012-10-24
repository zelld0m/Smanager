package com.search.manager.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.RuleVersionDaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.RuleVersionInfo;

@Service("ruleVersionService")
@RemoteProxy(
		name = "RuleVersionServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "ruleVersionService")
)
public class RuleVersionService {

	private static final Logger logger = Logger.getLogger(RuleVersionService.class);

	@Autowired private RuleVersionDaoService ruleversionDaoService;
	@Autowired private DeploymentService deploymentService;

	@RemoteMethod
	public boolean createRuleVersion(String ruleType, String ruleId, String name, String reason) {
		boolean success = false;
		try {
			success = ruleversionDaoService.createRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, UtilityService.getUsername(), name, reason);
		} catch (Exception e) {
			logger.error("Failed during createRuleVersion()",e);
		}
		return success;	
	}

	@RemoteMethod
	public boolean deleteRuleVersion(String ruleType, String ruleId, int version) {
		boolean success = false;
		try {
			success = ruleversionDaoService.deleteRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, version);
		} catch (Exception e) {
			logger.error("Failed during deleteRuleVersion()",e);
		}
		return success;	
	}

	@RemoteMethod
	public List<RuleVersionInfo> getRuleVersions(String ruleType, String ruleId) {
		List<RuleVersionInfo> versionList = null;
		try {
			versionList = ruleversionDaoService.getRuleVersionList(UtilityService.getStoreName(), ruleType, ruleId);
		} catch (Exception e) {
			logger.error("Failed during getRuleVersions()",e);
		}
		return versionList;
	}

	@RemoteMethod
	public List<ElevateProduct> getElevateRuleVersion(String ruleId, int version) {
		return ruleversionDaoService.readElevateRuleVersion(UtilityService.getStoreName(), ruleId, version, UtilityService.getServerName());
	}

	@RemoteMethod
	public List<Product> getExcludeRuleVersion(String ruleId, int version) {
		return ruleversionDaoService.readExcludeRuleVersion(UtilityService.getStoreName(), ruleId, version, UtilityService.getServerName());
	}

	@RemoteMethod
	public RedirectRule getQueryCleaningRuleVersion(String ruleId, int version) {
		return ruleversionDaoService.readQueryCleaningRuleVersion(UtilityService.getStoreName(), ruleId, version);
	}

	@RemoteMethod
	public Relevancy getRankingRuleVersion(String ruleId, int version) {
		return ruleversionDaoService.readRankingRuleVersion(UtilityService.getStoreName(), ruleId, version);
	}

	@RemoteMethod
	public boolean restoreRuleVersion(String ruleType, String ruleId, int version) {
		boolean success = ruleversionDaoService.restoreRuleVersion(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId, version);
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

		return success;
	}	
}