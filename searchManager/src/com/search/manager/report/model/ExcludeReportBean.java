package com.search.manager.report.model;

import com.search.manager.model.Product;
import com.search.manager.report.annotation.ReportField;

public class ExcludeReportBean extends ReportBean<Product> {

	public ExcludeReportBean(Product model) {
		super(model);
	}

	@ReportField(label="Name", size=20, sortOrder=2)
	public String getName(){
		return model.getName();
	}
	
	@ReportField(label="Status", size=20, sortOrder=3)
	public String getStatus(){
		return model.getIsExpired() ? "Expired" : "Active";
	}
	
	@ReportField(label="Valid Until", size=20, sortOrder=4)
	public String getValidity(){
		return model.getFormattedExpiryDate();
	}

	@ReportField(label="Manufacturer", size=20, sortOrder=5)
	public String getManufacturer(){
		return model.getManufacturer();
	}

	@ReportField(label="EDP", size=20, sortOrder=6)
	public String getEdp(){
		return model.getEdp();
	}
	
	@ReportField(label="Part #", size=20, sortOrder=7)
	public String getPartNumber(){
		return model.getDpNo();
	}

	@ReportField(label="Manufacturer Part #", size=20, sortOrder=8)
	public String getManufacturerPartNumber(){
		return model.getMfrPN();
	}
	
	@ReportField(label="Created By", size=20, sortOrder=9)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=10)
	public String getCreatedDate(){
		return model.getFormattedCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=11)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=12)
	public String getModifiedDate(){
		return model.getFormattedLastModifiedDate();
	}

}
