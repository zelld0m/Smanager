package com.search.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.CNetFacetTemplate;
import com.search.manager.model.ElevateResult;
import com.search.manager.utility.SolrRequestDispatcher;

public class SolrJsonResponseParser implements SolrResponseParser {

	private JSONObject initialJson = null;
	//    private JSONObject tmpJson = null;
	private JsonSlurper slurper = null;
	private JSONArray resultArray  = null; // DOCS entry
	private JSONObject explainObject  = null; // DOCS entry
	private JSONObject facetTemplate  = null; // Facet Template
	private JSONObject responseHeader = null;
	private Map<String, JSONObject> elevateEntries = new HashMap<String, JSONObject>();
	private String wrf = "";
	private String changedKeyword;
	private List<Map<String,String>> activeRules;
	
	private String requestPath;
	private int startRow;
	private int requestedRows;

	private static Logger logger = Logger.getLogger(SolrJsonResponseParser.class);

	private List<ElevateResult> elevatedList = null;
	private List<String> expiredElevatedEDPs = null;
	private List<ElevateResult> forceAddedList = null;

	public SolrJsonResponseParser() {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		slurper = new JsonSlurper(jsonConfig);
	}

	@Override
	public void setActiveRules(List<Map<String,String>> activeRules) throws SearchException {
		this.activeRules = activeRules;
	}

	@Override
	public void setElevatedItems(List<ElevateResult> list) throws SearchException {
		elevatedList = list;
	}
	
	@Override
	public void setExpiredElevatedEDPs(List<String> list) throws SearchException {
		expiredElevatedEDPs = list;
	}

