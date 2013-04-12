package com.search.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.groovy.JsonSlurper;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.SortType;
import com.search.manager.model.CNetFacetTemplate;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.FacetEntry;
import com.search.manager.model.SearchResult;

public class SolrJsonResponseParser extends SolrResponseParser {

	private JSONObject initialJson = null;
	private JsonSlurper slurper = null;
	private JSONArray resultArray  = null; // DOCS entry
	private JSONObject explainObject  = null; // DOCS entry
	private JSONObject facetTemplateJSON  = null; // Facet Template
	private JSONObject facetFields  = null; // Facet Sort
	private JSONObject responseHeader = null;
	private JSONObject responseHeaderParams = null;
	private List<JSONObject> elevatedResults = new ArrayList<JSONObject>();
	private List<JSONObject> demotedResults = new ArrayList<JSONObject>();
	
	private JSONObject spellcheckObject = null;
	private JSONObject spellcheckParams = null;

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
	
	private static JSONObject parseJsonResponse(JsonSlurper slurper,HttpResponse response) {
		BufferedReader reader = null;
		InputStream in = null;
		try {
			String encoding = (response.getEntity().getContentEncoding() != null) ? response.getEntity().getContentEncoding().getValue() : null;
			if (encoding == null) {
				encoding = "UTF-8";
			}
			in = response.getEntity().getContent();
			reader = new BufferedReader(new InputStreamReader(in, encoding));
			String line = null;
			StringBuilder jsonText = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				jsonText.append(line.trim());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Json response" + jsonText.toString());
			}
			return (JSONObject)slurper.parseText(jsonText.toString());
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			try { if (in != null) in.close();  } catch (IOException e) { }
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
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		
		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			initialJson = parseJsonResponse(slurper, solrResponse);
			// locate the result node and reference it <result name="response" maxScore="23.015398" start="0" numFound="360207">
			// results will be added here
			resultArray = initialJson.getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
			// explain values are added here
			explainObject = locateJSONObject(initialJson, new String[]{SolrConstants.ATTR_NAME_VALUE_DEBUG, SolrConstants.ATTR_NAME_VALUE_EXPLAIN});
			// number of records
			numFound = initialJson.getJSONObject(SolrConstants.TAG_RESPONSE).getInt(SolrConstants.ATTR_NUM_FOUND);
			initialJson.getJSONObject(SolrConstants.TAG_RESPONSE).put(SolrConstants.SOLR_PARAM_START, startRow);
			// put back rows in header
			responseHeader = initialJson.getJSONObject(SolrConstants.ATTR_NAME_VALUE_RESPONSE_HEADER);
			
			if(redirectRule != null && redirectRule.isRedirectChangeKeyword()) {
				JSONObject redirectObject = new JSONObject();
				Map<String, String> fields = new HashMap<String, String>();
				
				if(StringUtils.isNotBlank(originalKeyword)) {
					fields.put(SolrConstants.TAG_REDIRECT_ORIGINAL_KEYWORD, originalKeyword);
				}
				if(StringUtils.isNotBlank(redirectRule.getChangeKeyword())) {
					fields.put(SolrConstants.TAG_REDIRECT_REPLACEMENT_KEYWORD, redirectRule.getChangeKeyword());
				}
				if(redirectRule.getReplaceKeywordMessageType() != null) {
					fields.put(SolrConstants.TAG_REDIRECT_REPLACEMENT_TYPE, redirectRule.getReplaceKeywordMessageType()+"");
				}
				if(StringUtils.isNotBlank(redirectRule.getReplaceKeywordMessageCustomText())) {
					fields.put(SolrConstants.TAG_REDIRECT_CUSTOM_TEXT, redirectRule.getReplaceKeywordMessageCustomText());
				}
				redirectObject.putAll(fields);
				responseHeader.element(SolrConstants.TAG_REDIRECT, redirectObject);
			}
			
			if(spellRule != null) {
				JSONObject spellRuleObject = new JSONObject();
				Map<String, String> fields = new HashMap<String, String>();
				if(spellRule.getSuggestions() != null) {
					for(int i=0; i<spellRule.getSuggestions().length; i++) {
						fields.put(spellRule.getSuggestions()[i], spellRule.getSuggestions()[i]);
					}
				}
				spellRuleObject.putAll(fields);
				responseHeader.element(SolrConstants.TAG_DID_YOU_MEAN, spellRuleObject);
			}
			
			// TODO: make this get value from solr.xml
			if (StringUtils.isNotEmpty(facetTemplate)) {
				facetTemplateJSON = locateJSONObject(initialJson, new String[]{"facet_counts", "facet_fields", facetTemplate});
			}
			facetFields = locateJSONObject(initialJson, new String[]{"facet_counts", "facet_fields"});
			
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
			
			responseHeaderParams = responseHeader.getJSONObject(SolrConstants.ATTR_NAME_VALUE_PARAMS);
			responseHeaderParams.put(SolrConstants.SOLR_PARAM_ROWS, requestedRows);
			responseHeaderParams.put(SolrConstants.SOLR_PARAM_START, startRow);
		} catch (Exception e) {
			String error = "Error occured while trying to get template counts";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return numFound;
	}

