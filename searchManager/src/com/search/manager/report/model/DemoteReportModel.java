package com.search.manager.report.model;

import java.util.List;

public class DemoteReportModel extends ReportModel<DemoteReportBean> {

	public DemoteReportModel(ReportHeader reportHeader, List<DemoteReportBean> records) {
		super(reportHeader, DemoteReportBean.class, records);
	}
	
	public DemoteReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<DemoteReportBean> records) {
		super(reportHeader, subReportHeader, DemoteReportBean.class, records);
	}
	
}
