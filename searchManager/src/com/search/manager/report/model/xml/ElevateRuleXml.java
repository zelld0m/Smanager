package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "elevate")
public class ElevateRuleXml {
	
	public static final long SERIAL_VERSION_UID = 1L;
	
	private String keyword;
	private String store;
	private List<ElevatedSkuXml> elevatedSku;
	private long serVersion; 
	private String reason;
	
	public ElevateRuleXml() {
		this.serVersion = SERIAL_VERSION_UID;
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
	
}

