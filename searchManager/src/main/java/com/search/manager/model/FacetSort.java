package com.search.manager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.report.model.xml.FacetSortGroupXml;
import com.search.manager.report.model.xml.FacetSortRuleXml;

@DataTransferObject(converter = BeanConverter.class)
public class FacetSort extends ModelBean{

	private static final long serialVersionUID = -489935522624703568L;
	
	private String id;
	private String name;
	private RuleType ruleType;
	private SortType sortType;
	private Store store;
	private Map<String, List<String>> items;
	private Map<String, SortType> groupSortType;
	
	public FacetSort() {}
	
	public FacetSort(FacetSortRuleXml xml) {
		this.name = xml.getRuleName();
		this.id = xml.getRuleId();
		this.store = new Store(xml.getStore());
		this.sortType = xml.getSortType();
		this.ruleType = xml.getRuleType();
		this.createdBy = xml.getCreatedBy();
		this.createdDate = xml.getCreatedDate();
		
		items = new HashMap<String, List<String>>();
		groupSortType = new HashMap<String, SortType>(); 
		
		if (CollectionUtils.isNotEmpty(xml.getGroups())) {
			for (FacetSortGroupXml facetSort: xml.getGroups()) {
				items.put(facetSort.getGroupName(), 
						CollectionUtils.isEmpty(facetSort.getGroupItem())
							? new ArrayList<String>() 
							: facetSort.getGroupItem());
				groupSortType.put(facetSort.getGroupName(), facetSort.getSortType());
			}
		}
	}
	
	public FacetSort(String id, String name, RuleType ruleType, 
			SortType sortType, Store store, Map<String, List<String>> items, Map<String, SortType> groupSortType) {
		super();
		this.id = id;
		this.name = name;
		this.sortType = sortType;
		this.ruleType = ruleType;
		this.store = store;
		this.items = items;
		this.groupSortType = groupSortType;
	}
	
	public FacetSort(String id, String name, RuleType ruleType, 
			SortType sortType, Store store, Map<String, List<String>> items) {
		this(id, name, ruleType, sortType, store, items, null);
	}
	
	public FacetSort(String id, String name, RuleType ruleType, 
			SortType sortType, Store store) {
		this(id, name, ruleType, sortType, store, null);
	}
	
	public FacetSort(String name, RuleType ruleType,
			SortType sortType, Store store) {
		this("", name, ruleType, sortType, store);
	}
	
	public FacetSort(String name, String ruleType, 
			String sortType, String store) {
		this(name, RuleType.get(ruleType), SortType.get(sortType), new Store(store));
	}
	
	public FacetSort(String id, String name, String ruleType, 
			String sortType, String store) {
		this(id, name, RuleType.get(ruleType), SortType.get(sortType), new Store(store));
	}
	
	public FacetSort(String id, String name, String store) {
		super();
		this.id = id;
		this.name = name;
		this.store = new Store(store);
	}
	
	public FacetSort(String id, String store) {
		this(id, "", store);
	}

	public String getId() {
		return id;
	}

	public String getRuleId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRuleId(String ruleId) {
		this.id = ruleId;
	}

	public String getName() {
		return name;
	}
	
	public String getRuleName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setRuleName(String ruleName) {
		this.name = ruleName;
	}

	public SortType getSortType() {
		return sortType;
	}
	
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
	public String getStoreId(){
		if(getStore() != null)
			return getStore().getStoreId();
		return null;
	}
	
	public Map<String, List<String>> getItems() {
		return items;
	}

	public void setItems(Map<String, List<String>> items) {
		this.items = items;
	}
	
	public Map<String, SortType> getGroupSortType() {
		return groupSortType;
	}

	public void setGroupSortType(Map<String, SortType> groupSortType) {
		this.groupSortType = groupSortType;
	}

	public String getReadableString(){
		Map<String, List<String>> selectedItems = getItems();
		StringBuilder sb = new StringBuilder();
		List<String> selectedItemList = new ArrayList<String>(); 
		
		if(MapUtils.isNotEmpty(selectedItems)){
			for(Map.Entry<String, List<String>> facet: selectedItems.entrySet()){
				selectedItemList = new ArrayList<String>(facet.getValue());
				if(sb.length()>0) sb.append("; ");
				if(CollectionUtils.isNotEmpty(selectedItemList))
					sb.append("<strong>").append(facet.getKey()).append("</strong>").append(": ").append(StringUtils.join(selectedItemList.toArray(), " | "));
			}
			
			return sb.toString(); 
		}
		
		return "No Highlighted Facet";
	}
}
