package com.search.manager.report.model;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

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
					return "Product Site Taxonomy";
			}
			return "Facets";
		}
		
		return "Part Number";
	}
	
	@ReportField(label="Rule Details", size=60, sortOrder=3)
	public String getRuleDetails(){
		if(isPartNumber()){
			return getPartNumberDetails();
		}
		else if(model.getCondition() != null){
			return model.getCondition().getReadableString();
		}
		return "";
	}
	
	@ReportField(label="Status", size=20, sortOrder=4)
	public String getStatus(){
		return model.getIsExpired() ? "Expired" : "Active";
	}
	
	@ReportField(label="Valid Until", size=20, sortOrder=5)
	public DateTime getValidity(){
		return model.getExpiryDate();
	}

	@ReportField(label="Created By", size=20, sortOrder=6)
	public String getCreatedBy(){
		return model.getCreatedBy();
	}

	@ReportField(label="Created Date", size=20, sortOrder=7)
	public DateTime getCreatedDate(){
		return model.getCreatedDate();
	}

	@ReportField(label="Modified By", size=20, sortOrder=8)
	public String getModifiedBy(){
		return model.getLastModifiedBy();
	}
	
	@ReportField(label="Modified Date", size=20, sortOrder=9)
	public DateTime getModifiedDate(){
		return model.getLastModifiedDate();
	}

	private boolean isFacet() {
		return MemberTypeEntity.FACET == model.getMemberTypeEntity();
	}
	
	private boolean isPartNumber() {
		return MemberTypeEntity.PART_NUMBER == model.getMemberTypeEntity();
	}

	private String getPartNumberDetails() {
		StringBuffer details = new StringBuffer();
		
		if (StringUtils.isNotBlank(model.getEdp())){
			details = details.append("SystemProductID: ").append(model.getEdp());
		}
		if (StringUtils.isNotBlank(model.getManufacturer())){
			details = details.append(" Manufacturer: ").append(model.getManufacturer());
		}
		if (StringUtils.isNotBlank(model.getMfrPN())){
			details = details.append(" Mfr. Part #: ").append(model.getMfrPN());
		}
		if (StringUtils.isNotBlank(model.getName())){
			details = details.append(" Name: ").append(model.getName());
		}
		
		return details.toString();
	}

}
