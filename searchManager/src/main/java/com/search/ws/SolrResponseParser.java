package com.search.ws;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.groovy.JsonSlurper;

import org.apache.commons.collections.CollectionUtils;
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

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.SortType;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchResult;
import com.search.manager.model.SpellRule;

public abstract class SolrResponseParser {

	private static final Logger logger = LoggerFactory
			.getLogger(SolrResponseParser.class);

	/*
	 * Sends the original Solr Query Parameters, in case implementation needs to
	 * do something with it. Example JSON implemenation would need to get wrf
	 * parameter
	 */
	public abstract int getTemplateCounts(List<NameValuePair> requestParams)
			throws SearchException;

	public abstract int getCount(List<NameValuePair> requestParams)
			throws SearchException;

	public abstract int getNonElevatedItems(List<NameValuePair> requestParams)
			throws SearchException;

	public abstract String getCommonTemplateName(String templateNameField,
			List<NameValuePair> requestParams) throws SearchException;

	public abstract void getSpellingSuggestion(List<NameValuePair> requestParams)
			throws SearchException;

	public abstract boolean generateServletResponse(
			HttpServletResponse response, long totalTime)
			throws SearchException;

	protected JSONObject groupedFacetJson;
	protected String defaultSortOrder;
	protected String requestPath;
	protected int startRow;
	protected int requestedRows;
	protected String facetTemplate;
	protected String originalKeyword;
	protected boolean includeFacetTemplateFacet;
	protected boolean includeEDP;
	protected boolean isCNETImplementation;
	protected List<ElevateResult> elevatedList = null;
	protected List<String> expiredElevatedEDPs = null;
	protected List<Map<String, String>> activeRules;
	protected List<DemoteResult> demotedList = null;
	protected List<String> expiredDemotedEDPs = null;
	protected List<String> forceAddedEDPs = null;
	protected List<BannerRuleItem> bannerList = null;
	/* Did You Mean rule */
	protected SpellRule spellRule = null;
	// TODO: This should be configurable.
	protected int maxSuggestCount = 5;
	protected FacetSort facetSortRule;
	protected RedirectRule redirectRule;

	/* Enterprise Search start */
	protected Map<String, String> elevateFieldOverrides;
	protected Map<String, String> demoteFieldOverrides;
	
	protected JsonConfig jsonConfig;
	protected JsonSlurper slurper;
	protected Map<String, List<String>> popularFacetMap;
	
	
	public SolrResponseParser() {
		jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		slurper = new JsonSlurper(jsonConfig);
	}
	
	public void setElevateFieldOverrides(
			Map<String, String> elevateFieldOverrides) {
		this.elevateFieldOverrides = elevateFieldOverrides;
	}

	public void setDemoteFieldOverrides(Map<String, String> demoteFieldOverrides) {
		this.demoteFieldOverrides = demoteFieldOverrides;
	}

	/* Enterprise Search end */

	/* public setters and getters */
	public void setIncludeFacetTemplateFacet(boolean includeFacetTemplateFacet) {
		this.includeFacetTemplateFacet = includeFacetTemplateFacet;
	}

	public void setIncludeEDP(boolean includeEDP) {
		this.includeEDP = includeEDP;
	}

	public final void setActiveRules(List<Map<String, String>> activeRules)
			throws SearchException {
		this.activeRules = activeRules;
	}

	public final void setBannerRuleItems(List<BannerRuleItem> list) {
		bannerList = list;
	}

	public final void setElevatedItems(List<ElevateResult> list)
			throws SearchException {
		elevatedList = list;
	}

	public final void setExpiredElevatedEDPs(List<String> list)
			throws SearchException {
		expiredElevatedEDPs = list;
	}

	public final void setForceAddedEDPs(List<String> list)
			throws SearchException {
		forceAddedEDPs = list;
	}

	public final void setSolrUrl(String solrUrl) {
		requestPath = solrUrl;
	}

	public void setSolrQueryParameters(
			HashMap<String, List<NameValuePair>> paramMap)
			throws SearchException {
	}

	public final void setRequestRows(int startRow, int requestedRows)
			throws SearchException {
		this.startRow = startRow;
		this.requestedRows = requestedRows;
	}

	public final void setOriginalKeyword(String originalKeyword)
			throws SearchException {
		this.originalKeyword = originalKeyword;
	}

	public final void setDemotedItems(List<DemoteResult> list)
			throws SearchException {
		demotedList = list;
	}