	@Override
	public int getCount(List<NameValuePair> requestParams) throws SearchException {
		int numFound = -1;
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		
		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			JSONObject tmpJson = parseJsonResponse(slurper, solrResponse);
			numFound = tmpJson.getJSONObject(SolrConstants.TAG_RESPONSE).getInt(SolrConstants.ATTR_NUM_FOUND);
		} catch (Exception e) {
			String error = "Error occured while trying to get number of items";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}			
		}
		return numFound;
	}

	private void tagSearchResult(JSONObject resultObject, String edp, SearchResult result) {
		if (expiredElevatedEDPs.contains(edp)) {
			resultObject.element(SolrConstants.TAG_ELEVATE_EXPIRED,"");
		}
		if (expiredDemotedEDPs.contains(edp)) {
			resultObject.element(SolrConstants.TAG_DEMOTE_EXPIRED,"");
		}
		if (result instanceof ElevateResult) {
			resultObject.element(SolrConstants.TAG_ELEVATE, String.valueOf(((ElevateResult)result).getLocation()));
			resultObject.element(SolrConstants.TAG_ELEVATE_TYPE, String.valueOf(result.getEntity()));
			if (result.getEntity() == MemberTypeEntity.FACET) {
				resultObject.element(SolrConstants.TAG_ELEVATE_CONDITION, result.getCondition().getReadableString());						
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
			} else {
				if(forceAddedEDPs != null && forceAddedEDPs.contains(((DemoteResult) result).getEdp())) {
					resultObject.element(SolrConstants.TAG_FORCE_ADD, "");
				}
			}
			resultObject.element(SolrConstants.TAG_DEMOTE_ID, String.valueOf(result.getMemberId()));
		}
	}
	
	@Override
	protected int getEdps(List<NameValuePair> requestParams, List<? extends SearchResult> edpList, int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			JSONObject tmpJson = parseJsonResponse(slurper, solrResponse);
			JSONArray docs = tmpJson.getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = tmpJson.getJSONObject(SolrConstants.ATTR_NAME_VALUE_DEBUG).getJSONObject(SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
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
			String error = "Error occured while trying to get items";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return addedRecords;
	}

	@Override
	protected int getFacet(List<NameValuePair> requestParams, SearchResult facet) throws SearchException {
		int addedRecords = 0;
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			JSONObject tmpJson = parseJsonResponse(slurper, solrResponse);
			JSONArray docs = tmpJson.getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = tmpJson.getJSONObject(SolrConstants.ATTR_NAME_VALUE_DEBUG).getJSONObject(SolrConstants.ATTR_NAME_VALUE_EXPLAIN);
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
			String error = "Error occured while trying to get items";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return addedRecords;
	}

	@Override
	public int getNonElevatedItems(List<NameValuePair> requestParams) throws SearchException {
		int addedRecords = 0;
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			JSONObject tmpJson = parseJsonResponse(slurper, solrResponse);
			JSONArray docs = tmpJson.getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
			JSONObject tmpExplain = null;
			if (explainObject != null) {
				tmpExplain = locateJSONObject(tmpJson, new String[]{SolrConstants.ATTR_NAME_VALUE_DEBUG, SolrConstants.ATTR_NAME_VALUE_EXPLAIN});
			}

			for (int j = 0, length = docs.size(); j < length; j++) {
				addedRecords++;
				
				JSONObject doc = (JSONObject)docs.get(j);
				String edp = doc.getString("EDP");
				if (expiredElevatedEDPs.contains(edp)) {
					doc.element(SolrConstants.TAG_ELEVATE_EXPIRED,"");
				}
				if (expiredDemotedEDPs.contains(edp)) {
					doc.element(SolrConstants.TAG_DEMOTE_EXPIRED,"");
				}
				if (!includeEDP) { // remove EDP if not requested
					doc.remove("EDP");
				}

				resultArray.add(doc);
				if (explainObject != null) {
					explainObject.put(edp, tmpExplain.getString(edp));
				}
			}
		} catch (Exception e) {
			String error = "Error occured while trying to get non-elevated items";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return addedRecords;
	}

	@SuppressWarnings("unchecked")
	private void getFacetTemplates() {
		
		if (facetTemplateJSON == null) {
			return;
		}
		
		CNetFacetTemplate root = new CNetFacetTemplate("",0);
		for (String key: (Set<String>)facetTemplateJSON.keySet()) {
			
			int count = facetTemplateJSON.getInt(key);

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
		
		// remove the facet template if not requested
		if (!includeFacetTemplateFacet) {
			locateJSONObject(initialJson, new String[]{"facet_counts", "facet_fields"}).remove(facetTemplate);
		}
		
		LinkedHashMap<String, Long> lvl1Map = new LinkedHashMap<String, Long>();
		LinkedHashMap<String, Long> lvl2Map = new LinkedHashMap<String, Long>();
		LinkedHashMap<String, Long> lvl3Map = new LinkedHashMap<String, Long>();
		
		if (root.getFacetCount() == 0) {
			return;
		}
		
		JSONObject facets = new JSONObject();
		if (root.getFacetCount() > 1) {
			ConfigManager cm = ConfigManager.getInstance();
			
			if (facetSortRule == null || facetSortRule.getItems().get("Category") == null || !cm.isMemberOf("PCM", facetSortRule.getStoreId())){
				for (String lvl1Key: root.getFacets()) {
					CNetFacetTemplate lvl1 = root.getFacet(lvl1Key);
					lvl1Map.put(lvl1Key, lvl1.getCount());
				}
			}
			else {
				facetSortRule.getItems().containsKey("Category");
				List<FacetEntry> entries = new ArrayList<FacetEntry>();
				for (String lvl1Key: root.getFacets()) {
					entries.add(new FacetEntry(lvl1Key, root.getFacet(lvl1Key).getCount()));
				}
				SortType sortType = facetSortRule.getGroupSortType().get("Category");
				if (sortType == null) {
					sortType = facetSortRule.getSortType();
				}
				FacetEntry.sortEntries(entries, sortType, facetSortRule.getItems().get("Category"));
				
				for (FacetEntry entry: entries) {
					lvl1Map.put(entry.getLabel(), entry.getCount());
				}
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
			addSpellcheckEntries();
			getFacetTemplates();
			applyFacetSort();
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
				if (!includeEDP) { // remove EDP if not requested
					object.remove("EDP");
				}
				// insert the elevate results to the docs entry
				resultArray.add(i++, object);
			}
		}
	}
	
	private void addDemotedEntries() {
		if (demotedResults != null) {
			for (JSONObject object: demotedResults) {
				if (!includeEDP) { // remove EDP if not requested
					object.remove("EDP");
				}
				// insert the elevate results to the docs entry
				resultArray.add(object);
			}
		}
	}

    private void addSpellcheckEntries() throws SearchException {
        int count = 0;

        if (spellRule != null) {
            List<String> addedKeywords = new ArrayList<String>();
            LinkedHashMap<String, Object> suggestions = new LinkedHashMap<String, Object>();
            Map<String, Object> orig = new HashMap<String, Object>();

            suggestions.put(originalKeyword, orig);

            orig.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_START_OFFSET, 0);
            orig.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_END_OFFSET, originalKeyword.length());
            orig.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION,
                    new ArrayList<String>(Arrays.asList(spellRule.getSuggestions())));
            addedKeywords.addAll(Arrays.asList(spellRule.getSuggestions()));
            count = spellRule.getSuggestions().length;

            JSONObject suggestionsJson = spellcheckObject != null ? spellcheckObject
                    .getJSONObject(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTIONS) : null;

            if (count < maxSuggestCount && suggestionsJson != null) {
                if (suggestionsJson.has(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_COLLATION)) {
                    String collation = suggestionsJson.getString(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_COLLATION);

                    if (!addedKeywords.contains(collation)) {
                        ((List<String>) orig.get(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION)).add(collation);
                        count++;
                    }
                }

                JSONArray names = suggestionsJson.names();

                for (int i = 0; i < names.size() && count < maxSuggestCount; i++) {
                    if (SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_COLLATION.equals(names.getString(i))) {
                        continue;
                    }

                    JSONObject obj = suggestionsJson.getJSONObject(names.getString(i));
                    JSONArray sugs = obj.getJSONArray(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION);

                    if (!suggestions.containsKey(names.getString(i))) {
                        Map<String, Object> val = new HashMap<String, Object>();
                        suggestions.put(names.getString(i), val);
                        val.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION, new ArrayList<String>());

                        val.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_START_OFFSET,
                                obj.getInt(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_START_OFFSET));
                        val.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_END_OFFSET,
                                obj.getInt(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_END_OFFSET));
                    }

                    Map<String, Object> val = (Map<String, Object>) suggestions.get(names.getString(i));
                    List<String> sug = (List<String>) val.get(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION);

                    for (int j = 0; j < sugs.size(); j++) {
                        String kw = sugs.getString(j);

                        if (!addedKeywords.contains(kw)) {
                            sug.add(kw);
                            addedKeywords.add(kw);
                            count++;
                        }

                        if (count >= maxSuggestCount) {
                            break;
                        }
                    }
                }
            }

            for (String k : suggestions.keySet()) {
                Map<String, Object> val = (Map<String, Object>) suggestions.get(k);
                List<String> sug = (List<String>) val.get(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTION);

                val.put(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_NUMFOUND, sug.size());
            }

            JSONObject obj = new JSONObject();

            obj.element(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK_SUGGESTIONS, suggestions);
            initialJson.element(SolrConstants.TAG_SPELLCHECK, obj);
        }

        if (count == 0) {
            if (spellcheckObject != null) {
                initialJson.element(SolrConstants.TAG_SPELLCHECK, spellcheckObject);
            }
        }
        if (spellcheckParams != null) {
            JSONArray names = spellcheckParams.names();
            for (int i = 0; i < names.size(); i++) {
                String name = String.valueOf(names.get(i));
                if (name.startsWith(SolrConstants.ATTR_NAME_VALUE_SPELLCHECK)) {
                    responseHeaderParams.element(name, spellcheckParams.get(name));
                }
            }
        }
    }
	
	@SuppressWarnings("unchecked")
	private void applyFacetSort() {
		if (facetSortRule == null || facetFields == null) {
			return;
		}

		ConfigManager cm = ConfigManager.getInstance();
		
		for (String key: facetSortRule.getItems().keySet()) {
			if (!(StringUtils.equals("Category", key) && cm.isMemberOf("PCM", facetSortRule.getStoreId()))) {
				
				// grab a copy of the fields
				List<FacetEntry> entries = new ArrayList<FacetEntry>();
				
				JSONObject facetField = facetFields.getJSONObject(key);
				for (String facetValue: (Set<String>)facetField.keySet()) {
					entries.add(new FacetEntry(facetValue, facetField.getLong(facetValue)));
				}
				
				SortType sortType = facetSortRule.getGroupSortType().get(key);
				if (sortType == null) {
					sortType = facetSortRule.getSortType();
				}
				FacetEntry.sortEntries(entries, sortType, facetSortRule.getItems().get(key));
				
				JSONObject facets = new JSONObject();
				for (FacetEntry entry: entries) {
					facets.element(entry.getLabel(), entry.getCount());
				}
				facetFields.put(key, facets);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getCommonTemplateName(String templateNameField, List<NameValuePair> requestParams) throws SearchException {
		String templateName = "";
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			JSONObject facetFields = locateJSONObject(parseJsonResponse(slurper, solrResponse), 
					new String[] { SolrConstants.TAG_FACET_COUNTS, SolrConstants.TAG_FACET_FIELDS, templateNameField });
			Set<String> set = facetFields.keySet();
			if (set.size() == 1) {
				templateName = set.toArray(new String[0])[0];
			}
		} catch (Exception e) {
			String error = "Error occured while trying to get common template name";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return templateName;
	}

	@Override
	public void getSpellingSuggestion(List<NameValuePair> requestParams) throws SearchException {
		HttpClient client  = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;

		try {
			client = new DefaultHttpClient();
			post = new HttpPost(getSpellCheckRequestPath());
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
			}
			solrResponse = client.execute(post);
			JSONObject tmpJson = parseJsonResponse(slurper, solrResponse);
			spellcheckObject = tmpJson.getJSONObject(SolrConstants.TAG_SPELLCHECK);
			spellcheckParams = tmpJson.getJSONObject(SolrConstants.ATTR_NAME_VALUE_RESPONSE_HEADER)
								 	.getJSONObject(SolrConstants.ATTR_NAME_VALUE_PARAMS);
		} catch (Exception e) {
			String error = "Error occured while trying to get spelling suggestion";
			logSolrError(post, error, e);
			throw new SearchException(error ,e);
		} finally {
			if (post != null) {
				if (solrResponse != null) {
					EntityUtils.consumeQuietly(solrResponse.getEntity());
				}
				post.releaseConnection();
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
	}
	
}
