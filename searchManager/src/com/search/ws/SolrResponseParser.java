package com.search.ws;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;

import com.search.manager.model.ElevateResult;

public interface SolrResponseParser {
	public void setSolrUrl(String solrUrl) throws SearchException;
	/* Sends the original Solr Query Parameters, in case implementation needs to do something with it. Example JSON implemenation would need to get wrf parameter */
	public void setSolrQueryParameters(HashMap<String, List<NameValuePair>> paramMap) throws SearchException;
	public void setRequestRows(int startRow, int requestedRows) throws SearchException;
	public void setElevatedItems(List<ElevateResult> list) throws SearchException;
	public void setExpiredElevatedEDPs(List<String> list) throws SearchException;
	public void setChangeKeyword(String string) throws SearchException;
	public int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException;
	public int getElevatedCount(List<NameValuePair> requestParams) throws SearchException;
	public int getElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	public int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException;
	boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException;
}
