package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.model.Keyword;
import com.search.manager.model.Relevancy;
import com.search.manager.report.model.KeywordReportBean;
import com.search.manager.report.model.KeywordReportModel;
import com.search.manager.report.model.RelevancyFieldReportBean;
import com.search.manager.report.model.RelevancyFieldReportModel;
import com.search.manager.report.model.RelevancyReportBean;
import com.search.manager.report.model.RelevancyReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RelevancyService;

@Controller
@RequestMapping("/relevancy")
@Scope(value="prototype")
public class RelevancyController {

	private static final Logger logger = Logger.getLogger(AuditController.class);

	@Autowired private RelevancyService relevancyService;
	@Autowired private DownloadService downloadService;

	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		
		Map<String, String> longFields = new LinkedHashMap<String, String>();
		longFields.put("qf","Query Fields");
		longFields.put("bf","Boost Function");
		longFields.put("pf","Phrase Field");
		longFields.put("bq","Boost Query");
		
		Map<String, String> shortFields = new LinkedHashMap<String, String>();
		shortFields.put("mm","Min To Match");
		shortFields.put("qs","Query Slop");
		shortFields.put("tie","Tie Breaker");
		shortFields.put("ps","Phrase Slop");
		shortFields.put("q.alt","Q Alt");
		
		model.addAttribute("store", store);
		model.addAttribute("longFields", longFields);
		model.addAttribute("shortFields", shortFields);
		return "relevancy/relevancy";
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
		
		String relevancyId = request.getParameter("id");
		String filename = request.getParameter("filename");
		String type = request.getParameter("type");

		logger.debug(String.format("Received request to download report as an XLS: %s %s", relevancyId, filename));
		
		if (StringUtils.isBlank(filename)) {
			filename = "ranking rule";
		}

		Relevancy relevancy = relevancyService.getById(relevancyId);
		List<KeywordReportBean> keywords = new ArrayList<KeywordReportBean>();
		for (Keyword keyword: relevancyService.getKeywordInRule(relevancyId, null, 0, 0).getList()) {
			keywords.add(new KeywordReportBean(keyword));
		}

		List<RelevancyFieldReportBean> relevancyFields = new ArrayList<RelevancyFieldReportBean>();
		for (String key: relevancy.getParameters().keySet()) {
			String value = relevancy.getParameters().get(key);
			if (value != null) {
				relevancyFields.add(new RelevancyFieldReportBean(new BasicNameValuePair(key, value)));
			}
		}
		
		List<RelevancyReportBean> list = new ArrayList<RelevancyReportBean>();
		list.add(new RelevancyReportBean(relevancy));
		
		String subTitle = "Relevancy Rule [" + relevancy.getRelevancyName() + "]";
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, new Date());
		ReportModel<RelevancyReportBean> reportModel = new RelevancyReportModel(reportHeader, list);

		List<ReportModel<? extends ReportBean<?>>> subReports = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		subReports.add(new KeywordReportModel(null, keywords));
		subReports.add(new RelevancyFieldReportModel(null, relevancyFields));
		
		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
			downloadService.downloadXLS(response, reportModel, subReports);
		}
	}
	
}
