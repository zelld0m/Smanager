package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "exclude")
@XmlType(propOrder={"keyword", "excludedSku"})
public class ExcludeRuleXml extends RuleVersionXml {
	
	private static final long serialVersionUID = 311146321055559058L;
	private String keyword;
	private List<ExcludeItemXml> excludedSku;
	
	public ExcludeRuleXml() {
		super(serialVersionUID);
	}

	@XmlElementRef(type=BaseRuleItemXml.class)
	public List<ExcludeItemXml> getExcludedSku() {
		return excludedSku;
	}
	
	public void setExcludedSku(List<ExcludeItemXml> excludedSku) {
		this.excludedSku = excludedSku;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}