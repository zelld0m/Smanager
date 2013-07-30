package com.search.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.groovy.JsonSlurper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.Product;
import com.search.manager.model.SearchResult;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchHelper {

    private static final long serialVersionUID = 1L;
    private static final Logger logger =
            LoggerFactory.getLogger(SearchHelper.class);

    private static JSON parseJsonResponse(JsonSlurper slurper, HttpResponse response) {
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

    public static boolean isEdpDetectableInKeyword(String url, String edp, String keyword) {
        // TODO: implement this
        return false;
    }

    public static LinkedHashMap<String, Product> getProducts(List<? extends SearchResult> itemList, String store, String ruleId) {
        LinkedHashMap<String, Product> map = new LinkedHashMap<String, Product>();

        for (SearchResult e : itemList) {
            Product ep = new Product(e);
            ep.setStore(store);
            if (e.getMemberType() == MemberTypeEntity.PART_NUMBER) {
                map.put(e.getEdp(), ep);
            } else {
                map.put(e.getMemberId(), ep);
            }
        }

        if (MapUtils.isNotEmpty(map)) {
            SearchHelper.getProducts(map, store, UtilityService.getServerName(), ruleId);
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Product> getProductsIgnoreKeyword(Map<String, ? extends Product> map, String store, String ruleId) {
        if (MapUtils.isNotEmpty(map)) {
            SearchHelper.getProductsIgnoreKeyword(map, store, UtilityService.getServerName(), ruleId);
        }

        return (LinkedHashMap<String, Product>) map;
    }

    public static void getProducts(Map<String, ? extends Product> productList, String storeId, String server, String keyword) {
        HttpClient client = null;
        HttpPost post = null;
        HttpResponse solrResponse = null;
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
            String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(core\\)", core).concat("select?");
            int size = productList.size();
            boolean isWithEDP = false;
            StringBuilder edps = new StringBuilder("EDP:(");
            String edp = "";
            for (Product product : productList.values()) {
                edp = product.getEdp();
                if (product.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER && StringUtils.isNotBlank(edp)) {
                    edps.append(" ").append(edp);
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

                String solrSelectorParam = configManager.getSolrSelectorParam();

                if (StringUtils.isNotBlank(solrSelectorParam)) {
                    nameValuePairs.add(new BasicNameValuePair(solrSelectorParam, storeId));
                }

                nameValuePairs.add(new BasicNameValuePair("wt", "json"));
                nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
                if (logger.isDebugEnabled()) {
                    for (NameValuePair p : nameValuePairs) {
                        logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
                    }
                }

                /* JSON */
                JSONObject initialJson = null;
                JsonSlurper slurper = null;
                JSONArray resultArray = null;

                // send solr request
                client = new DefaultHttpClient();
                post = new HttpPost(serverUrl);
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                post.addHeader("Connection", "close");
                if (logger.isDebugEnabled()) {
                    logger.debug("URL: " + post.getURI());
                    logger.debug("Parameter: " + nameValuePairs);
                }
                solrResponse = client.execute(post);
                JsonConfig jsonConfig = new JsonConfig();
                jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
                slurper = new JsonSlurper(jsonConfig);
                initialJson = (JSONObject) parseJsonResponse(slurper, solrResponse);

                // locate the result node
                resultArray = ((JSONObject) initialJson)
                        .getJSONObject(SolrConstants.TAG_RESPONSE)
                        .getJSONArray(SolrConstants.TAG_DOCS);
                String name = null;
                String description = null;
                for (int i = 0, resultSize = resultArray.size(); i < resultSize; i++) {
                    JSONObject json = resultArray.getJSONObject(i);
                    Product product = productList.get(json.getString("EDP"));
                    name = "";
                    description = "";
                    if (product != null) {
                        @SuppressWarnings("unchecked")
                        Set<String> keys = (Set<String>) json.keySet();
                        for (String key : keys) {
                            String value = json.getString(key);
                            if ("EDP".equals(key)) {
                                product.setEdp(value);
                            } else if ("DPNo".equals(key)) {
                                product.setDpNo(value);
                            } else if ("MfrPN".equals(key)) {
                                product.setMfrPN(value);
                            } else if ("Manufacturer".equals(key)) {
                                product.setManufacturer(value);
                            } else if ("ImagePath".equals(key)) {
                                product.setImagePath(value);
                            } else if ("Name".equals(key)) {
                                name = value;
                            } else if ("Description".equals(key)) {
                                description = value;
                            } else if (key.matches("(.*)_Name$")) {
                                product.setName(value);
                            } else if (key.matches("(.*)_Description$")) {
                                product.setDescription(value);
                            }
                        }
                    }
                    if (StringUtils.isBlank(product.getName())) {
                        product.setName(name);
                    }
                    if (StringUtils.isBlank(product.getDescription())) {
                        product.setDescription(description);
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Error while retrieving from Solr", t);
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

    public static void getProductsIgnoreKeyword(Map<String, ? extends Product> productList, String storeId, String server, String keyword) {
        HttpClient client = null;
        HttpPost post = null;
        HttpResponse solrResponse = null;
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
            String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(core\\)", core).concat("select?");
            int size = productList.size();
            StringBuilder edps = new StringBuilder();
            String edp = "";

            for (Product product : productList.values()) {
                edp = product.getEdp();
                if (product.getMemberTypeEntity() == MemberTypeEntity.PART_NUMBER && StringUtils.isNotBlank(edp)) {
                    edps.append(" ").append(edp);
                }
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

            String solrSelectorParam = configManager.getSolrSelectorParam();

            if (StringUtils.isNotBlank(solrSelectorParam)) {
                nameValuePairs.add(new BasicNameValuePair(solrSelectorParam, storeId));
            }

            nameValuePairs.add(new BasicNameValuePair("wt", "json"));
            nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
            nameValuePairs.add(new BasicNameValuePair("gui", "true"));
            if (logger.isDebugEnabled()) {
                for (NameValuePair p : nameValuePairs) {
                    logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
                }
            }

            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            JSONArray resultArray = null;

            // send solr request
            client = new DefaultHttpClient();
            post = new HttpPost(serverUrl);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            post.addHeader("Connection", "close");
            if (logger.isDebugEnabled()) {
                logger.debug("URL: " + post.getURI());
                logger.debug("Parameter: " + nameValuePairs);
            }
            solrResponse = client.execute(post);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
            slurper = new JsonSlurper(jsonConfig);
            initialJson = (JSONObject) parseJsonResponse(slurper, solrResponse);

            // locate the result node
            resultArray = ((JSONObject) initialJson)
                    .getJSONObject(SolrConstants.TAG_RESPONSE)
                    .getJSONArray(SolrConstants.TAG_DOCS);
            String name = null;
            String description = null;
            for (int i = 0, resultSize = resultArray.size(); i < resultSize; i++) {
                JSONObject json = resultArray.getJSONObject(i);
                Product product = productList.get(json.getString("EDP"));
                name = "";
                description = "";
                if (product != null) {
                    @SuppressWarnings("unchecked")
                    Set<String> keys = (Set<String>) json.keySet();
                    for (String key : keys) {
                        String value = json.getString(key);
                        if ("EDP".equals(key)) {
                            product.setEdp(value);
                        } else if ("DPNo".equals(key)) {
                            product.setDpNo(value);
                        } else if ("MfrPN".equals(key)) {
                            product.setMfrPN(value);
                        } else if ("Manufacturer".equals(key)) {
                            product.setManufacturer(value);
                        } else if ("ImagePath".equals(key)) {
                            product.setImagePath(value);
                        } else if ("Name".equals(key)) {
                            name = value;
                        } else if ("Description".equals(key)) {
                            description = value;
                        } else if (key.matches("(.*)_Name$")) {
                            product.setName(value);
                        } else if (key.matches("(.*)_Description$")) {
                            product.setDescription(value);
                        }
                    }
                    if (StringUtils.isBlank(product.getName())) {
                        product.setName(name);
                    }
                    if (StringUtils.isBlank(product.getDescription())) {
                        product.setDescription(description);
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Error while retrieving from Solr", t);
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

        HttpClient client = null;
        HttpPost post = null;
        HttpResponse solrResponse = null;

        try {
            ConfigManager configManager = ConfigManager.getInstance();

            // build the query
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (String field : fields) {
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

            String core = configManager.getStoreParameter(storeId, "core");
            String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(core\\)", core).concat("select?");

            nameValuePairs.add(new BasicNameValuePair("q.alt", "*:*"));
            nameValuePairs.add(new BasicNameValuePair("qt", qt));
            nameValuePairs.add(new BasicNameValuePair("defType", "dismax"));
            nameValuePairs.add(new BasicNameValuePair("rows", "0"));
            nameValuePairs.add(new BasicNameValuePair("wt", "json"));
            nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
            nameValuePairs.add(new BasicNameValuePair("facet", "true"));
            nameValuePairs.add(new BasicNameValuePair("facet.sort", "true"));
            String solrSelectorParam = configManager.getSolrSelectorParam();

            if (StringUtils.isNotBlank(solrSelectorParam)) {
                nameValuePairs.add(new BasicNameValuePair(solrSelectorParam, storeId));
            }

            nameValuePairs.add(new BasicNameValuePair("facet.limit", "-1"));
            if (hasMincount) {
                nameValuePairs.add(new BasicNameValuePair("facet.mincount", "1"));
            }

            if (CollectionUtils.isNotEmpty(filters)) {
                for (String filter : filters) {
                    nameValuePairs.add(new BasicNameValuePair("fq", filter));
                }
            }

            if (logger.isDebugEnabled()) {
                for (NameValuePair p : nameValuePairs) {
                    logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
                }
            }

            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            JSONObject facetFields = null;
            JSONObject facets = null;

            // send solr request
            client = new DefaultHttpClient();
            post = new HttpPost(serverUrl);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            post.addHeader("Connection", "close");
            if (logger.isDebugEnabled()) {
                logger.debug("URL: " + post.getURI());
                logger.debug("Parameter: " + nameValuePairs);
            }
            solrResponse = client.execute(post);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
            slurper = new JsonSlurper(jsonConfig);
            initialJson = (JSONObject) parseJsonResponse(slurper, solrResponse);

            // locate the result node
            facetFields = ((JSONObject) initialJson)
                    .getJSONObject(SolrConstants.TAG_FACET_COUNTS)
                    .getJSONObject(SolrConstants.TAG_FACET_FIELDS);

            for (String key : (Set<String>) facetFields.keySet()) {
                facets = facetFields.getJSONObject(key);

                if (facets.size() > 0) {

                    List<String> list = new ArrayList<String>();

                    for (String value : (Set<String>) facets.keySet()) {
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
            logger.error("Error while retrieving from Solr", t);
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
        return map;
    }

    public static String getEdpByPartNumber(String server, String storeId, String partNumber) {
        String edp = "";
        HttpClient client = null;
        HttpPost post = null;
        HttpResponse solrResponse = null;
        if (StringUtils.isEmpty(partNumber)) {
            return edp;
        }
        try {
            ConfigManager configManager = ConfigManager.getInstance();

            // build the query
            String core = configManager.getStoreParameter(storeId, "core");
            String serverUrl = configManager.getServerParameter(server, "url").replaceAll("\\(core\\)", core).concat("select?");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("fl", "EDP"));
            nameValuePairs.add(new BasicNameValuePair("qt", "standard"));
            nameValuePairs.add(new BasicNameValuePair("rows", "1"));

            String solrSelectorParam = configManager.getSolrSelectorParam();

            if (StringUtils.isNotBlank(solrSelectorParam)) {
                nameValuePairs.add(new BasicNameValuePair(solrSelectorParam, storeId));
            }

            nameValuePairs.add(new BasicNameValuePair("q", "DPNo:" + partNumber));
            nameValuePairs.add(new BasicNameValuePair("wt", "json"));
            nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
            if (logger.isDebugEnabled()) {
                for (NameValuePair p : nameValuePairs) {
                    logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
                }
            }

            /* JSON */
            JSONObject initialJson = null;
            JsonSlurper slurper = null;
            JSONArray resultArray = null;

            // send solr request
            client = new DefaultHttpClient();
            client = new DefaultHttpClient();
            post = new HttpPost(serverUrl);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            post.addHeader("Connection", "close");
            if (logger.isDebugEnabled()) {
                logger.debug("URL: " + post.getURI());
                logger.debug("Parameter: " + nameValuePairs);
            }
            solrResponse = client.execute(post);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
            slurper = new JsonSlurper(jsonConfig);
            initialJson = (JSONObject) parseJsonResponse(slurper, solrResponse);

            // locate the result node
            resultArray = ((JSONObject) initialJson).getJSONObject(SolrConstants.TAG_RESPONSE).getJSONArray(SolrConstants.TAG_DOCS);
            if (resultArray.size() > 0) {
                edp = resultArray.getJSONObject(0).getString("EDP");
            }
        } catch (Throwable t) {
            logger.error("Error while retrieving from Solr", t);
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
        return edp;
    }

    public static boolean isForceAddCondition(String server, String storeId, String keyword, String fqCondition) {
        boolean forceAdd = false;
        HttpClient client = null;
        HttpPost post = null;
        HttpResponse solrResponse = null;

        try {
            // build the query

            ConfigManager configManager = ConfigManager.getInstance();
            String core = configManager.getStoreParameter(storeId, "core");
            String serverUrl = configManager.getServerParameter(server, "url")
                    .replaceAll("\\(core\\)", core).concat("select?")
                    .replace("http://", PropertiesUtils.getValue("browsejssolrurl"));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("q", keyword));
            nameValuePairs.add(new BasicNameValuePair("rows", "0"));
            nameValuePairs.add(new BasicNameValuePair("fq", fqCondition));
            nameValuePairs.add(new BasicNameValuePair("wt", "json"));
            nameValuePairs.add(new BasicNameValuePair("json.nl", "map"));
            nameValuePairs.add(new BasicNameValuePair("gui", "true"));
            String solrSelectorParam = configManager.getSolrSelectorParam();

            if (StringUtils.isNotBlank(solrSelectorParam)) {
                nameValuePairs.add(new BasicNameValuePair(solrSelectorParam, storeId));
            }

            nameValuePairs.add(new BasicNameValuePair("disableElevate", ""));
            nameValuePairs.add(new BasicNameValuePair("disableExclude", ""));
            nameValuePairs.add(new BasicNameValuePair("disableDemote", ""));
            if (logger.isDebugEnabled()) {
                for (NameValuePair p : nameValuePairs) {
                    logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
                }
            }

            // send solr request
            client = new DefaultHttpClient();
            post = new HttpPost(serverUrl);
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            post.addHeader("Connection", "close");
            if (logger.isDebugEnabled()) {
                logger.debug("URL: " + post.getURI());
                logger.debug("Parameter: " + nameValuePairs);
            }
            solrResponse = client.execute(post);
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
            forceAdd = (((JSONObject) parseJsonResponse(new JsonSlurper(jsonConfig), solrResponse))
                    .getJSONObject(SolrConstants.TAG_RESPONSE)).getInt(SolrConstants.ATTR_NUM_FOUND) > 0;
        } catch (Throwable t) {
            logger.error("Error while retrieving from Solr", t);
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
        return forceAdd;
    }
}
