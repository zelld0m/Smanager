package com.search.reports.manager.model;

import org.joda.time.DateTime;

public class ExcelFileReport extends LogProcess{
	String excelFileUploadedId;
	String storeId;
	int ruleTypeId;
	String keyword;
	String rank;
	String sku;
	String name;
	DateTime expiration;
	
	public ExcelFileReport() {		
	}

	public ExcelFileReport(String excelFileUploadedId,
			String storeId,
			int ruleTypeId,
			String keyword,
			String rank,
			String sku,
			String name,
			DateTime expiration,
			String createdBy,
			DateTime createdStamp,
			DateTime createdTxStamp,
			String lastUpdatedBy,
			DateTime lastUpdatedStamp,
			DateTime lastUpdatedTxStamp
			) {
		this.excelFileUploadedId = excelFileUploadedId;
		this.storeId = storeId;
		this.ruleTypeId = ruleTypeId;
		this.keyword = keyword;
		this.rank = rank;
		this.sku = sku;
		this.name = name;
		this.expiration = expiration;
		this.createdBy = createdBy;
		this.createdStamp = createdStamp;
		this.createdTxStamp = createdTxStamp;
		this.lastUpdatedBy =  lastUpdatedBy;
		this.lastUpdatedStamp = lastUpdatedStamp;
		this.lastUpdatedTxStamp = lastUpdatedTxStamp;		
	}
	
	public String getExcelFileUploadedId() {
		return excelFileUploadedId;
	}

	public void setExcelFileUploadedId(String excelFileUploadedId) {
		this.excelFileUploadedId = excelFileUploadedId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public int getRuleTypeId() {
		return ruleTypeId;
	}

	public void setRuleTypeId(int ruleTypeId) {
		this.ruleTypeId = ruleTypeId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DateTime getExpiration() {
		return expiration;
	}

	public void setExpiration(DateTime value) {
		this.expiration = value;
	}
	
}
