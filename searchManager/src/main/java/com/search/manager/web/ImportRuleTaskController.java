package com.search.manager.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.ImportRuleTaskService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropertiesUtils;
import com.search.manager.utility.StringUtil;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.model.TaskExecutionResult;
import com.search.manager.workflow.model.TaskStatus;
import com.search.manager.xml.file.RuleXmlUtil;
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
	@Autowired
	private RuleXmlUtil ruleXmlUtil;
	
	private static final String PROPERTY_MODULE_NAME = "workflow";
	private static final String PROPERTY_NAME = "targetStore";
	
	@RequestMapping(value = "/{store}", 
	                method = { RequestMethod.GET, RequestMethod.POST })
	public String execute(HttpServletRequest request, HttpServletResponse response, Model model
		, @PathVariable String store) throws IOException, DaoException, CoreServiceException {
		String storeId = utilityService.getStoreId();
	    ImportRuleTask importRuleTask = new ImportRuleTask();
	    importRuleTask = getStore(importRuleTask,storeId);    
	    SearchResult<ImportRuleTask> recordSet = importRuleTaskService.search(importRuleTask, 1, 10);
		
	    model.addAttribute("importRuleTasks", recordSet.getList());
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", 1);
		model.addAttribute("dateFormat", utilityService.getStoreDateTimeFormat());
		model.addAttribute("types", ImportType.values());
		model.addAttribute("statuses", TaskStatus.values());
		model.addAttribute("filter", ",,,");
		model.addAttribute("isTargetStore",isTargetStore());
		model.addAttribute("taskXmlMap", generateTaskXmlList(recordSet.getList()));
		
		if (!isTargetStore()){
			model.addAttribute("targetStores",configManager.getPropertyList("workflow", store, DAOConstants.SETTINGS_EXPORT_TARGET));
		}	
		return "importRuleTask/importRuleTask";
	}
	
	@RequestMapping(value = "/{store}/page/{pageNumber}/{filter}", 
	                method = { RequestMethod.GET, RequestMethod.POST })
	public String paging(HttpServletRequest request,
			HttpServletResponse response, Model model, @PathVariable String store
			, @PathVariable int pageNumber,@PathVariable String filter) throws IOException, DaoException, CoreServiceException {
		String storeId = utilityService.getStoreId();
		pageNumber=pageNumber<=0?1:pageNumber;
	    ImportRuleTask importRuleTask = new ImportRuleTask();
	    importRuleTask = getStore(importRuleTask,storeId);
        importRuleTask = setFilter(importRuleTask, filter);
		SearchResult<ImportRuleTask> recordSet = importRuleTaskService.search(importRuleTask, pageNumber, 10);
		model.addAttribute("importRuleTasks", recordSet.getList());
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("filter", filter);
		model.addAttribute("dateFormat", utilityService.getStoreDateTimeFormat());
		model.addAttribute("taskXmlMap", generateTaskXmlList(recordSet.getList()));
		
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
	
	private Map<String, Boolean> generateTaskXmlList(List<ImportRuleTask> list) {
		Map<String, Boolean> taskXmlMap = new HashMap<String, Boolean>();
		
		for(ImportRuleTask task : list) {
			taskXmlMap.put(task.getTaskId(), hasXml(task.getTargetStoreId(), task.getRuleEntity(), task.getTargetRuleId()));
		}
		
		return taskXmlMap;
	}
	
	private Boolean hasXml(String storeId, RuleEntity ruleEntity,String ruleId) {
		
		if(StringUtils.isEmpty(ruleId) || StringUtils.isEmpty(storeId) || ruleEntity == null)
			return false;
		String fileName = ruleXmlUtil.getFilename(PropertiesUtils.getValue("importfilepath"), storeId, ruleEntity, StringUtil.escapeKeyword(ruleId));
		try {
			return FileUtil.isExist(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private ImportRuleTask setFilter(ImportRuleTask model,String filter){
		if (!filter.isEmpty()){
			String[] arrFilter=filter.split(",");
			if (arrFilter.length==0){
				model.setTaskExecutionResult(null);
				model.setImportType(null);
			}
			for(int ctr = 0; ctr < arrFilter.length; ctr++){
				switch(ctr){
					case 0:
						if(!arrFilter[ctr].isEmpty()){
							TaskExecutionResult taskExecutionResult = new TaskExecutionResult(TaskStatus.getByDisplayText(arrFilter[ctr]),null,0,null,null,null);
							model.setTaskExecutionResult(taskExecutionResult);
						}else{
							model.setTaskExecutionResult(null);
						}
						break;
					case 1:
						if(!arrFilter[ctr].isEmpty()){
							model.setImportType(ImportType.getByDisplayText(arrFilter[ctr]));
						}else{
							model.setImportType(null);
						}
						break;
					case 2:						
					try {
						model.setTargetRuleName(URLDecoder.decode( arrFilter[ctr], "UTF-8" ));
					} catch (UnsupportedEncodingException e) {						
						e.printStackTrace();
					}
						break;		
					case 3:						
						model.setTargetStoreId(arrFilter[ctr]);
						break;							
				}
			}
		}
		return model;
	}
	
	private boolean isTargetStore(){
		return (StringUtils.equalsIgnoreCase(
				StringUtils.defaultIfBlank(
						configManager.getProperty(PROPERTY_MODULE_NAME,utilityService.getStoreId()
								, PROPERTY_NAME), "false"),"true"));
	}
}