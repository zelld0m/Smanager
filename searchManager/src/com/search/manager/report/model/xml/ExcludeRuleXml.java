package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "exclude")
@XmlType(propOrder={"keyword", "excludeItem"})
public class ExcludeRuleXml extends RuleVersionXml {
	
	private static final long serialVersionUID = 311146321055559058L;
	private String keyword;
	private List<ExcludeItemXml> excludeItem;
	
	public ExcludeRuleXml() {
		super(serialVersionUID);
	}
	
	public ExcludeRuleXml(String store, long version, String name, String notes, String createdBy, String keyword, List<ExcludeItemXml> excludeItem) {
		super(store, name, notes, createdBy);
		this.setSerial(serialVersionUID);
		this.setVersion(version);
		this.keyword = keyword;
		this.excludeItem = excludeItem;
	}

	@XmlElementRef(type=RuleItemXml.class)
	public List<ExcludeItemXml> getExcludeItem() {
		return excludeItem;
	}
	
	public void setExcludeItem(List<ExcludeItemXml> excludeItem) {
		this.excludeItem = excludeItem;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}