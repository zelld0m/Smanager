package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "demote")
@XmlType(propOrder={"keyword", "demoteItem"})
public class DemoteRuleXml extends RuleVersionXml {
	
	private static final long serialVersionUID = -7822668715298440691L;
	private String keyword;
	private List<DemoteItemXml> demoteItem;
	
	public DemoteRuleXml(String store, long version, String name, String notes, String createdBy, String keyword, List<DemoteItemXml> demoteItem) {
		super(store, name, notes, createdBy);
		this.setSerial(serialVersionUID);
		this.setVersion(version);
		this.keyword = keyword;
		this.demoteItem = demoteItem;
	}
	
	public DemoteRuleXml() {
		super(serialVersionUID);
	}
	
	@XmlElementRef(type=RuleItemXml.class)
	public List<DemoteItemXml> getDemoteItem() {
		return demoteItem;
	}
	
	public void setDemoteItem(List<DemoteItemXml> demoteItem) {
		this.demoteItem = demoteItem;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}