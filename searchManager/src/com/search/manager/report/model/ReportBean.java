package com.search.manager.report.model;

import com.search.manager.model.ModelBean;

public class ReportBean <T extends ModelBean> {

	protected T model;
	
	public ReportBean(T model) {
		this.model = model;
	}
	
}