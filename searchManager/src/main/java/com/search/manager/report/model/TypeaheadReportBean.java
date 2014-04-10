package com.search.manager.report.model;

import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.report.annotation.ReportField;

public class TypeaheadReportBean extends ReportBean<TypeaheadRule>{

	public TypeaheadReportBean(TypeaheadRule model) {
		super(model);
	}

	@ReportField(label = "Priority", size = 15, sortOrder = 1)
    public String getPriority() {
        return String.valueOf(model.getPriority());
    }
	
	@ReportField(label = "Rule Name", size = 15, sortOrder = 1)
    public String getRuleName() {
        return String.valueOf(model.getRuleName());
    }
	
	@ReportField(label = "Disabled", size = 15, sortOrder = 1)
    public String getDisabled() {
        return String.valueOf(model.getDisabled());
    }
}
