package com.search.manager.report.model;

import java.util.List;

public class ReplaceKeywordReportModel extends ReportModel<ReplaceKeywordReportBean> {

	public ReplaceKeywordReportModel(ReportHeader reportHeader, List<ReplaceKeywordReportBean> rrList) {
		super(reportHeader, ReplaceKeywordReportBean.class, rrList);
	}
	public ReplaceKeywordReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<ReplaceKeywordReportBean> records) {
		super(reportHeader, subReportHeader, ReplaceKeywordReportBean.class, records);
	}
}