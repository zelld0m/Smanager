package com.search.manager.report.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;

@XmlRootElement(name = "facetsort")
@XmlType(propOrder={"ruleType", "sortType", "item"})
@DataTransferObject(converter = BeanConverter.class)
public class FacetSortRuleXml extends RuleXml {
	
	private static final long serialVersionUID = 1L;
	
	private RuleType ruleType;
	private SortType sortType;
	private List<FacetSortItemXml> item;
	
	public FacetSortRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.FACET_SORT);
	}
	
	public FacetSortRuleXml(String store, long version, String name, String notes, String username, RuleType ruleType, SortType sortType,
			String ruleId, String ruleName, List<FacetSortItemXml> item) {
		super(store, name, notes, username);
		this.setRuleId(ruleId);
		this.setRuleName(ruleName);
		this.ruleType = ruleType;
		this.sortType = sortType;
		this.item = item;
		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(new Date());
	}

	public FacetSortRuleXml(String store, long version, String name, String notes, String username, String ruleType, String sortType,
			String ruleId, String ruleName, List<FacetSortItemXml> item) {
		this(store, version, name, notes, username, RuleType.get(ruleType), SortType.get(sortType), ruleId, ruleName, item);
	}

	@XmlAttribute(name="default-type")
	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
	@XmlAttribute(name="default-sort")
	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	@XmlElementRef
	public List<FacetSortItemXml> getItem() {
		return item;
	}
	
	public void setItem(List<FacetSortItemXml> item) {
		this.item = item;
	}
}