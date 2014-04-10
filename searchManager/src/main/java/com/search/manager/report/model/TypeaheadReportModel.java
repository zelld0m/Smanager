package com.search.manager.report.model;

import java.util.List;

public class TypeaheadReportModel extends ReportModel<TypeaheadReportBean>{

	 public TypeaheadReportModel(ReportHeader reportHeader, List<TypeaheadReportBean> records) {
	        super(reportHeader, TypeaheadReportBean.class, records);
	    }
	
	public TypeaheadReportModel(ReportHeader reportHeader,
			Class<TypeaheadReportBean> type, List<TypeaheadReportBean> records) {
		super(reportHeader, type, records);
	}

}
