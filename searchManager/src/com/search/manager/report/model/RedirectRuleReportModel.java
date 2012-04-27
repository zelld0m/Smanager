package com.search.manager.report.model;

import java.util.List;

public class RedirectRuleReportModel extends ReportModel<RedirectRuleReportBean> {

	public RedirectRuleReportModel(ReportHeader reportHeader, List<RedirectRuleReportBean> records) {
		super(reportHeader, RedirectRuleReportBean.class, records);
	}
	
}
