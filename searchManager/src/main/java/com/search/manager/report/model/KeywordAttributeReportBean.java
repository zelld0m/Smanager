package com.search.manager.report.model;

import java.util.List;

import com.search.manager.report.annotation.ReportField;
import com.search.manager.report.model.xml.KeywordAttributeXML;

public class KeywordAttributeReportBean extends ReportBean<KeywordAttributeXML>{
	
	public KeywordAttributeReportBean(KeywordAttributeXML model) {
		super(model);
	}

	@ReportField(label="Section Name", size=20, sortOrder=1)
	public String getName() {
		return model.getInputValue();
	}

	@ReportField(label="Priority", size=20, sortOrder=2)
	public Integer getPriority() {
		return model.getPriority();
	}

	@ReportField(label="Disabled", size=20, sortOrder=3)
	public Boolean getDisabled() {
		return model.getDisabled();
	}

	@ReportField(label="Section Items", size=20, sortOrder=4)
	public String getValues() {
		StringBuilder sb = new StringBuilder();
		
		List<KeywordAttributeXML> list = model.getKeywordAttributeItems();
		
		if(list != null && list.size() > 0) {
			
			for(KeywordAttributeXML attribute : list) {
				sb.append(attribute.getInputValue()).append(", ");
			}
						
		}
		
		return sb.toString();
	}

	
}
