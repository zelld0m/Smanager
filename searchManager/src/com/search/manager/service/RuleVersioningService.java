package com.search.manager.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.FileDaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RuleStatus;

@Service(value = "ruleVersioningService")
@RemoteProxy(
		name = "RuleVersioningServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "ruleVersioningService")
	)
public class RuleVersioningService {

	private static final Logger logger = Logger.getLogger(RuleVersioningService.class);
	
	@Autowired private FileDaoService fileService;
	@Autowired private DeploymentService deploymentService;
	
	@RemoteMethod
	public boolean createRuleVersion(String ruleType, String ruleId, String reason) {
		boolean success = false;
		try {
			fileService.createBackup(UtilityService.getStoreName(), ruleId, RuleEntity.find(ruleType), UtilityService.getUsername(), reason);
			success = true;
		} catch (Exception e) {
			logger.error("Failed during createRuleVersion()",e);
		}
		return success;	
	}

	@RemoteMethod
	public List<BackupInfo> getRuleVersions(String ruleType, String ruleId) {
		List<BackupInfo> backUpList = null;
		try {
			backUpList = fileService.getBackupInfo(UtilityService.getStoreName(), ruleType, ruleId);
		} catch (Exception e) {
			logger.error("Failed during getRuleVersions()",e);
		}
		return backUpList;
	}
	
	@RemoteMethod
	public List<ElevateProduct> getElevateRuleVersion(String ruleId, int version) {
		return fileService.readElevateRuleVersion(UtilityService.getStoreName(), ruleId, version, UtilityService.getServerName());
	}
	
	@RemoteMethod
	public List<Product> getExcludeRuleVersion(String ruleId, int version) {
		return fileService.readExcludeRuleVersion(UtilityService.getStoreName(), ruleId, version, UtilityService.getServerName());
	}
	
	@RemoteMethod
	public RedirectRule getQueryCleaningRuleVersion(String ruleId, int version) {
		return fileService.readQueryCleaningRuleVersion(UtilityService.getStoreName(), ruleId, version);
	}
	
	@RemoteMethod
	public Relevancy getRankingRuleVersion(String ruleId, int version) {
		return fileService.readRankingRuleVersion(UtilityService.getStoreName(), ruleId, version);
	}
	
	@RemoteMethod
	public boolean restoreRankingRuleVersion(String ruleId, int version) {
		boolean success = fileService.restoreRankingRuleVersion(UtilityService.getStoreName(), ruleId, version);
		RuleStatus ruleStatus = deploymentService.getRuleStatus("Ranking Rule", ruleId);
		if ("DELETE".equals(ruleStatus.getUpdateStatus())) {
			deploymentService.processRuleStatus("Ranking Rule", ruleId, null, false);
		}
		return success;
	}
	
	
}
