package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Product;

@XmlRootElement(name = "exclude")
@XmlType(propOrder={"keyword", "excludeItem"})
@DataTransferObject(converter = BeanConverter.class)
public class ExcludeRuleXml extends RuleXml implements ProductDetailsAware{
	
	private static final long serialVersionUID = 1L;
	private String keyword;
	private List<ExcludeItemXml> excludeItem;
	private List<Product> products;
	
	public ExcludeRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.EXCLUDE);
	}
	
	public ExcludeRuleXml(String store, long version, String name, String notes, String createdBy, String keyword, List<ExcludeItemXml> excludeItem) {
		super(store, name == null ? keyword : name, notes, createdBy);
		this.setRuleId(keyword);
		this.setRuleName(keyword);
		this.setSerial(serialVersionUID);
		this.setVersion(version);
		this.keyword = keyword;
		this.excludeItem = excludeItem;
		this.setCreatedDate(DateTime.now());
	}
	
	public ExcludeRuleXml(String store, String keyword, List<ExcludeItemXml> excludeItem){
		this(store, 0, "", "", "", keyword, excludeItem);
	}

	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@XmlElementRef(type=ExcludeItemXml.class)
	public List<ExcludeItemXml> getExcludeItem() {
		return excludeItem;
	}
	
	public void setExcludeItem(List<ExcludeItemXml> excludeItem) {
		this.excludeItem = excludeItem;
	}
	
	@XmlTransient
	public List<ExcludeItemXml> getItem() {
		return excludeItem;
	}

	@XmlTransient
	public List<Product> getProducts() {
		return products;
	}
	
	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.EXCLUDE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setProducts(List<? extends Product> products) {
		this.products = (List<Product>) products;
	}
}
