package com.search.manager.report.model;

import java.util.List;

public class RelevancyFieldReportModel extends ReportModel<RelevancyFieldReportBean> {

	public RelevancyFieldReportModel(ReportHeader reportHeader, List<RelevancyFieldReportBean> records) {
		super(reportHeader, RelevancyFieldReportBean.class, records);
	}
	public RelevancyFieldReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<RelevancyFieldReportBean> records) {
		super(reportHeader, subReportHeader, RelevancyFieldReportBean.class, records);
	}
}