package com.search.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.enums.RuleSource;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.CommentService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.ExportRuleMapSortType;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.DeploymentModel;
import com.search.manager.model.ExportRuleMap;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.report.model.xml.DBRuleVersion;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.rules.FacetSortService;
import com.search.manager.utility.StringUtil;
import com.search.manager.workflow.service.WorkflowService;
import com.search.manager.xml.file.RuleTransferUtil;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.ws.ConfigManager;

@Service(value = "ruleTransferService")
@RemoteProxy(
		name = "RuleTransferServiceJS",
		creator = SpringCreator.class,
		creatorParams =
		@Param(name = "beanName", value = "ruleTransferService"))
public class RuleTransferService {

	private static final Logger logger =
			LoggerFactory.getLogger(RuleTransferService.class);

	@Autowired
	private ConfigManager configManager;
	@Autowired
	private DeploymentService deploymentService;
	@Autowired
	private DaoService daoService;
	@Autowired
	private FacetSortService facetSortService;
	@Autowired
	private RuleTransferUtil ruleTransferUtil;
	@Autowired
	private UtilityService utilityService;
	@Autowired
	private RuleXmlUtil ruleXmlUtil;
	@Autowired
	private WorkflowService workflowService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	@Qualifier("commentServiceSp")
	private CommentService commentService;
	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;

	private static final int CREATE_RULE_STATUS = 0;
	private static final int SUBMIT_FOR_APPROVAL = 1;
	private static final int APPROVE_RULE = 2;
	private static final int PUBLISH_RULE = 3;
	private static final int PUBLISH_FAILED = 4;
	private static final int IMPORT_SUCCESS = 5;

	@RemoteMethod
	public RecordSet<RuleStatus> getPublishedRules(String ruleType) {
		return deploymentService.getDeployedRules(ruleType, "PUBLISHED");
	}

	@RemoteMethod
	public List<RuleXml> getAllRulesToImport(String ruleType) {
		return ruleTransferUtil.getAllExportedRules(utilityService.getStoreId(), ruleType);
	}

