package com.search.ws;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchResult;

public abstract class SolrResponseParser {
	public abstract void setSolrUrl(String solrUrl) throws SearchException;
	/* Sends the original Solr Query Parameters, in case implementation needs to do something with it. Example JSON implemenation would need to get wrf parameter */
	public abstract void setSolrQueryParameters(HashMap<String, List<NameValuePair>> paramMap) throws SearchException;
	public abstract void setRequestRows(int startRow, int requestedRows) throws SearchException;
	public abstract void setElevatedItems(List<ElevateResult> list) throws SearchException;
	public abstract void setExpiredElevatedEDPs(List<String> list) throws SearchException;
	public abstract void setChangeKeyword(String string) throws SearchException;
	public abstract void setActiveRules(List<Map<String,String>> activeRules) throws SearchException;
	public abstract int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getCount(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getElevatedItems(List<NameValuePair> requestParams, int reqRows) throws SearchException;
	public abstract void setDemotedItems(List<DemoteResult> list) throws SearchException;
	public abstract void setExpiredDemotedEDPs(List<String> list) throws SearchException;
	public abstract int getDemotedItems(List<NameValuePair> requestParams, int startRow, int requestedRows) throws SearchException;
	public abstract int getDemotedItems(List<NameValuePair> requestParams, int reqRows) throws SearchException;
	public abstract int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	public abstract boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException;
	public abstract void setForceAddedList(List<ElevateResult> forceAddedList) throws SearchException;
	public abstract int getForceAddTemplateCounts(List<NameValuePair> requestParams) throws SearchException;

	protected static void generateEdpList(StringBuilder values, Collection<? extends SearchResult> ... list) {
		for (Collection<? extends SearchResult> listEntry: list) {
			if (CollectionUtils.isNotEmpty(listEntry)) {
				for (SearchResult result: listEntry) {
					if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
						if (values.length() == 0) {
							values.append("EDP:(");
						}
						values.append(" ").append(result.getEdp());
					} 
				}
			}
		}
		if (values.length() > 0) {
			values.append(")");
		}
	}
	
	protected void generateExcludeFilterList(StringBuilder filter, Collection<? extends SearchResult> list, int currItem) {
		boolean edpFlag = false;
		boolean facetFlag = false;
		int i = 1;
		if (CollectionUtils.isNotEmpty(list)) {
			
			StringBuilder edpValues = new StringBuilder();
			StringBuilder facetValues = new StringBuilder();

			for (SearchResult result: list) {
				if (++i > currItem) {
					break;
				}
				if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (!edpFlag) {
						edpFlag = true;
					}
					edpValues.append(" ").append(result.getEdp());
				} else {
					if (!facetFlag) {
						facetFlag = true;
					} else {
						facetValues.append(" OR ");
					}
					facetValues.append("(").append(result.getCondition().getConditionForSolr()).append(")");
				}
			}
			
			if (edpFlag || facetFlag) {
				filter.append("-(");
				if (edpFlag) {
					filter.append("EDP:(").append(edpValues).append(")");
				}
				if (facetFlag) {
					if (edpFlag) {
						filter.append(" OR ");
					}
					filter.append(facetValues);
				}				
				filter.append(")");
			}
		}
	}
}
