package com.search.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.search.manager.model.ElevateResult;
import com.search.manager.utility.SolrRequestDispatcher;

public class SolrJsonResponseParser implements SolrResponseParser {

	private JSONObject initialJson = null;
	//    private JSONObject tmpJson = null;
	private JsonSlurper slurper = null;
	private JSONArray resultArray  = null; // DOCS entry
	private JSONObject explainObject  = null; // DOCS entry
	private JSONObject responseHeader = null;
	private Map<String, JSONObject> elevateEntries = new HashMap<String, JSONObject>();
	private String wrf = "";

	String requestPath;
	int startRow;
	int requestedRows;

	private static Logger logger = Logger.getLogger(SolrJsonResponseParser.class);

	List<ElevateResult> elevatedList = null;
	List<String> expiredElevatedEDPs = null;

	public SolrJsonResponseParser() {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		slurper = new JsonSlurper(jsonConfig);
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
		for (ElevateResult result: elevatedList) {
			node = nodeMap.get(result.getEdp());
			if (node != null) {
				node.element(SolrConstants.TAG_ELEVATE, String.valueOf(result.getLocation()));
				sortedElevateList.add(node);
			}
		}
		return sortedElevateList;
	}

	private static JSON parseJsonResponse(JsonSlurper slurper,HttpResponse response) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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
			JSONObject paramsHeader = (JSONObject)(responseHeader.get(SolrConstants.ATTR_NAME_VALUE_PARAMS));
			paramsHeader.put(SolrConstants.SOLR_PARAM_ROWS, requestedRows);
			paramsHeader.put(SolrConstants.SOLR_PARAM_START, startRow);
		} catch (Exception e) {
			throw new SearchException("Error occured while trying to get template counts" ,e);
		}
		return numFound;
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

	@Override
	public boolean generateServletResponse(HttpServletResponse response, long totalTime) throws SearchException {
		boolean success = false;
		try {
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

}
