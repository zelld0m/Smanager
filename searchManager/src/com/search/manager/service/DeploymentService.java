package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.search.manager.enums.RuleEntity;
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
	public RecordSet<RuleStatus> getApprovalList(String ruleType, Boolean includeApprovedFlag) {
		RecordSet<RuleStatus> rSet = null;
		int ruleTypeId = RuleEntity.getId(ruleType);
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			if (!includeApprovedFlag) {
				ruleStatus.setApprovalStatus("PENDING");
			}
			ruleStatus.setPublishedStatus("UNPUBLISHED");
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			rSet = daoService.getRuleStatus(searchCriteria );
		} catch (DaoException e) {
			logger.error("Failed during getAllRedirectRule()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public int approveRule(String ruleType, String ...ruleRefIdList) {
		return approveRule(ruleType, Arrays.asList(ruleRefIdList));
	}
	
	public int approveRule(String ruleType, List<String> ruleRefIdList) {
		ruleRefIdList = new ArrayList<String>();
		ruleRefIdList.add("test_rule");
		ruleRefIdList.add("Rule2");
		
		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), "APPROVED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int unapproveRule(String ruleType, String ...ruleRefIdList) {
		return unapproveRule(ruleType, Arrays.asList(ruleRefIdList));
	}
	
	public int unapproveRule(String ruleType, List<String> ruleRefIdList) {
		int result = -1;
		try {
			//REJECTED?
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), "PENDING");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getDeployedRules(String ruleType) {
		RecordSet<RuleStatus> rSet = null;
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setApprovalStatus("APPROVED");
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			rSet = daoService.getRuleStatus(searchCriteria );
		} catch (DaoException e) {
			logger.error("Failed during getAllRedirectRule()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public int publishRule(String ruleType, List<String> ruleRefIdList) {

		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateForPublishingList(ruleRefIdList, RuleEntity.getId(ruleType), "PUBLISHED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int unpublishRule(String ruleType, List<String> ruleRefIdList) {
		ruleRefIdList = new ArrayList<String>();
		ruleRefIdList.add("test_rule");
		ruleRefIdList.add("Rule2");
		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateForPublishingList(ruleRefIdList, RuleEntity.getId(ruleType), "UNPUBLISHED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int recallRule(String ruleType, List<String> ruleRefIdList) {
		return unpublishRule(ruleType, ruleRefIdList);
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