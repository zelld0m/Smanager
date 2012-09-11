package com.search.manager.report.model;

import java.util.List;

public class DemoteReportModel extends ReportModel<ElevateReportBean> {

	public DemoteReportModel(ReportHeader reportHeader, List<ElevateReportBean> records) {
		super(reportHeader, ElevateReportBean.class, records);
	}
	
}
