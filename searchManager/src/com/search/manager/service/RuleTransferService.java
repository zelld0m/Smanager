package com.search.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
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
import com.search.manager.enums.ImportType;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Product;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeItemXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleTransferUtil;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.ws.ConfigManager;
import com.search.ws.SearchHelper;

@Service(value = "ruleTransferService")
@RemoteProxy(
		name = "RuleTransferServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "ruleTransferService")
	)
public class RuleTransferService {

	@Autowired private DeploymentService deploymentService;
	@Autowired private DaoService daoService;
	
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
		RuleXml latestVersion = null;
		
		for(RuleXml rule : ruleVersions){
			if(latestVersion == null || rule.getVersion() > latestVersion.getVersion()){
				latestVersion = rule;
			}
		}
		
		return latestVersion;
	}
	
	@RemoteMethod
	public RuleXml getRuleToImport(String ruleType, String ruleId){
		return RuleTransferUtil.getRuleToImport(UtilityService.getStoreName(), RuleEntity.find(ruleType), ruleId);
	}
	
	@RemoteMethod
	public List<String> exportRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		List<String> successList = new ArrayList<String>();
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
			RuleXml ruleXml = getRuleToExport(ruleType, ruleId); //get latest version
			
			if(RuleTransferUtil.exportRule(store, ruleEntity, ruleId, ruleXml)){
				//TODO update rule status
				//TODO add Comment
				//TODO add audit trail
				successList.add(ruleId);
			}
		}
		return successList;
	}
	
	@RemoteMethod
	public List<String> importRules(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList, String[] importTypeList, String[] importAsRefIdList, String[] ruleNameList){
		List<String> successList = new ArrayList<String>();
		Map<String, String> forApprovalIdList = new HashMap<String, String>();
		Map<String, String> forPublishingIdList = new HashMap<String, String>();
		
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
			
			ImportType importType = ImportType.get(importTypeList[i]);
			String ruleName = ruleNameList[i];
			String importAsId = importAsRefIdList[i];
			String statusId = ruleStatusIdList[i];
			
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
			case QUERY_CLEANING:
			case RANKING_RULE:
				if(StringUtils.isBlank(importAsId)){
					DAOUtils.generateUniqueId();
				}
				break;
			default:
				break;
			}
			
						
			if(importRule(ruleEntity, store, ruleId, comment, importType, importAsId, ruleName)){
				switch(importType){
				case AUTO_PUBLISH:
					forPublishingIdList.put(importAsId, statusId);
				case FOR_APPROVAL:
					forApprovalIdList.put(importAsId, statusId);
					break;
				case FOR_REVIEW: //do nothing
				default: break;
				}
				successList.add(ruleId);
			}
		}
		
		if(forApprovalIdList.size() > 0){
			deploymentService.approveRule(ruleType, forApprovalIdList.keySet().toArray(new String[0]), comment, forApprovalIdList.values().toArray(new String[0]));
		}
		
		if(forPublishingIdList.size() > 0){
			deploymentService.publishRule(ruleType, forPublishingIdList.keySet().toArray(new String[0]), comment, forPublishingIdList.values().toArray(new String[0]));
		}
		
		return successList;
	}
	
	public boolean importRule(RuleEntity ruleEntity, String store, String ruleId, String comment, ImportType importType, String importAsRefId, String ruleName){
		RuleXml ruleXml = RuleTransferUtil.getRuleToImport(store, ruleEntity, ruleId);
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
		try{
			RuleXmlUtil.deleteFile(RuleTransferUtil.getFilename(store, ruleEntity, ruleId));
			success = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	@RemoteMethod
	public boolean getAutoExport(){
		ConfigManager cm = ConfigManager.getInstance();
		return BooleanUtils.toBoolean(cm.getStoreSetting(UtilityService.getStoreName(), DAOConstants.SETTINGS_AUTO_EXPORT));
	}
	
	@RemoteMethod
	public boolean setAutoExport(boolean autoexport){
		ConfigManager cm = ConfigManager.getInstance();
		return cm.setStoreSetting(UtilityService.getStoreName(), DAOConstants.SETTINGS_AUTO_EXPORT, BooleanUtils.toStringTrueFalse(autoexport));
	}
}
