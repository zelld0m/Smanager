package com.search.manager.web;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.ImportType;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.service.UtilityService;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.model.TaskExecutionResult;
import com.search.manager.workflow.model.TaskStatus;
import com.search.manager.workflow.service.ImportRuleTaskService;
import com.search.ws.ConfigManager;

/**
 * 
 * @author Jonathan Livelo
 * @since Nov 14, 2013
 * @version 1.0
 */
@Controller
@RequestMapping("/autoimport")
public class ImportRuleTaskController {

	@Autowired private ConfigManager configManager;
	@Autowired private ImportRuleTaskService importRuleTaskService;	
	@Autowired private UtilityService utilityService;
	
	private static final String PROPERTY_MODULE_NAME = "workflow";
	private static final String PROPERTY_NAME = "targetStore";
	
	@RequestMapping(value = "/{store}", 
	                method = { RequestMethod.GET, RequestMethod.POST })
	public String execute(HttpServletRequest request, HttpServletResponse response, Model model
		, @PathVariable String store) throws IOException, DaoException {
		String storeId = utilityService.getStoreId();
	    ImportRuleTask importRuleTask = new ImportRuleTask();
	    importRuleTask = getStore(importRuleTask,storeId);    
	    SearchCriteria<ImportRuleTask> searchCriteria = new SearchCriteria<ImportRuleTask>(importRuleTask, 1, 10);
		RecordSet<ImportRuleTask> recordSet = importRuleTaskService.getImportRuleTasks(searchCriteria);
		model.addAttribute("importRuleTasks", recordSet.getList());
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", 1);
		model.addAttribute("dateFormat", utilityService.getStoreDateTimeFormat());
		model.addAttribute("types", ImportType.values());
		model.addAttribute("statuses", TaskStatus.values());
		model.addAttribute("filter", ",,,");
		model.addAttribute("isTargetStore",isTargetStore());
		if (!isTargetStore()){
			model.addAttribute("targetStores",configManager.getPropertyList("workflow", store, DAOConstants.SETTINGS_EXPORT_TARGET));
		}	
		return "importRuleTask/importRuleTask";
	}
	
	@RequestMapping(value = "/{store}/page/{pageNumber}/{filter}", 
	                method = { RequestMethod.GET, RequestMethod.POST })
	public String paging(HttpServletRequest request,
			HttpServletResponse response, Model model, @PathVariable String store
			, @PathVariable int pageNumber,@PathVariable String filter) throws IOException, DaoException {
		String storeId = utilityService.getStoreId();
		pageNumber=pageNumber<=0?1:pageNumber;
	    ImportRuleTask importRuleTask = new ImportRuleTask();
	    importRuleTask = getStore(importRuleTask,storeId);
        SearchCriteria<ImportRuleTask> searchCriteria = new SearchCriteria<ImportRuleTask>(importRuleTask, pageNumber, 10);
        searchCriteria = setFilter(searchCriteria, filter);
		RecordSet<ImportRuleTask> recordSet = importRuleTaskService.getImportRuleTasks(searchCriteria);
		model.addAttribute("importRuleTasks", recordSet.getList());
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("filter", filter);
		model.addAttribute("dateFormat", utilityService.getStoreDateTimeFormat());
		
		return "importRuleTask/list";
	}	
	
	private ImportRuleTask getStore(ImportRuleTask importRuleTask,String storeId){
		if (isTargetStore()){
			importRuleTask.setTargetStoreId(storeId);			
		}else{
			importRuleTask.setSourceStoreId(storeId);
		}
		return importRuleTask;
	}
	
	private SearchCriteria<ImportRuleTask> setFilter(SearchCriteria<ImportRuleTask> searchCriteria,String filter){
		if (!filter.isEmpty()){
			String[] arrFilter=filter.split(",");
			if (arrFilter.length==0){
				searchCriteria.getModel().setTaskExecutionResult(null);
				searchCriteria.getModel().setImportType(null);
			}
			for(int ctr = 0; ctr < arrFilter.length; ctr++){
				switch(ctr){
					case 0:
						if(!arrFilter[ctr].isEmpty()){
							TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.getByDisplayText(arrFilter[ctr]),null,0,null,null,null);
							searchCriteria.getModel().setTaskExecutionResult(taskExecutionResult);
						}else{
							searchCriteria.getModel().setTaskExecutionResult(null);
						}
						break;
					case 1:
						if(!arrFilter[ctr].isEmpty()){
							searchCriteria.getModel().setImportType(ImportType.getByDisplayText(arrFilter[ctr]));
						}else{
							searchCriteria.getModel().setImportType(null);
						}
						break;
					case 2:						
						searchCriteria.getModel().setTargetRuleName(arrFilter[ctr]);
						break;		
					case 3:						
						searchCriteria.getModel().setTargetStoreId(arrFilter[ctr]);
						break;							
				}
			}
		}
		return searchCriteria;
	}
	
	private boolean isTargetStore(){
		return (StringUtils.equalsIgnoreCase(
				StringUtils.defaultIfBlank(
						configManager.getProperty(PROPERTY_MODULE_NAME,utilityService.getStoreId()
								, PROPERTY_NAME), "false"),"true"));
	}
}