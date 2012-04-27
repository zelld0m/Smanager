package com.search.manager.report.model;

import java.util.List;

public class RelevancyReportModel extends ReportModel<RelevancyReportBean> {

	public RelevancyReportModel(ReportHeader reportHeader, List<RelevancyReportBean> records) {
		super(reportHeader, RelevancyReportBean.class, records);
	}
	
}
