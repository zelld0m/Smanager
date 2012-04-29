package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.RecordSet;
import com.search.manager.report.model.AuditTrailReportBean;
import com.search.manager.report.model.AuditTrailReportModel;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.service.AuditService;
import com.search.manager.service.DownloadService;
import com.search.manager.service.UtilityService;

@Controller
@RequestMapping("/audit")
@Scope(value="prototype")
public class AuditController {
	
	private static final Logger logger = Logger.getLogger(AuditController.class);
	
	@Autowired private DaoCacheService daoCacheService;
	@Autowired private DownloadService downloadService;
	@Autowired private AuditService auditService;
	
	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Audit Trail");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "audit/audit";
	}
	
	@RequestMapping(value = "/{store}/xls", method = RequestMethod.GET)
	public void getXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		
		String type = request.getParameter("type");
		String userName = request.getParameter("username");
		String operation = request.getParameter("operation");
		String entity = request.getParameter("entity");
		String keyword = request.getParameter("keyword");
		String referenceId = request.getParameter("referenceId");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String totalSize = request.getParameter("totalSize");
		String filename = "audittrail";
		
		logger.debug(String.format("Received request to download report as an XLS: %s %s %s %s %s %s %s %s %s", userName, operation, entity, keyword, referenceId, startDate, endDate, totalSize, filename));
		
		int nTotalSize = 0;
		try {
			nTotalSize = Integer.parseInt(totalSize);
		} catch (Exception e) {
			nTotalSize = 1000;
		}
		RecordSet<AuditTrail> AuditTrails = auditService.getAuditTrail(userName, operation, entity, keyword, referenceId, startDate, endDate, 1, nTotalSize);
		
		List<AuditTrailReportBean> list = new ArrayList<AuditTrailReportBean>();
		for (AuditTrail p: AuditTrails.getList()) {
			list.add(new AuditTrailReportBean(p));
		}
		
		String subTitle = "Audit Trail";
		
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, new Date());
		ReportModel<AuditTrailReportBean> reportModel = new AuditTrailReportModel(reportHeader, list);
		
		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
			downloadService.downloadXLS(response, reportModel, null);
		}
	}
}
