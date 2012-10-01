package com.search.manager.report.model;

import java.util.List;

public class FacetSortReportModel extends ReportModel<FacetSortReportBean> {

	public FacetSortReportModel(ReportHeader reportHeader, List<FacetSortReportBean> records) {
		super(reportHeader, FacetSortReportBean.class, records);
	}
	
}
