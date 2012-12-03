package com.search.manager.report.model;

import org.apache.commons.lang.StringUtils;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.Product;
import com.search.manager.report.annotation.ReportField;

public class ExcludeReportBean extends ReportBean<Product> {

	public ExcludeReportBean(Product model) {
		super(model);
	}

	@ReportField(label="Type", size=20, sortOrder=1)
	public String getType(){
		if(isFacet()){
			if(model.getCondition() != null){
				if(model.getCondition().isIMSFilter())
					return "IMS Categories";
				else if (model.getCondition().isCNetFilter())
					return "Product Site Taxonomy";
			}
			return "Facets";
		}
		
		return "Part Number";
	}
	
	@ReportField(label="Rule Details", size=60, sortOrder=2)
	public String getRuleDetails(){
		if(isPartNumber()){
			return getPartNumberDetails();
		}
		else if(model.getCondition() != null){
			return model.getCondition().getReadableString();
		}
		return "";
	}
	
	@ReportField(label="Status", size=20, sortOrder=3)
	public String getStatus(){
		return model.getIsExpired() ? "Expired" : "Active";
	}
	
	@ReportField(label="Valid Until", size=20, sortOrder=4)
	public String getValidity(){
		return model.getFormattedExpiryDate();
	}

	@ReportField(label="Created By", size=20, sortOrder=5)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=6)
	public String getCreatedDate(){
		return model.getFormattedCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=7)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=12)
	public String getModifiedDate(){
		return model.getFormattedLastModifiedDate();
	}

	private boolean isFacet() {
		return MemberTypeEntity.FACET == model.getMemberTypeEntity();
	}
	
	private boolean isPartNumber() {
		return MemberTypeEntity.PART_NUMBER == model.getMemberTypeEntity();
	}

	private String getPartNumberDetails() {
		if (StringUtils.isNotBlank(model.getDpNo())){
			return new StringBuffer("Manufacturer:").append(model.getManufacturer()).append(" Name:").append(model.getName())
			.append("  SKU #:").append(model.getDpNo()).append("  Mfr. Part #:").append(model.getMfrPN()).toString();
		}
		
		
		return new StringBuffer("EDP: ").append(model.getEdp()).toString(); 
	}

}
