package com.search.manager.report.model;

import com.search.manager.model.Keyword;
import com.search.manager.report.annotation.ReportField;

public class KeywordReportBean extends ReportBean<Keyword> {

	public KeywordReportBean(Keyword model) {
		super(model);
	}

	@ReportField(label="Keyword", size=20, sortOrder=1)
	public String getElevation(){
		return String.valueOf(model.getKeywordId());
	}

}
