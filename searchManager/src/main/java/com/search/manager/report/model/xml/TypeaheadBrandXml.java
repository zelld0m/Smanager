package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.model.TypeaheadBrand;

@XmlRootElement(name = "typeaheadBrandXml")
@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadBrandXml extends RuleItemXml{

	private static final long serialVersionUID = 1L;

	private String brandName;
	private String vendorId;
	private Integer productCount;
	private Integer sortOrder;
	
	public TypeaheadBrandXml() {
		super();
	}
	
	public TypeaheadBrandXml(TypeaheadBrand brand) {
		super();
		this.brandName = brand.getBrandName();
		this.vendorId = brand.getVendorId();
		this.productCount = brand.getProductCount();
		this.sortOrder = brand.getSortOrder();
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
