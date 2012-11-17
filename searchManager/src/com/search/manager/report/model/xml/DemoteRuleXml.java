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
import com.search.manager.model.Product;

@XmlRootElement(name = "demote")
@XmlType(propOrder={"keyword", "demoteItem"})
@DataTransferObject(converter = BeanConverter.class)
public class DemoteRuleXml extends RuleXml implements ProductDetailsAware {
	
	private static final long serialVersionUID = 1L;
	private String keyword;
	private List<DemoteItemXml> demoteItem;
	private List<Product> products;
	
	public DemoteRuleXml(String store, long version, String name, String notes, String createdBy, String keyword, List<DemoteItemXml> demoteItem) {
		super(store, name == null ? keyword : name, notes, createdBy);
		this.setRuleId(keyword);
		this.setRuleName(keyword);
		this.setSerial(serialVersionUID);
		this.setVersion(version);
		this.keyword = keyword;
		this.demoteItem = demoteItem;
		this.setCreatedDate(new Date());
	}
	
	public DemoteRuleXml(String store, String keyword, List<DemoteItemXml> demoteItem){
		this(store, 0, "", "", "", keyword, demoteItem);
	}
	
	public DemoteRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.DEMOTE);
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	@XmlElementRef(type=RuleItemXml.class)
	public List<DemoteItemXml> getDemoteItem() {
		return demoteItem;
	}
	
	public void setDemoteItem(List<DemoteItemXml> demoteItem) {
		this.demoteItem = demoteItem;
	}
	
	@XmlTransient
	public List<DemoteItemXml> getItem() {
		return demoteItem;
	}

	@XmlTransient
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}	
	
	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.DEMOTE;
	}
}