	private List<JSONObject> sortElevateEntries(Map<String, JSONObject> nodeMap){
		JSONObject node;
		ArrayList<JSONObject> sortedElevateList = new ArrayList<JSONObject>();
		for (ElevateResult e: elevatedList) {
			String edp = e.getEdp();
			node = nodeMap.get(edp);
			if (node != null) {
				node.element(SolrConstants.TAG_ELEVATE, String.valueOf(e.getLocation()));
				node.element(SolrConstants.TAG_ELEVATE_TYPE, String.valueOf(e.getElevateEntity()));
				if (e.getElevateEntity() == MemberTypeEntity.FACET) {
					node.element(SolrConstants.TAG_ELEVATE_CONDITION, e.getCondition().getReadableString());						
				}
				if (expiredElevatedEDPs.contains(edp)) {
					node.element(SolrConstants.TAG_EXPIRED,"");
				}
				node.element(SolrConstants.TAG_ELEVATE_ID, String.valueOf(e.getMemberId()));
				sortedElevateList.add(node);
			}
		}
		return sortedElevateList;
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

	private int getForceAddCount(List<NameValuePair> requestParams) throws Exception {
		HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
		JSONObject jsonResponse = (JSONObject)parseJsonResponse(slurper, solrResponse);
		int forceAddCount = ((JSONObject)((JSONObject)jsonResponse).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
		if (forceAddCount > 0) {
			JSONArray facetNames = jsonResponse.getJSONObject(SolrConstants.TAG_FACET_COUNTS).getJSONObject(SolrConstants.TAG_FACET_FIELDS).names();
			for (int i = 0; i < facetNames.size(); i++) {
				String facet = facetNames.getString(i);
				JSONArray facetValues = jsonResponse.getJSONObject(SolrConstants.TAG_FACET_COUNTS).getJSONObject(SolrConstants.TAG_FACET_FIELDS).getJSONObject(facet).names();
				for (int j = 0; j < facetValues.size(); j++) {
					String facetValue = facetValues.getString(j);
					int origFacetCount = 0;
					int facetCount = jsonResponse.getJSONObject(SolrConstants.TAG_FACET_COUNTS).getJSONObject(SolrConstants.TAG_FACET_FIELDS).getJSONObject(facet).getInt(facetValue);
					try {
						origFacetCount = initialJson.getJSONObject(SolrConstants.TAG_FACET_COUNTS).getJSONObject(SolrConstants.TAG_FACET_FIELDS).getJSONObject(facet).getInt(facetValue);
					} catch (Exception ex) {
					}
					initialJson.getJSONObject(SolrConstants.TAG_FACET_COUNTS).getJSONObject(SolrConstants.TAG_FACET_FIELDS).getJSONObject(facet).put(facetValue, origFacetCount + facetCount);
				}
			}
		}
		return forceAddCount;
	}
	
	@Override
	public int getForceAddTemplateCounts(List<NameValuePair> requestParams) throws SearchException {
		int result = 0;
		try {
			int forceAddCount = 0;
			requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, ""));
			requestParams.add(new BasicNameValuePair("q.alt", "*:*"));
			requestParams.add(new BasicNameValuePair("defType", "dismax"));
			int numFound = ((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
			StringBuffer edpBuffer = new StringBuffer("EDP:(");
			for (ElevateResult e : forceAddedList) {
				if (MemberTypeEntity.PART_NUMBER == e.getElevateEntity()) {
					edpBuffer.append(e.getEdp()).append(" ");
				}
			}			
			edpBuffer.append(")");
			if (edpBuffer.length() > 8) {
				BasicNameValuePair edpNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, edpBuffer.toString());
				requestParams.add(edpNvp);
				forceAddCount = getForceAddCount(requestParams);
				numFound += forceAddCount;
				result += forceAddCount;
				requestParams.remove(edpNvp);
			}
			for (ElevateResult e : forceAddedList) {
				StringBuffer buffer = new StringBuffer("");
				if (MemberTypeEntity.FACET == e.getElevateEntity()) {
					buffer.append(e.getCondition().getConditionForSolr());
				} else {
					continue;
				}
				BasicNameValuePair facetNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, buffer.toString());
				requestParams.add(facetNvp);
				forceAddCount = getForceAddCount(requestParams);
				numFound += forceAddCount;
				result += forceAddCount;
				requestParams.remove(facetNvp);
			}
			((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).put(SolrConstants.ATTR_NUM_FOUND, numFound);
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get template counts" ,e);
		}
		return result;
	}

	@Override
	public int getElevatedCount(List<NameValuePair> requestParams) throws SearchException {
		int numElevateFound = -1;
		try {
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			JSON tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			numElevateFound = ((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get number of elevated items" ,e);
		}
		return numElevateFound;
	}

	@Override
	public int getElevatedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		try {
			StringBuilder elevatedEdps = new StringBuilder();
			generateEdpElevateList(elevatedEdps, elevatedList);
			generateEdpElevateList(elevatedEdps, forceAddedList);
			if (elevatedEdps.length() > 0) {
				elevatedEdps.append(")");
			}
			for (NameValuePair nameValuePair : requestParams) {
				if (SolrConstants.SOLR_PARAM_KEYWORD.equals(nameValuePair.getName())) {
					requestParams.remove(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD,nameValuePair.getValue()));
					break;
				}
			}
			requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY,elevatedEdps.toString()));
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
			JSONObject tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
			// locate the result node and get the numFound attribute
			// <result> tag that is parent node for all the <doc> tags
			JSONArray docs = (JSONArray)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).get(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = (JSONObject)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.ATTR_NAME_VALUE_DEBUG)).get(SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
			}
			for (int j = 0, length = docs.size(); j < length; j++) {
				JSONObject doc = (JSONObject)docs.get(j);
				String edp = doc.getString("EDP");
				elevateEntries.put(edp, doc);
			}

			// sort the edps
			List<JSONObject> docList = sortElevateEntries(elevateEntries);
			// "debug":{ "explain":{ "6230888":
			// for (int i = startRow, size = startRow + requestedRows, resultSize = docList.size(); i < size && i < resultSize; i++) {
			logger.debug("****************" + Math.min(startRow + requestedRows, docList.size()) + ";" + startRow + ";" + requestedRows);
			for (int i = Math.min(startRow + requestedRows, docList.size()) - 1; i >= startRow; i--) {
				addedRecords++;
				// insert the elevate results to the docs entry
				resultArray.add(0, docList.get(i));
				if (explainObject != null) {
					String edp = docList.get(i).getString("EDP");
					explainObject.put(edp, tmpExplain.getString(edp));
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get elevated items" ,e);
		}
		return addedRecords;
	}

	@Override
	public int getElevatedItems(List<NameValuePair> requestParams, List<ElevateResult> elevatedList) throws SearchException {
		int addedRecords = 0;
		try {
			JSONObject tmpExplain = null;
			Map<String, JSONObject> explainMap = new HashMap<String, JSONObject>();
			List<JSONObject> docList = new ArrayList<JSONObject>();
			int size = startRow + requestedRows;
			BasicNameValuePair kwNvp = null;
			if (forceAddedList.size() > 0) {
				for (NameValuePair nameValuePair : requestParams) {
					if (SolrConstants.SOLR_PARAM_KEYWORD.equals(nameValuePair.getName())) {
						kwNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD,nameValuePair.getValue());
						break;
					}
				}
			}
			
			int currItem = 1;
			for (ElevateResult e : elevatedList) {
				BasicNameValuePair nvp = null;
				BasicNameValuePair excludeEDPNVP = null;
				BasicNameValuePair excludeFacetNVP = null;
				StringBuilder elevateValues = new StringBuilder();
				StringBuilder elevateFacetValues = new StringBuilder();
				if (e.getElevateEntity() == MemberTypeEntity.PART_NUMBER) {
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "EDP:" + e.getEdp());
				} 
				else {
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, e.getCondition().getConditionForSolr());
				}
				if (e.isForceAdd() && kwNvp!=null) {
					requestParams.remove(kwNvp);
				}
				
				generateElevateList(elevateValues, elevateFacetValues, elevatedList, currItem++);
				if (elevateValues.length() > 0) {
					excludeEDPNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "-" + elevateValues.toString());
					requestParams.add(excludeEDPNVP);
				}				
				if (elevateFacetValues.length() > 0) {
					excludeFacetNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "-" + elevateFacetValues.toString());
					requestParams.add(excludeFacetNVP);
					if (e.isForceAdd() && kwNvp!=null) {
						requestParams.remove(kwNvp);
					}
				}
					
				requestParams.add(nvp);
				HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(requestPath, requestParams);
				requestParams.remove(nvp);
				if (e.isForceAdd()  && kwNvp!=null) {
					requestParams.add(kwNvp);
				}
				if (elevateValues.length() > 0) {
					requestParams.remove(excludeEDPNVP);
				}				
				if (elevateFacetValues.length() > 0) {
					requestParams.remove(excludeFacetNVP);
				}				
				JSONObject tmpJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
				// locate the result node and get the numFound attribute
				// <result> tag that is parent node for all the <doc> tags
				JSONArray docs = (JSONArray)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.TAG_RESPONSE)).get(SolrConstants.TAG_DOCS);
				if (explainObject != null) {
					tmpExplain = (JSONObject)((JSONObject)((JSONObject)tmpJson).get(SolrConstants.ATTR_NAME_VALUE_DEBUG)).get(SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
				}
				for (int j = 0, length = docs.size(); j < length; j++) {
					JSONObject doc = (JSONObject)docs.get(j);
					String edp = doc.getString("EDP");
					doc.element(SolrConstants.TAG_ELEVATE, String.valueOf(e.getLocation()));
					doc.element(SolrConstants.TAG_ELEVATE_TYPE, String.valueOf(e.getElevateEntity()));
					if (e.getElevateEntity() == MemberTypeEntity.FACET) {
						doc.element(SolrConstants.TAG_ELEVATE_CONDITION, e.getCondition().getReadableString());						
					}
					if (expiredElevatedEDPs.contains(edp)) {
						doc.element(SolrConstants.TAG_EXPIRED,"");
					}
					doc.element(SolrConstants.TAG_ELEVATE_ID, String.valueOf(e.getMemberId()));
					docList.add(doc);
					explainMap.put(edp, tmpExplain);
					if (docList.size() >= size) {
						break;
					}
				}
			}
			
			// sort the edps
			// "debug":{ "explain":{ "6230888":
			// for (int i = startRow, size = startRow + requestedRows, resultSize = docList.size(); i < size && i < resultSize; i++) {
			logger.debug("****************" + Math.min(size, docList.size()) + ";" + startRow + ";" + requestedRows);
			for (int i = Math.min(size, docList.size()) - 1; i >= startRow; i--) {
				addedRecords++;
				// insert the elevate results to the docs entry
				resultArray.add(0, docList.get(i));
				if (explainObject != null) {
					String edp = docList.get(i).getString("EDP");
					explainObject.put(edp, explainMap.get(edp).getString(edp));
				}
			}
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get elevated items" ,e);
		}
		return addedRecords;
	}

	private void generateElevateList(StringBuilder elevateValues, StringBuilder elevateFacetValues, Collection<ElevateResult> elevateList, int currItem) {
		boolean edpFlag = false;
		boolean facetFlag = false;
		int i = 1;
		if (!(elevateList == null || elevateList.isEmpty())) {
			for (ElevateResult elevate: elevateList) {
				if (++i > currItem) {
					break;
				}
				if (elevate.getElevateEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (!edpFlag) {
						elevateValues.append("EDP:(");
						edpFlag = true;
					}
					elevateValues.append(" ").append(elevate.getEdp());
				} else {
					if (!facetFlag) {
						elevateFacetValues.insert(0, "(");
						facetFlag = true;
					} else {
						elevateFacetValues.append(" OR ");
					}
					elevateFacetValues.append("(").append(elevate.getCondition().getConditionForSolr()).append(")");
				}
			}
			if (edpFlag) {
				elevateValues.append(")");
			}
			if (facetFlag) {
				elevateFacetValues.append(")");
			}
		}
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

	@Override
	public void setSolrUrl(String solrUrl) {
		requestPath= solrUrl;
	}

	@Override
	public void setSolrQueryParameters(HashMap<String, List<NameValuePair>> paramMap) throws SearchException {
		wrf = SearchServlet.getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_JSON_WRAPPER_FUNCTION);
	}

	@Override
	public void setRequestRows(int startRow, int requestedRows) throws SearchException {
		this.startRow = startRow;
		this.requestedRows = requestedRows;
	}

	@Override
	public void setChangeKeyword(String changedKeyword) throws SearchException {
		this.changedKeyword = changedKeyword;
	}

	public List<ElevateResult> getForceAddedList() {
		return forceAddedList;
	}

	public void setForceAddedList(List<ElevateResult> forceAddedList) {
		this.forceAddedList = forceAddedList;
	}

	private static void generateEdpElevateList(StringBuilder elevateValues, Collection<ElevateResult> elevateList) {
		if (!(elevateList == null || elevateList.isEmpty())) {
			for (ElevateResult elevate: elevateList) {
				if (elevate.getElevateEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (elevateValues.length() == 0) {
						elevateValues.append("EDP:(");
					}
					elevateValues.append(" ").append(elevate.getEdp());
				} 
			}
		}
	}
}
