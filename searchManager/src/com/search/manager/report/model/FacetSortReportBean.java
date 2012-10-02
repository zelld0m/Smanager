package com.search.manager.report.model;

import com.search.manager.model.FacetSort;
import com.search.manager.report.annotation.ReportField;

public class FacetSortReportBean extends ReportBean<FacetSort> {

	public FacetSortReportBean(FacetSort model) {
		super(model);
	}

	@ReportField(label="Rule Name", size=60, sortOrder=2)
	public String getRuleName(){
		return model.getRuleName();
	}
	
	@ReportField(label="Rule Type", size=60, sortOrder=2)
	public String getRuleType(){
		return model.getRuleType() != null ? model.getRuleType().getDisplayText() : "";
	}

	@ReportField(label="Created By", size=20, sortOrder=6)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=7)
	public String getCreatedDate(){
		return model.getFormattedCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=8)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=9)
	public String getModifiedDate(){
		return model.getFormattedLastModifiedDate();
	}
}
