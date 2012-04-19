package com.search.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;

@Service(value = "deploymentService")
@RemoteProxy(
		name = "DeploymentServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "deploymentService")
	)
public class DeploymentService {

	private static final Logger logger = Logger.getLogger(DeploymentService.class);
	
	@Autowired private DaoService daoService;

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getApprovalList(Integer ruleTypeId, Boolean includeApprovedFlag) {
		RecordSet<RuleStatus> rSet = null;
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			if (!includeApprovedFlag) {
				ruleStatus.setApprovalStatus("P");
			}
			ruleStatus.setPublishedStatus("U");
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			rSet = daoService.getRuleStatus(searchCriteria );
		} catch (DaoException e) {
			logger.error("Failed during getAllRedirectRule()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public int approveRule(List<String> ruleRefIdList, Integer ruleTypeId) {
		ruleRefIdList = new ArrayList<String>();
		ruleRefIdList.add("test_rule");
		ruleRefIdList.add("Rule2");
		
		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, ruleTypeId, "APPROVED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int unapproveRule(List<String> ruleRefIdList, Integer ruleTypeId) {
		int result = -1;
		try {
			//REJECTED?
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, ruleTypeId, "PENDING");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getDeployedRules(Integer ruleTypeId) {
		RecordSet<RuleStatus> rSet = null;
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			ruleStatus.setApprovalStatus("A");
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			rSet = daoService.getRuleStatus(searchCriteria );
		} catch (DaoException e) {
			logger.error("Failed during getAllRedirectRule()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public int publishRule(List<String> ruleRefIdList, Integer ruleTypeId) {
//		ruleRefIdList = new ArrayList<String>();
//		ruleRefIdList.add("test_rule");
//		ruleRefIdList.add("Rule2");

		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateForPublishingList(ruleRefIdList, ruleTypeId, "PUBLISHED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int unpublishRule(List<String> ruleRefIdList, Integer ruleTypeId) {
		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateForPublishingList(ruleRefIdList, ruleTypeId, "UNPUBLISHED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int recallRule(List<String> ruleRefIdList, Integer ruleTypeId) {
		return unpublishRule(ruleRefIdList, ruleTypeId);
	}

	@RemoteMethod
	public int AddComment(String ruleStatusId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	@RemoteMethod
	public int removeComment(String commentId) {
		int result = -1;
//		try {
//			
//		} catch (DaoException e) {
//			logger.error("Failed during updateRedirectRule()",e);
//		}
		return result;
	}

	private List<RuleStatus> generateApprovalList(List<String> ruleRefIdList, Integer ruleTypeId, String status) {
		List<RuleStatus> ruleStatusList = new ArrayList<RuleStatus>();
		for (String ruleRefId : ruleRefIdList) {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setApprovalStatus(status);
			ruleStatusList.add(ruleStatus);
		}
		return ruleStatusList;
	}

	private List<RuleStatus> generateForPublishingList(List<String> ruleRefIdList, Integer ruleTypeId, String status) {
		List<RuleStatus> ruleStatusList = new ArrayList<RuleStatus>();
		for (String ruleRefId : ruleRefIdList) {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setPublishedStatus(status);
			ruleStatusList.add(ruleStatus);
		}
		return ruleStatusList;
	}

}