	/*
	 * ruleType - elevate | exclude | demote | facet sort | query cleaning | ranking rule
	 * keywordFilter - keyword filter
	 * page - page number
	 * itemsPerPage - rows per page
	 * ruleFilter - all | rejected | nonrejected
	 * orderBy - EXPORT_DATE_DESC, EXPORT_DATE_ASC, RULE_NAME_DESC, RULE_NAME_ASC, PUBLISHED_DATE_DESC, PUBLISHED_DATE_ASC
	 * */
	@RemoteMethod
	public RecordSet<RuleXml> getRulesToImport(String ruleType, String keywordFilter, int page, int itemsPerPage, String ruleFilter, String orderBy) {
		String store = utilityService.getStoreId();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		List<RuleXml> list = new ArrayList<RuleXml>();
		RecordSet<ExportRuleMap> exportList = null;

		if (ruleEntity != null) {
			Boolean rejectStatus = null;

			if (StringUtils.isBlank(ruleFilter)) {
				rejectStatus = false; // set default value
			} else if (StringUtils.isNotBlank(ruleFilter) && !StringUtils.equalsIgnoreCase("all", ruleFilter)) {
				rejectStatus = BooleanUtils.toBoolean(ruleFilter, "rejected", "nonrejected");
			}

			ExportRuleMap searchExportRuleMap = new ExportRuleMap(null, null, keywordFilter, store, null, null, null, null, null, false, rejectStatus, ruleEntity);

			try {
				exportList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(searchExportRuleMap, page, itemsPerPage), ExportRuleMapSortType.find(orderBy));
				if (exportList != null && CollectionUtils.isNotEmpty(exportList.getList())) {
					for (ExportRuleMap ruleMap : exportList.getList()) {
						String ruleId = ruleMap.getRuleIdOrigin();
						boolean isRejected = BooleanUtils.isTrue(ruleMap.getRejected());
						RuleXml ruleXml = ruleTransferUtil.getRuleToImport(store, ruleEntity, StringUtil.escapeKeyword(ruleId));
						if (ruleXml != null) {
							ruleXml.setRejected(isRejected);
							list.add(ruleXml);
						} else {
							ruleXml = new RuleXml(ruleMap.getStoreIdOrigin(), ruleMap.getRuleIdOrigin(), ruleMap.getRuleNameOrigin(), true, isRejected);

							RuleStatus ruleStatus = new RuleStatus();

							ruleStatus.setLastPublishedDate(ruleMap.getPublishedDateTime());
							ruleStatus.setLastExportDate(ruleMap.getExportDateTime());
							ruleXml.setRuleStatus(ruleStatus);

							list.add(ruleXml);

							logger.warn(String.format("Missing ruleXml for store:%s, ruleEntity:%s, ruleId: %s", store, ruleType, ruleId));
						}
					}
				}
			} catch (DaoException e) {
				logger.error("Failed to retrieve rules for import", e);
			}
		}
		return new RecordSet<RuleXml>(list, (exportList == null) ? 0 : exportList.getTotalSize());
	}

	@RemoteMethod
	public RuleXml getRuleToExport(String ruleType, String ruleId) {
		List<RuleXml> ruleVersions = daoService.getPublishedRuleVersions(utilityService.getStoreId(), ruleType, ruleId);
		return ruleXmlUtil.getLatestVersion(ruleVersions);
	}

	@RemoteMethod
	public RuleXml getRuleToImport(String ruleType, String ruleId) {
		return ruleTransferUtil.getRuleToImport(utilityService.getStoreId(), RuleEntity.find(ruleType), ruleId);
	}

	@RemoteMethod
	public Map<String, List<String>> exportRule(String storeId, String ruleType, String[] ruleRefIdList, String comment) {

		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
		ArrayList<String> passedList = new ArrayList<String>();
		ArrayList<String> failedList = new ArrayList<String>();

		resultMap.put("PASSED", passedList);
		resultMap.put("FAILED", failedList);

		if (ArrayUtils.isNotEmpty(ruleRefIdList)) {
			String store = storeId;
			RuleEntity ruleEntity = RuleEntity.find(ruleType);
			for (String ruleId : ruleRefIdList) {
				boolean success = false;
				RuleXml ruleXml = getRuleToExport(ruleType, ruleId); //get latest version

				if (ruleXml != null && StringUtils.isNotBlank(ruleXml.getRuleId())) {
					if (workflowService.exportRule(store, ruleEntity, ruleId, ruleXml, ExportType.MANUAL, utilityService.getUsername(), comment)) {
						success = true;
					}
				}
				String ruleName = getRuleName(ruleEntity, ruleId, ruleXml == null ? null : ruleXml.getRuleName());
				if (ruleName == null) {
					// get from ruleStatus
					/*
					 * [old impl]
                    SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(new RuleStatus(RuleEntity.getId(ruleType), store, ruleId));
                    try {
                        RecordSet<RuleStatus> rSet = daoService.getRuleStatus(searchCriteria);
                        if (rSet != null && rSet.getTotalSize() > 0) {
                            ruleName = rSet.getList().get(0).getRuleName();
                        }
                    } catch (DaoException e) {
                        logger.error(String.format("Failed to get rule status for %s %s %s", store, ruleEntity, ruleId));
                    }*/
					try {
						SearchResult<RuleStatus> searchResult = ruleStatusService.search(new RuleStatus(RuleEntity.getId(ruleType), store, ruleId));
						if (searchResult.getTotalCount() > 0) {
							ruleName = searchResult.getResult().get(0).getRuleName();
						}
					} catch (CoreServiceException e) {
						logger.error(String.format("Failed to get rule status for %s %s %s", store, ruleEntity, ruleId));
					}
				}
				if (success) {
					passedList.add(ruleName);
				} else {
					failedList.add(ruleName);
				}

			}
		}
		return resultMap;
	}

	private Map<String, Integer> importRules(String store, String userName, RuleSource ruleSource, String ruleType, String[] ruleRefIdList, String comment, String[] importTypeList, String[] importAsRefIdList, String[] ruleNameList) {
		Map<String, Integer> statusMap = new LinkedHashMap<String, Integer>();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);

		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setOperation(String.valueOf(AuditTrailConstants.Operation.importRule));
		auditTrail.setUsername(userName);
		auditTrail.setStoreId(store);
		auditTrail.setEntity(String.valueOf(AuditTrailConstants.Entity.ruleStatus));

		Map<String, String> forPublishingMap = new LinkedHashMap<String, String>();
		Map<String, String> ruleNameMap = new LinkedHashMap<String, String>();

		for (int i = 0; i < ruleRefIdList.length; i++) {

			String ruleId = ruleRefIdList[i];
			ImportType importType = ImportType.getByDisplayText(importTypeList[i]);
			String ruleName = ruleNameList[i];
			String importAsId = importAsRefIdList[i];
			// initialize to status to 0
			int status = CREATE_RULE_STATUS;

			//if importAsId is null, generate a new id
			switch (ruleEntity) {
			case ELEVATE:
			case EXCLUDE:
			case DEMOTE:
				ruleId = importAsId = ruleName;
				break;
			case FACET_SORT:
				FacetSort facetSort = facetSortService.getRuleByName(store, ruleName);
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
			case TYPEAHEAD:
				TypeaheadRule rule = new TypeaheadRule();
				
				rule.setStoreId(store);
				rule.setRuleName(ruleName);
				
				try {
					SearchResult<TypeaheadRule> result = typeaheadRuleService.search(rule);
					
					if(result.getTotalSize() > 0)
						importAsId = result.getList().get(0).getRuleId();
					else
						importAsId = DAOUtils.generateUniqueId();
					
				} catch (CoreServiceException e1) { 
					e1.printStackTrace();
				}
								
				break;
			default:
				break;
			}

			if (importRule(ruleEntity, store, userName, ruleId, comment, importType, importAsId, ruleName)) {
				try {
					RuleStatus rsAfterImport = new RuleStatus(ruleEntity, store, importAsId, ruleName, userName, userName,
							RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED);
					rsAfterImport.setRuleSource(ruleSource);
					// [old impl] daoService.addRuleStatus(rsAfterImport);
					ruleStatusService.add(rsAfterImport);

					status = SUBMIT_FOR_APPROVAL;
					RuleStatus ruleStatus = new RuleStatus(RuleEntity.getId(ruleType), store, importAsId);
					// [old impl] SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(ruleStatus);
					RuleStatus currRuleStatus = null;

					try {
						// [old impl] RecordSet<RuleStatus> rSet = daoService.getRuleStatus(searchCriteria);
						SearchResult<RuleStatus> searchResult = ruleStatusService.search(ruleStatus);

						// if (rSet != null && CollectionUtils.isNotEmpty(rSet.getList())) {
						// currRuleStatus = rSet.getList().get(0);    
						if (searchResult.getTotalCount() > 0) {
							currRuleStatus = searchResult.getResult().get(0);
							// [old impl] daoService.addRuleStatusComment(RuleStatusEntity.IMPORTED, store, userName, comment, currRuleStatus.getRuleStatusId());
							commentService.addRuleStatusComment(RuleStatusEntity.IMPORTED, store, userName, comment, currRuleStatus.getRuleStatusId());
							auditTrail.setCreatedDate(DateTime.now());
							auditTrail.setReferenceId(ruleStatus.getRuleRefId());
							if (ruleEntity == RuleEntity.ELEVATE || ruleEntity == RuleEntity.EXCLUDE || ruleEntity == RuleEntity.DEMOTE) {
								auditTrail.setKeyword(ruleStatus.getRuleRefId());
							}
							auditTrail.setDetails(String.format("Imported reference id = [%1$s], rule type = [%2$s].",
									auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId())));
							daoService.addAuditTrail(auditTrail);

							if ("DELETE".equalsIgnoreCase(currRuleStatus.getUpdateStatus())) {
								RuleStatus rStatus = currRuleStatus;
								rStatus.setApprovalStatus("");
								rStatus.setUpdateStatus("");

								if (ruleStatusService.update(rStatus) != null) {
									logger.info("Remove delete rule status for " + currRuleStatus.getRuleStatusId());
								} else {
									logger.error("Failed to remove delete rule status for " + currRuleStatus.getRuleStatusId());
								}
							}

						} else {
							logger.error("No rule status found for " + ruleEntity + " : " + importAsId);
						}

					} catch (Exception e) {
						logger.error("Failed to get rule status for " + ruleEntity + " : " + importAsId, e);
					}

					if (ImportType.FOR_APPROVAL == importType || ImportType.AUTO_PUBLISH == importType) {
						//submit rule for approval
						ruleStatus = workflowService.processRuleStatus(store, userName, ruleSource, ruleType, importAsId, ruleName, false);
						status++;

						if (ruleStatus != null && ImportType.AUTO_PUBLISH == importType) {
							forPublishingMap.put(ruleStatus.getRuleRefId(), ruleStatus.getRuleStatusId());
							ruleNameMap.put(ruleStatus.getRuleRefId(), getRuleName(ruleEntity, ruleId, ruleName));
						} else {
							status = IMPORT_SUCCESS;
						}
					} else {
						status = IMPORT_SUCCESS;
					}

				} catch (Exception de) {
					String msg = "";
					switch (status) {
					case CREATE_RULE_STATUS:
						msg = "Failed to create rule status for: ";
						break;
					case SUBMIT_FOR_APPROVAL:
						msg = "Failed to submit rule for approval: ";
						break;
					}
					logger.error(msg + importAsId);
				} finally {
					statusMap.put(getRuleName(ruleEntity, ruleId, ruleName), status);
				}
			}
		}

		if (MapUtils.isNotEmpty(forPublishingMap)) {
			int status = APPROVE_RULE;
			String[] ruleRefIds = forPublishingMap.keySet().toArray(new String[0]);
			String[] ruleStatusIds = forPublishingMap.values().toArray(new String[0]);
			try {
				//approve rule
				if (CollectionUtils.isNotEmpty(deploymentService.approveRule(store, ruleType, ruleRefIds, comment, ruleStatusIds))) {
					status = PUBLISH_RULE;

					//publish rule
					RecordSet<DeploymentModel> deploymentRS = workflowService.publishRuleNoLock(store, userName, ruleSource, ruleType,
							ruleRefIds, comment, ruleStatusIds);
					if (deploymentRS == null || CollectionUtils.isEmpty(deploymentRS.getList())
							|| deploymentRS.getList().get(0).getPublished() != 1) {
						status = PUBLISH_FAILED;
					} else {
						status = IMPORT_SUCCESS;
					}
				}
			} catch (Exception de) {
				String msg = "";
				switch (status) {
				case APPROVE_RULE:
					msg = "Failed to approve rules: ";
					break;
				case PUBLISH_RULE:
					msg = "Failed to publish rules: ";
					break;
				}
				logger.error(msg + ruleRefIds);
			} finally {
				if (ruleRefIds != null) {
					for (String ruleRefId : ruleRefIds) {
						statusMap.put(ruleNameMap.get(ruleRefId), status);
					}
				}
			}
		}
		return statusMap;
	}

	public Map<String, String> processImportRejectRules(String storeId, String storeName, String userName, RuleSource ruleSource, String ruleType,
			String[] importRuleRefIdList, String comment,
			String[] importTypeList, String[] importAsRefIdList,
			String[] ruleNameList, String[] rejectRuleRefIdList,
			String[] rejectRuleNameList) throws PublishLockException {

		Map<String, String> successList = new HashMap<String, String>();
		Integer status = null;
		boolean autoPublish = false;
		boolean obtainedLock = false;

		if (ArrayUtils.isNotEmpty(importTypeList)) {
			for (String importType : importTypeList) {
				if (ImportType.AUTO_PUBLISH.equals(ImportType.getByDisplayText(importType))) {
					autoPublish = true;
					break;
				}
			}
		}

		try {
			if (autoPublish) {
				obtainedLock = utilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
			}
			if (ArrayUtils.isNotEmpty(importRuleRefIdList)) {
				Map<String, Integer> statusMap = importRules(storeId, userName, ruleSource, ruleType, importRuleRefIdList, comment,
						importTypeList, importAsRefIdList, ruleNameList);

				for (String key : statusMap.keySet()) {
					status = statusMap.get(key);
					if (status != null) {
						switch (status) {
						case 0:
							successList.put(key, "import_fail");
							break;
						case 1:
						case 2:
							successList.put(key, "import_success_submit_for_approval_fail");
							break;
						case 3:
						case 4:
							successList.put(key, "import_success_publish_fail");
							break;
						case 5:
							successList.put(key, "import_success");
							break;
						}
					}
				}
			}

			if (ArrayUtils.isNotEmpty(rejectRuleRefIdList)) {
				Map<String, Integer> statusMap = unimportRules(storeId, ruleType, rejectRuleRefIdList,
						comment, rejectRuleNameList);
				for (String key : statusMap.keySet()) {
					status = statusMap.get(key);
					if (status != null) {
						switch (status) {
						case 0:
							successList.put(key, "reject_fail");
							break;
						case 1:
							successList.put(key, "reject_success");
							break;
						}
					}
				}
			}

		} finally {
			if (obtainedLock) {
				utilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
			}
		}
		return successList;
	}

	@RemoteMethod
	public Map<String, String> importRejectRules(String storeId, String storeName, String ruleType,
			String[] importRuleRefIdList, String comment,
			String[] importTypeList, String[] importAsRefIdList,
			String[] ruleNameList, String[] rejectRuleRefIdList,
			String[] rejectRuleNameList) throws PublishLockException {

		String userName = utilityService.getUsername();
		return processImportRejectRules(storeId, storeName, userName, RuleSource.USER, ruleType, importRuleRefIdList, comment, importTypeList, importAsRefIdList, ruleNameList, rejectRuleRefIdList, rejectRuleNameList);

	}

	private String getRuleName(RuleEntity ruleEntity, String ruleId, String ruleName) {
		switch (ruleEntity) {
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE:
			return ruleId;
		case FACET_SORT:
		case QUERY_CLEANING:
		case RANKING_RULE:
			return ruleName;
		default:
			return ruleName;
		}
	}

	private boolean importRule(RuleEntity ruleEntity, String store, String userName, String ruleId, String comment, ImportType importType, String importAsRefId, String ruleName) {
		boolean success = false;
		String id = ruleXmlUtil.getRuleId(ruleEntity, ruleId);
		RuleXml ruleXml = ruleTransferUtil.getRuleToImport(store, ruleEntity, id);

		String storeIdOrigin = ruleXml.getStore();
		String ruleIdOrigin = ruleXml.getRuleId();
		String ruleNameOrigin = ruleXml.getRuleName();

		switch (ruleEntity) {
		case SPELL:
			try {
				success = daoService.importSpellRule(store, storeIdOrigin, userName,
						Integer.valueOf(((DBRuleVersion) ruleXml).getProps().get("maxSuggest")));
			} catch (DaoException e) {
				logger.error("Error importing Did You Mean rules.", e);
			}
			break;
		default:
			ruleXml.setStore(store);
			ruleXml.setRuleId(importAsRefId);
			ruleXml.setRuleName(ruleName);
			ruleXml.setCreatedBy(userName);
			success = ruleTransferUtil.importRule(store, importAsRefId, ruleXml);
		}

		if (success) {
			logger.info(String.format("Rule Xml [store=%s, ruleEntity=%s, ruleId=%s] successfully imported.",
					store, ruleEntity.name(), ruleId));
			ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, store,
					importAsRefId, ruleName, ruleEntity);
			exportRuleMap.setDeleted(ruleTransferUtil.deleteRuleFile(ruleEntity, store, ruleId, comment));
			exportRuleMap.setRejected(false);
			exportRuleMap.setImportDateTime(DateTime.now());

			try {
				daoService.saveExportRuleMap(exportRuleMap);
			} catch (DaoException e) {
				logger.error("Failed to add mapping of ruleId", e);
			}
		}

		return success;
	}

	/**
	 * Deletes xml file of rejected rule
	 *
	 * @return list of rule name of successfully rejected rule
	 */
	private Map<String, Integer> unimportRules(String storeId, String ruleType, String[] ruleRefIdList, String comment, String[] ruleNameList) {
		Map<String, Integer> statusMap = new LinkedHashMap<String, Integer>();
		String store = storeId;
		RuleEntity ruleEntity = RuleEntity.find(ruleType);

		for (int i = 0; i < ruleRefIdList.length; i++) {
			String ruleId = ruleRefIdList[i];
			String ruleName = ruleNameList[i];
			String refId = ruleId;
			int status = 0;

			switch (ruleEntity) {
			case ELEVATE:
			case EXCLUDE:
			case DEMOTE:
				refId = ruleNameList[i]; //delete file by rule name
				break;
			case FACET_SORT:
			case QUERY_CLEANING:
			case RANKING_RULE:
				refId = ruleRefIdList[i]; //delete file by rule id
				break;
			default:
				break;
			}

			RuleXml ruleXml = ruleTransferUtil.getRuleToImport(store, ruleEntity, ruleXmlUtil.getRuleId(ruleEntity, refId));
			if (ruleXml != null) {
				ExportRuleMap exportRuleMap = new ExportRuleMap(ruleXml.getStore(), refId, null, store, null, null, ruleEntity);
				exportRuleMap.setDeleted(false);
				exportRuleMap.setRejected(true);
				try {
					daoService.saveExportRuleMap(exportRuleMap);
					status++;
					//TODO addComment
					//TODO addAuditTrail
					statusMap.put(getRuleName(ruleEntity, ruleId, ruleName), status);
				} catch (DaoException e) {
					logger.error("Failed to add mapping of ruleId", e);
				}
			}
		}
		return statusMap;
	}

	@RemoteMethod
	public boolean getAutoExport() {
		return BooleanUtils.toBoolean(configManager.getProperty("workflow", utilityService.getStoreId(), DAOConstants.SETTINGS_AUTO_EXPORT));
	}

	@RemoteMethod
	public void setAutoExport(Boolean autoexport) {
		configManager.setProperty("workflow", utilityService.getStoreId(), DAOConstants.SETTINGS_AUTO_EXPORT, autoexport.toString());
	}

	@RemoteMethod
	public boolean getAutoImport() {
		return BooleanUtils.toBoolean(configManager.getProperty("workflow", utilityService.getStoreId(), DAOConstants.SETTINGS_AUTO_IMPORT));
	}

	@RemoteMethod
	public void setAutoImport(Boolean autoexport) {
		configManager.setProperty("workflow", utilityService.getStoreId(), DAOConstants.SETTINGS_AUTO_IMPORT, autoexport.toString());
	}

	@RemoteMethod
	public ExportRuleMap getRuleTransferMap(String storeIdOrigin, String ruleIdOrigin, String ruleEntity) {
		String storeIdTarget = utilityService.getStoreId();
		ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, ruleIdOrigin, null, storeIdTarget, null, null, RuleEntity.getId(ruleEntity));

		try {
			List<ExportRuleMap> rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap), null).getList();

			if (CollectionUtils.isNotEmpty(rtList)) {
				return rtList.get(0);
			}

		} catch (DaoException e) {
			logger.error("Failed to retrieve mapping of ruleId", e);
			return null;
		}

		return null;
	}

	@RemoteMethod
	public List<ExportRuleMap> getExportMapList(String storeIdOrigin, String[] ruleIdsOrigin, String ruleEntity) {
		String storeIdTarget = utilityService.getStoreId();
		ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, null, null, storeIdTarget, null, null, RuleEntity.getId(ruleEntity));

		try {
			RecordSet<ExportRuleMap> rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap), null);

			if (rtList != null) {
				return rtList.getList();
			}

		} catch (DaoException e) {
			logger.error("Failed to retrieve mapping of ruleId", e);
		}
		return null;
	}

	@RemoteMethod
	public Map<String, ExportRuleMap> getMapRuleTransferMap(String storeIdOrigin, String[] ruleIdsOrigin, String ruleEntity) {
		String storeIdTarget = utilityService.getStoreId();
		ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, null, null, storeIdTarget, null, null, RuleEntity.getId(ruleEntity));
		Map<String, ExportRuleMap> map = new HashMap<String, ExportRuleMap>();
		boolean returnAllIdsOrigin = ArrayUtils.isEmpty(ruleIdsOrigin);

		try {
			List<ExportRuleMap> rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap), null).getList();
			if (CollectionUtils.isNotEmpty(rtList)) {
				for (ExportRuleMap item : rtList) {
					if (returnAllIdsOrigin || ArrayUtils.contains(ruleIdsOrigin, item.getRuleIdOrigin())) {
						if (map.containsKey(item.getRuleIdOrigin())) {
							logger.error("Duplicate rule map detected! Please check store origin: %s; rule id origin: %s; store targer: %s;" + item.getStoreIdOrigin());
							continue;
						}
						map.put(item.getRuleIdOrigin(), item);
					}
				}
			}
			return map;
		} catch (DaoException e) {
			logger.error("Failed to retrieve mapping of ruleId", e);
		}
		return null;
	}
}
