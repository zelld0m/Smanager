package com.search.manager.report.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.Product;

@XmlRootElement(name = "elevate")
@XmlType(propOrder={"keyword", "elevateItem"})
@DataTransferObject(converter = BeanConverter.class)
public class ElevateRuleXml extends RuleXml implements ProductDetailsAware{
	
	private static final long serialVersionUID = 1L;
	private static final RuleEntity RULE_ENTITY = RuleEntity.ELEVATE;
	private String keyword;
	private List<ElevateItemXml> elevateItem;
	private List<ElevateProduct> products;
	
	public ElevateRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RULE_ENTITY);
	}
	
	public ElevateRuleXml(String store, long version, String name, String notes, String createdBy, String keyword, List<ElevateItemXml> eItemXml) {
		super(store, name == null ? keyword : name, notes, createdBy);
		this.setRuleId(keyword);
		this.setRuleName(keyword);
		this.setSerial(serialVersionUID);
		this.setRuleEntity(RULE_ENTITY);
		this.setVersion(version);
		this.keyword = keyword;
		this.setCreatedDate(new Date());
		setElevateItem(eItemXml);
	}
	
	public ElevateRuleXml(String store, String keyword, List<ElevateItemXml> elevateItem){
		this(store, 0, "", "", "", keyword, elevateItem);
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	@XmlElementRef
	public List<ElevateItemXml> getElevateItem() {
		return elevateItem;
	}
	
	public void setElevateItem(List<ElevateItemXml> elevateItem) {
		this.elevateItem = elevateItem;
	}
	
	@XmlTransient
	public List<ElevateItemXml> getItem() {
		return elevateItem;
	}
	
	public void setItem(List<ElevateItemXml> item) {
		this.elevateItem = item;
	}

	@XmlTransient
	public List<ElevateProduct> getProducts() {
		return products;
	}

	public void setProducts(List<? extends Product> products) {
		this.products = (List<ElevateProduct>) products;
		
	}
}
