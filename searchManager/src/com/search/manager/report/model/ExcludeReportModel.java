package com.search.manager.report.model;

import java.util.List;

public class ExcludeReportModel extends ReportModel<ExcludeReportBean> {

	public ExcludeReportModel(ReportHeader reportHeader, List<ExcludeReportBean> records) {
		super(reportHeader, ExcludeReportBean.class, records);
	}
	public ExcludeReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<ExcludeReportBean> records) {
		super(reportHeader, subReportHeader, ExcludeReportBean.class, records);
	}
}
