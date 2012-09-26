package com.search.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.groovy.JsonSlurper;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.CNetFacetTemplate;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.SearchResult;
import com.search.manager.utility.SolrRequestDispatcher;

public class SolrJsonResponseParser extends SolrResponseParser {

	private JSONObject initialJson = null;
	//    private JSONObject tmpJson = null;
	private JsonSlurper slurper = null;
	private JSONArray resultArray  = null; // DOCS entry
	private JSONObject explainObject  = null; // DOCS entry
	private JSONObject facetTemplate  = null; // Facet Template
	private JSONObject responseHeader = null;
	private List<JSONObject> elevatedResults = new ArrayList<JSONObject>();
	private List<JSONObject> demotedResults = new ArrayList<JSONObject>();
	
	private String wrf = "";

	private static Logger logger = Logger.getLogger(SolrJsonResponseParser.class);

	public SolrJsonResponseParser() {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		slurper = new JsonSlurper(jsonConfig);
	}

	public void setSolrQueryParameters(HashMap<String, List<NameValuePair>> paramMap) throws SearchException {
		wrf = SearchServlet.getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_JSON_WRAPPER_FUNCTION);
	}
	
	private static JSON parseJsonResponse(JsonSlurper slurper,HttpResponse response) {
		BufferedReader reader = null;
		try {
			String encoding = (response.getEntity().getContentEncoding() != null) ? response.getEntity().getContentEncoding().getValue() : null;
			if (encoding == null) {
				encoding = "UTF-8";
			}
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), encoding));
			String line = null;
			StringBuilder jsonText = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				jsonText.append(line.trim());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Json response" + jsonText.toString());
			}
			return slurper.parseText(jsonText.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try { if (reader != null) reader.close(); } catch (Exception e) {}
		}
		return null;
	}

	private static JSONObject locateJSONObject(JSONObject initialJson, String[] traverseList) {
		JSONObject json = initialJson;
		for (String element: traverseList) {
			json = json.getJSONObject(element);
			if (json.isNullObject()) {
				json = null;
				break;
			}
		}
		return json;
	}

	@Override
	public int getTemplateCounts(List<NameValuePair> requestParams) throws SearchException {
		int numFound = -1;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			// locate the result node and reference it <result name="response" maxScore="23.015398" start="0" numFound="360207">
			// results will be added here
			resultArray = (JSONArray)((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).get(SolrConstants.TAG_DOCS);
			// explain values are added here
			explainObject = locateJSONObject(initialJson, new String[]{SolrConstants.ATTR_NAME_VALUE_DEBUG, SolrConstants.ATTR_NAME_VALUE_EXPLAIN});
			// number of records
			numFound = ((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
			((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).put(SolrConstants.SOLR_PARAM_START, startRow);
			// put back rows in header
			responseHeader = ((JSONObject)((JSONObject)initialJson).get(SolrConstants.ATTR_NAME_VALUE_RESPONSE_HEADER));
			if (StringUtils.isNotEmpty(changedKeyword)) {
				responseHeader.element(SolrConstants.TAG_REDIRECT, changedKeyword);
			}
			// TODO: make this get value from solr.xml
			facetTemplate = locateJSONObject(initialJson, new String[]{"facet_counts", "facet_fields", "PCMall_FacetTemplate"});
			
			if (activeRules != null) {
				JSONArray searchRules = new JSONArray();
				int i = 0;
				for (Map<String, String> rule: activeRules) {
					JSONObject ruleObject = new JSONObject();
					ruleObject.element(SolrConstants.TAG_RULE, rule);
					searchRules.element(i++, ruleObject);
				}
				responseHeader.element(SolrConstants.TAG_SEARCH_RULES, searchRules);
			}
			
			JSONObject paramsHeader = (JSONObject)(responseHeader.get(SolrConstants.ATTR_NAME_VALUE_PARAMS));
			paramsHeader.put(SolrConstants.SOLR_PARAM_ROWS, requestedRows);
			paramsHeader.put(SolrConstants.SOLR_PARAM_START, startRow);
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get template counts" ,e);
		}
		return numFound;
	}

	@Override
	public int getCount(List<NameValuePair> requestParams) throws SearchException {
		int numFound = -1;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			JSON tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			numFound = ((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get number of items" ,e);
		}
		return numFound;
	}

	private void tagSearchResult(JSONObject resultObject, String edp, SearchResult result) {
		if (result instanceof ElevateResult) {
			resultObject.element(SolrConstants.TAG_ELEVATE, String.valueOf(((ElevateResult)result).getLocation()));
			resultObject.element(SolrConstants.TAG_ELEVATE_TYPE, String.valueOf(result.getEntity()));
			if (result.getEntity() == MemberTypeEntity.FACET) {
				resultObject.element(SolrConstants.TAG_ELEVATE_CONDITION, result.getCondition().getReadableString());						
			}
			if (expiredElevatedEDPs.contains(edp)) {
				resultObject.element(SolrConstants.TAG_EXPIRED,"");
			}
			if (((ElevateResult)result).isForceAdd()) {
				resultObject.element(SolrConstants.TAG_FORCE_ADD,"");
			}
			resultObject.element(SolrConstants.TAG_ELEVATE_ID, String.valueOf(result.getMemberId()));
		}
		else if (result instanceof DemoteResult) {
			resultObject.element(SolrConstants.TAG_DEMOTE, String.valueOf(((DemoteResult)result).getLocation()));
			resultObject.element(SolrConstants.TAG_DEMOTE_TYPE, String.valueOf(result.getEntity()));
			if (result.getEntity() == MemberTypeEntity.FACET) {
				resultObject.element(SolrConstants.TAG_DEMOTE_CONDITION, result.getCondition().getReadableString());						
			}
			if (expiredDemotedEDPs.contains(edp)) {
				resultObject.element(SolrConstants.TAG_EXPIRED,"");
			}
			resultObject.element(SolrConstants.TAG_DEMOTE_ID, String.valueOf(result.getMemberId()));
		}
	}
	
	@Override
	protected int getEdps(List<NameValuePair> requestParams, List<? extends SearchResult> edpList, int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			JSONObject tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			JSONArray docs = (JSONArray)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).get(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = (JSONObject)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.ATTR_NAME_VALUE_DEBUG)).get(SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
			}
			
			Map<String, JSONObject> entries = new HashMap<String, JSONObject>();
			for (int j = 0, length = docs.size(); j < length; j++) {
				JSONObject doc = (JSONObject)docs.get(j);
				String edp = doc.getString("EDP");
				entries.put(edp, doc);
			}

			// sort the entries
			JSONObject node;
			int currRow = 0;
			for (SearchResult result: edpList) {
				String edp = result.getEdp();
				node = entries.get(edp);
				if (node != null) {
					if (currRow++ < startRow) {
						continue;
					}
					if (addedRecords + 1 > requestedRows) {
						break;
					}
					addedRecords++;
					tagSearchResult(node, edp, result);
					// insert the elevate results to the docs entry
					if (result instanceof ElevateResult) {
						elevatedResults.add(node);
					}
					else if (result instanceof DemoteResult) {
						demotedResults.add(node);
					}
					if (explainObject != null) {
						explainObject.put(edp, tmpExplain.getString(edp));
					}
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get items" ,e);
		}
		return addedRecords;
	}

	@Override
	protected int getFacet(List<NameValuePair> requestParams, SearchResult facet) throws SearchException {
		int addedRecords = 0;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			JSONObject tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			JSONArray docs = (JSONArray)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).get(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = (JSONObject)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.ATTR_NAME_VALUE_DEBUG)).get(SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
			}
			for (int j = 0, length = docs.size(); j < length; j++) {
				JSONObject doc = (JSONObject)docs.get(j);
				String edp = doc.getString("EDP");
				// insert the demote results to the docs entry
				addedRecords++;
				tagSearchResult(doc, edp, facet);
				if (facet instanceof ElevateResult) {
					elevatedResults.add(doc);
				}
				else if (facet instanceof DemoteResult) {
					demotedResults.add(doc);
				}
				if (explainObject != null) {
					explainObject.put(edp, tmpExplain.getString(edp));
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get items" ,e);
		}
		return addedRecords;
	}

	@Override
	public int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			JSONObject tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			JSONArray docs = (JSONArray)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).get(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = locateJSONObject(tmpJson, new String[]{SolrConstants.ATTR_NAME_VALUE_DEBUG, SolrConstants.ATTR_NAME_VALUE_EXPLAIN});
			}

			for (int j = 0, length = docs.size(); j < length; j++) {
				addedRecords++;
				
				JSONObject doc = (JSONObject)docs.get(j);
				String edp = doc.getString("EDP");
				if (expiredElevatedEDPs.contains(edp)) {
					doc.element(SolrConstants.TAG_EXPIRED,"");
				}

				resultArray.add(doc);
				if (explainObject != null) {
					explainObject.put(edp, tmpExplain.getString(edp));
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get non-elevated items" ,e);
		}
		return addedRecords;
	}

	@SuppressWarnings("unchecked")
	private void getFacetTemplates() {
		
		if (facetTemplate == null) {
			return;
		}
		
		CNetFacetTemplate root = new CNetFacetTemplate("",0);
		for (String key: (Set<String>)facetTemplate.keySet()) {
			
			int count = facetTemplate.getInt(key);

			String[] category = key.split("\\ \\|\\ ");
			// TODO: optimize
			if (category.length > 0) {
				
				CNetFacetTemplate tmpFacet     = root;
				CNetFacetTemplate currentFacet = root;
				
				// facet template is blank
				root.addCount(count);
				if (StringUtils.isBlank(category[0])) {
					continue;
				}
				
				// lvl 1 facet
				currentFacet = root.getFacet(category[0]);
				if (currentFacet == null) {
					currentFacet = new CNetFacetTemplate(category[0], 0);
					root.addFacet(currentFacet);
				}
				currentFacet.addCount(count);

				// lvl 2 facet
				if (category.length > 1) {
					tmpFacet = currentFacet.getFacet(category[1]);
					if (tmpFacet == null) {
						tmpFacet = new CNetFacetTemplate(category[1], 0);
						currentFacet.addFacet(tmpFacet);
					}
					tmpFacet.addCount(count);
					
					// lvl 3 facet
					if (category.length > 2) {
						currentFacet = tmpFacet;
						tmpFacet = currentFacet.getFacet(category[2]);
						if (tmpFacet == null) {
							tmpFacet = new CNetFacetTemplate(category[2], 0);
							currentFacet.addFacet(tmpFacet);
						}
						tmpFacet.addCount(count);
					}
				}
			}
		}
		
		// remove the facet template
		locateJSONObject(initialJson, new String[]{"facet_counts", "facet_fields"}).remove("PCMall_FacetTemplate");
		
		LinkedHashMap<String, Long> lvl1Map = new LinkedHashMap<String, Long>();
		LinkedHashMap<String, Long> lvl2Map = new LinkedHashMap<String, Long>();
		LinkedHashMap<String, Long> lvl3Map = new LinkedHashMap<String, Long>();
		
		if (root.getFacetCount() == 0) {
			return;
		}
		
		JSONObject facets = new JSONObject();
		if (root.getFacetCount() > 1) {
			for (String lvl1Key: root.getFacets()) {
				CNetFacetTemplate lvl1 = root.getFacet(lvl1Key);
				lvl1Map.put(lvl1Key, lvl1.getCount());
			}
		}
		else {
			// lvl1
			String lvl1Key = root.getFacets().get(0);
			CNetFacetTemplate lvl1 = root.getFacet(lvl1Key);
			lvl1Map.put(lvl1Key, lvl1.getCount());
			
			if (lvl1.getFacetCount() > 1) {
				// more than 1 level 2
				for (String lvl2Key: lvl1.getFacets()) {
					CNetFacetTemplate lvl2 = lvl1.getFacet(lvl2Key);
					if (lvl2 != null) {
						lvl2Map.put(lvl2Key, lvl2.getCount());						
					}
				}
			}
			else {
				// only one level 2
				if(lvl1.getFacets().size() > 0){
					String lvl2Key = lvl1.getFacets().get(0);
					CNetFacetTemplate lvl2 = lvl1.getFacet(lvl2Key);
					lvl2Map.put(lvl2Key, lvl2.getCount());
					
					for (String lvl3Key: lvl2.getFacets()) {
						CNetFacetTemplate lvl3 = lvl2.getFacet(lvl3Key);
						if (lvl3 != null) {
							lvl3Map.put(lvl3Key, lvl3.getCount());						
						}
					}
				}
			}
		}
		
		if (!lvl1Map.isEmpty()) {
			facets.element("Level1", lvl1Map);
		}
		if (!lvl2Map.isEmpty()) {
			facets.element("Level2", lvl2Map);
		}
		if (!lvl3Map.isEmpty()) {
			facets.element("Level3", lvl3Map);
		}
		
		initialJson.element("FacetTemplate", facets);
	}
	
	@Override
	public boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException {
		boolean success = false;
		try {
			addElevatedEntries();
			addDemotedEntries();
			getFacetTemplates();
			responseHeader.put(SolrConstants.ATTR_NAME_VALUE_QTIME, totalTime);
			boolean wrfPresent = !StringUtils.isEmpty(wrf);
			response.setContentType("application/json;charset=UTF-8");
			Writer writer = response.getWriter();
			if (wrfPresent) {
				writer.write(wrf);
				writer.write("(");
			}
			initialJson.write(writer);
			if (wrfPresent) {
				writer.write(")");
			}
			success = true;
		}
		catch (Exception e) {
			throw new SearchException("Error occured while trying to generate Http response" ,e);
		}
		return success;
	}

	private void addElevatedEntries() {
		if (elevatedResults != null) {
			int i = 0;
			for (JSONObject object: elevatedResults) {
				// insert the elevate results to the docs entry
				resultArray.add(i++, object);
			}
		}
	}
	
	private void addDemotedEntries() {
		if (demotedResults != null) {
			for (JSONObject object: demotedResults) {
				// insert the elevate results to the docs entry
				resultArray.add(object);
			}
		}
	}
	
}
