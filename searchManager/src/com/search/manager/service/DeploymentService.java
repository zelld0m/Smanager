package com.search.manager.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.model.Comment;
import com.search.manager.model.DeploymentModel;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleXmlUtil;
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

	@RemoteMethod
	public RecordSet<RuleStatus> getApprovalList(String ruleType, Boolean includeApprovedFlag) {
		RecordSet<RuleStatus> rSet = null;
		int ruleTypeId = RuleEntity.getId(ruleType);
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(ruleTypeId);
			ruleStatus.setStoreId(UtilityService.getStoreId());
			if (includeApprovedFlag) {
				ruleStatus.setApprovalStatus(RuleStatusEntity.getString(RuleStatusEntity.PENDING, RuleStatusEntity.APPROVED));
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

	private String[] getRuleStatusIdList(String[] ruleRefIdList, String[] ruleStatusIdList, List<String> ruleRefIdsToMatch) {
		List<String> list = new ArrayList<String>();
		int i = 0;
		for (String ruleRefId: ruleRefIdsToMatch) {
			i = ArrayUtils.indexOf(ruleRefIdList, ruleRefId);
			if (i >= 0) {
				list.add(ruleStatusIdList[i]);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	@RemoteMethod
	public List<String> approveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		// TODO: add transaction dependency handshake
		List<String> result = approveRule(ruleType, Arrays.asList(ruleRefIdList));
		daoService.addRuleStatusComment(RuleStatusEntity.APPROVED, UtilityService.getStoreId(), UtilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, result));
		return result;
	}

	private List<String> approveRule(String ruleType, List<String> ruleRefIdList) {
		List<String> result = new ArrayList<String>();
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), RuleStatusEntity.APPROVED.toString());
			getSuccessList(result, daoService.updateRuleStatus(RuleStatusEntity.APPROVED, ruleStatusList, UtilityService.getUsername(), DateTime.now()));
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
		daoService.addRuleStatusComment(RuleStatusEntity.REJECTED, UtilityService.getStoreId(), UtilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, result));
		return result;
	}

	public List<String> unapproveRule(String ruleType, List<String> ruleRefIdList) {
		List<String> result = new ArrayList<String>();
		try {
			List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType),RuleStatusEntity.REJECTED.toString());
			getSuccessList(result, daoService.updateRuleStatus(RuleStatusEntity.REJECTED, ruleStatusList, UtilityService.getUsername(), DateTime.now()));
		} catch (DaoException e) {
			logger.error("Failed during unapproveRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getDeployedRules(String ruleType, String filterBy) {
		RecordSet<RuleStatus> rSet = null;
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setStoreId(UtilityService.getStoreId());
			SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
			if (StringUtils.isBlank(filterBy))	{
				ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
				ruleStatus.setPublishedStatus(RuleStatusEntity.UNPUBLISHED.toString());
				RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria );
				ruleStatus.setApprovalStatus(null);
				ruleStatus.setPublishedStatus(RuleStatusEntity.PUBLISHED.toString());
				RecordSet<RuleStatus> publishedRset = daoService.getRuleStatus(searchCriteria );
				rSet = combineRecordSet(approvedRset, publishedRset);
			} else if (filterBy.equalsIgnoreCase(RuleStatusEntity.APPROVED.toString())) {
				ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
				ruleStatus.setUpdateStatus("ADD,UPDATE");
				rSet = daoService.getRuleStatus(searchCriteria );
			} else if (filterBy.equalsIgnoreCase(RuleStatusEntity.PUBLISHED.toString())) {
				ruleStatus.setPublishedStatus(RuleStatusEntity.PUBLISHED.toString());
				ruleStatus.setUpdateStatus("ADD,UPDATE");
				rSet = daoService.getRuleStatus(searchCriteria );
			} else if ("DELETE".equalsIgnoreCase(filterBy)) {
				ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
				ruleStatus.setUpdateStatus("DELETE");
				rSet = daoService.getRuleStatus(searchCriteria );
			}
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

	
	public RecordSet<DeploymentModel> publishRuleNoLock(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		String store = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		boolean isAutoExport = BooleanUtils.toBoolean(UtilityService.getStoreSetting(DAOConstants.SETTINGS_AUTO_EXPORT));
		List<String> approvedRuleList = null;
		List<DeploymentModel> publishingResultList = new ArrayList<DeploymentModel>();

		try {
			if(ArrayUtils.isEmpty(ruleRefIdList)){
				logger.error("No rule id specified");	
			}else if(ArrayUtils.getLength(ruleRefIdList)!=ArrayUtils.getLength(ruleStatusIdList)){
				logger.error(String.format("Inconsistent rule id & rule status id count, RuleID: %s, RuleStatusID: %s", StringUtils.join(ruleRefIdList), StringUtils.join(ruleStatusIdList)));	
			}else{
				approvedRuleList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), null, RuleStatusEntity.APPROVED.toString());
			}
		} catch (DaoException e) {
			logger.error("Failed during retrieval of approved rules list", e);
		}

		Map<String,Boolean> ruleMap = new HashMap<String, Boolean>();

		//publish qualified rule, only approved rule
		if (CollectionUtils.isEmpty(approvedRuleList)){
			logger.error("No approved rules retrieved for publishing");					
		}else{
			ruleMap = publishRule(ruleType, approvedRuleList);
		}

		if (MapUtils.isEmpty(ruleMap)){
			logger.error(String.format("No rules were published from the list of rule id: %s", StringUtils.join(ruleRefIdList, ',')));
		}

		DeploymentModel deploymentModel = null;
		RuleEntity ruleEntity = null;
		List<String> publishedRuleStatusIdList =  new ArrayList<String>();
		String ruleId = "";

		//Populate deployment model for all rules queued for publishing
		for(int i=0; i < Array.getLength(ruleRefIdList); i++) {	
			ruleId = ruleRefIdList[i];
			deploymentModel = new DeploymentModel(ruleId, 0);

			if(MapUtils.isNotEmpty(ruleMap) && ruleMap.containsKey(ruleId) && BooleanUtils.isTrue(ruleMap.get(ruleId))) {
				ruleEntity = RuleEntity.find(ruleType);
				deploymentModel.setPublished(1);
				publishedRuleStatusIdList.add(ruleStatusIdList[i]);
				if(daoService.createPublishedVersion(store, ruleEntity, ruleId, username, null, comment)) {
					daoService.addRuleStatusComment(RuleStatusEntity.PUBLISHED, store, username, comment, publishedRuleStatusIdList.toArray(new String[0]));
					logger.info(String.format("Published Rule XML created: %s %s", ruleEntity, ruleId));	
					if (isAutoExport) {
						RuleXml ruleXml = RuleXmlUtil.getLatestVersion(daoService.getPublishedRuleVersions(store, ruleType, ruleId));
						if (ruleXml != null) {
							try {
								daoService.exportRule(store, ruleEntity, ruleId, ruleXml, ExportType.AUTOMATIC, username, "Automatic Export on Publish");
							} catch (DaoException e) {
								// TODO: make more detailed
								logger.error("Error occurred while exporting rule: ", e);
							}
						}
					}
				}
				else {
					logger.error(String.format("Failed to create published rule xml: %s %s", ruleEntity, ruleId));	
				}
			}

			publishingResultList.add(deploymentModel);
		}
		return new RecordSet<DeploymentModel>(publishingResultList, publishingResultList.size());
	}
	
	
	@RemoteMethod
	public RecordSet<DeploymentModel> publishRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		boolean obtainedLock = false;
		try {
			obtainedLock = UtilityService.obtainPublishLock(RuleEntity.find(ruleType));
			return publishRuleNoLock(ruleType, ruleRefIdList, comment, ruleStatusIdList);
		} finally {
			if (obtainedLock) {
				UtilityService.releasePublishLock(RuleEntity.find(ruleType));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String,Boolean> publishRule(String ruleType, List<String> ruleRefIdList) {
		try {
			List<RuleStatus> ruleStatusList = getPublishingListFromMap(publishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString());	
			Map<String,Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.PUBLISHED, ruleStatusList, UtilityService.getUsername(), DateTime.now());

			if(ruleMap != null && ruleMap.size() > 0)
				return ruleMap;

		} catch (Exception e) {
			logger.error("Failed during publishRule()",e);
		}

		return Collections.EMPTY_MAP;
	}

	@RemoteMethod
	public RecordSet<DeploymentModel> unpublishRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		boolean obtainedLock = false;
		try {
			obtainedLock = UtilityService.obtainPublishLock(RuleEntity.find(ruleType));
			//clean list, only approved rules should be published
			List<String> cleanList = null;
			List<String> publishedRuleIds = new ArrayList<String>();
			List<DeploymentModel> deployList = new ArrayList<DeploymentModel>();
			try {
				cleanList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString(), null);
			} catch (DaoException e) {
				logger.error("Failed during getCleanList()",e);
			}
			Map<String,Boolean> ruleMap = unpublishRule(ruleType, cleanList);
	
			for(String ruleId : ruleRefIdList){	
				DeploymentModel deploy = new DeploymentModel();
				deploy.setRuleId(ruleId);
				deploy.setPublished(0);
	
				if(ruleMap != null && ruleMap.size() > 0){
					if(ruleMap.containsKey(ruleId)){
						if(ruleMap.get(ruleId)) {
							deploy.setPublished(1);
							publishedRuleIds.add(ruleId);
						}
					}
				}
				deployList.add(deploy);
			}
			daoService.addRuleStatusComment(RuleStatusEntity.UNPUBLISHED, UtilityService.getStoreId(), UtilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, publishedRuleIds));
			return new RecordSet<DeploymentModel>(deployList,deployList.size());
		} finally {
			if (obtainedLock) {
				UtilityService.releasePublishLock(RuleEntity.find(ruleType));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String,Boolean> unpublishRule(String ruleType, List<String> ruleRefIdList) {
		try {
			List<RuleStatus> ruleStatusList = getPublishingListFromMap(unpublishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.UNPUBLISHED.toString());	
			Map<String,Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.UNPUBLISHED, ruleStatusList, UtilityService.getUsername(), DateTime.now());

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
			ruleStatus.setStoreId(UtilityService.getStoreId());
			result = daoService.getRuleStatus(ruleStatus);
		} catch (DaoException e) {
			logger.error("Failed during getRuleStatus()",e);
		}
		return result == null? new RuleStatus() : result;
	}

	@RemoteMethod
	public RecordSet<RuleStatus> getAllRuleStatus(String ruleType) {
		try {
			RuleStatus ruleStatus = new RuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setStoreId(UtilityService.getStoreId());
			return daoService.getRuleStatus(new SearchCriteria<RuleStatus>(ruleStatus));
		} catch (DaoException e) {
			logger.error("Failed during getAllRuleStatus()",e);
		}
		return null;
	}

	@RemoteMethod
	// Used by Submit For Approval and Delete Rule
	public RuleStatus processRuleStatus(String ruleType, String ruleRefId, String description, Boolean isDelete) {
		int result = -1;
		try {
			String username = UtilityService.getUsername();
			RuleStatus ruleStatus = createRuleStatus();
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setDescription(description);
			ruleStatus.setLastModifiedBy(username);
			ruleStatus.setStoreId(UtilityService.getStoreId());
			result = isDelete ? daoService.updateRuleStatusDeletedInfo(ruleStatus, username)
					: daoService.updateRuleStatusApprovalInfo(ruleStatus, RuleStatusEntity.PENDING, username, DateTime.now());
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
		ruleStatus.setStoreId(UtilityService.getStoreId());
		return ruleStatus;
	}

	private Map<String, Boolean> publishWSMap(List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return service.deployRulesMap(UtilityService.getStoreId(), ruleList, ruleType);
	}

	private Map<String, Boolean> unpublishWSMap(List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return service.unDeployRulesMap(UtilityService.getStoreId(), ruleList, ruleType);
	}
}