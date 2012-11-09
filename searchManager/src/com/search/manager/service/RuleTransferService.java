package com.search.manager.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
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
import com.search.manager.utility.FileUtil;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.manager.xml.file.RuleTransferUtil;
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
	public List<String> exportRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		List<String> successList = new ArrayList<String>();
		
		//create xml file
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			RuleXml ruleXml = new RuleXml();
			String keyword = ruleRefIdList[i];
			StoreKeyword sk = new StoreKeyword(store, keyword);
			String ruleId = "";
			
			switch(ruleEntity){
			case ELEVATE:
				ruleId = keyword;
				SearchCriteria<ElevateResult> elevateCriteria = new SearchCriteria<ElevateResult>(new ElevateResult(sk));
				List<ElevateItemXml> elevateItemXmlList = new ArrayList<ElevateItemXml>();
				try {
					List<ElevateResult> elevateItemList = daoService.getElevateResultList(elevateCriteria).getList();
					LinkedHashMap<String, Product> map = SearchHelper.getProducts(elevateItemList, store, ruleId);
					
					for (ElevateResult elevateResult : elevateItemList) {
						Product p = elevateResult.getMemberType()==MemberTypeEntity.PART_NUMBER ? map.get(elevateResult.getEdp()): null;
						elevateItemXmlList.add(new ElevateItemXml(elevateResult, p));
					}
				} catch (DaoException e) {
					return null;
				}	
				
				ruleXml = new ElevateRuleXml(store, 0, null, null, null, keyword, elevateItemXmlList);
				break;
			case EXCLUDE:
				ruleId = keyword;
				SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(sk));
				List<ExcludeItemXml> excludeItemXmlList = new ArrayList<ExcludeItemXml>();
				try {
					List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(excludeCriteria).getList();
					LinkedHashMap<String, Product> map = SearchHelper.getProducts(excludeItemList, store, ruleId);
					
					for (ExcludeResult result : excludeItemList) {
						Product p = result.getMemberType()==MemberTypeEntity.PART_NUMBER ? map.get(result.getEdp()): null;
						excludeItemXmlList.add(new ExcludeItemXml(result));
					}
				} catch (DaoException e) {
					return null;
				}	
				
				ruleXml = new ExcludeRuleXml(store, 0, null, null, null, keyword, excludeItemXmlList);
				break;
			case DEMOTE: 
				ruleId = keyword;
				SearchCriteria<DemoteResult> demoteCriteria = new SearchCriteria<DemoteResult>(new DemoteResult(sk));
				List<DemoteItemXml> demoteItemXmlList = new ArrayList<DemoteItemXml>();
				try {
					List<DemoteResult> demoteItemList = daoService.getDemoteResultList(demoteCriteria).getList();
					LinkedHashMap<String, Product> map = SearchHelper.getProducts(demoteItemList, store, ruleId);
					for (DemoteResult result : demoteItemList) {
						Product p = result.getMemberType()==MemberTypeEntity.PART_NUMBER ? map.get(result.getEdp()): null;
						//TODO pass Product p ItemXml constructor
						demoteItemXmlList.add(new DemoteItemXml(result));
					}
				} catch (DaoException e) {
					return null;
				}	
				ruleXml = new DemoteRuleXml(store, 0, null, null, null, keyword, demoteItemXmlList);
				break;
			}
			
			if(RuleTransferUtil.exportRuleAsXML(store, ruleEntity, ruleId, ruleXml)){
				//TODO update rule status
				
				successList.add(ruleId);
			}
		}
		return successList;
	}
	
	@RemoteMethod
	public List<String> importRules(String ruleType, String[] ruleRefIdList, String comment){
		List<String> successList = new ArrayList<String>();
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			String ruleId = ruleRefIdList[i];
						
			if(importRule(ruleEntity, store, ruleId, comment)){
				successList.add(ruleId);
			}
		}
		return successList;
	}
	
	public boolean importRule(RuleEntity ruleEntity, String store, String ruleId, String comment){
		if(RuleXmlUtil.restoreRule(RuleTransferUtil.getRule(store, ruleEntity, ruleId))){
			deleteRule(ruleEntity, store, ruleId, comment);
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
						
			if(deleteRule(ruleEntity, store, ruleId, comment)){
				//TODO addComment
				//TODO addAuditTrail
				successList.add(ruleId);
			}
		}
		return successList;
	}
	
	public boolean deleteRule(RuleEntity ruleEntity, String store, String ruleId, String comment){
		String filepath = RuleTransferUtil.getFileName(store, ruleEntity, ruleId);
		boolean success = false;
		try {
			FileUtil.deleteFile(filepath);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
}
