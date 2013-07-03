package com.search.manager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class CNetFacetTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	private String label;
	private long   count;
	
	private Map<String, CNetFacetTemplate> facetMap = new HashMap<String, CNetFacetTemplate>();

	public CNetFacetTemplate(String label, long count) {
		this.label = label;
		this.count = count;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void addCount(long count) {
		this.count += count;
	}

	public long getCount() {
		return count;
	}

	public void addFacet(CNetFacetTemplate facetTemplate) {
		facetMap.put(facetTemplate.label, facetTemplate);
	}

	public CNetFacetTemplate getFacet(String label) {
		// TODO split at " | "
		return facetMap.get(label);
	}
	
	public Map<String, CNetFacetTemplate> getFacetMap() {
		return facetMap;
	}
	
	public List<String> getFacets() {
		return new ArrayList<String>(facetMap.keySet());
	}
	
	public int getFacetCount() {
		return facetMap.size();
	}
	
	

}
