package com.search.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.search.manager.model.Product;
import com.search.manager.utility.SolrRequestDispatcher;

public class SearchHelper {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(SearchServlet.class);
		
	public static JSON parseJsonResponse(JsonSlurper slurper,HttpResponse response) {
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

	public static boolean isEdpDetectableInKeyword(String url, String edp, String keyword) {
		// TODO: implement this
		return false;
	}
	
	public static void getProducts(Map<String, ? extends Product> productList, String storeId, String server, String keyword) {
		try {
			if (productList == null || productList.isEmpty()) {
				return;
			}
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			String facetName = configManager.getStoreParameter(storeId, "facet-name");
			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}
			String core = configManager.getStoreParameter(storeId, "core");
			String fields = configManager.getParameter("big-bets", "fields").replaceAll("\\(facet\\)", facetName);
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", core).concat("select?");
			int size = productList.size();
			StringBuilder edps = new StringBuilder("EDP:(");
			for (Product product: productList.values()) {
				edps.append(" ").append(product.getEdp());
			}
			edps.append(")");
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("q", keyword));
			nameValuePairs.add(new BasicNameValuePair("fl", fields));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("rows", String.valueOf(size)));
			nameValuePairs.add(new BasicNameValuePair("fq", edps.toString()));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}
			
            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            JSONArray resultArray = null;
            
            // send solr request
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
    		slurper = new JsonSlurper(jsonConfig);
    		initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
    		
			// locate the result node
    		resultArray = ((JSONObject)initialJson)
    							.getJSONObject(SolrConstants.TAG_RESPONSE)
    							.getJSONArray(SolrConstants.TAG_DOCS);
    		for (int i = 0, resultSize = resultArray.size(); i < resultSize; i++) {
    			JSONObject json = resultArray.getJSONObject(i);
    			Product product = productList.get(json.getString("EDP"));
    			if (product != null) {
	    			Set<String> keys = (Set<String>)json.keySet();
	    			for (String key: keys) {
	    				String value = json.getString(key);
	    				if ("EDP".equals(key)) {
	    					product.setEdp(value);
	    				}
	    				else if ("DPNo".equals(key)) {
	    					product.setDpNo(value);
	    				}
	    				else if ("MfrPN".equals(key)) {
	    					product.setMfrPN(value);
	    				}
	    				else if ("Manufacturer".equals(key)) {
	    					product.setManufacturer(value);
	    				}
	    				else if ("ImagePath".equals(key)) {
	    					product.setImagePath(value);
	    				}
	    				else if (key.matches("(.*)_Name$")) {
	    					product.setName(value);
	    				}
	    				else if (key.matches("(.*)_Description$")) {
	    					product.setDescription(value);
	    				}
	    			}
    			}
    		}
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
	}

	
	@SuppressWarnings("unchecked")
	public static List<String> getFacetValues(String server, String storeId, String field) {
		List<String> list = new ArrayList<String>();
		if (StringUtils.isEmpty(field)) {
			return list;
		}
		
		try {
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", storeId).concat("select?");
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("q.alt", "*:*"));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("defType", "dismax"));
			nameValuePairs.add(new BasicNameValuePair("rows", "0"));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			nameValuePairs.add(new BasicNameValuePair("facet", "true"));
			nameValuePairs.add(new BasicNameValuePair("facet.sort", "true"));
			nameValuePairs.add(new BasicNameValuePair("facet.field", field));
			nameValuePairs.add(new BasicNameValuePair("facet.limit", "-1"));
			
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}
			
            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            JSONObject facets = null;
            
            // send solr request
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
    		slurper = new JsonSlurper(jsonConfig);
    		initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
    		
			// locate the result node
    		facets = ((JSONObject)initialJson)
    							.getJSONObject(SolrConstants.TAG_FACET_COUNTS)
    							.getJSONObject(SolrConstants.TAG_FACET_FIELDS)
    							.getJSONObject(field);
    		if (facets.size() > 0) {
    			for (String value: (Set<String>)facets.keySet()) {
    				list.add(value);
    			}
    			//sort the list
    			Collections.sort(list, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {
						return s1.compareToIgnoreCase(s2);
					}
    			});
    		}
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
		return list;
	}
	
	public static String getEdpByPartNumber(String server, String storeId, String keyword, String partNumber) {
		String edp = "";
		if (StringUtils.isEmpty(partNumber)) {
			return edp;
		}
		try {
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			String facetName = configManager.getStoreParameter(storeId, "facet-name");
			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", storeId).concat("select?");
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("q", keyword));
			nameValuePairs.add(new BasicNameValuePair("fl", "EDP"));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("rows", "1"));
			nameValuePairs.add(new BasicNameValuePair("fq", "DPNo:" + partNumber));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}
			
            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            JSONArray resultArray = null;
            
            // send solr request
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
    		slurper = new JsonSlurper(jsonConfig);
    		initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
    		
			// locate the result node
    		resultArray = ((JSONObject)initialJson)
    							.getJSONObject(SolrConstants.TAG_RESPONSE)
    							.getJSONArray(SolrConstants.TAG_DOCS);
    		if (resultArray.size() > 0) {
    			JSONObject json = resultArray.getJSONObject(0);
    			return json.getString("EDP");
    		}
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
		return edp;
	}

}