	public final void setExpiredDemotedEDPs(List<String> list)
			throws SearchException {
		expiredDemotedEDPs = list;
	}

	public final void setFacetSortRule(FacetSort facetSortRule)
			throws SearchException {
		this.facetSortRule = facetSortRule;
	}

	public void setFacetTemplate(String facetTemplate) {
		this.facetTemplate = facetTemplate;
	}

	public final void setRedirectRule(RedirectRule redirectRule)
			throws SearchException {
		this.redirectRule = redirectRule;
	}

	public SpellRule getSpellRule() {
		return spellRule;
	}

	public void setSpellRule(SpellRule spellRule) {
		this.spellRule = spellRule;
	}

	/* Used by both elevate and demote */
	private static void generateEdpList(StringBuilder values,
			Collection<? extends SearchResult> list) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (SearchResult result : list) {
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

	protected void generateExcludeFilterList(StringBuilder filter,
			List<? extends SearchResult> list, int currItem, boolean reverse,
			Map<String, String> overrideMap) {
		boolean edpFlag = false;
		boolean facetFlag = false;
		int i = 1;
		if (CollectionUtils.isNotEmpty(list)) {

			StringBuilder edpValues = new StringBuilder();
			StringBuilder facetValues = new StringBuilder();

			for (SearchResult result : list) {

				if (reverse) {
					if (i++ <= currItem) {
						continue;
					}
				} else if (++i > currItem) {
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
					String strCondition = result.getCondition()
							.getConditionForSolr();
					if (overrideMap != null) {
						strCondition = StringUtils.replaceEach(strCondition,
								overrideMap.keySet().toArray(new String[0]),
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

	protected abstract int getEdps(List<NameValuePair> requestParams,
			List<? extends SearchResult> edpList, int startRow,
			int requestedRows) throws SearchException;

	protected abstract int getFacet(List<NameValuePair> requestParams,
			SearchResult facet) throws SearchException;

	// TODO: merge getElevatedItems and getDemotedItems
	/* For elevate */
	public final int getElevatedItems(List<NameValuePair> requestParams,
			int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		try {
			int currItem = 1;
			List<NameValuePair> currentRequestParams = new ArrayList<NameValuePair>();
			BasicNameValuePair zeroRowNVP = new BasicNameValuePair(
					SolrConstants.SOLR_PARAM_ROWS, "0");
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
					for (int j = i + 1; j < elevatedRecords; j++) {
						ElevateResult e2 = elevatedList.get(j);
						if (e2.getEntity() == MemberTypeEntity.PART_NUMBER) {
							elevateEdps.add(e2);
							i++;
							numEdpAdded++;
						} else {
							break;
						}
					}
					StringBuilder builder = new StringBuilder();
					generateEdpList(builder, elevateEdps);
					currentRequestParams.add(new BasicNameValuePair(
							SolrConstants.SOLR_PARAM_FIELD_QUERY, builder
									.toString()));
				} else {
					String strCondition = elevateResult.getCondition()
							.getConditionForSolr();
					if (elevateFieldOverrides != null) {
						strCondition = StringUtils.replaceEach(
								strCondition,
								elevateFieldOverrides.keySet().toArray(
										new String[0]), elevateFieldOverrides
										.values().toArray(new String[0]));
					}
					currentRequestParams
							.add(new BasicNameValuePair(
									SolrConstants.SOLR_PARAM_FIELD_QUERY,
									strCondition));
				}

				generateExcludeFilterList(elevateFilter, elevatedList,
						currItem++, false, elevateFieldOverrides);
				if (isEdpList) {
					currItem += numEdpAdded - 1; // - 1 to compensate for
													// currItem++ in above line
				}

				if (elevateFilter.length() > 0) {
					currentRequestParams.add(new BasicNameValuePair(
							SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateFilter
									.toString()));
				}

				// check if current elevate result contains any matches
				int numFound = getCount(currentRequestParams);
				if (numFound > 0) { // match found let's get the necessary
									// entries
					if (numFound > startRow) {
						currentRequestParams.remove(zeroRowNVP);
						currentRequestParams.add(new BasicNameValuePair(
								SolrConstants.SOLR_PARAM_START, String
										.valueOf(isEdpList ? 0 : startRow)));
						currentRequestParams.add(new BasicNameValuePair(
								SolrConstants.SOLR_PARAM_ROWS, String
										.valueOf(isEdpList ? numEdpAdded
												: requestedRows)));
						if (isEdpList) {
							requestedRows -= getEdps(currentRequestParams,
									elevateEdps, startRow, requestedRows);
						} else {
							requestedRows -= getFacet(currentRequestParams,
									elevateResult);
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
			throw new SearchException(
					"Error occured while trying to get elevated items", e);
		}
		return addedRecords;
	}

	/* For demote */
	public final int getDemotedItems(List<NameValuePair> requestParams,
			int startRow, int requestedRows) throws SearchException {
		int addedRecords = 0;
		try {
			int currItem = 1;
			List<NameValuePair> currentRequestParams = new ArrayList<NameValuePair>();
			BasicNameValuePair zeroRowNVP = new BasicNameValuePair(
					SolrConstants.SOLR_PARAM_ROWS, "0");
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
					for (int j = i + 1; j < demotedRecords; j++) {
						DemoteResult e2 = demotedList.get(j);
						if (e2.getEntity() == MemberTypeEntity.PART_NUMBER) {
							demotedEdps.add(e2);
							i++;
							numEdpAdded++;
						} else {
							break;
						}
					}
					StringBuilder builder = new StringBuilder();
					generateEdpList(builder, demotedEdps);
					currentRequestParams.add(new BasicNameValuePair(
							SolrConstants.SOLR_PARAM_FIELD_QUERY, builder
									.toString()));
				} else {
					String strCondition = demoteResult.getCondition()
							.getConditionForSolr();
					if (demoteFieldOverrides != null) {
						strCondition = StringUtils.replaceEach(
								strCondition,
								demoteFieldOverrides.keySet().toArray(
										new String[0]), demoteFieldOverrides
										.values().toArray(new String[0]));
					}
					currentRequestParams
							.add(new BasicNameValuePair(
									SolrConstants.SOLR_PARAM_FIELD_QUERY,
									strCondition));
				}

				if (isEdpList) {
					currItem += numEdpAdded - 1; // -1 to compensate for
													// currItem++ in line below
				}
				generateExcludeFilterList(demoteFilter, demotedList,
						currItem++, true, demoteFieldOverrides);
				if (demoteFilter.length() > 0) {
					currentRequestParams.add(new BasicNameValuePair(
							SolrConstants.SOLR_PARAM_FIELD_QUERY, demoteFilter
									.toString()));
				}

				// check if current demote result contains any matches
				int numFound = getCount(currentRequestParams);
				if (numFound > 0) { // match found let's get the necessary
									// entries
					if (numFound > startRow) {
						currentRequestParams.remove(zeroRowNVP);
						currentRequestParams.add(new BasicNameValuePair(
								SolrConstants.SOLR_PARAM_START, String
										.valueOf(isEdpList ? 0 : startRow)));
						currentRequestParams.add(new BasicNameValuePair(
								SolrConstants.SOLR_PARAM_ROWS, String
										.valueOf(isEdpList ? numEdpAdded
												: requestedRows)));
						if (isEdpList) {
							requestedRows -= getEdps(currentRequestParams,
									demotedEdps, startRow, requestedRows);
						} else {
							requestedRows -= getFacet(currentRequestParams,
									demoteResult);
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
			throw new SearchException(
					"Error occured while trying to get demoted items", e);
		}
		return addedRecords;
	}

	protected void logSolrError(HttpPost post, String description, Exception e) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%s: %s", description, post
				.getRequestLine().getUri()));

		if (post != null && post.getEntity() != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				post.getEntity().writeTo(baos);
				builder.append("?").append(baos.toString("UTF-8"));
				logger.error("URL Decoded Solr Request: {}",
						URLDecoder.decode(builder.toString(), "UTF-8"));
			} catch (IOException e1) {
			} catch (UnsupportedOperationException uso) {
			}
		}

		logger.error(builder.toString(), e);
	}
	
	protected static JSONObject locateJSONObject(JSONObject initialJson, String[] traverseList) {
        JSONObject json = initialJson;
        for (String element : traverseList) {
            json = json.getJSONObject(element);
            if (json.isNullObject()) {
                json = null;
                break;
            }
        }
        return json;
    }
	
	protected static JSONObject parseJsonResponse(JsonSlurper slurper, HttpResponse response) {
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
            return (JSONObject) slurper.parseText(jsonText.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

	protected String getSpellCheckRequestPath() {
		return StringUtils.replaceOnce(requestPath, "select",
				"spellCheckCompRH");
	}

	public void setMaxSuggestCount(int maxSuggestCount) {
		this.maxSuggestCount = maxSuggestCount;
	}

	public boolean isCNETImplementation() {
		return isCNETImplementation;
	}

	public void setCNETImplementation(boolean isCNETImplementation) {
		this.isCNETImplementation = isCNETImplementation;
	}

	public void setDefaultSortOrder(String defaultSortOrder) {
		this.defaultSortOrder = defaultSortOrder;
	}

	public int getPopularFacet(List<NameValuePair> requestParams, String[] facetFields, String sortBy, String sortOrder, String maxRow) throws SearchException {
		if(facetSortRule != null && !SortType.DEFAULT_ORDER.equals(facetSortRule.getSortType()))
			return 0;
		if(popularFacetMap == null) {
			popularFacetMap = new HashMap<String, List<String>>();
		}
		int facetCount = 0;
		HttpClient client = null;
		HttpPost post = null;
		InputStream in = null;
		HttpResponse solrResponse = null;
		List<NameValuePair> originalRequestParams = new ArrayList<NameValuePair>(requestParams);
		// Remove unused parameters for this request.
		for (NameValuePair param : originalRequestParams) {
			if (StringUtils.equals(SolrConstants.SOLR_PARAM_SPELLCHECK, param.getName())
                    || StringUtils.equals(SolrConstants.TAG_FACET, param.getName())
                    || StringUtils.equals(SolrConstants.TAG_FACET_MINCOUNT, param.getName())
                    || StringUtils.equals(SolrConstants.TAG_FACET_LIMIT, param.getName())
                    || StringUtils.equals(SolrConstants.TAG_FACET_FIELD, param.getName())
                    || StringUtils.equals(SolrConstants.SOLR_PARAM_ROWS, param.getName())
                    || StringUtils.equals(SolrConstants.SOLR_PARAM_SORT, param.getName())
                    || StringUtils.equals(SolrConstants.SOLR_PARAM_START, param.getName())
                    || StringUtils.equals(SolrConstants.SOLR_PARAM_WRITER_TYPE, param.getName())) {
				requestParams.remove(param);
            }
		}
		
		requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "*:* AND NOT "+ sortBy +":0"));
		requestParams.add(new BasicNameValuePair("group", "true"));
		requestParams.add(new BasicNameValuePair("group.sort", sortBy + " " + (sortOrder != null ? sortOrder : "desc")));
		requestParams.add(new BasicNameValuePair("group.limit", "0"));
		requestParams.add(new BasicNameValuePair("wt", "json"));
		requestParams.add(new BasicNameValuePair("sort", sortBy + " " + (sortOrder != null ? sortOrder : "desc")));
		requestParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, maxRow));
		for(String field : facetFields) {
			requestParams.add(new BasicNameValuePair("group.field", field));
		}
		
		try {
			client = new DefaultHttpClient();
			post = new HttpPost(requestPath);
			post.setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
			post.addHeader("Connection", "close");
			if (logger.isDebugEnabled()) {
				logger.debug("URL: " + post.getURI());
				logger.debug("Parameter: " + requestParams);
				logger.info("Facet sorting query: {}", post.toString());
			}
			solrResponse = client.execute(post);
			
			groupedFacetJson = parseJsonResponse(slurper, solrResponse);
						
			JSONObject groups = groupedFacetJson.getJSONObject("grouped");
			
			for(String field : facetFields) {
				if(!groups.isNullObject() && !groups.getJSONObject(field).isNullObject()) {
					JSONArray fieldGroups = groups.getJSONObject(field).getJSONArray("groups");

					if(fieldGroups != null && fieldGroups.size() > 0) {
						List<String> values = new ArrayList<String>();

						for(int i=0; i < fieldGroups.size(); i++) {
							String value = fieldGroups.getJSONObject(i).getString("groupValue");

							if(isCNETImplementation())
								value = value.split("\\|")[0].trim();

							if(!values.contains(value))
								values.add(value);
						}
						facetCount += values.size();
						popularFacetMap.put(field, values);
					}
				}
			}
			
			
		} catch (Exception e) {
			String error = "Error occured while trying to get facet sorting by popularity";
			logSolrError(post, error, e);
			throw new SearchException(error, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
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
		return facetCount;
	}

}
