package com.search.manager.core.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadBrand extends ModelBean{

	private static final long serialVersionUID = -2621695396142439556L;
	
	private String typeaheadBrandId;
	private String ruleId;
	private String brandName;
	private String vendorId;
	private Integer productCount;
	private Integer sortOrder;
	
	public String getTypeaheadBrandId() {
		return typeaheadBrandId;
	}
	public void setTypeaheadBrandId(String typeaheadBrandId) {
		this.typeaheadBrandId = typeaheadBrandId;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	
	public Integer getProductCount() {
		return productCount;
	}
	public void setProductCount(Integer productCount) {
		this.productCount = productCount;
	}
	
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}
