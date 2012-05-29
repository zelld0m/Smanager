package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.Comment;
import com.search.manager.model.DeploymentModel;
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
				ruleStatus.setApprovalStatus(RuleStatusEntity.PENDING.toString());
			}
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			rSet = daoService.getRuleStatus(searchCriteria );
		} catch (DaoException e) {
			logger.error("Failed during getApprovalList()",e);
		}
		return rSet;	
	}

	@RemoteMethod
	public List<String> approveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		// TODO: add transaction dependency handshake
		List<String> result = approveRule(ruleType, Arrays.asList(ruleRefIdList));
		addComment( comment, ruleStatusIdList);
		return result;
	}
	
	private List<String> approveRule(String ruleType, List<String> ruleRefIdList) {
		List<String> result = new ArrayList<String>();
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), RuleStatusEntity.APPROVED.toString());
			getSuccessList(result, daoService.updateRuleStatus(ruleStatusList));
		} catch (DaoException e) {
			logger.error("Failed during approveRule()",e);
		}
		return result;
	}

	private void getSuccessList(List<String> result, Map<String, Boolean> map) {
		for (Map.Entry<String, Boolean>  e : map.entrySet())  {
			if (e.getValue()) {
				result.add(e.getKey());
			}
		}
	}

	@RemoteMethod
	public List<String> unapproveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		// TODO: add transaction dependency handshake
		List<String> result = unapproveRule(ruleType, Arrays.asList(ruleRefIdList));
		addComment(comment, ruleStatusIdList);
		return result;
	}
	
	public List<String> unapproveRule(String ruleType, List<String> ruleRefIdList) {
		List<String> result = new ArrayList<String>();
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType),RuleStatusEntity.REJECTED.toString());
			getSuccessList(result, daoService.updateRuleStatus(ruleStatusList));
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
			ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
			ruleStatus.setPublishedStatus(RuleStatusEntity.UNPUBLISHED.toString());
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria );
			ruleStatus.setApprovalStatus(null);
			ruleStatus.setPublishedStatus(RuleStatusEntity.PUBLISHED.toString());
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
	public RecordSet<DeploymentModel> publishRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		//clean list, only approved rules should be published
		List<String> cleanList = null;
		List<DeploymentModel> deployList = new ArrayList<DeploymentModel>();
		try {
			cleanList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), null, RuleStatusEntity.APPROVED.toString());
		} catch (DaoException e) {
			logger.error("Failed during getCleanList()",e);
		}
		// TODO: add transaction dependency handshake
		addComment( comment, ruleStatusIdList);
		Map<String,Boolean> ruleMap = publishRule(ruleType, cleanList);

			for(String ruleId : ruleRefIdList){	
				DeploymentModel deploy = new DeploymentModel();
				deploy.setRuleId(ruleId);
				deploy.setPublished(0);
				
				if(ruleMap != null && ruleMap.size() > 0){
					if(ruleMap.containsKey(ruleId)){
						if(ruleMap.get(ruleId))
							deploy.setPublished(1);
					}	
				}
				deployList.add(deploy);
			}
		return new RecordSet<DeploymentModel>(deployList,deployList.size());
	}
	
	private Map<String,Boolean> publishRule(String ruleType, List<String> ruleRefIdList) {
		try {
			List<RuleStatus> ruleStatusList = getPublishingListFromMap(publishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString());	
			Map<String,Boolean> ruleMap = daoService.updateRuleStatus(ruleStatusList);
			
			if(ruleMap != null && ruleMap.size() > 0)
				return ruleMap;
	
		} catch (Exception e) {
			logger.error("Failed during publishRule()",e);
		}
	
		return Collections.EMPTY_MAP;
	}

	@RemoteMethod
	public RecordSet<DeploymentModel> unpublishRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		//clean list, only approved rules should be published
		List<String> cleanList = null;
		List<DeploymentModel> deployList = new ArrayList<DeploymentModel>();
		try {
			cleanList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString(), null);
		} catch (DaoException e) {
			logger.error("Failed during getCleanList()",e);
		}
		addComment(comment, ruleStatusIdList);
		Map<String,Boolean> ruleMap = unpublishRule(ruleType, cleanList);

		for(String ruleId : ruleRefIdList){	
			DeploymentModel deploy = new DeploymentModel();
			deploy.setRuleId(ruleId);
			deploy.setPublished(0);
			
			if(ruleMap != null && ruleMap.size() > 0){
				if(ruleMap.containsKey(ruleId)){
					if(ruleMap.get(ruleId))
						deploy.setPublished(1);
				}	
			}
			deployList.add(deploy);
		}
		return new RecordSet<DeploymentModel>(deployList,deployList.size());
	}
	
	public Map<String,Boolean> unpublishRule(String ruleType, List<String> ruleRefIdList) {
		try {
			List<RuleStatus> ruleStatusList = getPublishingListFromMap(unpublishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.UNPUBLISHED.toString());	
			Map<String,Boolean> ruleMap = daoService.updateRuleStatus(ruleStatusList);
			
			if(ruleMap != null && ruleMap.size() > 0)
				return ruleMap;
	
		} catch (Exception e) {
			logger.error("Failed during unpublishRule()",e);
		}
	
		return Collections.EMPTY_MAP;
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
		return result == null? new RuleStatus() : result;
	}

	@RemoteMethod
	public RuleStatus processRuleStatus(String ruleType, String ruleRefId, String description, Boolean isDelete) {

		int result = -1;
		try {
			RuleStatus ruleStatus = createRuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setDescription(description);
			ruleStatus.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.processRuleStatus(ruleStatus, isDelete);
			if (result > 0) return getRuleStatus(ruleType, ruleRefId);
		} catch (DaoException e) {
			logger.error("Failed during processRuleStatus()",e);
		}
		return null;
	}

	@RemoteMethod
	public int recallRule(String ruleType, List<String> ruleRefIdList) {
		return 0;
		//return unpublishRule(ruleType, ruleRefIdList);
	}

	public int addComment(String pComment, String ...ruleStatusId) {
		int result = -1;
		try {
			for(String rsId: ruleStatusId){
				Comment comment = new Comment();
				comment.setReferenceId(rsId);
				comment.setRuleTypeId(RuleEntity.RULE_STATUS.getCode());
				comment.setUsername(UtilityService.getUsername());
				comment.setComment(pComment);
				daoService.addComment(comment);
			}
		} catch (DaoException e) {
			logger.error("Failed during addComment()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<Comment> getComment(String ruleStatusId, int page,int itemsPerPage) {
		RecordSet<Comment> rSet = null;
		try {
			Comment comment = new Comment();
			comment.setReferenceId(ruleStatusId);
			comment.setRuleTypeId(RuleEntity.RULE_STATUS.getCode());
			rSet = daoService.getComment(new SearchCriteria<Comment>(comment, null, null, page, itemsPerPage));
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

	private List<RuleStatus> getPublishingListFromMap(Map<String, Boolean> ruleRefIdMap, Integer ruleTypeId, String status) {
		List<RuleStatus> rsList = new ArrayList<RuleStatus>();
		for (Map.Entry<String, Boolean>  e : ruleRefIdMap.entrySet())  {
			if (e.getValue()) {
				RuleStatus ruleStatus = createRuleStatus();
				ruleStatus.setRuleTypeId(ruleTypeId);
				ruleStatus.setRuleRefId(e.getKey());
				ruleStatus.setPublishedStatus(status);
				rsList.add(ruleStatus);
			}
		}
		return rsList;
	}
	
	private RuleStatus createRuleStatus() {
		String userName = UtilityService.getUsername();
		RuleStatus ruleStatus = new RuleStatus();
		ruleStatus.setCreatedBy(userName);
		ruleStatus.setLastModifiedBy(userName);
		return ruleStatus;
	}

	private Map<String, Boolean> publishWSMap(List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return service.deployRulesMap(UtilityService.getStoreName(), ruleList, ruleType);
	}

	private Map<String, Boolean> unpublishWSMap(List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return service.unDeployRulesMap(UtilityService.getStoreName(), ruleList, ruleType);
	}

}
