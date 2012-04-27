package com.search.manager.report.model;

import java.util.List;

public class ApprovalRuleStatusReportModel extends ReportModel<ApprovalRuleStatusReportBean> {

	public ApprovalRuleStatusReportModel(ReportHeader reportHeader, List<ApprovalRuleStatusReportBean> records) {
		super(reportHeader, ApprovalRuleStatusReportBean.class, records);
	}
	
}