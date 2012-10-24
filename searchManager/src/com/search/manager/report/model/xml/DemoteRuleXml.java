package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "demote")
@XmlType(propOrder={"keyword", "demotedSku"})
public class DemoteRuleXml extends RuleVersionXml {
	
	private static final long serialVersionUID = -7822668715298440691L;
	private String keyword;
	private List<DemoteItemXml> demotedSku;
	
	public DemoteRuleXml() {
		super(serialVersionUID);
	}
	
	@XmlElementRef(type=BaseRuleItemXml.class)
	public List<DemoteItemXml> getDemotedSku() {
		return demotedSku;
	}
	
	public void setDemotedSku(List<DemoteItemXml> demotedSku) {
		this.demotedSku = demotedSku;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}