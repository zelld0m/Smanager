package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;

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

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.model.DemoteReportBean;
import com.search.manager.report.model.DemoteReportModel;
import com.search.manager.report.model.ElevateReportBean;
import com.search.manager.report.model.ElevateReportModel;
import com.search.manager.report.model.ExcludeReportBean;
import com.search.manager.report.model.ExcludeReportModel;
import com.search.manager.report.model.FacetSortReportBean;
import com.search.manager.report.model.FacetSortReportModel;
import com.search.manager.report.model.RedirectRuleReportBean;
import com.search.manager.report.model.RedirectRuleReportModel;
import com.search.manager.report.model.RelevancyReportBean;
import com.search.manager.report.model.RelevancyReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleTransferService;
import com.search.manager.xml.file.RuleXmlReportUtil;

@Controller
@RequestMapping("/")
@Scope(value="prototype")
public class RuleTransferController {
	
	private static final Logger logger = Logger.getLogger(RuleTransferController.class);
	
	@Autowired private RuleTransferService ruleTransferService;
	@Autowired private DownloadService downloadService;
		
	/**
	 * Downloads the report as an Excel format.
	 * <p>
	 * Make sure this method doesn't return any model. Otherwise, you'll get
	 * an "IllegalStateException: getOutputStream() has already been called for this response"
	 */
	@RequestMapping(value = "export/{store}/xls", method = RequestMethod.GET)
	// TODO: change to POST, retrieve filter type
	
	
	public void getXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		String filename = StringUtils.isNotBlank(request.getParameter("filename")) ? request.getParameter("filename") : "export rule";
		String fileType = request.getParameter("type");
		long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
		String ruleType = request.getParameter("ruleType");
		Date headerDate = new Date(clientTimezone);
		RuleEntity ruleEntity = RuleEntity.find(ruleType);

		logger.debug(String.format("Received request to download report as an XLS: %s", filename));
			
		String subTitle = "Export Rule [" + ruleEntity.name() + "]";
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);

		switch(ruleEntity){
		case ELEVATE: 		exportElevate(response, reportHeader, fileType, ruleType);	break;
		case DEMOTE: 		exportDemote(response, reportHeader, fileType, ruleType);	break;
		case EXCLUDE:		exportExclude(response, reportHeader, fileType, ruleType); 	break;
		case FACET_SORT: 	exportFacetSort(response, reportHeader, fileType, ruleType);break;
		case QUERY_CLEANING:exportRedirect(response, reportHeader, fileType, ruleType);	break;
		case RANKING_RULE: 	exportRelevancy(response, reportHeader, fileType, ruleType);break;
		default: break;
		}
	}
	
	private void download(HttpServletResponse response, ReportModel<? extends ReportBean<?>> mainModel, ArrayList<ReportModel<? extends ReportBean<?>>> subModels, String fileType){
		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(fileType)) {
			try {
				downloadService.downloadXLS(response, mainModel, subModels);
			} catch (ClassNotFoundException e) {
				logger.error("Error encountered while trying to download.", e);
				e.printStackTrace();
			}
		}
	}
	
	private void exportElevate(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType){
		ReportModel<ElevateReportBean> reportModel = new ElevateReportModel(reportHeader, new ArrayList<ElevateReportBean>());
		
		RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		if(rules != null){
			for(RuleStatus ruleStatus : rules.getList()){
				RuleXml xml = ruleTransferService.getRuleToExport(ruleType, ruleStatus.getRuleId());
				if(xml != null)
				subModels.add(new ElevateReportModel(reportHeader, RuleXmlReportUtil.getElevateProducts((ElevateRuleXml) xml)));
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void exportDemote(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType){
		ReportModel<DemoteReportBean> reportModel = new DemoteReportModel(reportHeader, new ArrayList<DemoteReportBean>());
		
		RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		if(rules != null){
			for(RuleStatus ruleStatus : rules.getList()){
				RuleXml xml = ruleTransferService.getRuleToExport(ruleType, ruleStatus.getRuleId());
				if(xml != null)
				subModels.add(new DemoteReportModel(reportHeader, RuleXmlReportUtil.getDemoteProducts((DemoteRuleXml) xml)));
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void exportExclude(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType){
		ReportModel<ExcludeReportBean> reportModel = new ExcludeReportModel(reportHeader, new ArrayList<ExcludeReportBean>());
		
		RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		if(rules != null){
			for(RuleStatus ruleStatus : rules.getList()){
				RuleXml xml = ruleTransferService.getRuleToExport(ruleType, ruleStatus.getRuleId());
				if(xml != null)
				subModels.add(new ExcludeReportModel(reportHeader, RuleXmlReportUtil.getExcludeProducts((ExcludeRuleXml) xml)));
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void exportFacetSort(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType){
		ReportModel<FacetSortReportBean> reportModel = new FacetSortReportModel(reportHeader, new ArrayList<FacetSortReportBean>());
		
		RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		if(rules != null){
			for(RuleStatus ruleStatus : rules.getList()){
				String ruleId = ruleStatus.getRuleId();
				FacetSortRuleXml xml = (FacetSortRuleXml) ruleTransferService.getRuleToExport(ruleType, ruleId);
				if(xml != null){
					subModels.add(new FacetSortReportModel(reportHeader, RuleXmlReportUtil.getFacetSortReportBeanList(xml)));
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void exportRedirect(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType){
		ReportModel<RedirectRuleReportBean> reportModel = new RedirectRuleReportModel(reportHeader, new ArrayList<RedirectRuleReportBean>());
		
		RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		if(rules != null){
			for(RuleStatus ruleStatus : rules.getList()){
				String ruleId = ruleStatus.getRuleId();
				RedirectRuleXml xml = (RedirectRuleXml) ruleTransferService.getRuleToExport(ruleType, ruleId);
				if(xml != null){
					subModels.addAll(RuleXmlReportUtil.getRedirectSubReports(xml));
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void exportRelevancy(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType){
		ReportModel<RelevancyReportBean> reportModel = new RelevancyReportModel(reportHeader, new ArrayList<RelevancyReportBean>());
		
		RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		if(rules != null){
			for(RuleStatus ruleStatus : rules.getList()){
				String ruleId = ruleStatus.getRuleId();
				RankingRuleXml xml = (RankingRuleXml) ruleTransferService.getRuleToExport(ruleType, ruleId);
				if(xml != null){
					subModels.addAll(RuleXmlReportUtil.getRelevancySubReports(xml));
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
}
