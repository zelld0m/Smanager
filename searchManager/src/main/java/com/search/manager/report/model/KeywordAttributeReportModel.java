package com.search.manager.report.model;

import java.util.List;

public class KeywordAttributeReportModel extends ReportModel<KeywordAttributeReportBean>{

	public KeywordAttributeReportModel(ReportHeader reportHeader,
			Class<KeywordAttributeReportBean> type,
			List<KeywordAttributeReportBean> records) {
		super(reportHeader, type, records);
	}


}
