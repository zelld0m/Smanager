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
import com.search.manager.model.Comment;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;

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
			if (includeApprovedFlag) {
				ruleStatus.setApprovalStatus("PENDING,APPROVED");
			} else {
				ruleStatus.setApprovalStatus("PENDING");
			}
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			rSet = daoService.getRuleStatus(searchCriteria );
		} catch (DaoException e) {
			logger.error("Failed during getApprovalList()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public int approveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		// TODO: add transaction dependency handshake
		approveRule(ruleType, Arrays.asList(ruleRefIdList));
		addComment( comment, ruleStatusIdList);
		return 0;
	}
	
	public int approveRule(String ruleType, List<String> ruleRefIdList) {
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
	public int unapproveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		// TODO: add transaction dependency handshake
		unapproveRule(ruleType, Arrays.asList(ruleRefIdList));
		addComment(comment, ruleStatusIdList);
		return 0;
	}
	
	public int unapproveRule(String ruleType, List<String> ruleRefIdList) {
		int result = -1;
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), "REJECTED");
			daoService.updateRuleStatus(ruleStatusList);
		} catch (DaoException e) {
			logger.error("Failed during unapproveRule()",e);
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
			ruleStatus.setPublishedStatus("UNPUBLISHED");
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria );
			ruleStatus.setApprovalStatus(null);
			ruleStatus.setPublishedStatus("PUBLISHED");
			RecordSet<RuleStatus> publishedRset = daoService.getRuleStatus(searchCriteria );
			rSet = combineRecordSet(approvedRset, publishedRset);
		} catch (DaoException e) {
			logger.error("Failed during getDeployedRules()",e);
		}
		return rSet;	
	}

	private RecordSet<RuleStatus> combineRecordSet(RecordSet<RuleStatus> approvedRset, RecordSet<RuleStatus> publishedRset) {
		List<RuleStatus> list = new ArrayList<RuleStatus>();
		list.addAll(approvedRset.getList());
		list.addAll(publishedRset.getList());
		return new RecordSet<RuleStatus>(list, approvedRset.getTotalSize() + publishedRset.getTotalSize());
	}

	@RemoteMethod
	public int publishRule(String ruleType, List<String> ruleRefIdList) {
		int result = -1;
		try {
			if (publishWS(ruleRefIdList, RuleEntity.find(ruleType))) {
				List<RuleStatus> ruleStatusList = generateForPublishingList(ruleRefIdList, RuleEntity.getId(ruleType), "PUBLISHED");
				result = daoService.updateRuleStatus(ruleStatusList);
			}
		} catch (DaoException e) {
			logger.error("Failed during publishRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int unpublishRule(String ruleType, List<String> ruleRefIdList) {
		int result = -1;
		try {
			if (recallWS(ruleRefIdList, RuleEntity.find(ruleType))) {
				List<RuleStatus> ruleStatusList = generateForPublishingList(ruleRefIdList, RuleEntity.getId(ruleType), "UNPUBLISHED");
				result = daoService.updateRuleStatus(ruleStatusList);
			}
			
		} catch (DaoException e) {
			logger.error("Failed during unpublishRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public RuleStatus getRuleStatus(String ruleType, String ruleRefId) {

		RuleStatus result = null;
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			result = daoService.getRuleStatus(ruleStatus);
		} catch (DaoException e) {
			logger.error("Failed during unpublishRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int processRuleStatus(String ruleType, String ruleRefId, String description, Boolean isDelete) {

		int result = -1;
		try {
			RuleStatus ruleStatus = createRuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setDescription(description);
			result = daoService.processRuleStatus(ruleStatus, isDelete);
		} catch (DaoException e) {
			logger.error("Failed during processRuleStatus()",e);
		}
		return result;
	}

	@RemoteMethod
	public int recallRule(String ruleType, List<String> ruleRefIdList) {
		return unpublishRule(ruleType, ruleRefIdList);
	}

	@RemoteMethod
	public int addComment(String comment, String ...ruleStatusId) {
		int result = -1;
		try {
			for(String rsId: ruleStatusId){
				daoService.addComment(rsId, comment, UtilityService.getUsername());
			}
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<Comment> getComment(String ruleStatusId, Integer commentId) {
		RecordSet<Comment> rSet = null;
		try {
			rSet = daoService.getComment(ruleStatusId, commentId);
		} catch (DaoException e) {
			logger.error("Failed during getComment()",e);
		}
		return rSet;
	}

	@RemoteMethod
	public int removeComment(Integer commentId) {
		int result = -1;
		try {
			daoService.removeComment(commentId);
		} catch (DaoException e) {
			logger.error("Failed during removeComment()",e);
		}
		return result;
	}

	private List<RuleStatus> generateApprovalList(List<String> ruleRefIdList, Integer ruleTypeId, String status) {
		List<RuleStatus> ruleStatusList = new ArrayList<RuleStatus>();
		for (String ruleRefId : ruleRefIdList) {
			RuleStatus ruleStatus = createRuleStatus();
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
			RuleStatus ruleStatus = createRuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setPublishedStatus(status);
			ruleStatusList.add(ruleStatus);
		}
		return ruleStatusList;
	}
	
	private RuleStatus createRuleStatus() {
		String userName = UtilityService.getUsername();
		RuleStatus ruleStatus = new RuleStatus();
		ruleStatus.setCreatedBy(userName);
		ruleStatus.setLastModifiedBy(userName);
		return ruleStatus;
	}

	private boolean publishWS(List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return ((SearchGuiClientServiceImpl) service).deployRules(UtilityService.getStoreName(), ruleList, ruleType);
	}

	private boolean recallWS(List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return ((SearchGuiClientServiceImpl) service).recallRules(UtilityService.getStoreName(), ruleList, ruleType);
	}
}
