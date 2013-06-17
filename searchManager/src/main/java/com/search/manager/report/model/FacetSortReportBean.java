package com.search.manager.report.model;

import com.search.manager.model.FacetGroup;
import com.search.manager.report.annotation.ReportField;

public class FacetSortReportBean extends ReportBean<FacetGroup> {

//	private String ruleName;
//	private String ruleType;
	private String highlightedFacets;
	private String sortType;
	
	public FacetSortReportBean(FacetGroup model) {
		super(model);
	}
	
	public FacetSortReportBean(FacetGroup model, String highlightedFacets) {
		super(model);
		this.highlightedFacets = highlightedFacets;
	}
	
	@ReportField(label="Facet Name", size=20, sortOrder=1)
	public String getFacetName(){
		return model.getName();
	}
	
	@ReportField(label="Highlighted Facets", size=40, sortOrder=2)
	public String getHighlightedFacets(){
		return highlightedFacets;
	}

	@ReportField(label="Sorting of Other Facets", size=30, sortOrder=3)
	public String getSortType(){
		return model.getSortType() != null ? model.getSortType().getDisplayText() : sortType;
	}
	
	@ReportField(label="Created By", size=20, sortOrder=4)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=5)
	public String getCreatedDate(){
		return model.getFormattedCreatedDate();
	}

	/*@ReportField(label="Modified By", size=20, sortOrder=6)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=7)
	public String getModifiedDate(){
		return DateAndTimeUtils.formatMMddyyyy(model.getLastModifiedDate());
	}*/
}
