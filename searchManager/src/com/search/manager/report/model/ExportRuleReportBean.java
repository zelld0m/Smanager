package com.search.manager.report.model;

import java.util.List;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.RuleStatus;
import com.search.manager.report.annotation.ReportField;

public class ExportRuleReportBean extends ReportBean<RuleStatus> {
	private List<? extends ReportBean<?>> subReports;

	public ExportRuleReportBean(RuleStatus model) {
		super(model);
	}
	
	public ExportRuleReportBean(RuleStatus model, List<? extends ReportBean<?>> subReports) {
		super(model);
		this.subReports = subReports;
	}

	@ReportField(label="Id", size=20, sortOrder=1)
	public String getRuleId(){
		return model.getRuleId();
	}

	@ReportField(label="Name", size=20, sortOrder=2)
	public String getName(){
		return model.getRuleName();
	}
	
	@ReportField(label="Published Date", size=20, sortOrder=3)
	public String getPublishedDate(){
		if(model.getLastPublishedDateTime() != null){
			return JodaDateTimeUtil.formatFromStorePattern(model.getLastPublishedDateTime(), JodaPatternType.DATE_TIME);
		}
		return "";
	}
	
	@ReportField(label="Export Type", size=20, sortOrder=4)
	public String getExportType(){
		if(model.getExportType() != null){
			return model.getExportType().getDisplayText();
		}
		return "";
	}
	
	@ReportField(label="Export Date", size=20, sortOrder=5)
	public String getCreatedBy(){
		if(model.getLastExportDateTime() != null){
			return model.getFormattedLastExportDateTime();
		}
		return "";
	}

	public List<? extends ReportBean<?>> getSubReports() {
		return subReports;
	}

	public void setSubReports(List<? extends ReportBean<?>> subReports) {
		this.subReports = subReports;
	}
	
	
}