package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "demote")
public class DemoteRuleXml extends BaseRuleVersionXml {
	
	private static final long serialVersionUID = -7822668715298440691L;
	private String keyword;
	private String store;
	private List<DemotedSkuXml> demotedSku;
	
	public DemoteRuleXml() {
		super(serialVersionUID);
	}
	
	public List<DemotedSkuXml> getDemotedSku() {
		return demotedSku;
	}
	
	public void setDemotedSku(List<DemotedSkuXml> demotedSku) {
		this.demotedSku = demotedSku;
	}
	
	public String getStore() {
		return store;
	}
	
	public void setStore(String store) {
		this.store = store;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}