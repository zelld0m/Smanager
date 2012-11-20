package com.search.manager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.RuleXml;
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
	
	@RemoteMethod
	public RecordSet<RuleStatus> getPublishedRules(String ruleType){
		return deploymentService.getDeployedRules(ruleType, "PUBLISHED");
	}
	
	@RemoteMethod
	public List<RuleXml> getAllRulesToImport(String ruleType){
		return RuleTransferUtil.getAllExportedRules(UtilityService.getStoreName(), ruleType);
	}
	
	@RemoteMethod
	public RuleXml getRuleToExport(String ruleType, String ruleId){   
		List<RuleXml> ruleVersions = daoService.getPublishedRuleVersions(UtilityService.getStoreName(), ruleType, ruleId);
		return RuleXmlUtil.getLatestVersion(ruleVersions);
	}
	
	@RemoteMethod
	public RuleXml getRuleToImport(String ruleType, String ruleId){
		return RuleTransferUtil.getRuleToImport(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId);
	}
	
	@RemoteMethod
	public List<String> exportRule(String ruleType, String[] ruleRefIdList, String comment) {
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		List<String> successList = new ArrayList<String>();
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
			RuleXml ruleXml = getRuleToExport(ruleType, ruleId); //get latest version

			if(RuleTransferUtil.exportRule(store, ruleEntity, ruleId, ruleXml)){
				RuleStatus ruleStatus = new RuleStatus(RuleEntity.getId(ruleType), store, ruleId);
				SearchCriteria<RuleStatus> searchCriteria =new SearchCriteria<RuleStatus>(ruleStatus,null,null,null,null);
				try {
					RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria);
					if (approvedRset.getTotalSize() > 0) {
						ruleStatus = approvedRset.getList().get(0);
						daoService.updateRuleStatusExportInfo(ruleStatus, UtilityService.getUsername(), ExportType.MANUAL, new Date());
						successList.add(ruleId);
					}
					else {
						logger.error("No rule status found for " + ruleEntity + " : "  + ruleId);
					}
				} catch (DaoException e) {
					logger.error("Failed to update rule status for " + ruleEntity + " : "  + ruleId, e);
				}
			}
		}
		return successList;
	}
	
	@RemoteMethod
	public List<String> importRules(String ruleType, String[] ruleRefIdList, String comment, String[] importTypeList, String[] importAsRefIdList, String[] ruleNameList){
		List<String> successList = new ArrayList<String>();
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
			ImportType importType = ImportType.get(importTypeList[i]);
			String ruleName = ruleNameList[i];
			String importAsId = importAsRefIdList[i];
			
			//if importAsId is null, generate a new id
			switch(ruleEntity){
			case ELEVATE:
			case EXCLUDE:
			case DEMOTE:
				if(StringUtils.isBlank(importAsId)){
					importAsId = ruleId;
				}
				ruleName = importAsId;
				break;
			case FACET_SORT:
				FacetSort facetSort = facetSortService.getRuleByName(ruleName);
				importAsId = facetSort != null ? facetSort.getRuleId() : DAOUtils.generateUniqueId();
			case QUERY_CLEANING:
			case RANKING_RULE:
				if(StringUtils.isBlank(importAsId)){
					importAsId = DAOUtils.generateUniqueId();
				}
				break;
			default:
				break;
			}
						
			if(importRule(ruleEntity, store, ruleId, comment, importType, importAsId, ruleName)){
				if(ImportType.FOR_APPROVAL == importType || ImportType.AUTO_PUBLISH == importType){
					//submit rule for approval
					RuleStatus ruleStatus = deploymentService.processRuleStatus(ruleType, importAsId, ruleName, false);

					if(ruleStatus != null && ImportType.AUTO_PUBLISH == importType){
						//approve rule
						deploymentService.approveRule(ruleType, new String[] {ruleStatus.getRuleRefId()}, comment, new String[] {ruleStatus.getRuleStatusId()});
						//publish rule
						deploymentService.publishRule(ruleType, new String[] {ruleStatus.getRuleRefId()}, comment, new String[] {ruleStatus.getRuleStatusId()});
					}
				}
				successList.add(ruleId);
			}
		}
		return successList;
	}
	
	public boolean importRule(RuleEntity ruleEntity, String store, String ruleId, String comment, ImportType importType, String importAsRefId, String ruleName){
		RuleXml ruleXml = RuleTransferUtil.getRuleToImport(store, ruleEntity, ruleId);
		ruleXml.setStore(store);
		ruleXml.setRuleId(importAsRefId);
		ruleXml.setRuleName(ruleName);
		
		if(RuleXmlUtil.importRule(ruleXml)){
			return deleteRuleFile(ruleEntity, store, ruleId, comment);
		}
		return false;
	}
	
	/**
	 * Deletes xml file of rejected rule  
	 * @return list of rule name of successfully rejected rule
	 */
	@RemoteMethod
	public List<String> unimportRules(String ruleType, String[] ruleRefIdList, String comment){
		List<String> successList = new ArrayList<String>();
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
						
			if(deleteRuleFile(ruleEntity, store, ruleId, comment)){
				//TODO addComment
				//TODO addAuditTrail
				successList.add(ruleId);
			}
		}
		return successList;
	}
	
	public boolean deleteRuleFile(RuleEntity ruleEntity, String store, String ruleId, String comment){
		boolean success = false;
		String id = RuleXmlUtil.getRuleId(ruleEntity, ruleId);
		try{
			RuleXmlUtil.deleteFile(RuleTransferUtil.getFilename(store, ruleEntity, id));
			success = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	@RemoteMethod
	public boolean getAutoExport(){
		return BooleanUtils.toBoolean(UtilityService.getStoreSetting(DAOConstants.SETTINGS_AUTO_EXPORT));
	}
	
	@RemoteMethod
	public boolean setAutoExport(boolean autoexport){
		return UtilityService.setStoreSetting(DAOConstants.SETTINGS_AUTO_EXPORT, BooleanUtils.toStringTrueFalse(autoexport));
	}
}
