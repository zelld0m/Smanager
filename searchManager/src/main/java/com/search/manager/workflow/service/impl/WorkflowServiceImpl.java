package com.search.manager.workflow.service.impl;

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
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.search.manager.core.enums.RuleSource;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ImportRuleTask;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TaskStatus;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.CommentService;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.mail.WorkflowNotificationMailService;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.DeploymentModel;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.UtilityService;
import com.search.manager.service.rules.FacetSortService;
import com.search.manager.workflow.service.ExportRuleMapService;
import com.search.manager.workflow.service.WorkflowService;
import com.search.manager.xml.file.RuleTransferUtil;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.ws.ConfigManager;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;

@Repository(value="workflowService")
public class WorkflowServiceImpl implements WorkflowService{

	private static final Logger logger =
			LoggerFactory.getLogger(WorkflowServiceImpl.class);

	@Autowired
	private ConfigManager configManager;
	@Autowired
	private DaoService daoService;
	@Autowired
	private ExportRuleMapService exportRuleMapService;
	@Autowired
	private FacetSortService facetSortService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	private RuleXmlUtil ruleXmlUtil;
	@Autowired
	private UtilityService utilityService;
	@Autowired
	private WorkflowNotificationMailService mailService;
	@Autowired
	private RuleTransferUtil ruleTransferUtil;
	@Autowired
	@Qualifier("importRuleTaskServiceSp")
	private ImportRuleTaskService importRuleTaskService;
	@Autowired
    @Qualifier("commentServiceSp")
	private CommentService commentService;

