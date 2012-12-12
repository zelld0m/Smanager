package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
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
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.DemoteRuleXml;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.ExcludeRuleXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RedirectRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleTransferService;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.manager.xml.file.RuleXmlReportUtil;

@Controller
@RequestMapping("/")
@Scope(value="prototype")
public class RuleTransferController {
	
	private static final Logger logger = Logger.getLogger(RuleTransferController.class);
	
	private static final String IMPORT = "import";
	private static final String EXPORT = "export";
	
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
	public void getExportXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
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
		case ELEVATE: 		downloadElevate(response, reportHeader, fileType, ruleType, EXPORT);	break;
		case DEMOTE: 		downloadDemote(response, reportHeader, fileType, ruleType, EXPORT);	break;
		case EXCLUDE:		downloadExclude(response, reportHeader, fileType, ruleType, EXPORT); 	break;
		case FACET_SORT: 	downloadFacetSort(response, reportHeader, fileType, ruleType, EXPORT);break;
		case QUERY_CLEANING:downloadRedirect(response, reportHeader, fileType, ruleType, EXPORT);	break;
		case RANKING_RULE: 	downloadRelevancy(response, reportHeader, fileType, ruleType, EXPORT);break;
		default: break;
		}
	}
	
	@RequestMapping(value = "import/{store}/xls", method = RequestMethod.GET)
	// TODO: change to POST, retrieve filter type
	public void getImportXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
		String filename = StringUtils.isNotBlank(request.getParameter("filename")) ? request.getParameter("filename") : "export rule";
		String fileType = request.getParameter("type");
		long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
		String ruleType = request.getParameter("ruleType");
		Date headerDate = new Date(clientTimezone);
		RuleEntity ruleEntity = RuleEntity.find(ruleType);

		logger.debug(String.format("Received request to download report as an XLS: %s", filename));
			
		String subTitle = "Import Rule [" + ruleEntity.name() + "]";
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);

		switch(ruleEntity){
		case ELEVATE: 		downloadElevate(response, reportHeader, fileType, ruleType, IMPORT);	break;
		case DEMOTE: 		downloadDemote(response, reportHeader, fileType, ruleType, IMPORT);	break;
		case EXCLUDE:		downloadExclude(response, reportHeader, fileType, ruleType, IMPORT); 	break;
		case FACET_SORT: 	downloadFacetSort(response, reportHeader, fileType, ruleType, IMPORT);break;
		case QUERY_CLEANING:downloadRedirect(response, reportHeader, fileType, ruleType, IMPORT);	break;
		case RANKING_RULE: 	downloadRelevancy(response, reportHeader, fileType, ruleType, IMPORT);break;
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
	
	private SubReportHeader getSubReportHeader(RuleXml xml, RuleEntity ruleEntity, String transferType){
		SubReportHeader subReportHeader = new SubReportHeader();
		
		switch(ruleEntity){
		case ELEVATE:
		case EXCLUDE:
		case DEMOTE:
			subReportHeader.addRow("Rule Info: ", xml.getRuleId());
			break;
		case FACET_SORT:
			FacetSortRuleXml fsXml = (FacetSortRuleXml) xml;
			subReportHeader.addRow("Rule Info: ", fsXml.getRuleName() + "[" + fsXml.getRuleId() + "]");
			subReportHeader.addRow("Rule Type: ", fsXml.getRuleType() != null ? fsXml.getRuleType().getDisplayText() : "");
			break;
		case QUERY_CLEANING:
		case RANKING_RULE:
			subReportHeader.addRow("Rule Info: ", xml.getRuleName() + "[" + xml.getRuleId() + "]");
			break;
		default: break;
		}
		
		if(xml.getRuleStatus() != null){
			subReportHeader.addRow("Published Date: ", xml.getRuleStatus().getLastPublishedDate() != null ? DateAndTimeUtils.formatDateUsingConfig(UtilityService.getStoreName(), xml.getRuleStatus().getLastPublishedDate()) : "");
			if(EXPORT.equalsIgnoreCase(transferType)){
				subReportHeader.addRow("Export Type: ", xml.getRuleStatus().getExportType() != null ? xml.getRuleStatus().getExportType().getDisplayText() : "");
				subReportHeader.addRow("Export Date: ", xml.getRuleStatus().getLastExportDate() != null ? DateAndTimeUtils.formatDateUsingConfig(UtilityService.getStoreName(), xml.getRuleStatus().getLastExportDate()) : "");
			}
		}
		
		return subReportHeader;
	}
	
	private void downloadElevate(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String transferType){
		ReportModel<ElevateReportBean> reportModel = new ElevateReportModel(reportHeader, new ArrayList<ElevateReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		
		if(EXPORT.equalsIgnoreCase(transferType)){
			RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
			if(rules != null){
				List<RuleStatus> list = rules.getList();
				if(CollectionUtils.isNotEmpty(list)){
					for(RuleStatus ruleStatus : list){
						RuleXml xml = ruleTransferService.getRuleToExport(ruleType, ruleStatus.getRuleId());
						if(xml != null){
							SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
							subModels.add(new ElevateReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getElevateProducts((ElevateRuleXml) xml)));
						}
					}
				}
			}
		}
		else if(IMPORT.equalsIgnoreCase(transferType)){
			List<RuleXml> rules = ruleTransferService.getAllRulesToImport(ruleType);
			if(CollectionUtils.isNotEmpty(rules)){
				for(RuleXml xml : rules){
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
						subModels.add(new ElevateReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getElevateProducts((ElevateRuleXml) xml)));
					}
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadDemote(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String transferType){
		ReportModel<DemoteReportBean> reportModel = new DemoteReportModel(reportHeader, new ArrayList<DemoteReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		
		if(EXPORT.equalsIgnoreCase(transferType)){
			RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
			if(rules != null){
				List<RuleStatus> list = rules.getList();
				if(CollectionUtils.isNotEmpty(list)){
					for(RuleStatus ruleStatus : list){
						RuleXml xml = ruleTransferService.getRuleToExport(ruleType, ruleStatus.getRuleId());
						if(xml != null){
							SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
							subModels.add(new DemoteReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getDemoteProducts((DemoteRuleXml) xml)));
						}
					}
				}
			}
		}else if(IMPORT.equalsIgnoreCase(transferType)){
			List<RuleXml> rules = ruleTransferService.getAllRulesToImport(ruleType);
			if(CollectionUtils.isNotEmpty(rules)){
				for(RuleXml xml : rules){
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
						subModels.add(new DemoteReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getDemoteProducts((DemoteRuleXml) xml)));
					}
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadExclude(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String transferType){
		ReportModel<ExcludeReportBean> reportModel = new ExcludeReportModel(reportHeader, new ArrayList<ExcludeReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		if(EXPORT.equalsIgnoreCase(transferType)){
			RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
			if(rules != null){
				List<RuleStatus> list = rules.getList();
				if(CollectionUtils.isNotEmpty(list)){
					for(RuleStatus ruleStatus : list){
						RuleXml xml = ruleTransferService.getRuleToExport(ruleType, ruleStatus.getRuleId());
						if(xml != null){
							SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
							subModels.add(new ExcludeReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getExcludeProducts((ExcludeRuleXml) xml)));
						}
					}
				}
			}
		}else if(IMPORT.equalsIgnoreCase(transferType)){
			List<RuleXml> rules = ruleTransferService.getAllRulesToImport(ruleType);
			if(CollectionUtils.isNotEmpty(rules)){
				for(RuleXml xml : rules){
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
						subModels.add(new ExcludeReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getExcludeProducts((ExcludeRuleXml) xml)));
					}
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadFacetSort(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String transferType){
		ReportModel<FacetSortReportBean> reportModel = new FacetSortReportModel(reportHeader, new ArrayList<FacetSortReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		if(EXPORT.equalsIgnoreCase(transferType)){
			RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
			if(rules != null){
				List<RuleStatus> list = rules.getList();
				if(CollectionUtils.isNotEmpty(list)){
					for(RuleStatus ruleStatus : list){
						String ruleId = ruleStatus.getRuleId();
						FacetSortRuleXml xml = (FacetSortRuleXml) ruleTransferService.getRuleToExport(ruleType, ruleId);
						if(xml != null){
							SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
							subModels.add(new FacetSortReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getFacetSortReportBeanList(xml)));
						}
					}
				}
			}
		}else if(IMPORT.equalsIgnoreCase(transferType)){
			List<RuleXml> rules = ruleTransferService.getAllRulesToImport(ruleType);
			if(CollectionUtils.isNotEmpty(rules)){
				for(RuleXml rule : rules){
					FacetSortRuleXml xml = (FacetSortRuleXml) rule;
					if(rule != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
						subModels.add(new FacetSortReportModel(reportHeader, subReportHeader, RuleXmlReportUtil.getFacetSortReportBeanList(xml)));
					}
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadRedirect(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String transferType){
		ReportModel<RedirectRuleReportBean> reportModel = new RedirectRuleReportModel(reportHeader, new ArrayList<RedirectRuleReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		if(EXPORT.equalsIgnoreCase(transferType)){
			RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
			if(rules != null){
				List<RuleStatus> list = rules.getList();
				if(CollectionUtils.isNotEmpty(list)){
					for(RuleStatus ruleStatus : list){
						String ruleId = ruleStatus.getRuleId();
						RedirectRuleXml xml = (RedirectRuleXml) ruleTransferService.getRuleToExport(ruleType, ruleId);
						if(xml != null){
							SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
							subModels.addAll(RuleXmlReportUtil.getRedirectSubReports(xml, reportHeader, subReportHeader));
						}
					}
				}
			}
		}else if(IMPORT.equalsIgnoreCase(transferType)){
			List<RuleXml> rules = ruleTransferService.getAllRulesToImport(ruleType);
			if(CollectionUtils.isNotEmpty(rules)){
				for(RuleXml rule : rules){
					RedirectRuleXml xml = (RedirectRuleXml) rule;
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
						subModels.addAll(RuleXmlReportUtil.getRedirectSubReports(xml, reportHeader, subReportHeader));
					}
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
	
	private void downloadRelevancy(HttpServletResponse response, ReportHeader reportHeader, String fileType, String ruleType, String transferType){
		ReportModel<RelevancyReportBean> reportModel = new RelevancyReportModel(reportHeader, new ArrayList<RelevancyReportBean>());
		ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

		if(EXPORT.equalsIgnoreCase(transferType)){
			RecordSet<RuleStatus> rules = ruleTransferService.getPublishedRules(ruleType);
			if(rules != null){
				List<RuleStatus> list = rules.getList();
				if(CollectionUtils.isNotEmpty(list)){
					for(RuleStatus ruleStatus : list){
						String ruleId = ruleStatus.getRuleId();
						RankingRuleXml xml = (RankingRuleXml) ruleTransferService.getRuleToExport(ruleType, ruleId);
						if(xml != null){
							SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
							subModels.addAll(RuleXmlReportUtil.getRelevancySubReports(xml, reportHeader, subReportHeader));
						}
					}
				}
			}
		}else if(IMPORT.equalsIgnoreCase(transferType)){
			List<RuleXml> rules = ruleTransferService.getAllRulesToImport(ruleType);
			if(CollectionUtils.isNotEmpty(rules)){
				for(RuleXml rule : rules){
					RankingRuleXml xml = (RankingRuleXml) rule;
					if(xml != null){
						SubReportHeader subReportHeader = getSubReportHeader(xml, RuleEntity.find(ruleType), transferType);
						subModels.addAll(RuleXmlReportUtil.getRelevancySubReports(xml, reportHeader, subReportHeader));
					}
				}
			}
		}
		download(response, reportModel, subModels, fileType);
	}
}
