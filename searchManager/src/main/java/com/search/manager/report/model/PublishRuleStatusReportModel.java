package com.search.manager.report.model;

import java.util.List;

public class PublishRuleStatusReportModel  extends ReportModel<PublishRuleStatusReportBean> {

	public PublishRuleStatusReportModel(ReportHeader reportHeader, List<PublishRuleStatusReportBean> records) {
		super(reportHeader, PublishRuleStatusReportBean.class, records);
	}
	
}