package com.search.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchResult;

public abstract class SolrResponseParser {

	/* Sends the original Solr Query Parameters, in case implementation needs to do something with it. Example JSON implemenation would need to get wrf parameter */
	public abstract int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getCount(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getElevatedItems(List<NameValuePair> requestParams, int reqRows) throws SearchException;
	public abstract int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	public abstract boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException;
	public abstract int getForceAddTemplateCounts(List<NameValuePair> requestParams) throws SearchException;
	
	protected String requestPath;
	protected int startRow;
	protected int requestedRows;
	protected String changedKeyword;
	
	protected List<ElevateResult> elevatedList = null;
	protected List<String> expiredElevatedEDPs = null;
	protected List<Map<String,String>> activeRules;
	protected List<ElevateResult> forceAddedList = null;
	
	protected List<DemoteResult> demotedList = null;
	protected List<String> expiredDemotedEDPs = null;

	public final void setActiveRules(List<Map<String,String>> activeRules) throws SearchException {
		this.activeRules = activeRules;
	}

	public final void setElevatedItems(List<ElevateResult> list) throws SearchException {
		elevatedList = list;
	}

	public final void setExpiredElevatedEDPs(List<String> list) throws SearchException {
		expiredElevatedEDPs = list;
	}
	
	public final void setSolrUrl(String solrUrl) {
		requestPath= solrUrl;
	}

	public void setSolrQueryParameters(HashMap<String, List<NameValuePair>> paramMap) throws SearchException {
	}

	public final void setRequestRows(int startRow, int requestedRows) throws SearchException {
		this.startRow = startRow;
		this.requestedRows = requestedRows;
	}

	public final void setChangeKeyword(String changedKeyword) throws SearchException {
		this.changedKeyword = changedKeyword;
	}

	public final List<ElevateResult> getForceAddedList() {
		return forceAddedList;
	}

	public final void setForceAddedList(List<ElevateResult> forceAddedList) {
		this.forceAddedList = forceAddedList;
	}
	
	public final void setDemotedItems(List<DemoteResult> list) throws SearchException {
		demotedList = list;
	}
	
	public final void setExpiredDemotedEDPs(List<String> list) throws SearchException {
		expiredDemotedEDPs = list;
	}
	
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
	
	protected void generateExcludeFilterList(StringBuilder filter, Collection<? extends SearchResult> list, int currItem, boolean reverse) {
		boolean edpFlag = false;
		boolean facetFlag = false;
		int i = 1;
		if (CollectionUtils.isNotEmpty(list)) {
			
			StringBuilder edpValues = new StringBuilder();
			StringBuilder facetValues = new StringBuilder();

			for (SearchResult result: list) {
				
				if (reverse) {
					if (i++ <= currItem) {
						continue;
					}
				}
				else if (++i > currItem) {
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
	
	protected abstract int getDemotedEdps(List<NameValuePair> requestParams) throws SearchException;
	protected abstract int getDemotedFacet(List<NameValuePair> requestParams, DemoteResult demoteFacet) throws SearchException;

	public final int getDemotedItems(List<NameValuePair> requestParams,  int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		try {
			int currItem = 1;
			List<NameValuePair> currentRequestParams = new ArrayList<NameValuePair>();
			BasicNameValuePair zeroRowNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, "0");
			List<DemoteResult> demotedEdps = new ArrayList<DemoteResult>();
			int demotedRecords = demotedList.size();
			for (int i = 0; i < demotedRecords; i++) {
				
				DemoteResult demoteResult = demotedList.get(i);
				
				StringBuilder demoteFilter = new StringBuilder();
				boolean isEdpList = false;
				demotedEdps.clear();
				
				currentRequestParams.clear();
				currentRequestParams.addAll(requestParams);
				currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(startRow)));
				currentRequestParams.add(zeroRowNVP);
				
				if (demoteResult.getEntity() == MemberTypeEntity.PART_NUMBER) {
					isEdpList = true;
					demotedEdps.add(demoteResult);
					for (int j = i+1; j < demotedRecords; j++) {
						DemoteResult e2 = demotedList.get(j);
						if (e2.getEntity() == MemberTypeEntity.PART_NUMBER) {
							demotedEdps.add(e2);
							i++;
							currItem++;
						}
					}
					StringBuilder builder = new StringBuilder();
					generateEdpList(builder, demotedEdps);
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, builder.toString()));
				}
				else {
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, demoteResult.getCondition().getConditionForSolr()));
				}
				
				
				generateExcludeFilterList(demoteFilter, demotedList, currItem++, true);
				if (demoteFilter.length() > 0) {
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, demoteFilter.toString()));
				}
				
				// check if current demote result contains any matches
				int numFound = getCount(currentRequestParams);
				if (numFound > 0) { // match found let's get the necessary entries
					if (numFound > startRow) {
						currentRequestParams.remove(zeroRowNVP);
						currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(requestedRows)));
						if (isEdpList) {
							requestedRows -= getDemotedEdps(currentRequestParams);
						}
						else {
							requestedRows -= getDemotedFacet(currentRequestParams, demoteResult);
						}
					}
					startRow -= numFound;
					if (startRow < 0) {
						startRow = 0;
					}
				}
				
				if (requestedRows <= 0) {
					break;
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get demoted items" ,e);
		}
		return addedRecords;
	}
	
}
