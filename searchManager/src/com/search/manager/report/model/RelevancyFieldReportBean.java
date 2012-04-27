package com.search.manager.report.model;

import org.apache.http.message.BasicNameValuePair;

import com.search.manager.report.annotation.ReportField;

public class RelevancyFieldReportBean extends ReportBean<BasicNameValuePair>  {

	public RelevancyFieldReportBean(BasicNameValuePair model) {
		super(model);
	}

	@ReportField(label="Relevancy Parameter", size=20, sortOrder=3)
	public String getParameter(){
		return model.getName();
	}

	@ReportField(label="Relevancy Value", size=20, sortOrder=3)
	public String getValue(){
		return model.getValue();
	}

}
