package com.search.manager.report.model;

import java.util.List;

public class KeywordReportModel extends ReportModel<KeywordReportBean> {

	public KeywordReportModel(ReportHeader reportHeader, List<KeywordReportBean> records) {
		super(reportHeader, KeywordReportBean.class, records);
	}
	
}