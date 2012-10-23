package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "elevate")
public class ElevateRuleXml extends BaseRuleVersionXml{
	
	private static final long serialVersionUID = 3396383695675861472L;
	private String keyword;
	private String store;
	private List<ElevatedSkuXml> elevatedSku;
	
	public ElevateRuleXml() {
		super(serialVersionUID);
	}
	
	public List<ElevatedSkuXml> getElevatedSku() {
		return elevatedSku;
	}
	
	public void setElevatedSku(List<ElevatedSkuXml> elevatedSku) {
		this.elevatedSku = elevatedSku;
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