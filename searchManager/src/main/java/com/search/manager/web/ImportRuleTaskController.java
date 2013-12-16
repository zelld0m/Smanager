package com.search.manager.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.dao.DaoException;
import com.search.manager.model.RecordSet;
import com.search.manager.service.UtilityService;
import com.search.manager.workflow.model.ImportRuleTask;
import com.search.manager.workflow.service.impl.ImportRuleTaskService;

/**
 * 
 * @author Jonathan Livelo
 * @since Nov 14, 2013
 * @version 1.0
 */
@Controller
@RequestMapping("/importRuleTask")
public class ImportRuleTaskController {

	@Autowired
	private ImportRuleTaskService importRuleTaskService;
	@Autowired
	private UtilityService utilityService;


	@RequestMapping(value = "/", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String execute(HttpServletRequest request,
			HttpServletResponse response, Model model) throws IOException, DaoException {
		RecordSet<ImportRuleTask> recordSet = importRuleTaskService
				.getImportRuleTasks(1);
		model.addAttribute("importRuleTasks", recordSet.getList());
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", 1);
		model.addAttribute("dateFomat", utilityService.getStoreDateFormat());
		return "importRuleTask/importRuleTask";
	}
	
	@RequestMapping(value = "/paging/{pageNumber}", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String paging(HttpServletRequest request,
			HttpServletResponse response, Model model,@PathVariable int pageNumber) throws IOException, DaoException {
		RecordSet<ImportRuleTask> recordSet = importRuleTaskService
				.getImportRuleTasks(pageNumber);
		model.addAttribute("importRuleTasks", recordSet.getList());
		model.addAttribute("totalCount", recordSet.getTotalSize());
		model.addAttribute("currentPage", pageNumber);
		model.addAttribute("dateFomat", utilityService.getStoreDateFormat());
		return "importRuleTask/list";
	}	
}
