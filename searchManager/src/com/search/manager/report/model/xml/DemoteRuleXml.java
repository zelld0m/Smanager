package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "demote")
public class DemoteRuleXml {
	
	public static final long SERIAL_VERSION_UID = 1L;
	
	private String keyword;
	private String store;
	private List<DemotedSkuXml> demotedSku;
	private long serVersion; 
	private String reason;
	private String name;
	
	public DemoteRuleXml() {
		this.serVersion = SERIAL_VERSION_UID;
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public long getSerVersion() {
		return serVersion;
	}
	public void setSerVersion(long serVersion) {
		this.serVersion = serVersion;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}