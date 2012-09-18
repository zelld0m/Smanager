package com.search.ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.groovy.JsonSlurper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.Product;
import com.search.manager.utility.PropsUtils;
import com.search.manager.utility.SolrRequestDispatcher;

public class SearchHelper {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(SearchServlet.class);

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
			boolean isWithEDP = false;
			StringBuilder edps = new StringBuilder("EDP:(");
			for (Product product: productList.values()) {
				edps.append(" ").append(product.getEdp());
				if (product.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER) {
					isWithEDP = true;
				}
			}
			edps.append(")");
			if (isWithEDP) {			
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
				String name = null;
				String description = null;
				for (int i = 0, resultSize = resultArray.size(); i < resultSize; i++) {
					JSONObject json = resultArray.getJSONObject(i);
					Product product = productList.get(json.getString("EDP"));
					if (product != null) {
						@SuppressWarnings("unchecked")
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
							else if ("Name".equals(key)) {
								name = value;
							}
							else if ("Description".equals(key)) {
								description = value;
							}
							else if (key.matches("(.*)_Name$")) {
								product.setName(value);
							}
							else if (key.matches("(.*)_Description$")) {
								product.setDescription(value);
							}
						}
					}
					if (StringUtils.isBlank(product.getName())) {
						product.setName(name);
					}
					if (StringUtils.isBlank(product.getDescription())) {
						product.setName(description);
					}
				}
			}
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
	}

	public static void getProductsIgnoreKeyword(Map<String, ? extends Product> productList, String storeId, String server, String keyword) {
		try {
			if (productList == null || productList.isEmpty()) {
				return;
			}
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			String facetName = configManager.getStoreParameter(storeId, "facet-name");
			// TODO: replace qt with relevancy
			String qt = "standard";

			String core = configManager.getStoreParameter(storeId, "core");
			String fields = configManager.getParameter("big-bets", "fields").replaceAll("\\(facet\\)", facetName);
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", core).concat("select?");
			int size = productList.size();
			StringBuilder edps = new StringBuilder();
			for (Product product: productList.values()) {
				edps.append(" ").append(product.getEdp());
			}

			if (edps.toString().trim().length() == 0) {
				return;
			} else {
				edps.insert(0, "EDP:(").append(")");
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("q", edps.toString()));
			nameValuePairs.add(new BasicNameValuePair("fl", fields));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("rows", String.valueOf(size)));
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
					@SuppressWarnings("unchecked")
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

	public static List<String> getFacetValues(String server, String storeId, String field) {
		return getFacetValues(server, storeId, field, null);
	}

	public static List<String> getFacetValues(String server, String storeId, String field, List<String> filters) {
		return getFacetValues(server, storeId, field, filters, true);
	}

	public static Map<String, List<String>> getFacetValues(String server, String storeId, List<String> fields) {
		return getFacetValues(server, storeId, fields, null);
	}

	public static Map<String, List<String>> getFacetValues(String server, String storeId, List<String> fields, List<String> filters) {
		return getFacetValues(server, storeId, fields, filters, true);
	}

	public static List<String> getFacetValues(String server, String storeId, String field, List<String> filters, boolean hasMincount) {
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(field);
		Map<String, List<String>> map = getFacetValues(server, storeId, fields, filters, hasMincount);
		if (map != null) {
			return map.get(field);
		}
		return new ArrayList<String>();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, List<String>> getFacetValues(String server, String storeId, List<String> fields, List<String> filters, boolean hasMincount) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		if (CollectionUtils.isEmpty(fields)) {
			return map;
		}

		try {
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (String field: fields) {
				if (StringUtils.isNotBlank(field)) {
					nameValuePairs.add(new BasicNameValuePair("facet.field", field));					
				}
			}

			if (nameValuePairs.isEmpty()) {
				return map;
			}

			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}

			String coreName = configManager.getParameterByStore(storeId, "core");
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", coreName).concat("select?");

			nameValuePairs.add(new BasicNameValuePair("q.alt", "*:*"));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("defType", "dismax"));
			nameValuePairs.add(new BasicNameValuePair("rows", "0"));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			nameValuePairs.add(new BasicNameValuePair("facet", "true"));
			nameValuePairs.add(new BasicNameValuePair("facet.sort", "true"));
			nameValuePairs.add(new BasicNameValuePair("facet.limit", "-1"));
			if(hasMincount) {
				nameValuePairs.add(new BasicNameValuePair("facet.mincount", "1"));				
			}

			if (CollectionUtils.isNotEmpty(filters)) {
				for (String filter: filters) {
					nameValuePairs.add(new BasicNameValuePair("fq", filter));					
				}
			}

			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}

			/* JSON */
			JSONObject initialJson = null;
			JsonSlurper slurper = null;
			JSONObject facetFields = null;
			JSONObject facets = null;

			// send solr request
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
			slurper = new JsonSlurper(jsonConfig);
			initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);

			// locate the result node
			facetFields = ((JSONObject)initialJson)
			.getJSONObject(SolrConstants.TAG_FACET_COUNTS)
			.getJSONObject(SolrConstants.TAG_FACET_FIELDS);

			for (String key: (Set<String>)facetFields.keySet()) {
				facets = facetFields.getJSONObject(key);

				if (facets.size() > 0) {

					List<String> list = new ArrayList<String>();

					for (String value: (Set<String>)facets.keySet()) {
						list.add(value);
					}
					// TODO: add facet sorting rule here
					// sort the list
					Collections.sort(list, new Comparator<String>() {
						@Override
						public int compare(String s1, String s2) {
							return s1.compareToIgnoreCase(s2);
						}
					});

					map.put(key, list);
				}
			}


		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
		return map;
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

	public static void getProductViaSim(String server, String storeId, String keyword, ElevateProduct product) {
		String edp = product.getEdp();
		try {
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			String facetName = configManager.getStoreParameter(storeId, "facet-name");
			String core = configManager.getStoreParameter(storeId, "core");
			List<NameValuePair> params = configManager.getDefaultSolrParameters(core);
			String fields = configManager.getParameter("big-bets", "fields").replaceAll("\\(facet\\)", facetName);

			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", storeId).concat("select?").replace("http://",PropsUtils.getValue("browsejssolrurl"));

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			NameValuePair nvp = new BasicNameValuePair("q", keyword); 
			nameValuePairs.add(nvp);
			nameValuePairs.add(new BasicNameValuePair("fl", fields));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("rows", "1"));
			nameValuePairs.add(new BasicNameValuePair("fq", "EDP:" + edp));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			nameValuePairs.add(params.get(0));
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
			resultArray = ((JSONObject)initialJson).getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
			if (resultArray.size() > 0) {
				product.setFoundFlag(true);
			} else {
				product.setFoundFlag(false);
				nameValuePairs.remove(nvp);
				nvp = new BasicNameValuePair("q", "");
				nameValuePairs.add(nvp);
				solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
				jsonConfig = new JsonConfig();
				jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
				slurper = new JsonSlurper(jsonConfig);
				initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);

				// locate the result node
				resultArray = ((JSONObject)initialJson).getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
			}
			if (resultArray.size() > 0) {
				JSONObject json = resultArray.getJSONObject(0);
				@SuppressWarnings("unchecked")
				Set<String> keys = (Set<String>)json.keySet();
				for (String key: keys) {
					String value = json.getString(key);
					if ("DPNo".equals(key)) {
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
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
		
	}

	public static String getEdpViaSim(String server, String storeId, String keyword, String partNumber) {
		String edp = "";
		if (StringUtils.isEmpty(partNumber)) {
			return edp;
		}
		try {
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			String facetName = configManager.getStoreParameter(storeId, "facet-name");
			String core = configManager.getStoreParameter(storeId, "core");
			List<NameValuePair> params = configManager.getDefaultSolrParameters(core);

			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", storeId).concat("select?").replace("http://",PropsUtils.getValue("browsejssolrurl"));

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("q", keyword));
			nameValuePairs.add(new BasicNameValuePair("fl", "EDP"));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("rows", "1"));
			nameValuePairs.add(new BasicNameValuePair("fq", "DPNo:" + partNumber));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			nameValuePairs.add(params.get(0));
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
	
	public static int getFacetCount(String server, String storeId, String keyword, String fqCondition) {
		int count = 0;
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
			nameValuePairs.add(new BasicNameValuePair("fq", fqCondition));
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
            
            // send solr request
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
    		slurper = new JsonSlurper(jsonConfig);
    		initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
    		
    		count = ((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
		return count;
	}

	public static int getFacetCountViaSim(String server, String storeId, String keyword, String fqCondition) {
		int count = 0;
		try {
			ConfigManager configManager = ConfigManager.getInstance();

			// build the query
			String facetName = configManager.getStoreParameter(storeId, "facet-name");

			String core = configManager.getStoreParameter(storeId, "core");
			List<NameValuePair> params = configManager.getDefaultSolrParameters(core);
			// TODO: replace qt with relevancy
			String qt = configManager.getStoreParameter(storeId, "qt");
			if (StringUtils.isEmpty(qt)) {
				qt = "standard";
			}
			String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(store\\)", storeId).concat("select?").replace("http://",PropsUtils.getValue("browsejssolrurl"));
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("q", keyword));
			nameValuePairs.add(new BasicNameValuePair("fl", "EDP"));
			nameValuePairs.add(new BasicNameValuePair("qt", qt));
			nameValuePairs.add(new BasicNameValuePair("rows", "1"));
			nameValuePairs.add(new BasicNameValuePair("fq", fqCondition));
			nameValuePairs.add(new BasicNameValuePair("wt", "json"));
			nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
			nameValuePairs.add(new BasicNameValuePair("gui", "true"));
			nameValuePairs.add(params.get(0));
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}
			
            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            
            // send solr request
			HttpResponse solrResponse = SolrRequestDispatcher.dispatchRequest(serverUrl, nameValuePairs);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
    		slurper = new JsonSlurper(jsonConfig);
    		initialJson = (JSONObject)parseJsonResponse(slurper, solrResponse);
    		
    		count = ((JSONObject)((JSONObject)initialJson).get(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND);
		} catch (Throwable t) {
			logger.error("Error while retrieving from Solr" , t);
		}
		return count;
	}

}
