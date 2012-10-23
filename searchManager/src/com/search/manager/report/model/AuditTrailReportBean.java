package com.search.manager.report.model;

import com.search.manager.model.AuditTrail;
import com.search.manager.report.annotation.ReportField;
import com.search.manager.utility.DateAndTimeUtils;

public class AuditTrailReportBean extends ReportBean<AuditTrail> {

	public AuditTrailReportBean(AuditTrail model) {
		super(model);
	}

	@ReportField(label="Date", size=20, sortOrder=1)
	public String getDate(){
		return DateAndTimeUtils.formatMMddyyyyhhmmaa(model.getDate());
	}

	@ReportField(label="Ref ID", size=20, sortOrder=2)
	public String getReferenceId(){
		return model.getReferenceId();
	}
	
	@ReportField(label="Username", size=20, sortOrder=3)
	public String getUsername(){
		return model.getUsername();
	}
	
	@ReportField(label="Type", size=20, sortOrder=4)
	public String getType(){
		return model.getEntity();
	}

	@ReportField(label="Action", size=20, sortOrder=5)
	public String getAction(){
		return model.getOperation();
	}

	@ReportField(label="Keyword", size=20, sortOrder=6)
	public String getKeyword(){
		return model.getKeyword();
	}
	
	@ReportField(label="Description", size=20, sortOrder=7)
	public String getDetails(){
		return model.getDetails();
	}
}
