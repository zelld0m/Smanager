package com.search.manager.report.model;

import java.util.List;

public class ExportRuleReportModel extends ReportModel<ExportRuleReportBean> {

	public ExportRuleReportModel(ReportHeader reportHeader, List<ExportRuleReportBean> records) {
		super(reportHeader, ExportRuleReportBean.class, records);
	}
	
}
