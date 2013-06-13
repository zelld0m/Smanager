package com.search.manager.report.model;

import com.search.manager.model.Relevancy;
import com.search.manager.report.annotation.ReportField;

public class RelevancyReportBean extends ReportBean<Relevancy>{

	public RelevancyReportBean(Relevancy model) {
		super(model);
	}

	@ReportField(label="Id", size=20, sortOrder=1)
	public String getElevation(){
		return String.valueOf(model.getRelevancyId());
	}

	@ReportField(label="Name", size=20, sortOrder=2)
	public String getName(){
		return model.getRelevancyName();
	}
	
	@ReportField(label="Description", size=40, sortOrder=3)
	public String getStatus(){
		return model.getDescription();
	}
	
	@ReportField(label="Schedule", size=20, sortOrder=4)
	public String getValidity(){
//		StringBuilder builder = new StringBuilder();
//		if (model.getStartDateTime() != null) {
//			builder.append("from " + model.getFormattedStartDate());
//		}
//		if (model.getEndDateTime() != null) {
//			builder.append(" to " + model.getFormattedEndDate());
//		}
		return ""; //TODO: builder.toString();
	}

	@ReportField(label="Created By", size=20, sortOrder=5)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=6)
	public String getCreatedDate(){
		return model.getFormattedCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=7)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=8)
	public String getModifiedDate(){
		return model.getFormattedLastModifiedDate();
	}	
}