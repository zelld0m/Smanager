package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "exclude")
public class ExcludeRuleXml extends BaseRuleVersionXml {
	
	private static final long serialVersionUID = 311146321055559058L;
	private String keyword;
	private String store;
	private List<ExcludedSkuXml> excludedSku;
	
	public ExcludeRuleXml() {
		super(serialVersionUID);
	}

	public List<ExcludedSkuXml> getExcludedSku() {
		return excludedSku;
	}
	
	public void setExcludedSku(List<ExcludedSkuXml> excludedSku) {
		this.excludedSku = excludedSku;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}	
}