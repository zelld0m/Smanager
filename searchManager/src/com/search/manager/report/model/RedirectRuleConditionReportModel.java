package com.search.manager.report.model;

import java.util.List;

public class RedirectRuleConditionReportModel extends ReportModel<RedirectRuleConditionReportBean> {

	public RedirectRuleConditionReportModel(ReportHeader reportHeader, List<RedirectRuleConditionReportBean> records) {
		super(reportHeader, RedirectRuleConditionReportBean.class, records);
	}
	public RedirectRuleConditionReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<RedirectRuleConditionReportBean> records) {
		super(reportHeader, subReportHeader, RedirectRuleConditionReportBean.class, records);
	}
}