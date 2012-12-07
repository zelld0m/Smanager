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

import com.search.manager.enums.RuleEntity;
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
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.manager.xml.file.RuleXmlReportUtil;

@Controller
@RequestMapping("/version")
@Scope(value="prototype")
public class RuleVersionController {
	
	private static final Logger logger = Logger.getLogger(RuleVersionController.class);
	
	@Autowired private RuleVersionService ruleVersionService;
	@Autowired private DownloadService downloadService;
		
	/**
	 * Downloads the report as an Excel format.
	 * <p>
	 * Make sure this method doesn't return any model. Otherwise, you'll get
	 * an "IllegalStateException: getOutputStream() has already been called for this response"
	 */
	@RequestMapping(value = "/{store}/xls", method = RequestMethod.GET)
	// TODO: change to POST, retrieve filter type
	public void getXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		String filename = StringUtils.isNotBlank(request.getParameter("filename")) ? request.getParameter("filename") : "rule version";
		String fileType = request.getParameter("type");
		long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
		String ruleType = request.getParameter("ruleType");
		String ruleName = request.getParameter("ruleName");
		String ruleId = request.getParameter("ruleId");
		Date headerDate = new Date(clientTimezone);
		RuleEntity ruleEntity = RuleEntity.find(ruleType);

		logger.debug(String.format("Received request to download report as an XLS: %s", filename));
			
		ReportHeader reportHeader = getReportHeader(filename, headerDate, ruleName, ruleEntity);

		switch(ruleEntity){
		case ELEVATE: 		downloadElevate(response, reportHeader, fileType, ruleType, ruleId);	break;
		case DEMOTE: 		downloadDemote(response, reportHeader, fileType, ruleType, ruleId);		break;
		case EXCLUDE:		downloadExclude(response, reportHeader, fileType, ruleType, ruleId); 	break;
		case FACET_SORT: 	downloadFacetSort(response, reportHeader, fileType, ruleType, ruleId);	break;
		case QUERY_CLEANING:downloadRedirect(response, reportHeader, fileType, ruleType, ruleId);	break;
		case RANKING_RULE: 	downloadRelevancy(response, reportHeader, fileType, ruleType, ruleId);	break;
		default: break;
		}
	}
	
	private ReportHeader getReportHeader(String filename, Date headerDate, String ruleName, RuleEntity ruleEntity) {
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", "", filename, headerDate);
		String subTitle = "";
		
		switch(ruleEntity){
		case ELEVATE: 		
			subTitle = "List of Elevated Items for [" + ruleName + "]";
			break;
		case DEMOTE: 	
			subTitle = "List of Demoted Items for [" + ruleName + "]";
			break;
		case EXCLUDE:	
			subTitle = "List of Excluded Items for [" + ruleName + "]";
			break;
		case FACET_SORT: 	
			subTitle = "Facet Sort Rule [" + ruleName + "]";
			break;
		case QUERY_CLEANING:
			subTitle = "Query Cleaning Rule [" + ruleName + "]";
			break;
		case RANKING_RULE: 	
			subTitle = "Ranking Rule [" + ruleName + "]";
			break;
		default: break;
		}
		
		reportHeader.setSubReportName(subTitle);
		return reportHeader;
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
	
	private SubReportHeader getSubReportHeader(RuleXml xml, RuleEntity ruleEntity){
		SubReportHeader subReportHeader = new SubReportHeader();
		subReportHeader.addRow("Date Created: ", xml.getCreatedDate() != null ? DateAndTimeUtils.formatDateUsingConfig(UtilityService.getStoreName(), xml.getCreatedDate()) : "");
		subReportHeader.addRow("Created By: ", xml.getCreatedBy());
		subReportHeader.addRow("Version No.: ", String.valueOf(xml.getVersion()));
		
		return subReportHeader;
	}
	
	private void downloadElevate(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String ruleId){
		ReportModel<ElevateReportBean> reportModel = new ElevateReportModel(reportHeader, new ArrayList<ElevateReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		
		List<RuleXml> rules = ruleVersionService.getRuleVersions(ruleType, ruleId);
		if(rules != null){
			for(RuleXml xml : rules){
				if(xml != null){
					SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType));
					subModels.add(new ElevateReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getElevateProducts((ElevateRuleXml) xml)));
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadDemote(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String ruleId){
		ReportModel<DemoteReportBean> reportModel = new DemoteReportModel(reportHeader, new ArrayList<DemoteReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		
		List<RuleXml> rules = ruleVersionService.getRuleVersions(ruleType, ruleId);
			if(rules != null){
				for(RuleXml xml : rules){
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType));
						subModels.add(new DemoteReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getDemoteProducts((DemoteRuleXml) xml)));
					}
				}
			}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadExclude(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String ruleId){
		ReportModel<ExcludeReportBean> reportModel = new ExcludeReportModel(reportHeader, new ArrayList<ExcludeReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		List<RuleXml> rules = ruleVersionService.getRuleVersions(ruleType, ruleId);
			if(rules != null){
				for(RuleXml xml : rules){
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType));
						subModels.add(new ExcludeReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getExcludeProducts((ExcludeRuleXml) xml)));
					}
				}
			}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadFacetSort(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String ruleId){
		ReportModel<FacetSortReportBean> reportModel = new FacetSortReportModel(reportHeader, new ArrayList<FacetSortReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		List<RuleXml> rules = ruleVersionService.getRuleVersions(ruleType, ruleId);
			if(rules != null){
				for(RuleXml rule : rules){
					FacetSortRuleXml xml = (FacetSortRuleXml) rule;
					if(rule != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType));
						subModels.add(new FacetSortReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getFacetSortReportBeanList(xml)));
					}
				}
			}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadRedirect(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String ruleId){
		ReportModel<RedirectRuleReportBean> reportModel = new RedirectRuleReportModel(reportHeader, new ArrayList<RedirectRuleReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		List<RuleXml> rules = ruleVersionService.getRuleVersions(ruleType, ruleId);
			if(rules != null){
				for(RuleXml rule : rules){
					RedirectRuleXml xml = (RedirectRuleXml) rule;
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType));
						subModels.addAll(RuleXmlReportUtil.getRedirectSubReports(xml, reportHeader, subReportHeader));
					}
				}
			}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadRelevancy(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String ruleId){
		ReportModel<RelevancyReportBean> reportModel = new RelevancyReportModel(reportHeader, new ArrayList<RelevancyReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		List<RuleXml> rules = ruleVersionService.getRuleVersions(ruleType, ruleId);
			if(rules != null){
				for(RuleXml rule : rules){
					RankingRuleXml xml = (RankingRuleXml) rule;
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType));
						subModels.addAll(RuleXmlReportUtil.getRelevancySubReports(xml, reportHeader, subReportHeader));
					}
				}
			}
		download(response, reportModel, subModels, fileType);
	}
}
