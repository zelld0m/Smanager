package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "exclude")
public class ExcludeRuleXml {
	
	public static final long SERIAL_VERSION_UID = 1L;
	
	private String keyword;
	private List<ExcludedSkuXml> excludedSku;
	private long serVersion; 
	private String reason;
	private String name;
	
	public ExcludeRuleXml() {
		this.serVersion = SERIAL_VERSION_UID;
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

