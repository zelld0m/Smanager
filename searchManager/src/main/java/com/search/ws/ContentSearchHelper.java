package com.search.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentSearchHelper {

	protected final ExecutorService execService = Executors.newCachedThreadPool();
	private static final Logger logger = LoggerFactory.getLogger(ContentSearchHelper.class);
	private JsonConfig jsonConfig;
	private JsonSlurper slurper;
	private JSONObject initialJson = null;
	private String solrUrl = "";
	private final String NAME = "name";
	private final String TYPE = "type";
	private final String SHOW_DATE = "showCreatedDate";
	private final String SHOW_IMAGE = "showImage";
	private final String SHOW_AUTHOR = "showAuthor";
	private final String POSITION = "position";
	private final String DISPLAY = "maxDisplay";

	public ContentSearchHelper() {
		jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		slurper = new JsonSlurper(jsonConfig);
	}

	public void setSolrUrl(String solrUrl) {
		this.solrUrl = solrUrl;
	}

	public boolean generateSectionsResponse(String storeId, HashMap<String, List<NameValuePair>> paramMap,
			List<NameValuePair> requestParams, List<Map<String, String>> sectionProps, 
			HttpServletResponse response, String wrf) throws SearchException {
        boolean success = false;
        try {
        	initialJson = querySolr(requestParams); // base query

        	NameValuePair keywordQuery = paramMap.get(SolrConstants.SOLR_PARAM_KEYWORD).size() > 0
        			? paramMap.get(SolrConstants.SOLR_PARAM_KEYWORD).get(0)
        			: new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, "*:*");
            addSections(keywordQuery, sectionProps);

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
        } catch (Exception e) {
            throw new SearchException("Error occured while trying to generate Http response", e);
        }
        return success;
    }

	public boolean generateContentSearchResponse(String storeId, HashMap<String, List<NameValuePair>> paramMap,
			List<NameValuePair> requestParams, HttpServletResponse response, String wrf) throws SearchException {
        boolean success = false;
        try {
        	initialJson = querySolr(requestParams); // base query

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
        } catch (Exception e) {
            throw new SearchException("Error occured while trying to generate Http response", e);
        }
        return success;
    }

	private static JSON parseJsonResponse(JsonSlurper slurper, HttpResponse response) {
		BufferedReader reader = null;
		InputStream in = null;
		try {

			String encoding = (response.getEntity().getContentEncoding() != null) ? response.getEntity()
			        .getContentEncoding().getValue() : null;
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
			return slurper.parseText(jsonText.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	private JSONObject querySolr(List<NameValuePair> requestParams) {
		HttpClient client = null;
		HttpPost post = null;
		HttpResponse solrResponse = null;
		JSONObject result = null;
		try {
            client = new DefaultHttpClient();
            post = new HttpPost(solrUrl);
            post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
            post.addHeader("Connection", "close");
            if (logger.isDebugEnabled()) {
                logger.debug("URL: " + post.getURI());
                logger.debug("Parameter: " + requestParams);
            }
            solrResponse = client.execute(post);
            result = formatFacets((JSONObject) parseJsonResponse(slurper, solrResponse));
        } catch (Exception e) {
            String error = "Error occured while trying to get number of items";
            logger.error(error, e);
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
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject formatFacets(JSONObject response) {
		JSONObject facetFields = SolrResponseParser.locateJSONObject(response, new String[]{"facet_counts", "facet_fields"});
		if (facetFields == null) {
			return response;
		}
		Iterator<String> facetItr = facetFields.keys();
		while (facetItr.hasNext()) {
			String key = facetItr.next();
			JSONObject updatedFacet = new JSONObject();
			Map<String, Integer> pairs = new HashMap<String, Integer>();
			JSONArray facetField = facetFields.getJSONArray(key);
			for (int i = 0; i < facetField.size(); i = i + 2) {
				pairs.put(facetField.getString(i), facetField.getInt(i+1));
            }
			Iterator mapItr = pairs.entrySet().iterator();
			while (mapItr.hasNext()) {
				Map.Entry pair = (Map.Entry) mapItr.next();
				updatedFacet.element((String) pair.getKey(), pair.getValue());
            }
			facetFields.put(key, updatedFacet);
		}
		return response;
	}

	private void addSections(final NameValuePair keywordQuery, List<Map<String, String>> sectionProps) {
		JSONObject[] data = new JSONObject[sectionProps.size()];
		JSONArray sections = new JSONArray();
		ExecutorCompletionService<JSONObject> completionService = new ExecutorCompletionService<JSONObject>(execService);
		int tasks = 0;

		for (final Map<String, String> property : sectionProps) {
			completionService.submit(new Callable<JSONObject>() {
				@Override
				public JSONObject call() throws Exception {
					JSONObject result = new JSONObject();
					result.element(NAME, property.get(NAME));
					result.element(TYPE, property.get(TYPE));
					result.element(SHOW_DATE, Boolean.parseBoolean(property.get(SHOW_DATE)));
					result.element(SHOW_IMAGE, Boolean.parseBoolean(property.get(SHOW_IMAGE)));
					result.element(SHOW_AUTHOR, Boolean.parseBoolean(property.get(SHOW_AUTHOR)));
					result.element(POSITION, Integer.parseInt(property.get(POSITION)));
					result.element(DISPLAY, Integer.parseInt(property.get(DISPLAY)));
					result.element("data", getContentDocs(keywordQuery, property));
					return result;
				}
			});
			tasks++;
		}
		while (tasks > 0) {
			Future<JSONObject> completed = null;
			try {
				completed = completionService.take();
				JSONObject resp = completed.get();
				data[resp.getInt(POSITION)] = resp;
			} catch (InterruptedException e) {
				logger.error("Concurrent process exception: ", e);
			} catch (ExecutionException e) {
				logger.error("Error on Adding Section: ", e);
			}
			
			tasks--;
		}
		sections.addAll(Arrays.asList(data));
		initialJson.element("Sections", sections);
	}

	private JSONObject getContentDocs(NameValuePair keywordQuery, Map<String, String> property) {
		JSONObject resultDocs = new JSONObject();
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		requestParams.add(keywordQuery);

		// compose the query parameters based on the property settings
		String typeProperty = property.get(TYPE);
		List<String> types = Arrays.asList(typeProperty.split("\\s*,\\s*"));
		StringBuilder fqType = new StringBuilder("type:(");
		for (String type : types) {
			fqType.append(type + " OR ");
		}
		fqType.delete(fqType.length() - 4, fqType.length());
		fqType.append(")");
		requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, fqType.toString()));
		requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, property.get(DISPLAY)));
		
		JSONObject obj = querySolr(requestParams);
		resultDocs = obj.getJSONObject(SolrConstants.TAG_RESPONSE);

		return resultDocs;
	}

}
