package com.search.manager.report.model;

import org.apache.commons.lang.StringUtils;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteProduct;
import com.search.manager.report.annotation.ReportField;

public class DemoteReportBean extends ReportBean<DemoteProduct> {

	public DemoteReportBean(DemoteProduct model) {
		super(model);
	}

	@ReportField(label="Demote", size=20, sortOrder=1)
	public String getElevation(){
		return String.valueOf(model.getLocation());
	}

	@ReportField(label="Type", size=20, sortOrder=2)
	public String getType(){
		if(isFacet()){
			if(model.getCondition() != null){
				if(model.getCondition().isIMSFilter())
					return "IMS Categories";
				else if (model.getCondition().isCNetFilter())
					return "Facet Template Categories";
			}
			return "Facets";
		}
		
		return "Part Number";
	}
	
	@ReportField(label="Rule Details", size=60, sortOrder=3)
	public String getRuleDetails(){
		return isPartNumber()?getPartNumberDetails():model.getCondition().getCondition();
	}
	
	@ReportField(label="Status", size=20, sortOrder=4)
	public String getStatus(){
		return model.getIsExpired() ? "Expired" : "Active";
	}
	
	@ReportField(label="Valid Until", size=20, sortOrder=5)
	public String getValidity(){
		return model.getFormattedExpiryDate();
	}

	@ReportField(label="Created By", size=20, sortOrder=6)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=7)
	public String getCreatedDate(){
		return model.getFormattedCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=8)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=9)
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
			return new StringBuffer("Manufacturer:").append(model.getManufacturer()).append("  EDP:").append(model.getEdp())
			.append("  Part #:").append(model.getDpNo()).append("  Manufacturer Part #:").append(model.getMfrPN()).toString();
		}
		
		return new StringBuffer("EDP: ").append(model.getEdp()).toString(); 
	}

}