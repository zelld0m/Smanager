package com.search.manager.report.model;

import org.joda.time.DateTime;

import com.search.manager.model.RedirectRule;
import com.search.manager.report.annotation.ReportField;

public class RedirectRuleReportBean extends ReportBean<RedirectRule> {

	public RedirectRuleReportBean(RedirectRule model) {
		super(model);
	}

	@ReportField(label="Id", size=20, sortOrder=1)
	public String getElevation(){
		return model.getRuleId();
	}

	@ReportField(label="Name", size=20, sortOrder=2)
	public String getName(){
		return model.getRuleName();
	}
	
	@ReportField(label="Description", size=40, sortOrder=3)
	public String getStatus(){
		return model.getDescription();
	}
	
	@ReportField(label="Type", size=20, sortOrder=4)
	public String getType(){
		return model.isRedirectToPage() ? "Redirect to page" : 
			"Fine-tuning search query";
	}
	
	@ReportField(label="Created By", size=20, sortOrder=5)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=6)
	public DateTime getCreatedDate(){
		return model.getCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=7)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=8)
	public DateTime getModifiedDate(){
		return model.getLastModifiedDate();
	}
}