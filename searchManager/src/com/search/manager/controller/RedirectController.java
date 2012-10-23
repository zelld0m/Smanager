package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.report.model.KeywordReportBean;
import com.search.manager.report.model.KeywordReportModel;
import com.search.manager.report.model.RedirectRuleConditionReportBean;
import com.search.manager.report.model.RedirectRuleConditionReportModel;
import com.search.manager.report.model.RedirectRuleReportBean;
import com.search.manager.report.model.RedirectRuleReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RedirectService;
import com.search.manager.service.UtilityService;

@Controller
@RequestMapping("/redirect")
@Scope(value="prototype")
public class RedirectController {

	private static final Logger logger = Logger.getLogger(RedirectController.class);

	@Autowired private DaoCacheService daoCacheService;
	@Autowired private RedirectService redirectService;
	@Autowired private DownloadService downloadService;
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Query Cleaning");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "rules/redirect";
	}
	
	/**
	 * Downloads the report as an Excel format.
	 * <p>
	 * Make sure this method doesn't return any model. Otherwise, you'll get
	 * an "IllegalStateException: getOutputStream() has already been called for this response"
	 */
	@RequestMapping(value = "/{store}/xls", method = RequestMethod.GET)
	// TODO: change to POST, retrieve filter type
	public void getXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		
		String ruleId = request.getParameter("id");
		String filename = request.getParameter("filename");
		String type = request.getParameter("type");
		long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
		
		Date headerDate = new Date(clientTimezone);

		logger.debug(String.format("Received request to download report as an XLS: %s %s", ruleId, filename));
		
		if (StringUtils.isBlank(filename)) {
			filename = "ranking rule";
		}

		RedirectRule redirectRule = redirectService.getRule(ruleId);
		
		List<KeywordReportBean> keywords = new ArrayList<KeywordReportBean>();
		for (Keyword keyword: redirectService.getAllKeywordInRule(ruleId, null, 0, 0).getList()) {
			keywords.add(new KeywordReportBean(keyword));
		}

		List<RedirectRuleConditionReportBean> conditions = new ArrayList<RedirectRuleConditionReportBean>();
		for (RedirectRuleCondition condition: redirectService.getConditionInRule(ruleId, 0, 0).getList()) {
			conditions.add(new RedirectRuleConditionReportBean(condition.getCondition()));
		}		
		
		List<RedirectRuleReportBean> list = new ArrayList<RedirectRuleReportBean>();
		list.add(new RedirectRuleReportBean(redirectRule));
		
		String subTitle = "Query Cleaning Rule [" + redirectRule.getRuleName() + "]";
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
		RedirectRuleReportModel reportModel = new RedirectRuleReportModel(reportHeader, list);

		List<ReportModel<? extends ReportBean<?>>> subReports = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		subReports.add(new KeywordReportModel(null, keywords));
		subReports.add(new RedirectRuleConditionReportModel(null, conditions));
		
		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
			downloadService.downloadXLS(response, reportModel, subReports);
		}
	}
	
}