	public boolean exportRule(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, ExportType exportType, String username, String comment){
		// TODO: change return type to Map
		boolean exported = false;
		boolean exportedOnce = false;
		boolean isSourceAutoImport = BooleanUtils.toBoolean(configManager.getProperty("workflow", store, DAOConstants.SETTINGS_AUTO_IMPORT));
		
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setEntity(String.valueOf(AuditTrailConstants.Entity.ruleStatus));
		auditTrail.setOperation(String.valueOf(AuditTrailConstants.Operation.exportRule));
		auditTrail.setUsername(username);
		auditTrail.setStoreId(store);
		DateTime exportDateTime = DateTime.now();

		RuleStatus ruleStatus = null;
		try {
		    /*
		     *  [old impl] 
			SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(
					new RuleStatus(ruleEntity.getCode(), store, ruleId), null, null, null, null);
			RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria);
			if (approvedRset != null && CollectionUtils.isNotEmpty(approvedRset.getList())) {
				ruleStatus = approvedRset.getList().get(0);
			} else {
				logger.error("No rule status found for " + ruleEntity + " : " + ruleId);
			}*/
		    SearchResult<RuleStatus> searchResult = ruleStatusService.search(new RuleStatus(ruleEntity.getCode(), store, ruleId));
		    if (searchResult.getTotalCount() > 0) {
		        ruleStatus = searchResult.getResult().get(0);
		    } else {
		        logger.error("No rule status found for " + ruleEntity + " : " + ruleId);
		    }
		} catch (CoreServiceException e) {
			logger.error("Failed to retrieve rule status for " + ruleEntity + " : " + ruleId, e);
		}

		for (String targetStore : configManager.getPropertyList("workflow", store, DAOConstants.SETTINGS_EXPORT_TARGET)) {
			exported = ruleTransferUtil.exportRule(targetStore, ruleEntity, ruleId, rule);

			boolean isAutoImport = BooleanUtils.toBoolean(configManager.getProperty("workflow", targetStore, DAOConstants.SETTINGS_AUTO_IMPORT));
			boolean isRuleEntityEnabled = BooleanUtils.toBoolean(configManager.getProperty("workflow", targetStore, "enable."+rule.getRuleEntity().getXmlName()));

			ExportRuleMap exportRuleMap = new ExportRuleMap(store, ruleId, rule.getRuleName(),
					targetStore, null, null, ruleEntity);
			exportRuleMap.setExportDateTime(exportDateTime);
			exportRuleMap.setDeleted(false);
			if (ruleStatus != null) {
				exportRuleMap.setPublishedDateTime(ruleStatus.getLastPublishedDate());
			}
			try { //TODO:
				daoService.saveExportRuleMap(exportRuleMap);
			} catch (DaoException e) {
				e.printStackTrace();
			}
			exportedOnce |= exported;
			if (!exported) {
				logger.error("Failed to export " + ruleEntity + " : " + ruleId + " to store " + targetStore);
			} else {
				if(isAutoImport && isRuleEntityEnabled && isSourceAutoImport) {
					
					String importTypeSetting = configManager.getProperty("workflow", targetStore, "status."+ruleEntity.getXmlName());
					try {
						importExportedRule(targetStore, configManager.getStoreName(targetStore), username, rule.getRuleEntity(), rule.getRuleId(), comment, importTypeSetting != null ? importTypeSetting : ImportType.FOR_APPROVAL.getDisplayText(), generateImportAsId(store, ruleId, rule.getRuleName(), targetStore, rule.getRuleName(), rule.getRuleEntity()), rule.getRuleName());
					} catch(CoreServiceException e) {
						logger.error("Error in WorkflowService.exportRule: ", e);
					}
				}
			}
		}

		if (exportedOnce) {
			try {
				if (ruleStatus != null) {
					// RULE STATUS
					// [old impl] daoService.updateRuleStatusExportInfo(ruleStatus, username, exportType, exportDateTime);
					ruleStatusService.updateRuleStatusExportInfo(ruleStatus, username, exportType, exportDateTime);
					
					// AUDIT TRAIL
					auditTrail.setCreatedDate(exportDateTime);
					auditTrail.setReferenceId(ruleStatus.getRuleRefId());
					if (ruleEntity == RuleEntity.ELEVATE || ruleEntity == RuleEntity.EXCLUDE || ruleEntity == RuleEntity.DEMOTE) {
						auditTrail.setKeyword(ruleStatus.getRuleRefId());
					}
					auditTrail.setDetails(String.format("Exported reference id = [%1$s], rule type = [%2$s], export type = [%3$s].",
							auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId()), ExportType.AUTOMATIC));
					daoService.addAuditTrail(auditTrail);
					
					// COMMENT
					// [old impl] daoService.addRuleStatusComment(RuleStatusEntity.EXPORTED, store, username, comment, ruleStatus.getRuleStatusId());
					commentService.addRuleStatusComment(RuleStatusEntity.EXPORTED, store, username, comment, ruleStatus.getRuleStatusId());
				} else {
					logger.error("No rule status found for " + ruleEntity + " : " + ruleId);
				}
			} catch (Exception e) {
				logger.error("Failed to update rule status for " + ruleEntity + " : " + ruleId, e);
			}
		}
		return exported;
	}

	private void importExportedRule(String storeId, String storeName, String userName, RuleEntity ruleEntity, String importRuleRefId, String comment, String importType, String importAsRefId, String ruleName) throws CoreServiceException {
		ImportRuleTask importRuleTask = new ImportRuleTask(null, ruleEntity, utilityService.getStoreId(), importRuleRefId, ruleName, storeId, importAsRefId, ruleName, ImportType.getByDisplayText(importType), null);
		
		List<ImportRuleTask> list = importRuleTaskService.search(importRuleTask, 0, 0).getList();
		
		if(list != null) {
			for(ImportRuleTask item : list) {
				TaskStatus status = item.getTaskExecutionResult().getTaskStatus();
				if(!TaskStatus.COMPLETED.equals(status) && !TaskStatus.IN_PROCESS.equals(status)) {
					item.getTaskExecutionResult().setTaskStatus(TaskStatus.AUTO_CANCELED);
					importRuleTaskService.update(item);
				}
			}
		}
		
		importRuleTask.setCreatedBy(userName);
		importRuleTask.setCreatedDate(new DateTime());
		importRuleTaskService.add(importRuleTask);
	}

	public RuleStatus processRuleStatus(String storeId, String username, RuleSource ruleSource, String ruleType, String ruleRefId, String description, Boolean isDelete) {
		boolean result;
		try {
			RuleStatus ruleStatus = new RuleStatus();
	        ruleStatus.setCreatedBy(username);
	        ruleStatus.setLastModifiedBy(username);
	        ruleStatus.setStoreId(storeId);
	        
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setDescription(description);
			ruleStatus.setLastModifiedBy(username);
			ruleStatus.setStoreId(storeId);
			ruleStatus.setRuleSource(ruleSource);
			/*
			 *  [old impl] 
			result = isDelete ? daoService.updateRuleStatusDeletedInfo(ruleStatus, username)
					: daoService.updateRuleStatusApprovalInfo(ruleStatus, RuleStatusEntity.PENDING, username, DateTime.now());
            */
			
			if (isDelete) {
			    result = ruleStatusService.updateRuleStatusDeletedInfo(ruleStatus, username);
			} else {
			    result = ruleStatusService.updateRuleStatusApprovalInfo(ruleStatus, RuleStatusEntity.PENDING, username, DateTime.now()) != null;
			}
			
			if (result) {
				RuleStatus ruleStatusInfo = ruleStatusService.getRuleStatus(storeId, ruleType, ruleRefId);
				try {
					if (!isDelete && "1".equals(configManager.getProperty("mail", storeId, "pendingNotification"))) {
						List<RuleStatus> ruleStatusInfoList = new ArrayList<RuleStatus>();
						ruleStatusInfoList.add(ruleStatusInfo);
						mailService.sendNotification(storeId, ruleSource, RuleStatusEntity.PENDING, ruleType, username, ruleStatusInfoList, "");
					}
				} catch (Exception e) {
					logger.error("Failed during sending 'Submitted For Approval' notification. processRuleStatus()", e);
				}

				return ruleStatusInfo;
			}
		} catch (CoreServiceException e) {
			logger.error("Failed during processRuleStatus()", e);
		} catch (Exception e) {
			logger.error("Failed during processRuleStatus()", e);
		}
		return null;
	}

	public RecordSet<DeploymentModel> publishRule(String storeId, String storeName, String userName, RuleSource ruleSource, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		boolean obtainedLock = false;

		try {
			obtainedLock = utilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
			return publishRuleNoLock(storeId, userName, ruleSource, ruleType, ruleRefIdList, comment, ruleStatusIdList);

		} finally {
			if (obtainedLock) {
				utilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
			}
		}
	}

	public RecordSet<DeploymentModel> publishRuleNoLock(String store, String username, RuleSource ruleSource, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		boolean isAutoExport = BooleanUtils.toBoolean(configManager.getProperty("workflow", store, DAOConstants.SETTINGS_AUTO_EXPORT));
		List<String> approvedRuleList = null;
		List<DeploymentModel> publishingResultList = new ArrayList<DeploymentModel>();

		try {
			if (ArrayUtils.isEmpty(ruleRefIdList)) {
				logger.error("No rule id specified");
			} else if (ArrayUtils.getLength(ruleRefIdList) != ArrayUtils.getLength(ruleStatusIdList)) {
				logger.error(String.format("Inconsistent rule id & rule status id count, RuleID: %s, RuleStatusID: %s", StringUtils.join(ruleRefIdList), StringUtils.join(ruleStatusIdList)));
			} else {
				// [old impl] approvedRuleList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), null, RuleStatusEntity.APPROVED.toString());
			    approvedRuleList = ruleStatusService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), null, RuleStatusEntity.APPROVED.toString());
			}
		} catch (CoreServiceException e) {
			logger.error("Failed during retrieval of approved rules list", e);
		}

		Map<String, Boolean> ruleMap = new HashMap<String, Boolean>();

		//publish qualified rule, only approved rule
		if (CollectionUtils.isEmpty(approvedRuleList)) {
			logger.error("No approved rules retrieved for publishing");
		} else {
			// Actual publishing of rules to production
			ruleMap = publishRule(store, username, ruleSource, ruleType, approvedRuleList, comment);
		}

		if (MapUtils.isEmpty(ruleMap)) {
			logger.error(String.format("No rules were published from the list of rule id: %s", StringUtils.join(ruleRefIdList, ',')));
		}

		DeploymentModel deploymentModel = null;
		RuleEntity ruleEntity = null;
		List<String> publishedRuleStatusIdList = new ArrayList<String>();
		String ruleId = "";

		//Populate deployment model for all rules queued for publishing
		// The following code generates xml files for published rules required for export.
		// Note that at this point, rules have already been published.
		for (int i = 0; i < Array.getLength(ruleRefIdList); i++) {
			ruleId = ruleRefIdList[i];
			deploymentModel = new DeploymentModel(ruleId, 0);

			if (MapUtils.isNotEmpty(ruleMap) && ruleMap.containsKey(ruleId) && BooleanUtils.isTrue(ruleMap.get(ruleId))) {
				ruleEntity = RuleEntity.find(ruleType);
				deploymentModel.setPublished(1);
				publishedRuleStatusIdList.add(ruleStatusIdList[i]);
				String name = null;

				if (RuleEntity.SPELL.equals(ruleEntity)) {
					name = "Did You Mean Rules";
				}

				if (daoService.createPublishedVersion(store, ruleEntity, ruleId, username, name, comment)) {
					// [old impl] daoService.addRuleStatusComment(RuleStatusEntity.PUBLISHED, store, username, comment, publishedRuleStatusIdList.toArray(new String[0]));
				    
				    try {
                        commentService.addRuleStatusComment(RuleStatusEntity.PUBLISHED, store, username, comment, publishedRuleStatusIdList.toArray(new String[0]));
                    } catch (CoreServiceException e) {
                        logger.error("Error adding rule status comment. ", e);
                    }
				    
					logger.info(String.format("Published Rule XML created: %s %s", ruleEntity, ruleId));
					if (isAutoExport) {
						RuleXml ruleXml = ruleXmlUtil.getLatestVersion(daoService.getPublishedRuleVersions(store, ruleType, ruleId));
						if (ruleXml != null) {
							exportRule(store, ruleEntity, ruleId, ruleXml, ExportType.AUTOMATIC, username, "Automatic Export on Publish");
						}
					}
				} else {
					logger.error(String.format("Failed to create published rule xml: %s %s", ruleEntity, ruleId));
				}
			}

			publishingResultList.add(deploymentModel);
		}

		return new RecordSet<DeploymentModel>(publishingResultList, publishingResultList.size());
	}

	@SuppressWarnings("unchecked")
	private Map<String, Boolean> publishRule(String storeId, String userName, RuleSource ruleSource, String ruleType, List<String> ruleRefIdList, String comment) {
		try {

			// insert custom handling for linguistics rules here
			if (RuleEntity.SPELL.equals(RuleEntity.find(ruleType))) {
				// Spell rule publishing has two phases.
				// 1. Create published version on the database.
				// 2. Create xml file to be transferred to WS to be picked up and transferred to solr.
				// When any of the two steps fail, deployment should be considered a failure.
				// Published version in DB should rollback. File should not be existing.
				daoService.publishSpellRules(storeId);
			}

			List<RuleStatus> ruleStatusList = getPublishingListFromMap(storeId, userName, ruleSource, publishWSMap(storeId, ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString());
			// [old impl] Map<String, Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.PUBLISHED, ruleStatusList, userName, DateTime.now());
			Map<String, Boolean> ruleMap = ruleStatusService.updateRuleStatus(RuleStatusEntity.PUBLISHED, ruleStatusList, userName, DateTime.now());
			
			List<String> result = new ArrayList<String>();
			getSuccessList(result, ruleMap);
			
			if (ruleMap != null && ruleMap.size() > 0) {
				try {
					if (mailService.isPushToProdNotificationEnable(storeId)) {
						List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
						mailService.sendNotification(storeId, ruleSource, RuleStatusEntity.PUBLISHED, ruleType, userName, ruleStatusInfoList, comment);
					}
				} catch (Exception e) {
					logger.error("Failed during sending pushToProd notification. publishRule()", e);
				}
				return ruleMap;
			}

		} catch (Exception e) {
			logger.error("Failed during publishRule()", e);
		}

		return Collections.EMPTY_MAP;
	}

	private List<RuleStatus> getRuleStatusInfo(List<String> results, List<RuleStatus> ruleStatusList) {
		List<RuleStatus> ruleStatusInfoList = new ArrayList<RuleStatus>();
		for (RuleStatus ruleStatus : ruleStatusList) {
			try {
				if (results.contains(ruleStatus.getRuleRefId())) {
					// [old impl] ruleStatus = daoService.getRuleStatus(ruleStatus);
				    SearchResult<RuleStatus> searchResult = ruleStatusService.search(ruleStatus);
				    if (searchResult.getTotalCount() > 0) {
				        ruleStatus = searchResult.getResult().get(0);
	                    ruleStatusInfoList.add(ruleStatus);
				    }
				}
			} catch (CoreServiceException e) {
				logger.error("Error getting rule status info.", e);
			}
		}

		return ruleStatusInfoList;
	}

	private List<RuleStatus> getPublishingListFromMap(String storeId, String userName, RuleSource ruleSource, Map<String, Boolean> ruleRefIdMap, Integer ruleTypeId, String status) {
		List<RuleStatus> rsList = new ArrayList<RuleStatus>();
		for (Map.Entry<String, Boolean> e : ruleRefIdMap.entrySet()) {
			if (e.getValue()) {
				RuleStatus ruleStatus = new RuleStatus();
	            ruleStatus.setCreatedBy(userName);
	            ruleStatus.setLastModifiedBy(userName);
	            ruleStatus.setStoreId(storeId);
	            ruleStatus.setRuleSource(ruleSource);
				ruleStatus.setRuleTypeId(ruleTypeId);
				ruleStatus.setRuleRefId(e.getKey());
				ruleStatus.setPublishedStatus(status);
				rsList.add(ruleStatus);
			}
		}
		return rsList;
	}

	private Map<String, Boolean> publishWSMap(String storeId, List<String> ruleList, RuleEntity ruleType) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();
		return service.deployRulesMap(storeId, ruleList, ruleType);
	}

	private void getSuccessList(List<String> result, Map<String, Boolean> map) {
		for (Map.Entry<String, Boolean> e : map.entrySet()) {
			if (e.getValue()) {
				result.add(e.getKey());
			}
		}
	}

	//TODO: retireve ExportRuleMap for QUERY_CLEANING and RANKING_RULE
	public String generateImportAsId(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleNameTarget, RuleEntity ruleType) {
		String importAsId = null;

		switch (ruleType) {
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE:
			importAsId = ruleNameTarget;
			break;
		case FACET_SORT:
			FacetSort facetSort = facetSortService.getRuleByName(storeIdTarget, ruleNameTarget);
			if (facetSort != null) {
				importAsId = facetSort.getRuleId();
			} else {
				importAsId = DAOUtils.generateUniqueId();
			}
			break;
		case QUERY_CLEANING:
		case RANKING_RULE:
			ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, null, ruleNameTarget, ruleType);
			try {
				exportRuleMap = exportRuleMapService.getExportRuleMap(exportRuleMap);
			} catch (DaoException e) {
				logger.error("Error executing WorkflowService.getImportAsId ", e);
			}

			if(exportRuleMap != null) {
				importAsId = exportRuleMap.getRuleIdTarget();
			}

			if (StringUtils.isBlank(importAsId) || "0".equalsIgnoreCase(importAsId)) {
				importAsId = DAOUtils.generateUniqueId();
			}
			break;
		default:
			break;
		}

		return importAsId;
	}
}
