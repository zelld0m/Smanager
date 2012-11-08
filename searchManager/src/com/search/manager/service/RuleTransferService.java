package com.search.manager.service;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

=======
>>>>>>> refs/remotes/origin/sprint_rule_import_export
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.DemoteItemXml;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateItemXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeItemXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.RuleVersionXml;
import com.search.manager.xml.file.RuleTransferUtil;

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
	public List<RuleVersionXml> getAllRulesToImport(String ruleType){
		return RuleTransferUtil.getAllExportedRules(UtilityService.getStoreName(), ruleType);
	}

	@RemoteMethod
	public List<String> exportRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
		String store = UtilityService.getStoreName();
		RuleEntity ruleEntity = RuleEntity.find(ruleType);
		List<String> successList = new ArrayList<String>();
		
		//create xml file
		for(int i = 0 ; i < ruleRefIdList.length; i++){
			RuleVersionXml ruleXml = new RuleVersionXml();
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
					for (ElevateResult elevateResult : elevateItemList) {
						elevateItemXmlList.add(new ElevateItemXml(elevateResult));
					}
				} catch (DaoException e) {
					return null;
				}	
				
				ruleXml = new ElevateRuleXml(store, 0, null, null, null, keyword, elevateItemXmlList);
			case EXCLUDE:
				ruleId = keyword;
				SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(new ExcludeResult(sk));
				List<ExcludeItemXml> excludeItemXmlList = new ArrayList<ExcludeItemXml>();
				try {
					List<ExcludeResult> excludeItemList = daoService.getExcludeResultList(excludeCriteria).getList();
					for (ExcludeResult excludeResult : excludeItemList) {
						excludeItemXmlList.add(new ExcludeItemXml(excludeResult));
					}
				} catch (DaoException e) {
					return null;
				}	
				
				ruleXml = new ExcludeRuleXml(store, 0, null, null, null, keyword, excludeItemXmlList);
			case DEMOTE: 
				ruleId = keyword;
				SearchCriteria<DemoteResult> demoteCriteria = new SearchCriteria<DemoteResult>(new DemoteResult(sk));
				List<DemoteItemXml> demoteItemXmlList = new ArrayList<DemoteItemXml>();
				try {
					List<DemoteResult> demoteItemList = daoService.getDemoteResultList(demoteCriteria).getList();
					for (DemoteResult demoteResult : demoteItemList) {
						demoteItemXmlList.add(new DemoteItemXml(demoteResult));
					}
				} catch (DaoException e) {
					return null;
				}	
				ruleXml = new DemoteRuleXml(store, 0, null, null, null, keyword, demoteItemXmlList);
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
		//TODO
		return null;
<<<<<<< HEAD
	}
	
	public boolean importRule(String ruleType, String ruleRefId, String comment){
		//TODO
		return false;
	}
	
	/**
	 * 
	 * @return list of rule name of successfully rejected rule
	 */
	@RemoteMethod
	public List<String> unimportRules(String ruleType, String[] ruleRefIdList, String comment){
		//TODO
		return null;
	}
	
	public boolean unimportRule(String ruleType, String ruleRefId, String comment){
		//TODO
		return false;
=======
>>>>>>> refs/remotes/origin/sprint_rule_import_export
	}
}
