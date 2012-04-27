package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.model.ApprovalRuleStatusReportBean;
import com.search.manager.report.model.ApprovalRuleStatusReportModel;
import com.search.manager.report.model.PublishRuleStatusReportBean;
import com.search.manager.report.model.PublishRuleStatusReportModel;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.service.DeploymentService;
import com.search.manager.service.DownloadService;

@Controller
@RequestMapping("/")
public class WorkflowController {
	
	private static final Logger logger = Logger.getLogger(WorkflowController.class);

	@Autowired private DeploymentService deploymentService;
	@Autowired private DownloadService downloadService;

	@RequestMapping(value="/approval/{store}")
	public String pendingApproval(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		return "setting/approval";
	}
	
	
	@RequestMapping(value="/production/{store}")
	public String pushToProd(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		return "setting/production";
	}

	/**
	 * Downloads the report as an Excel format.
	 * <p>
	 * Make sure this method doesn't return any model. Otherwise, you'll get
	 * an "IllegalStateException: getOutputStream() has already been called for this response"
	 */
	@RequestMapping(value = "/approval/{store}/xls", method = RequestMethod.GET)
	// TODO: change to POST, retrieve filter type
	public void getApprovalXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		
		String ruleType = request.getParameter("ruleType");
		String includeApproved = request.getParameter("includeApproved");		
		String filename = request.getParameter("filename");
		String type = request.getParameter("type");

		logger.debug(String.format("Received request to download report as an XLS: %s %s %s %s", ruleType, includeApproved, filename, type));
		
		if (StringUtils.isBlank(filename)) {
			filename = "for approval list";
		}

		RecordSet<RuleStatus> ruleStatuses = deploymentService.getApprovalList(ruleType, BooleanUtils.toBoolean("includeApproved"));
		List<ApprovalRuleStatusReportBean> list = new ArrayList<ApprovalRuleStatusReportBean>();
		for (RuleStatus ruleStatus: ruleStatuses.getList()) {
			ruleStatus.setCommentList(deploymentService.getComment(ruleStatus.getRuleStatusId(), null).getList());
			list.add(new ApprovalRuleStatusReportBean(ruleStatus));
		}
		
		String subTitle = "Approval List [" + ruleType + "]";
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, new Date());
		ApprovalRuleStatusReportModel reportModel = new ApprovalRuleStatusReportModel(reportHeader, list);

		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
			downloadService.downloadXLS(response, reportModel, null);
		}
	}
	
	/**
	 * Downloads the report as an Excel format.
	 * <p>
	 * Make sure this method doesn't return any model. Otherwise, you'll get
	 * an "IllegalStateException: getOutputStream() has already been called for this response"
	 */
	@RequestMapping(value = "/production/{store}/xls", method = RequestMethod.GET)
	// TODO: change to POST, retrieve filter type
	public void getPublishXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		
		String ruleType = "Elevate";//request.getParameter("ruleType");
		String filename = request.getParameter("filename");
		String type = "EXCEL"; //request.getParameter("type");

		logger.debug(String.format("Received request to download report as an XLS: %s %s %s", ruleType, filename, type));
		
		if (StringUtils.isBlank(filename)) {
			filename = "for publishing list";
		}

		RecordSet<RuleStatus> ruleStatuses = deploymentService.getDeployedRules(ruleType);
		List<PublishRuleStatusReportBean> list = new ArrayList<PublishRuleStatusReportBean>();
		for (RuleStatus ruleStatus: ruleStatuses.getList()) {
			ruleStatus.setCommentList(deploymentService.getComment(ruleStatus.getRuleStatusId(), null).getList());
			list.add(new PublishRuleStatusReportBean(ruleStatus));
		}
		
		String subTitle = "Publish List [" + ruleType + "]";
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, new Date());
		PublishRuleStatusReportModel reportModel = new PublishRuleStatusReportModel(reportHeader, list);

		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
			downloadService.downloadXLS(response, reportModel, null);
		}
	}
	
}
