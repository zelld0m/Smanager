package com.search.manager.report.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@XmlRootElement(name = "elevate")
@XmlType(propOrder={"keyword", "elevateItem"})
@DataTransferObject(converter = BeanConverter.class)
public class ElevateRuleXml extends RuleVersionXml{
	
	private static final long serialVersionUID = 3396383695675861472L;
	private String keyword;
	private List<ElevateItemXml> elevateItem;
	
	public ElevateRuleXml(String store, long version, String name, String notes, String createdBy, String keyword, List<ElevateItemXml> elevateItem) {
		super(store, name, notes, createdBy);
		this.setRuleId(keyword);
		this.setRuleName(keyword);
		this.setSerial(serialVersionUID);
		this.setVersion(version);
		this.keyword = keyword;
		this.elevateItem = elevateItem;
		this.setCreatedDate(new Date());
	}

	public ElevateRuleXml() {
		super(serialVersionUID);
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
}