package com.search.manager.report.model;

import org.apache.commons.lang.StringUtils;

import com.search.manager.enums.ReplaceKeywordMessageType;
import com.search.manager.model.RedirectRule;
import com.search.manager.report.annotation.ReportField;

public class ReplaceKeywordReportBean extends ReportBean<RedirectRule> {

	public ReplaceKeywordReportBean(RedirectRule model) {
		super(model);
	}

	@ReportField(label="ReplaceKeyword", size=20, sortOrder=1)
	public String getReplaceKeywordMessage(){
		StringBuilder sb = new StringBuilder();
		ReplaceKeywordMessageType messageType = model.getReplaceKeywordMessageType();
		
		if(messageType != null){
			sb.append(messageType.getDescription());
			
			if(messageType == ReplaceKeywordMessageType.CUSTOM_TEXT){
				sb.append(" ");
				if(StringUtils.isNotBlank(model.getReplaceKeywordMessageCustomText())){
					sb.append("(").append(model.getReplaceKeywordMessageCustomText()).append(")");
				}
			}
		}
		return sb.toString();
	}
}
