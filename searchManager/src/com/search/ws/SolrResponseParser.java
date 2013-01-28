package com.search.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.SearchResult;

public abstract class SolrResponseParser {
 
	private static Logger logger = Logger.getLogger(SolrResponseParser.class);

	/* Sends the original Solr Query Parameters, in case implementation needs to do something with it. Example JSON implemenation would need to get wrf parameter */
	public abstract int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getCount(List<NameValuePair> requestParams) throws SearchException;
	public abstract int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	public abstract String getCommonTemplateName(String templateNameField, List<NameValuePair> requestParams) throws SearchException;
	public abstract boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException;
	
	protected String requestPath;
	protected int startRow;
	protected int requestedRows;
	protected String changedKeyword;
	
	protected List<ElevateResult> elevatedList = null;
	protected List<String> expiredElevatedEDPs = null;
	protected List<Map<String,String>> activeRules;
	
	protected List<DemoteResult> demotedList = null;
	protected List<String> expiredDemotedEDPs = null;

	protected FacetSort facetSortRule;
	
	/* Enterprise Search start */
	protected boolean forEnterpriseSearch;
	protected Map<String, String> enterpriseSearchElevateFieldOverrides;
	protected Map<String, String> enterpriseSearchDemoteFieldOverrides;

	public boolean isForEnterpriseSearch() {
		return forEnterpriseSearch;
	}
	public void setForEnterpriseSearch(boolean forEnterpriseSearch) {
		this.forEnterpriseSearch = forEnterpriseSearch;
	}
	
	public Map<String, String> getEnterpriseSearchElevateFieldOverrides() {
		return enterpriseSearchElevateFieldOverrides;
	}
	
	public void setEnterpriseSearchElevateFieldOverrides(Map<String, String> enterpriseSearchElevateFieldOverrides) {
		this.enterpriseSearchElevateFieldOverrides = enterpriseSearchElevateFieldOverrides;
	}
	
	public Map<String, String> getEnterpriseSearchDemoteFieldOverrides() {
		return enterpriseSearchDemoteFieldOverrides;
	}
	
	public void setEnterpriseSearchDemoteFieldOverrides(Map<String, String> enterpriseSearchDemoteFieldOverrides) {
		this.enterpriseSearchDemoteFieldOverrides = enterpriseSearchDemoteFieldOverrides;
	}
	/* Enterprise Search end */
	
	/* public setters and getters */
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
	
	public final void setDemotedItems(List<DemoteResult> list) throws SearchException {
		demotedList = list;
	}
	
	public final void setExpiredDemotedEDPs(List<String> list) throws SearchException {
		expiredDemotedEDPs = list;
	}
	
	public final void setFacetSortRule(FacetSort facetSortRule) throws SearchException {
		this.facetSortRule = facetSortRule;
	}
	
