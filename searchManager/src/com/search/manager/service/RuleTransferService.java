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
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.utility.StringUtil;
import com.search.manager.xml.file.RuleTransferUtil;
import com.search.manager.xml.file.RuleXmlUtil;

@Service(value = "ruleTransferService")
@RemoteProxy(
		name = "RuleTransferServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "ruleTransferService")
)
public class RuleTransferService {

	@Autowired private DeploymentService deploymentService;
	@Autowired private DaoService daoService;
	@Autowired private FacetSortService facetSortService;

	private static final Logger logger = Logger.getLogger(RuleTransferService.class);

	private static final int CREATE_RULE_STATUS = 0;
	private static final int SUBMIT_FOR_APPROVAL = 1;
	private static final int APPROVE_RULE = 2;
	private static final int PUBLISH_RULE = 3;
	private static final int PUBLISH_FAILED = 4;
	private static final int IMPORT_SUCCESS = 5;

	@RemoteMethod
	public RecordSet<RuleStatus> getPublishedRules(String ruleType){
		return deploymentService.getDeployedRules(ruleType, "PUBLISHED");
	}

	@RemoteMethod
	public List<RuleXml> getAllRulesToImport(String ruleType){
		return RuleTransferUtil.getAllExportedRules(UtilityService.getStoreId(), ruleType);
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
		String store = UtilityService.getStoreId();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		List<RuleXml> list = new ArrayList<RuleXml>();
		RecordSet<ExportRuleMap> exportList = null;
		
		if (ruleEntity != null) {
			Boolean rejectStatus = null;
			
			if(StringUtils.isBlank(ruleFilter)) {
				rejectStatus = false; // set default value
			} else if (StringUtils.isNotBlank(ruleFilter) && !StringUtils.equalsIgnoreCase("all", ruleFilter)) {
				rejectStatus = BooleanUtils.toBoolean(ruleFilter, "rejected", "nonrejected");
			}
			
			ExportRuleMap searchExportRuleMap = new ExportRuleMap(null, null, keywordFilter, store, null, null, null, null, null, false, rejectStatus, ruleEntity);
	
			try {
				exportList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(searchExportRuleMap, page, itemsPerPage), ExportRuleMapSortType.find(orderBy));
				if (exportList != null && CollectionUtils.isNotEmpty(exportList.getList())) {
					for (ExportRuleMap ruleMap: exportList.getList()) {
						String ruleId = ruleMap.getRuleIdOrigin();
						boolean isRejected =  BooleanUtils.isTrue(ruleMap.getRejected());
						RuleXml ruleXml = RuleTransferUtil.getRuleToImport(store, ruleEntity, StringUtil.escapeKeyword(ruleId));
						if (ruleXml != null) {
							ruleXml.setRejected(isRejected);
							list.add(ruleXml);
						}
						else {
							ruleXml = new RuleXml(ruleMap.getStoreIdOrigin(), ruleMap.getRuleIdOrigin(), ruleMap.getRuleNameOrigin(), true, isRejected);

							RuleStatus ruleStatus = new RuleStatus();

							ruleStatus.setLastPublishedDateTime(ruleMap.getPublishedDateTime());
							ruleStatus.setLastExportDateTime(ruleMap.getExportDateTime());
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
	public RuleXml getRuleToExport(String ruleType, String ruleId){   
		List<RuleXml> ruleVersions = daoService.getPublishedRuleVersions(UtilityService.getStoreId(), ruleType, ruleId);
		return RuleXmlUtil.getLatestVersion(ruleVersions);
	}

	@RemoteMethod
	public RuleXml getRuleToImport(String ruleType, String ruleId){
		return RuleTransferUtil.getRuleToImport(UtilityService.getStoreId(), RuleEntity.find(ruleType), ruleId);
	}

	@RemoteMethod
	public Map<String, List<String>> exportRule(String ruleType, String[] ruleRefIdList, String comment) {

		HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
		ArrayList<String> passedList = new ArrayList<String>();
		ArrayList<String> failedList = new ArrayList<String>();

		resultMap.put("PASSED", passedList);
		resultMap.put("FAILED", failedList);

		if (ArrayUtils.isNotEmpty(ruleRefIdList)) {
			String store = UtilityService.getStoreId();
			RuleEntity ruleEntity = RuleEntity.find(ruleType);
			for (String ruleId: ruleRefIdList){
				boolean success = false;
				RuleXml ruleXml = getRuleToExport(ruleType, ruleId); //get latest version
				if(ruleXml != null && StringUtils.isNotBlank(ruleXml.getRuleId())){
					try {
						if(daoService.exportRule(store, ruleEntity, ruleId, ruleXml, ExportType.MANUAL, UtilityService.getUsername(), comment)) {
							success = true;
						}
					} catch (DaoException e) {
						// TODO: make more detailed
						logger.error("Error occurred while exporting rule: ", e);
					}
				}
				String ruleName = getRuleName(ruleEntity, ruleId, ruleXml == null ? null : ruleXml.getRuleName());
				if (ruleName == null) {
					// get from ruleStatus
					SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(new RuleStatus(RuleEntity.getId(ruleType), store, ruleId));
					try {
						RecordSet<RuleStatus> rSet = daoService.getRuleStatus(searchCriteria);
						if (rSet != null && rSet.getTotalSize() > 0) {
							ruleName = rSet.getList().get(0).getRuleName();
						}
					} catch (DaoException e) {
						logger.error(String.format("Failed to get rule status for %s %s %s", store, ruleEntity, ruleId));
					}
				}
				if (success) {
					passedList.add(ruleName);
				} 
				else {
					failedList.add(ruleName);
				}
				
			}
		}
		return resultMap;
	}

	private Map<String, Integer> importRules(String ruleType, String[] ruleRefIdList, String comment, String[] importTypeList, String[] importAsRefIdList, String[] ruleNameList){
		Map<String, Integer> statusMap = new LinkedHashMap<String, Integer>();
		String store = UtilityService.getStoreId();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		String userName = UtilityService.getUsername();

		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setOperation(String.valueOf(AuditTrailConstants.Operation.importRule));
		auditTrail.setUsername(userName);
		auditTrail.setStoreId(store);
		auditTrail.setEntity(String.valueOf(AuditTrailConstants.Entity.ruleStatus));

		Map<String, String> forPublishingMap = new LinkedHashMap<String, String>();
		Map<String, String> ruleNameMap = new LinkedHashMap<String, String>();
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			
			String ruleId = ruleRefIdList[i];
			ImportType importType = ImportType.getByDisplayText(importTypeList[i]);
			String ruleName = ruleNameList[i];
			String importAsId = importAsRefIdList[i];

			// initialize to status to 0
			int status = CREATE_RULE_STATUS;

			//if importAsId is null, generate a new id
			switch(ruleEntity){
			case ELEVATE:
			case EXCLUDE:
			case DEMOTE:
				ruleId = importAsId = ruleName;
				break;
			case FACET_SORT:
				FacetSort facetSort = facetSortService.getRuleByName(ruleName);
				if(facetSort != null){
					importAsId = facetSort.getRuleId();
					ruleName = facetSort.getRuleName();
				}
				else {
					importAsId = DAOUtils.generateUniqueId();
				}
				break;
			case QUERY_CLEANING:
			case RANKING_RULE:
				if(StringUtils.isBlank(importAsId) || "0".equalsIgnoreCase(importAsId)){
					importAsId = DAOUtils.generateUniqueId();
				}
				break;
			default:
				break;
			}

			if(importRule(ruleEntity, store, ruleId, comment, importType, importAsId, ruleName)){
				try {
					daoService.addRuleStatus(new RuleStatus(ruleEntity, store, importAsId, ruleName, userName, userName, 
							RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
					status = SUBMIT_FOR_APPROVAL;
					RuleStatus ruleStatus = new RuleStatus(RuleEntity.getId(ruleType), store, importAsId);
					SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(ruleStatus);
					RuleStatus currRuleStatus = null;

					try {
						RecordSet<RuleStatus> rSet = daoService.getRuleStatus(searchCriteria);

						if (rSet != null && CollectionUtils.isNotEmpty(rSet.getList())) {
							currRuleStatus = rSet.getList().get(0);
							daoService.addRuleStatusComment(RuleStatusEntity.IMPORTED, store, userName, comment, currRuleStatus.getRuleStatusId());
							auditTrail.setDateTime(DateTime.now());
							auditTrail.setReferenceId(ruleStatus.getRuleRefId());
							if (ruleEntity == RuleEntity.ELEVATE || ruleEntity == RuleEntity.EXCLUDE || ruleEntity == RuleEntity.DEMOTE) {
								auditTrail.setKeyword(ruleStatus.getRuleRefId());
							}
							auditTrail.setDetails(String.format("Imported reference id = [%1$s], rule type = [%2$s].", 
									auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId())));
							daoService.addAuditTrail(auditTrail);							

							if("DELETE".equalsIgnoreCase(currRuleStatus.getUpdateStatus())){
								RuleStatus rStatus = currRuleStatus;
								rStatus.setApprovalStatus("");
								rStatus.setUpdateStatus("");

								if(daoService.updateRuleStatus(rStatus) > 0){
									logger.info("Remove delete rule status for " + currRuleStatus.getRuleStatusId());
								}else{
									logger.error("Failed to remove delete rule status for " + currRuleStatus.getRuleStatusId());
								};
							}

						}
						else {
							logger.error("No rule status found for " + ruleEntity + " : "  + importAsId);
						}

					} catch (DaoException e) {
						logger.error("Failed to get rule status for " + ruleEntity + " : "  + importAsId, e);
					}
					
					if(ImportType.FOR_APPROVAL == importType || ImportType.AUTO_PUBLISH == importType){
						//submit rule for approval
						ruleStatus = deploymentService.processRuleStatus(ruleType, importAsId, ruleName, false);
						status++;
						
						if(ruleStatus != null && ImportType.AUTO_PUBLISH == importType){
							forPublishingMap.put(ruleStatus.getRuleRefId(), ruleStatus.getRuleStatusId());
							ruleNameMap.put(ruleStatus.getRuleRefId(), getRuleName(ruleEntity, ruleId, ruleName));
						}
						else{
							status = IMPORT_SUCCESS;
						}
					} else {
						status = IMPORT_SUCCESS;
					}
					
				} catch (DaoException de) {
					String msg = "";
					switch (status) {
						case CREATE_RULE_STATUS: msg = "Failed to create rule status for: "; break;
						case SUBMIT_FOR_APPROVAL: msg = "Failed to submit rule for approval: "; break;
					}
					logger.error(msg + importAsId);
				}finally{
					statusMap.put(getRuleName(ruleEntity, ruleId, ruleName), status);
				}
			}
		}
			
		if(MapUtils.isNotEmpty(forPublishingMap)) {
			int status = APPROVE_RULE;
			String[] ruleRefIds = forPublishingMap.keySet().toArray(new String[0]);
			String[] ruleStatusIds = forPublishingMap.values().toArray(new String[0]);
			try{
				//approve rule
				if (CollectionUtils.isNotEmpty(deploymentService.approveRule(ruleType, ruleRefIds, comment, ruleStatusIds))) {
					status = PUBLISH_RULE;
					
					//publish rule
					RecordSet<DeploymentModel> deploymentRS = deploymentService.publishRuleNoLock(ruleType, 
							ruleRefIds, comment, ruleStatusIds);
					if (deploymentRS == null  || CollectionUtils.isEmpty(deploymentRS.getList()) 
							|| deploymentRS.getList().get(0).getPublished() != 1) {
						status = PUBLISH_FAILED;
					} else {
						status = IMPORT_SUCCESS;
					}
				}
			}catch (Exception de) {
				String msg = "";
				switch (status) {
					case APPROVE_RULE: msg = "Failed to approve rules: "; break;
					case PUBLISH_RULE: msg = "Failed to publish rules: "; break;
				}
				logger.error(msg + ruleRefIds);
			}finally{
				if(ruleRefIds != null){
					for(String ruleRefId : ruleRefIds){
						statusMap.put(ruleNameMap.get(ruleRefId), status);
					}
				}
			}
		}
		return statusMap;
	}
	
	@RemoteMethod
	public Map<String, String> importRejectRules(String ruleType,
			String[] importRuleRefIdList, String comment,
			String[] importTypeList, String[] importAsRefIdList,
			String[] ruleNameList, String[] rejectRuleRefIdList,
			String[] rejectRuleNameList) throws PublishLockException {
		Map<String, String> successList = new HashMap<String, String>();

		Integer status = null;
		boolean autoPublish = false;
		boolean obtainedLock = false;
		
		if (ArrayUtils.isNotEmpty(importTypeList)) {
			for (String importType: importTypeList) {
				if (ImportType.AUTO_PUBLISH.equals(ImportType.getByDisplayText(importType))) {
					autoPublish = true;
					break;
				}
			}
		}
		
		try {
			if (autoPublish) {
				obtainedLock = UtilityService.obtainPublishLock(RuleEntity.find(ruleType));
			}
			if (ArrayUtils.isNotEmpty(importRuleRefIdList)) {
				Map<String, Integer> statusMap = importRules(ruleType, importRuleRefIdList, comment,
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
				Map<String, Integer> statusMap = unimportRules(ruleType, rejectRuleRefIdList,
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
				UtilityService.releasePublishLock(RuleEntity.find(ruleType));
			}
		}
		return successList;
	}
	
	private String getRuleName(RuleEntity ruleEntity, String ruleId, String ruleName){
		switch(ruleEntity){
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

	private boolean importRule(RuleEntity ruleEntity, String store, String ruleId, String comment, ImportType importType, String importAsRefId, String ruleName) {
		boolean success = false;
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		RuleXml ruleXml = RuleTransferUtil.getRuleToImport(store, ruleEntity, id);

		String storeIdOrigin = ruleXml.getStore();
		String ruleIdOrigin = ruleXml.getRuleId();
		String ruleNameOrigin = ruleXml.getRuleName();

		ruleXml.setStore(store);
		ruleXml.setRuleId(importAsRefId);
		ruleXml.setRuleName(ruleName);
		ruleXml.setCreatedBy(UtilityService.getUsername());

		if(RuleTransferUtil.importRule(store, importAsRefId, ruleXml)){
			success = true;
			logger.info(String.format("Rule Xml [store=%s, ruleEntity=%s, ruleId=%s] successfully imported.", store, ruleEntity.name(), ruleId));
			ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, store, importAsRefId, ruleName, ruleEntity);
			exportRuleMap.setDeleted(RuleTransferUtil.deleteRuleFile(ruleEntity, store, ruleId, comment));
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
	 * @return list of rule name of successfully rejected rule
	 */
	private Map<String, Integer> unimportRules(String ruleType, String[] ruleRefIdList, String comment, String[] ruleNameList){
		Map<String, Integer> statusMap = new LinkedHashMap<String, Integer>();
		String store = UtilityService.getStoreId();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);

		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
			String ruleName = ruleNameList[i];
			String refId = ruleId;
			int status = 0;

			switch(ruleEntity){
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

			RuleXml ruleXml = RuleTransferUtil.getRuleToImport(store, ruleEntity, RuleXmlUtil.getRuleId(ruleEntity, refId));
			if(ruleXml != null) {
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
	public boolean getAutoExport(){
		return BooleanUtils.toBoolean(UtilityService.getStoreSetting(DAOConstants.SETTINGS_AUTO_EXPORT));
	}

	@RemoteMethod
	public boolean setAutoExport(boolean autoexport){
		return UtilityService.setStoreSetting(DAOConstants.SETTINGS_AUTO_EXPORT, BooleanUtils.toStringTrueFalse(autoexport));
	}

	@RemoteMethod
	public ExportRuleMap getRuleTransferMap(String storeIdOrigin, String ruleIdOrigin, String ruleEntity){
		String storeIdTarget = UtilityService.getStoreId();
		ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, ruleIdOrigin, null,  storeIdTarget, null, null, RuleEntity.getId(ruleEntity));

		try {
			List<ExportRuleMap> rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap), null).getList();

			if(CollectionUtils.isNotEmpty(rtList)){
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
		String storeIdTarget = UtilityService.getStoreId();
		ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, null, null, storeIdTarget, null, null, RuleEntity.getId(ruleEntity));
		
		try {
			RecordSet<ExportRuleMap> rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap), null);
			
			if(rtList != null)
				return rtList.getList();
			
		} catch (DaoException e) {
			logger.error("Failed to retrieve mapping of ruleId", e);
		}
		return null;
	}
	
	@RemoteMethod
	public Map<String, ExportRuleMap> getMapRuleTransferMap(String storeIdOrigin, String[] ruleIdsOrigin, String ruleEntity) {
		String storeIdTarget = UtilityService.getStoreId();
		ExportRuleMap exportRuleMap = new ExportRuleMap(storeIdOrigin, null, null, storeIdTarget, null, null, RuleEntity.getId(ruleEntity));
		Map<String, ExportRuleMap> map = new HashMap<String, ExportRuleMap>();
		boolean returnAllIdsOrigin = ArrayUtils.isEmpty(ruleIdsOrigin);
		
		try {
			List<ExportRuleMap> rtList = daoService.getExportRuleMap(new SearchCriteria<ExportRuleMap>(exportRuleMap), null).getList();
			if(CollectionUtils.isNotEmpty(rtList)) {
				for (ExportRuleMap item: rtList) {
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
