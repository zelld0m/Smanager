package com.search.manager.report.model;

import java.util.List;

public class ElevateReportModel extends ReportModel<ElevateReportBean> {

	public ElevateReportModel(ReportHeader reportHeader, List<ElevateReportBean> records) {
		super(reportHeader, ElevateReportBean.class, records);
	}
	
}