	/* Used by both elevate and demote */
	private static void generateEdpList(StringBuilder values, Collection<? extends SearchResult> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (SearchResult result: list) {
				if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (values.length() == 0) {
						values.append("EDP:(");
					}
					values.append(" ").append(result.getEdp());
				} 
			}
		}
		if (values.length() > 0) {
			values.append(")");
		}
	}
	
	protected void generateExcludeFilterList(StringBuilder filter, List<? extends SearchResult> list, int currItem, boolean reverse) {
		boolean edpFlag = false;
		boolean facetFlag = false;
		int i = 1;
		if (CollectionUtils.isNotEmpty(list)) {
			
			StringBuilder edpValues = new StringBuilder();
			StringBuilder facetValues = new StringBuilder();

			Map<String,String> overrideMap = null;
			if (forEnterpriseSearch) {
				if (list.get(0) instanceof ElevateResult) {
					overrideMap = enterpriseSearchElevateFieldOverrides;
				}
				else if (list.get(0) instanceof DemoteResult) {
					overrideMap = enterpriseSearchDemoteFieldOverrides;
				}
			}
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
					String strCondition = result.getCondition().getConditionForSolr();
					if (overrideMap != null) {
						strCondition = StringUtils.replaceEach(strCondition, overrideMap.keySet().toArray(new String[0]),
								overrideMap.values().toArray(new String[0]));
					}
					facetValues.append("(").append(strCondition).append(")");
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
	
	protected abstract int getEdps(List<NameValuePair> requestParams, List<? extends SearchResult> edpList, int startRow, int requestedRows) throws SearchException;
	protected abstract int getFacet(List<NameValuePair> requestParams, SearchResult facet) throws SearchException;

	// TODO: merge getElevatedItems and getDemotedItems
	/* For elevate */
	public final int getElevatedItems(List<NameValuePair> requestParams,  int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		try {
			int currItem = 1;
			List<NameValuePair> currentRequestParams = new ArrayList<NameValuePair>();
			BasicNameValuePair zeroRowNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, "0");
			List<ElevateResult> elevateEdps = new ArrayList<ElevateResult>();
			int elevatedRecords = elevatedList.size();
			
			for (int i = 0; i < elevatedRecords; i++) {
				
				ElevateResult elevateResult = elevatedList.get(i);
				
				StringBuilder elevateFilter = new StringBuilder();
				boolean isEdpList = false;
				elevateEdps.clear();
				
				currentRequestParams.clear();
				currentRequestParams.addAll(requestParams);
				currentRequestParams.add(zeroRowNVP);
				
				int numEdpAdded = 0;
				if (elevateResult.getEntity() == MemberTypeEntity.PART_NUMBER) {
					isEdpList = true;
					elevateEdps.add(elevateResult);
					numEdpAdded++;
					for (int j = i+1; j < elevatedRecords; j++) {
						ElevateResult e2 = elevatedList.get(j);
						if (e2.getEntity() == MemberTypeEntity.PART_NUMBER) {
							elevateEdps.add(e2);
							i++;
							numEdpAdded++;
						}
						else {
							break;
						}
					}
					StringBuilder builder = new StringBuilder();
					generateEdpList(builder, elevateEdps);
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, builder.toString()));
				}
				else {
					String strCondition = elevateResult.getCondition().getConditionForSolr();
					if (forEnterpriseSearch) {
						strCondition = StringUtils.replaceEach(strCondition, enterpriseSearchElevateFieldOverrides.keySet().toArray(new String[0]),
								enterpriseSearchElevateFieldOverrides.values().toArray(new String[0]));
					}
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, strCondition));
				}
				
				generateExcludeFilterList(elevateFilter, elevatedList, currItem++, false);
				if (isEdpList) {
					currItem += numEdpAdded - 1; // - 1 to compensate for currItem++ in above line
				}
				
				if (elevateFilter.length() > 0) {
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateFilter.toString()));
				}
				
				// check if current elevate result contains any matches
				int numFound = getCount(currentRequestParams);
				if (numFound > 0) { // match found let's get the necessary entries
					if (numFound > startRow) {
						currentRequestParams.remove(zeroRowNVP);
						currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, 
								String.valueOf(isEdpList ? 0 : startRow)));
						currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, 
								String.valueOf(isEdpList ? numEdpAdded : requestedRows)));
						if (isEdpList) {
							requestedRows -= getEdps(currentRequestParams, elevateEdps, startRow, requestedRows);
						}
						else {
							requestedRows -= getFacet(currentRequestParams, elevateResult);
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
			throw new SearchException("Error occured while trying to get elevated items" ,e);
		}
		return addedRecords;
	}
	
	/* For demote */
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
				currentRequestParams.add(zeroRowNVP);

				int numEdpAdded = 0;
				if (demoteResult.getEntity() == MemberTypeEntity.PART_NUMBER) {
					isEdpList = true;
					demotedEdps.add(demoteResult);
					numEdpAdded++;
					for (int j = i+1; j < demotedRecords; j++) {
						DemoteResult e2 = demotedList.get(j);
						if (e2.getEntity() == MemberTypeEntity.PART_NUMBER) {
							demotedEdps.add(e2);
							i++;
							numEdpAdded++;
						}
						else {
							break;
						}
					}
					StringBuilder builder = new StringBuilder();
					generateEdpList(builder, demotedEdps);
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, builder.toString()));
				}
				else {
					String strCondition = demoteResult.getCondition().getConditionForSolr();
					if (forEnterpriseSearch) {
						strCondition = StringUtils.replaceEach(strCondition, enterpriseSearchDemoteFieldOverrides.keySet().toArray(new String[0]),
								enterpriseSearchDemoteFieldOverrides.values().toArray(new String[0]));
					}
					currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, strCondition));
				}
				
				if (isEdpList) {
					currItem += numEdpAdded - 1; // -1 to compensate for currItem++ in line below
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
						currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, 
								String.valueOf(isEdpList ? 0 : startRow)));
						currentRequestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, 
								String.valueOf(isEdpList ? numEdpAdded : requestedRows)));
						if (isEdpList) {
							requestedRows -= getEdps(currentRequestParams, demotedEdps, startRow, requestedRows);
						}
						else {
							requestedRows -= getFacet(currentRequestParams, demoteResult);
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
	
	protected void logSolrError(HttpPost post, String description, Exception e) {
		StringBuilder builder = new StringBuilder();
		builder.append(description).append(": ").append(requestPath);
		if (post != null && post.getEntity() != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				post.getEntity().writeTo(baos);
				builder.append("?").append(baos.toString("UTF-8"));
			} catch (IOException e1) {
			}
		}
		logger.error(builder.toString());
	}

}
