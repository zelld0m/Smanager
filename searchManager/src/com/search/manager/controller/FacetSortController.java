package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.report.model.FacetSortReportBean;
import com.search.manager.report.model.FacetSortReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.service.DownloadService;
import com.search.manager.service.FacetSortService;
import com.search.manager.service.UtilityService;

@Controller
@RequestMapping("/facet")
public class FacetSortController {
	
	private static final Logger logger = Logger.getLogger(FacetSortController.class);
	
	@Autowired private DaoCacheService daoCacheService;
	@Autowired private FacetSortService facetSortService;
	@Autowired private DownloadService downloadService;

	@RequestMapping(value="/{store}")
	public String execute(HttpServletRequest request,HttpServletResponse response, Model model, @PathVariable String store){
		model.addAttribute("store", store);
		try {
			daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Facets");
		} catch (Exception e) {
			logger.error("Failed to access local cache ", e);
		}
		return "bigbets/facet";
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
			filename = "sort facet rule";
		}

		FacetSort facetSortRule = facetSortService.getRuleById(ruleId);
		RecordSet<FacetGroup> groups = facetSortService.getAllFacetGroup(facetSortRule.getId());
		Map<String, List<String>> items = facetSortRule.getItems();
		
		List<FacetSortReportBean> list = new ArrayList<FacetSortReportBean>();
		
		for(FacetGroup group : groups.getList()){
			StringBuilder sb = new StringBuilder();
			List<String> facets = items.get(group.getName());
			
			if(CollectionUtils.isNotEmpty(facets)){
				for(int i=0 ; i < facets.size(); i++){
					sb.append((i+1) + " - " + facets.get(i) + "\n");
				}
			}

			String highlightedFacets = sb.toString();
			if(StringUtils.isNotBlank(highlightedFacets)){
				highlightedFacets = highlightedFacets.substring(0, highlightedFacets.length()-1);
			}
			else{
				highlightedFacets = "No Highlighted Facets";
			}
			
			FacetSortReportBean reportBean = new FacetSortReportBean(group, highlightedFacets);
			list.add(reportBean);
		}
		
		
		
		String subTitle = "Facet Sort Rule [" + facetSortRule.getRuleName() + "] \n Type: " + facetSortRule.getRuleType().getDisplayText();
		ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
		FacetSortReportModel reportModel = new FacetSortReportModel(reportHeader, list);

		List<ReportModel<? extends ReportBean<?>>> subReports = new ArrayList<ReportModel<? extends ReportBean<?>>>();
		//TODO subReports.add();
		
		// Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
		if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
			downloadService.downloadXLS(response, reportModel, subReports);
		}
	}
}
