package com.search.manager.report.model;

import com.search.manager.report.annotation.ReportField;


public class RedirectRuleConditionReportBean extends ReportBean<String> {

	public RedirectRuleConditionReportBean(String model) {
		super(model);
	}
	
	@ReportField(label="Condition", size=20, sortOrder=1)
	public String getElevation(){
		return String.valueOf(model);
	}
}