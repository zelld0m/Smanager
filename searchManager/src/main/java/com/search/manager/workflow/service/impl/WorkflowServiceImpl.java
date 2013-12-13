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
import org.springframework.stereotype.Repository;

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
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.UtilityService;
import com.search.manager.service.rules.FacetSortService;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.service.ImportRuleTaskService;
import com.search.manager.workflow.service.RuleStatusService;
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
	private FacetSortService facetSortService;
	@Autowired
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
	private ImportRuleTaskService importRuleTaskService;

	public boolean exportRule(String store, RuleEntity ruleEntity, String ruleId, RuleXml rule, ExportType exportType, String username, String comment){
		// TODO: change return type to Map
				boolean exported = false;
				boolean exportedOnce = false;

				AuditTrail auditTrail = new AuditTrail();
				auditTrail.setEntity(String.valueOf(AuditTrailConstants.Entity.ruleStatus));
				auditTrail.setOperation(String.valueOf(AuditTrailConstants.Operation.exportRule));
				auditTrail.setUsername(username);
				auditTrail.setStoreId(store);
				DateTime exportDateTime = DateTime.now();

				RuleStatus ruleStatus = null;
				try {
					SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(
							new RuleStatus(ruleEntity.getCode(), store, ruleId), null, null, null, null);
					RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria);
					if (approvedRset != null && CollectionUtils.isNotEmpty(approvedRset.getList())) {
						ruleStatus = approvedRset.getList().get(0);
					} else {
						logger.error("No rule status found for " + ruleEntity + " : " + ruleId);
					}
				} catch (DaoException e) {
					logger.error("Failed to retrieve rule status for " + ruleEntity + " : " + ruleId, e);
				}

				for (String targetStore : utilityService.getStoresToExport(store)) {
					exported = ruleTransferUtil.exportRule(targetStore, ruleEntity, ruleId, rule);

					boolean isAutoImport = BooleanUtils.toBoolean(configManager.getProperty("settings", targetStore, DAOConstants.SETTINGS_AUTO_IMPORT));
					boolean isRuleEntityEnabled = BooleanUtils.toBoolean(configManager.getProperty("workflow", targetStore, "enable."+rule.getRuleEntity().getNthValue(1)));

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
						if(isAutoImport && isRuleEntityEnabled) {
							importExportedRule(targetStore, configManager.getStoreName(targetStore), username, rule.getRuleEntity(), rule.getRuleId(), comment, ImportType.AUTO_IMPORT.getDisplayText(), generateImportAsId(ruleEntity, rule.getRuleName()), rule.getRuleName());
						}
					}
				}

				if (exportedOnce) {
					try {
						if (ruleStatus != null) {
							// RULE STATUS
							daoService.updateRuleStatusExportInfo(ruleStatus, username, exportType, exportDateTime);
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
							daoService.addRuleStatusComment(RuleStatusEntity.EXPORTED, store, username, comment, ruleStatus.getRuleStatusId());
						} else {
							logger.error("No rule status found for " + ruleEntity + " : " + ruleId);
						}
					} catch (DaoException e) {
						logger.error("Failed to update rule status for " + ruleEntity + " : " + ruleId, e);
					}
				}
				return exported;
	}

	private void importExportedRule(String storeId, String storeName, String userName, RuleEntity ruleEntity, String importRuleRefId, String comment, String importType, String importAsRefId, String ruleName) {
		ImportRuleTask importRuleTask = new ImportRuleTask(null, ruleEntity, utilityService.getStoreId(), importRuleRefId, ruleName, storeId, importAsRefId, ruleName, ImportType.getByDisplayText(importType), null);
		importRuleTask.setCreatedBy(userName);
		importRuleTask.setCreatedDate(new DateTime());
		importRuleTaskService.addImportRuleTask(importRuleTask);
	}

	public RuleStatus processRuleStatus(String storeId, String username, String ruleType, String ruleRefId, String description, Boolean isDelete) {
		int result = -1;
		try {
			RuleStatus ruleStatus = ruleStatusService.createRuleStatus(storeId, username);
			ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
			ruleStatus.setRuleRefId(ruleRefId);
			ruleStatus.setDescription(description);
			ruleStatus.setLastModifiedBy(username);
			ruleStatus.setStoreId(storeId);
			result = isDelete ? daoService.updateRuleStatusDeletedInfo(ruleStatus, username)
					: daoService.updateRuleStatusApprovalInfo(ruleStatus, RuleStatusEntity.PENDING, username, DateTime.now());

			if (result > 0) {
				RuleStatus ruleStatusInfo = ruleStatusService.getRuleStatus(storeId, ruleType, ruleRefId);
				try {
					if (!isDelete && "1".equals(configManager.getProperty("mail", storeId, "pendingNotification"))) {
						List<RuleStatus> ruleStatusInfoList = new ArrayList<RuleStatus>();
						ruleStatusInfoList.add(ruleStatusInfo);
						mailService.sendNotification(storeId, RuleStatusEntity.PENDING, ruleType, username, ruleStatusInfoList, "");
					}
				} catch (Exception e) {
					logger.error("Failed during sending 'Submitted For Approval' notification. processRuleStatus()", e);
				}

				return ruleStatusInfo;
			}
		} catch (DaoException e) {
			logger.error("Failed during processRuleStatus()", e);
		} catch (Exception e) {
			logger.error("Failed during processRuleStatus()", e);
		}
		return null;
	}

	public RecordSet<DeploymentModel> publishRule(String storeId, String storeName, String userName, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		boolean obtainedLock = false;

		try {
			obtainedLock = utilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
			return publishRuleNoLock(storeId, userName, ruleType, ruleRefIdList, comment, ruleStatusIdList);

		} finally {
			if (obtainedLock) {
				utilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
			}
		}
	}

	public RecordSet<DeploymentModel> publishRuleNoLock(String store, String username, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
		boolean isAutoExport = BooleanUtils.toBoolean(utilityService.getStoreSetting(store, DAOConstants.SETTINGS_AUTO_EXPORT));
		List<String> approvedRuleList = null;
		List<DeploymentModel> publishingResultList = new ArrayList<DeploymentModel>();

		try {
			if (ArrayUtils.isEmpty(ruleRefIdList)) {
				logger.error("No rule id specified");
			} else if (ArrayUtils.getLength(ruleRefIdList) != ArrayUtils.getLength(ruleStatusIdList)) {
				logger.error(String.format("Inconsistent rule id & rule status id count, RuleID: %s, RuleStatusID: %s", StringUtils.join(ruleRefIdList), StringUtils.join(ruleStatusIdList)));
			} else {
				approvedRuleList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), null, RuleStatusEntity.APPROVED.toString());
			}
		} catch (DaoException e) {
			logger.error("Failed during retrieval of approved rules list", e);
		}

		Map<String, Boolean> ruleMap = new HashMap<String, Boolean>();

		//publish qualified rule, only approved rule
		if (CollectionUtils.isEmpty(approvedRuleList)) {
			logger.error("No approved rules retrieved for publishing");
		} else {
			// Actual publishing of rules to production
			ruleMap = publishRule(store, username, ruleType, approvedRuleList, comment);
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
					daoService.addRuleStatusComment(RuleStatusEntity.PUBLISHED, store, username, comment, publishedRuleStatusIdList.toArray(new String[0]));
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
	private Map<String, Boolean> publishRule(String storeId, String userName, String ruleType, List<String> ruleRefIdList, String comment) {
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

			List<RuleStatus> ruleStatusList = getPublishingListFromMap(storeId, userName, publishWSMap(storeId, ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString());
			Map<String, Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.PUBLISHED, ruleStatusList, userName, DateTime.now());
			List<String> result = new ArrayList<String>();
			getSuccessList(result, ruleMap);
			if (ruleMap != null && ruleMap.size() > 0) {
				try {
					if ("1".equals(configManager.getProperty("mail", storeId, "pushToProdNotification"))) {
						List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
						mailService.sendNotification(storeId, RuleStatusEntity.PUBLISHED, ruleType, userName, ruleStatusInfoList, comment);
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
					ruleStatus = daoService.getRuleStatus(ruleStatus);
					ruleStatusInfoList.add(ruleStatus);
				}
			} catch (DaoException e) {
				logger.error("Error getting rule status info.", e);
			}
		}

		return ruleStatusInfoList;
	}

	private List<RuleStatus> getPublishingListFromMap(String storeId, String userName, Map<String, Boolean> ruleRefIdMap, Integer ruleTypeId, String status) {
		List<RuleStatus> rsList = new ArrayList<RuleStatus>();
		for (Map.Entry<String, Boolean> e : ruleRefIdMap.entrySet()) {
			if (e.getValue()) {
				RuleStatus ruleStatus = ruleStatusService.createRuleStatus(storeId, userName);
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

	public String generateImportAsId(RuleEntity ruleEntity, String ruleName) {
		String importAsId = null;

		switch (ruleEntity) {
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE:
			importAsId = ruleName;
			break;
		case FACET_SORT:
			FacetSort facetSort = facetSortService.getRuleByName(ruleName);
			if (facetSort != null) {
				importAsId = facetSort.getRuleId();
				ruleName = facetSort.getRuleName();
			} else {
				importAsId = DAOUtils.generateUniqueId();
			}
			break;
		case QUERY_CLEANING:
		case RANKING_RULE:
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
