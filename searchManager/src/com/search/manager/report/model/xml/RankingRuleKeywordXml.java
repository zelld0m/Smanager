package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "keyword")
public class RankingRuleKeywordXml {
	
	private String keyword;
	private int priority;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
}

