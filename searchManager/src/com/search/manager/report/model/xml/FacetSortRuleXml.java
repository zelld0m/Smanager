package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetSort;

@XmlRootElement(name = "facetsort")
@XmlType(propOrder={"ruleType", "sortType", "groups"})
@DataTransferObject(converter = BeanConverter.class)
public class FacetSortRuleXml extends RuleXml {
	
	private static final long serialVersionUID = 1L;
	
	private RuleType ruleType;
	private SortType sortType;
	private List<FacetSortGroupXml> groups;
	
	public FacetSortRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.FACET_SORT);
	}
	
	public FacetSortRuleXml(String store, long version, String name, String notes, String username, RuleType ruleType, SortType sortType,
			String ruleId, String ruleName, List<FacetSortGroupXml> groups) {
		super(store, name == null ? ruleName : name, notes, username);
		this.setRuleId(ruleId);
		this.setRuleName(ruleName);
		this.ruleType = ruleType;
		this.sortType = sortType;
		this.groups = groups;
		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDateTime(DateTime.now());
	}

	public FacetSortRuleXml(String store, long version, String name, String notes, String username, String ruleType, String sortType,
			String ruleId, String ruleName, List<FacetSortGroupXml> groups) {
		this(store, version, name, notes, username, RuleType.get(ruleType), SortType.get(sortType), ruleId, ruleName, groups);
	}
	
	public FacetSortRuleXml(FacetSort facetSort, List<FacetGroup> facetGroups){
		this.setStore(facetSort.getStoreId());
		this.setRuleName(facetSort.getRuleName());
		this.setRuleId(facetSort.getRuleId());
		this.ruleType = facetSort.getRuleType();
		this.sortType = facetSort.getSortType();
		
		Map<String, List<String>> groups = facetSort.getItems();
		Map<String, SortType> groupSorts = facetSort.getGroupSortType();
		List<FacetSortGroupXml> facetSortGroupXmlList = new ArrayList<FacetSortGroupXml>();
		
		if (CollectionUtils.isNotEmpty(facetGroups)) {
			for(FacetGroup facetGroup: facetGroups){
				String mapKey = facetGroup.getName();
				facetSortGroupXmlList.add(new FacetSortGroupXml(mapKey, groups.get(mapKey), groupSorts.get(mapKey), 
						facetSort.getSortType(), facetGroup.getCreatedBy(), facetGroup.getCreatedDateTime()));
			}
		}
		
		this.groups = facetSortGroupXmlList;
		this.setCreatedBy(facetSort.getCreatedBy());
		this.setCreatedDateTime(facetSort.getCreatedDateTime());
		this.setLastModifiedBy(facetSort.getLastModifiedBy());
		this.setLastModifiedDateTime(facetSort.getLastModifiedDateTime());
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
	public List<FacetSortGroupXml> getGroups() {
		return groups;
	}
	
	public void setGroups(List<FacetSortGroupXml> groups) {
		this.groups = groups;
	}
}