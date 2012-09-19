package com.search.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.SearchResult;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.manager.utility.SearchLogger;

public class SearchServlet extends HttpServlet {

	@Autowired DaoService daoService;
	@Autowired DaoCacheService daoCacheService;

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(SearchServlet.class);

	private ConfigManager configManager;
	
	// these fields should not contain multiple entries
	private final static String[] uniqueFields = {
			SolrConstants.SOLR_PARAM_ROWS,
			SolrConstants.SOLR_PARAM_KEYWORD,
			SolrConstants.SOLR_PARAM_WRITER_TYPE ,
			SolrConstants.SOLR_PARAM_START
			};

	public final ExecutorService execService = Executors.newCachedThreadPool();

	public void setDaoCacheService(DaoCacheService daoCacheService) {
		this.daoCacheService = daoCacheService;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		super.init(config);
		configManager = ConfigManager.getInstance();
	}

	private static boolean addNameValuePairToMap(HashMap<String, List<NameValuePair>> map, String paramName, NameValuePair pair) {
		boolean added = true;
		if (ArrayUtils.contains(uniqueFields, paramName) && map.containsKey(paramName)) {
			logger.warn(String.format("Request contained multiple declarations for parameter %1$s. Discarding subsequent declarations.", paramName));
			added = false;
		}
		else {
			if (!map.containsKey(paramName)) {
				map.put(paramName, new ArrayList<NameValuePair>());
			}
			map.get(paramName).add(pair);
		}
		return added;
	}

	public static String getValueFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? "" : list.get(0).getValue();
	}

	private static StringBuilder getAllValuesFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		StringBuilder value = new StringBuilder();
		if (list!=null) {
			boolean firstFlag = true;
			for (NameValuePair nameValuePair : list) {
				if (firstFlag) {
					firstFlag = false;
				} else {
					value.append(" AND ");
				}
				value.append(nameValuePair.getValue());
			}
		}
		return value;
	}

	private static StringBuilder getAllForceAddFq(String[] faFqs, String existingFq) {
		StringBuilder value = new StringBuilder();
		String andFq = "";
		if (StringUtils.isNotBlank(existingFq)) {
			andFq = " AND " + existingFq;
		}
		for (int i = 0; i < faFqs.length; i++) {
			if (i > 0) {
				value.append(" OR ");
			}
			value.append("(").append(faFqs[i]).append(andFq).append(")");
		}
		return value;
	}

	private static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? null : list.get(0);
	}

	private static void removeNameValuePairFromList(List<NameValuePair> list, String paramterName) {
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			NameValuePair nameValuePair = (NameValuePair) iterator.next();
			if (nameValuePair.getName().equals(paramterName)) {
				iterator.remove();
			}
			
		}
	}
	
	private static boolean generateFilterList(StringBuilder edpValues, StringBuilder facetValues, StringBuilder allValues, Collection<? extends SearchResult> list) {
		boolean withFacetFlag = false;
		boolean edpFlag = false;
		boolean facetFlag = false;
		if (!(list == null || list.isEmpty())) {
			for (SearchResult result: list) {
				if (result instanceof ElevateResult && ((ElevateResult)result).isForceAdd()) {
					if (result.getEntity() == MemberTypeEntity.FACET) {
						withFacetFlag = true;
					}
					continue;
				}
				if (result.getEntity().equals(MemberTypeEntity.PART_NUMBER)) {
					if (!edpFlag) {
						edpValues.append("EDP:(");
						edpFlag = true;
					}
					edpValues.append(" ").append(result.getEdp());
				} else {
					if (!facetFlag) {
						facetValues.insert(0, "(");
						facetFlag = true;
					} else {
						facetValues.append(" OR ");
					}
					facetValues.append("(").append(result.getCondition().getConditionForSolr()).append(")");
				}
			}
			
			if (edpFlag || facetFlag) {
				allValues.append("(");
				if (edpFlag) {
					edpValues.append(")");
					allValues.append(edpValues);
				}
				if (facetFlag) {
					withFacetFlag = true;
					if (edpFlag) {
						allValues.append(" OR ");
					}
					facetValues.append(")");
					allValues.append(facetValues);					
				}
				allValues.append(")");
			}
		}
		return withFacetFlag;
	}
	
	private static Map<String,String> generateActiveRule(String type, String id, String name, boolean active) {
		Map<String,String> activeRule = new HashMap<String,String>();
		activeRule.put(SolrConstants.TAG_RULE_TYPE, type);
		activeRule.put(SolrConstants.TAG_RULE_ID, id);
		activeRule.put(SolrConstants.TAG_RULE_NAME, name);
		activeRule.put(SolrConstants.TAG_RULE_ACTIVE, String.valueOf(active));			
		return activeRule;
	}
	
	private void addNameValuePairToList(List<NameValuePair> list, NameValuePair nvp) {
		if (nvp != null) {
			list.add(nvp);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: 
		// remove the exclude list from the result header
		// fix if EDP not part of fl

		// to track asynchronous task completion
		ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(execService);
		int tasks = 0;

		try {
			Long start = new Date().getTime();

			NameValuePair nvp;

			final SolrResponseParser solrHelper;

			// get the server name, solr path, core name and do mapping for the store name to use for the search
			Pattern pathPattern = Pattern.compile("http://(.*):.*/(.*)/(.*)/select.*");
			String requestPath = "http:/" + request.getPathInfo();

			if (StringUtils.isEmpty(requestPath)) {
				response.sendError(400, "Invalid request");
				return;
			}
			Matcher matcher = pathPattern.matcher(requestPath);
			if (!matcher.matches()) {
				response.sendError(400, "Invalid request");
				return;
			}

			String serverName = matcher.group(1);
			String solr = matcher.group(2);
			String coreName = matcher.group(3);
			String storeName = configManager.getStoreName(coreName);

			if (logger.isDebugEnabled()) {
				logger.debug("Server name: " + serverName);
				logger.debug("Solr path: " + solr);
				logger.debug("Core name: " + coreName);
				logger.debug("store name: " + storeName);
			}

			// parse the parameters, construct POST form
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Set<String> paramNames = request.getParameterMap().keySet();

			HashMap<String, List<NameValuePair>> paramMap = new HashMap<String, List<NameValuePair>>();
			
			// workaround for spellchecker
			nvp = new BasicNameValuePair("echoParams", "explicit");
			if (addNameValuePairToMap(paramMap, "echoParams", nvp)) {
				nameValuePairs.add(nvp);
			}

			StringBuilder fqBuffer = new StringBuilder();
			for (String paramName: paramNames) {
				for (String paramValue: request.getParameterValues(paramName)) {
					
					if(paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_KEYWORD)) {
						
						String origKeyword = paramValue;
						if (origKeyword == null) {
							origKeyword = "";
						}
						
						paramValue = StringUtils.trimToEmpty(paramValue).replaceAll("[\\p{Cntrl}]", "");
						
						String convertedKeyword = paramValue;
						logger.info(String.format("CLEANUP %s keyword: %s[%s] %s[%s]", storeName,
								origKeyword, HexUtils.convert(origKeyword.getBytes()),
								convertedKeyword, HexUtils.convert(convertedKeyword.getBytes())));
					}
					
					nvp = new BasicNameValuePair(paramName, paramValue);
					if (addNameValuePairToMap(paramMap, paramName, nvp)) {
						nameValuePairs.add(nvp);
					}
				}
			}
			
			boolean fromSearchGui = "true".equalsIgnoreCase(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_GUI));

			if (fromSearchGui && StringUtils.isNotBlank(UtilityService.getStoreFacetTemplate())) {
				nvp = new BasicNameValuePair("facet.field", UtilityService.getStoreFacetTemplate());
				if (addNameValuePairToMap(paramMap, "facet.field", nvp)) {
					nameValuePairs.add(nvp);
				}
			}

			// default parameters for the core
			for (NameValuePair pair: ConfigManager.getInstance().getDefaultSolrParameters(coreName)) {
				// TODO: FIX This will distort the results if there is no redirect filter rule.
				if (addNameValuePairToMap(paramMap, pair.getName(), pair)) {
					nameValuePairs.add(pair);
				}
			}
			
			// grab the keyword
			String keyword = StringUtils.trimToEmpty(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD));
			
			String originalKeyword = keyword;
			if (StringUtils.isNotBlank(keyword)) {
				// workaround for search compare
				if (keyword.startsWith("DPNo:")) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
					nvp = new BasicNameValuePair("fq", keyword);
					if (addNameValuePairToMap(paramMap, "fq", nvp)) {
						nameValuePairs.add(nvp);
					}
					keyword = "";
				}
			}
			boolean keywordPresent = !StringUtils.isEmpty(keyword);
			boolean disableElevate    = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_ELEVATE) != null;
			boolean disableExclude    = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_EXCLUDE) != null;
			boolean disableDemote    = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_DEMOTE) != null;
			boolean disableRedirect   = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_REDIRECT) != null;
			boolean disableRedirectIdPresent = StringUtils.isNotBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_REDIRECT));
			String  disableRedirectId = disableRedirect ? getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_REDIRECT): "";
			boolean disableRelevancy  = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_RELEVANCY) != null;
			List<Map<String,String>> activeRules = new ArrayList<Map<String, String>>();
			
			StoreKeyword sk = new StoreKeyword(coreName, keyword);

			// redirect 
			RedirectRule redirect = null;
			NameValuePair redirectFqNvp = null; 
			try {
				List<String> keywordHistory = new ArrayList<String>();
				while (true) { // look for change keyword
					if (StringUtils.isBlank(keyword)) {
						break;
					}
					keywordHistory.add(StringUtils.lowerCase(keyword));
					redirect = (fromSearchGui) ? daoService.getRedirectRule(new RedirectRule(sk.getStoreId(), sk.getKeywordId()))
							: daoCacheService.getRedirectRule(sk);
					if (redirect == null) {
						break;
					}
					else {
						
						boolean stop = disableRedirect && (!disableRedirectIdPresent || StringUtils.equals(disableRedirectId, redirect.getRuleId()));
						
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_REDIRECT, redirect.getRuleId(), redirect.getRuleName(), !stop));				
						
						if (stop) {
							redirect = null;
							break;
						}
						
						if (!redirect.isRedirectChangeKeyword()) {
							break;
						}
						logger.info("Applying redirect rule " + redirect.getRuleName() + " with id " + redirect.getRuleId());
						keyword = StringUtils.trimToEmpty(redirect.getChangeKeyword());
						sk.setKeyword(new Keyword(keyword));
						// remove the original keyword
						nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
						paramMap.remove(SolrConstants.SOLR_PARAM_KEYWORD);
						if (StringUtils.isEmpty(keyword)) {
							sk.setKeyword(null);
							keywordPresent = false;
							break;
						}
						else {
							// set the new keyword
							nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, redirect.getChangeKeyword());
							if (addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD, nvp)) {
								nameValuePairs.add(nvp);
							}
							if (keywordHistory.contains(StringUtils.lowerCase(keyword))) {
								logger.warn("Loop in change keywords detected. Aborting search for new keyword. Reverting to original keyword.");
								redirect = null;
								keyword = originalKeyword;
								sk.setKeyword(new Keyword(keyword));
								nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
								paramMap.remove(SolrConstants.SOLR_PARAM_KEYWORD);
								nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, keyword);
								if (addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD, nvp)) {
									nameValuePairs.add(nvp);
								}
								break;
							}
						}
					}
				}
				
				if (redirect != null && !redirect.isRedirectChangeKeyword()) {
					logger.info("Applying redirect rule " + redirect.getRuleName() + " with id " + redirect.getRuleId());
					if (redirect.isRedirectToPage()) {
						// TODO: fix redirect to page implementation
						nvp = new BasicNameValuePair(SolrConstants.REDIRECT_URL, redirect.getRedirectToPage());
						nameValuePairs.add(nvp);					
					}
					else if (redirect.isRedirectFilter()) {
						StringBuilder builder = new StringBuilder();
						for (String condition: redirect.getConditions()) {
							if (StringUtils.isNotEmpty(condition)) {
								RedirectRuleCondition rr = new RedirectRuleCondition(condition);
								rr.setStoreId(coreName);
								builder.append("(").append(rr.getConditionForSolr()).append(") OR ");
							}							
						}
						if (builder.length() > 0) {
							builder.delete(builder.length() - 4, builder.length());
						}
						redirectFqNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, builder.toString());
						nameValuePairs.add(redirectFqNvp);
						if (BooleanUtils.isNotTrue(redirect.getIncludeKeyword())) {
							nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
							paramMap.remove(SolrConstants.SOLR_PARAM_KEYWORD);							
						}
					}					
				}
			} catch (Exception e) {
				logger.error("Failed to get redirect for keyword: " + originalKeyword, e);
			}
			
			// set relevancy filters if any was specified
			String relevancyId = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_RELEVANCY_ID);
			Relevancy relevancy = null;
			if (!fromSearchGui) {
				relevancy = keywordPresent ? daoCacheService.getRelevancyRule(sk) : daoCacheService.getDefaultRelevancyRule(new Store(coreName));
				if (relevancy != null) {
					logger.debug("Applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());					
				}
				else {
					logger.error("Unable to find default relevancy!");
				}
			}
			else {
				if (StringUtils.isNotBlank(relevancyId)) {
					relevancy = new Relevancy();
					relevancy.setRelevancyId(relevancyId);
				}
				else if (keywordPresent) {
					// get relevancy mapped to keyword
					relevancy = new Relevancy("", "");
					relevancy.setStore(new Store(coreName));
					RecordSet<RelevancyKeyword>relevancyKeywords = daoService.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
							new RelevancyKeyword(sk.getKeyword(), relevancy), new Date(), new Date(), 0, 0),
							MatchType.LIKE_NAME, ExactMatch.MATCH);
					if (relevancyKeywords.getTotalSize() > 0) {
						relevancy.setRelevancyId(relevancyKeywords.getList().get(0).getRelevancy().getRelevancyId());						
					}
					else {
						// apply default relevancy
						relevancy.setRelevancyId(coreName + "_" + "default");
					}
				}
				else {
					relevancy = new Relevancy();
					relevancy.setRelevancyId(coreName + "_" + "default");
				}
				
				if (relevancy != null) {
					// load relevancy details
					relevancy = daoService.getRelevancyDetails(relevancy);
					if (relevancy != null) {
						if (!disableRelevancy) {
							logger.debug("Applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());							
						}
						else {
							logger.debug("Relevancy disabled. Not applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());							
						}
					}
					else {
						logger.error("Unable to find default relevancy!");
					}
				}
			}
			
			
			if (relevancy != null) {
				activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_RELEVANCY, relevancy.getRelevancyId(), relevancy.getRelevancyName(), !disableRelevancy));				
			}
			
			NameValuePair dtNvp = new BasicNameValuePair("defType", "dismax");
			NameValuePair forceAddFqNVP = null;
			NameValuePair origKeywordNVP = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD);
			NameValuePair forceAddKeywordNVP = null;
			
			if (!disableRelevancy && relevancy != null) {
				nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
				nameValuePairs.add(dtNvp);
				Map<String, String> parameters = relevancy.getParameters();
				for (String paramName: parameters.keySet()) {
					String paramValue = parameters.get(paramName);
					if (StringUtils.isNotEmpty(paramValue)) {
						logger.debug("adding " + paramName + ": " + paramValue);
						nvp = new BasicNameValuePair(paramName, paramValue);
						if (addNameValuePairToMap(paramMap, paramName, nvp)) {
							nameValuePairs.add(nvp);
						}						
					}
				}
			}
			else {
				// remove qt parameter
				if (StringUtils.isBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE))) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
					nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_QUERY_TYPE, configManager.getParameterByCore(coreName, SolrConstants.SOLR_PARAM_QUERY_TYPE)));
				}
			}
			
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(configManager.getStoreParameter(coreName, "sort"));
				logger.debug(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
				logger.debug(">>>>>>>>>>>>>>" + configManager.getStoreParameter(coreName, "sort") + ">>>>>>>>>>>>>>>" + getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
			}
			
			List<ElevateResult> elevatedList = null;
			List<ElevateResult> forceAddList = new ArrayList<ElevateResult>();
			List<String> expiredElevatedList = new ArrayList<String>();
			List<DemoteResult> demoteList = null;
			List<String> expiredDemotedList = new ArrayList<String>();
			List<ExcludeResult> excludeList = null;
			boolean bestMatchFlag = configManager.getStoreParameter(coreName, "sort").equals(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));

			if (keywordPresent) {
				if (fromSearchGui) {
					if (daoService.getKeyword(sk.getStoreId(), sk.getKeywordId()) != null) {
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_ELEVATE, keyword, keyword, !disableElevate));
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_EXCLUDE, keyword, keyword, !disableExclude));
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_DEMOTE, keyword, keyword, !disableDemote));
					}
					if (!disableElevate) {
						if (bestMatchFlag) {
							ElevateResult elevateFilter = new ElevateResult();
							elevateFilter.setStoreKeyword(sk);
							SearchCriteria<ElevateResult> elevateCriteria = new SearchCriteria<ElevateResult>(elevateFilter,new Date(),null,0,0);
							SearchCriteria<ElevateResult> expiredElevateCriteria = new SearchCriteria<ElevateResult>(elevateFilter,null,DateAndTimeUtils.getDateYesterday(),0,0);
			
							if (keywordPresent) {
								elevatedList = daoService.getElevateResultList(elevateCriteria).getList();
								List<ElevateResult> expiredList = daoService.getElevateResultList(expiredElevateCriteria).getList();
								if (logger.isDebugEnabled()) {
									logger.debug("Expired Elevated List: ");
								}
								for (ElevateResult expired: expiredList) {
									if (logger.isDebugEnabled()) {
										logger.debug("\t" + expired.getEdp());
									}
									if (MemberTypeEntity.PART_NUMBER.equals(expired.getElevateEntity())) {
										expiredElevatedList.add(expired.getEdp());
									}
								}
							}
						}
						ElevateResult forceAddFilter = new ElevateResult();
						forceAddFilter.setStoreKeyword(sk);
						forceAddFilter.setForceAdd(true);
						SearchCriteria<ElevateResult> forceAddCriteria = new SearchCriteria<ElevateResult>(forceAddFilter,new Date(),null,0,0);
						forceAddList = daoService.getElevateResultList(forceAddCriteria).getList();
					}
					
					if (!disableDemote) {
						if (bestMatchFlag) {
							DemoteResult demoteFilter = new DemoteResult();
							demoteFilter.setStoreKeyword(sk);
							SearchCriteria<DemoteResult> demoteCriteria = new SearchCriteria<DemoteResult>(demoteFilter,new Date(),null,0,0);
							SearchCriteria<DemoteResult> expiredDemoteCriteria = new SearchCriteria<DemoteResult>(demoteFilter,null,DateAndTimeUtils.getDateYesterday(),0,0);
			
							if (keywordPresent) {
								demoteList = daoService.getDemoteResultList(demoteCriteria).getList();
								List<DemoteResult> expiredList = daoService.getDemoteResultList(expiredDemoteCriteria).getList();
								if (logger.isDebugEnabled()) {
									logger.debug("Expired Demoted List: ");
								}
								for (DemoteResult expired: expiredList) {
									if (logger.isDebugEnabled()) {
										logger.debug("\t" + expired.getEdp());
									}
									if (MemberTypeEntity.PART_NUMBER.equals(expired.getDemoteEntity())) {
										expiredDemotedList.add(expired.getEdp());
									}
								}
							}
						}
					}
					
					if (!disableExclude) {
						ExcludeResult excludeFilter  = new ExcludeResult();
						excludeFilter.setStoreKeyword(sk);
						SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(excludeFilter,new Date(),null,0,0);
						excludeList = daoService.getExcludeResultList(excludeCriteria).getList();						
					}
				}
				else {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_ELEVATE, keyword, keyword, !disableElevate));
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_EXCLUDE, keyword, keyword, !disableExclude));
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_DEMOTE,  keyword, keyword, !disableDemote));				
					if (keywordPresent) {
						if (!disableElevate) {
							elevatedList = daoCacheService.getElevateRules(sk);			
							for (ElevateResult elevateResult : elevatedList) {
								if (elevateResult.isForceAdd()) {
									forceAddList.add(elevateResult);
								}
							}
							if (!bestMatchFlag) {
								elevatedList = new ArrayList<ElevateResult>();
							}	
						}
						if (!disableDemote && bestMatchFlag) {
							demoteList = daoCacheService.getDemoteRules(sk);			
						}
						if (!disableExclude) {
							excludeList = daoCacheService.getExcludeRules(sk);						
						}
					}
				}			
			}
			
			if (elevatedList == null) {
				elevatedList = new ArrayList<ElevateResult>();
			}

			/* First Request */
			// get expected resultformat
			String tmp = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_WRITER_TYPE);
			if (SolrConstants.SOLR_PARAM_VALUE_JSON.equalsIgnoreCase(tmp)) {
				solrHelper = new SolrJsonResponseParser();
			}
			else if (StringUtils.isEmpty(tmp) || SolrConstants.SOLR_PARAM_VALUE_XML.equalsIgnoreCase(tmp)
					|| SolrConstants.SOLR_PARAM_VALUE_STANDARD.equalsIgnoreCase(tmp)) {
				solrHelper = new SolrXmlResponseParser();
			}
			else { // unsupported writer type
				response.sendError(500);
				return;
			}
			// set Solr URL
			solrHelper.setSolrUrl(requestPath);
			solrHelper.setSolrQueryParameters(paramMap);
			solrHelper.setElevatedItems(elevatedList);
			solrHelper.setExpiredElevatedEDPs(expiredElevatedList);
			solrHelper.setForceAddedList(forceAddList);
			solrHelper.setDemotedItems(demoteList);
			solrHelper.setExpiredDemotedEDPs(expiredDemotedList);
			
			if (!StringUtils.equalsIgnoreCase(keyword, originalKeyword)) {
				solrHelper.setChangeKeyword(keyword);				
			}

			// remove json.wrf parameter as this is not a JSON standard
			nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_JSON_WRAPPER_FUNCTION));

			// get start row
			int startRow = 0;
			tmp = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_START);
			if (!StringUtils.isEmpty(tmp)) {
				startRow = Integer.valueOf(tmp);
				nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_START));
				paramMap.remove(SolrConstants.SOLR_PARAM_START);
			}
			nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, "0");
			addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_START, nvp);
			nameValuePairs.add(nvp);

			// get number of requested rows
			int requestedRows = 25;
			tmp = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_ROWS);
			if (!StringUtils.isEmpty(tmp)) {
				requestedRows = Integer.valueOf(tmp);
			}
			nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_ROWS));
			paramMap.remove(SolrConstants.SOLR_PARAM_ROWS);
			solrHelper.setRequestRows(startRow, requestedRows);

			// set number of requested rows to 0
			nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, "0");
			addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_ROWS, nvp);
			nameValuePairs.add(nvp);

			// collate exclude list
			if (excludeList != null && !excludeList.isEmpty()) {
				StringBuilder excludeFilters = new StringBuilder();
				generateFilterList(new StringBuilder(), new StringBuilder(), excludeFilters, excludeList);
				if (excludeFilters.length() > 0) {
					excludeFilters.insert(0, "-");
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeFilters.toString());
					nameValuePairs.add(nvp);
				}
			}
			
			// collate elevate list
			StringBuilder elevateValues = new StringBuilder();
			StringBuilder elevateFacetValues = new StringBuilder();
			StringBuilder elevateFilters = new StringBuilder();
			boolean withFacetFlag = generateFilterList(elevateValues, elevateFacetValues, elevateFilters, elevatedList);
			
			// collate demoted list
			StringBuilder demoteValues = new StringBuilder();
			StringBuilder demoteFacetValues = new StringBuilder();
			StringBuilder demoteFilters = new StringBuilder();
			generateFilterList(demoteValues, demoteFacetValues, demoteFilters, demoteList);
			boolean withDemoteFacet = demoteFacetValues.length() > 0;

			Integer numFound = 0;
			Integer numForceAddFound = 0;
			Integer numElevateFound = 0;
			Integer numNormalFound = 0;
			Integer numDemoteFound = 0;

			// send solr request

			// TODO: workaround for spellchecker
			if (StringUtils.isNotBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD))) {
				requestPath = requestPath.replaceFirst("select", "spellCheckCompRH");
				solrHelper.setSolrUrl(requestPath);
			}
			
			solrHelper.setActiveRules(activeRules);


			// create force add filters
			if (forceAddList.size() > 0) {
				StringBuilder keywordBuffer = new StringBuilder();
				String[] faFqs = new String[forceAddList.size()];
				String existingFq= getAllValuesFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_FIELD_QUERY).toString();
				int ctr = 0;
				for (ElevateResult e : forceAddList) {
					if (e.getElevateEntity() == MemberTypeEntity.PART_NUMBER) {
						faFqs[ctr++] = "EDP:" + e.getEdp();
					} else {
						faFqs[ctr++] = e.getCondition().getConditionForSolr();
					}
				}
				
				NameValuePair qfNvp = getNameValuePairFromMap(paramMap, "qf");
				if (redirect !=null && redirect.isRedirectFilter()) {
					forceAddFqNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, "(" + redirectFqNvp.getValue() + " AND " + existingFq + ") OR (" + getAllForceAddFq(faFqs, existingFq) + ")");
					if (BooleanUtils.isTrue(redirect.getIncludeKeyword())) {
						keywordBuffer.append("(_query_:\"{!dismax qf='").append(qfNvp.getValue()).append("' v='").append(origKeywordNVP != null?origKeywordNVP.getValue():originalKeyword).append("'}\") ");
					}
					if (keywordBuffer.length() > 0) {
						keywordBuffer.append(" OR ");
					}
					keywordBuffer.append(redirectFqNvp.getValue());
				} else {
					forceAddFqNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, existingFq + " OR " + getAllForceAddFq(faFqs,existingFq).toString());
					keywordBuffer.append("(_query_:\"{!dismax qf='").append(qfNvp.getValue()).append("' v='").append(origKeywordNVP != null?origKeywordNVP.getValue():originalKeyword).append("'}\")");
				}
				keywordBuffer.append(" OR ").append(getAllForceAddFq(faFqs,existingFq));

				forceAddKeywordNVP = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, keywordBuffer.toString());
			}

			Future<Integer> getTemplateCount = null;
			Future<Integer> getForceAddTemplateCount = null;
			Future<Integer> getElevatedCount = null;
			Future<Integer> getDemotedCount = null;

			// TASK 1A - get total number of items
			final ArrayList<NameValuePair> getTemplateCountParams = new ArrayList<NameValuePair>(nameValuePairs);
			if (forceAddList.size() > 0) {
				getTemplateCountParams.remove(dtNvp);
				removeNameValuePairFromList(getTemplateCountParams, SolrConstants.SOLR_PARAM_FIELD_QUERY);
				addNameValuePairToList(getTemplateCountParams, forceAddFqNVP);
				addNameValuePairToList(getTemplateCountParams, forceAddKeywordNVP);
				getTemplateCountParams.remove(origKeywordNVP);
				if (redirect !=null && redirect.isRedirectFilter()) {
					getTemplateCountParams.remove(redirectFqNvp);
				}				
			}
			
			getTemplateCount = completionService.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return solrHelper.getTemplateCounts(getTemplateCountParams);
				}
			});
			tasks++;

			// TASK 1B - get count of force added items (should be merged with task 1A) 
			// TODO: optional remove the spellcheck parameters for succeeding requests
			nameValuePairs.remove(getNameValuePairFromMap(paramMap,"spellcheck"));
			nameValuePairs.remove(getNameValuePairFromMap(paramMap,"facet"));

			// exclude demoted items
			if (demoteFilters.length() > 0) {
				demoteFilters.insert(0, "-");
			}
			NameValuePair excludeDemoteNameValuePair = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, demoteFilters.toString()); 
			if (demoteFilters.length() > 0) {
				demoteFilters.deleteCharAt(0);
			}

			if (bestMatchFlag) {
				nameValuePairs.add(excludeDemoteNameValuePair);
				if (forceAddList.size() > 0) {
					final ArrayList<NameValuePair> getForceAddTemplateCountParams = new ArrayList<NameValuePair>(nameValuePairs);
					getForceAddTemplateCountParams.remove(origKeywordNVP);
					if (redirect != null && redirect.isRedirectFilter()) {
						getForceAddTemplateCountParams.remove(redirectFqNvp);
					}
					getForceAddTemplateCount = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getForceAddTemplateCounts(getForceAddTemplateCountParams);
						}
					});
					tasks++;
				}
				
				if (requestedRows != 0 && (elevateFilters.length() > 0)) {
					/* Second Request */
					// set filter to exclude & include elevated list only
					// TASK 1C - get count of elevated items (sans excluded items)
					if (elevateFilters.length() > 0) {
						final ArrayList<NameValuePair> getElevatedCountParams = new ArrayList<NameValuePair>(nameValuePairs);
						getElevatedCountParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateFilters.toString()));
						getElevatedCount = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getCount(getElevatedCountParams);
							}
						});
						tasks++;
					}
				}

				if (requestedRows != 0 && (demoteFilters.length() > 0)) {
					// TASK 1D - get count of demoted items
					if (demoteFilters.length() > 0) {
						final ArrayList<NameValuePair> getDemotedCountParams = new ArrayList<NameValuePair>(nameValuePairs);
						getDemotedCountParams.remove(excludeDemoteNameValuePair);
						getDemotedCountParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, demoteFilters.toString()));
						if (forceAddList.size() > 0) {
							// demote the force added items
							getDemotedCountParams.remove(dtNvp);
							removeNameValuePairFromList(getDemotedCountParams, SolrConstants.SOLR_PARAM_FIELD_QUERY);
							if (redirect != null && redirect.isRedirectFilter()) {
								getDemotedCountParams.remove(redirectFqNvp);
							}
							addNameValuePairToList(getDemotedCountParams, forceAddFqNVP);
							addNameValuePairToList(getDemotedCountParams, forceAddKeywordNVP);
							getDemotedCountParams.remove(origKeywordNVP);
						}
						
						getDemotedCount = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getCount(getDemotedCountParams);
							}
						});
						tasks++;
					}
				}
			}
			
			while (tasks > 0) {
				Future<Integer> completed = completionService.take();
				if (completed.equals(getTemplateCount)) {
					numFound = completed.get();
					logger.debug("Results found: " + numFound);
				}
				else if (getForceAddTemplateCount != null && completed.equals(getForceAddTemplateCount)) {
					numForceAddFound = completed.get();
					logger.debug("Results found: " + numForceAddFound);
				}
				else if (getElevatedCount!= null && completed.equals(getElevatedCount)) {
					numElevateFound += completed.get();
					logger.debug("Elevate result size: " + numElevateFound);
				}
				else if (getDemotedCount != null && completed.equals(getDemotedCount)) {
					numDemoteFound = completed.get();
					logger.debug("Demote result size: " + numDemoteFound);
				}
				tasks--;
			}

			numElevateFound += numForceAddFound;
			numNormalFound = numFound - numElevateFound - numDemoteFound;
			
			// cleanup request parameters, remove start and row
			nameValuePairs.remove(paramMap.get(SolrConstants.SOLR_PARAM_START).get(0));
			nameValuePairs.remove(paramMap.get(SolrConstants.SOLR_PARAM_ROWS).get(0));

			// TODO FIX: numFound should contain force added items
			if (requestedRows != 0 && (numFound + numElevateFound) != 0) {

				Future<Integer> getElevatedItems = null;
				Future<Integer> getNormalItems = null;
				Future<Integer> getDemotedItems = null;

				// TASK 2A - get elevated items
				// check if elevateList is to be included in this batch
				if (bestMatchFlag && numElevateFound > startRow) {
					logger.debug("total number of elevated found including force added items:" + numElevateFound);
					
					final ArrayList<NameValuePair> getElevatedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					getElevatedItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(startRow + requestedRows)));
					getElevatedItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(0)));

					if (withFacetFlag) {
						if (forceAddList.size() > 0 && redirect !=null && redirect.isRedirectFilter()) {
							getElevatedItemsParams.remove(dtNvp);
							removeNameValuePairFromList(getElevatedItemsParams, SolrConstants.SOLR_PARAM_FIELD_QUERY);
							addNameValuePairToList(getElevatedItemsParams, forceAddFqNVP);
							addNameValuePairToList(getElevatedItemsParams, forceAddKeywordNVP);
							getElevatedItemsParams.remove(origKeywordNVP);
							getElevatedItemsParams.remove(redirectFqNvp);
						}						
						final int numElevate = numElevateFound - startRow > requestedRows? requestedRows:numElevateFound - startRow;
						getElevatedItems = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getElevatedItems(getElevatedItemsParams, numElevate);
							}
						});
					} else {
						getElevatedItems = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getElevatedItems(getElevatedItemsParams);
							}
						});
					}
					tasks++;
					requestedRows -= (numElevateFound - startRow);
				}

				// TASK 2B - get normal (non-elevated and non-demoted) items
				// update start row
				if (numElevateFound > 0) {
					startRow -= numElevateFound;
					if (startRow < 0) {
						startRow = 0;
					}
				}
				
				logger.debug("[Normal items]start row:" + startRow);
				logger.debug("[Normal items]requested rows:" + requestedRows);
				if (requestedRows > 0) {
					/* Third Request */
					// set filter to not include elevate and exclude list
					// set rows parameter to original number requested minus results returned in second request
					// grab all the doc nodes <doc>

					final ArrayList<NameValuePair> getNormalItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					if (elevateFilters.length() > 0) {
						elevateFilters.insert(0, "-");
						getNormalItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateFilters.toString()));
					}
					getNormalItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(startRow + requestedRows)));
					getNormalItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(startRow)));
					
					// if not best match, insert force added items
					if (!bestMatchFlag && forceAddList.size() > 0) {
						getNormalItemsParams.remove(dtNvp);
						removeNameValuePairFromList(getNormalItemsParams, SolrConstants.SOLR_PARAM_FIELD_QUERY);
						if (redirect != null && redirect.isRedirectFilter()) {
							getNormalItemsParams.remove(redirectFqNvp);
						}
						addNameValuePairToList(getNormalItemsParams, forceAddFqNVP);
						addNameValuePairToList(getNormalItemsParams, forceAddKeywordNVP);
						getNormalItemsParams.remove(origKeywordNVP);
					}

					getNormalItems = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getNonElevatedItems(getNormalItemsParams);
						}
					});
					tasks++;
					
					requestedRows -= (numNormalFound - startRow);
				}

				// TASK 2C - get demoted items
				if (numFound > 0) {
					startRow -= numNormalFound;
					if (startRow < 0) {
						startRow = 0;
					}
				}
				
				// TODO: implement
				if (bestMatchFlag && requestedRows > 0 && numDemoteFound > 0) {
					
					final ArrayList<NameValuePair> getDemotedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					getDemotedItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(requestedRows)));
					getDemotedItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(startRow)));
					getDemotedItemsParams.remove(excludeDemoteNameValuePair);
					if (forceAddList.size() > 0) {
						// demote the force added items
						getDemotedItemsParams.remove(dtNvp);
						removeNameValuePairFromList(getDemotedItemsParams, SolrConstants.SOLR_PARAM_FIELD_QUERY);
						if (redirect != null && redirect.isRedirectFilter()) {
							getDemotedItemsParams.remove(redirectFqNvp);
						}
						addNameValuePairToList(getDemotedItemsParams, forceAddFqNVP);
						addNameValuePairToList(getDemotedItemsParams, forceAddKeywordNVP);
						getDemotedItemsParams.remove(origKeywordNVP);
					}
					// TODO: insert force add parameters
					
					if (withFacetFlag) {
						final int numDemote = numDemoteFound - startRow > requestedRows? requestedRows:numElevateFound - startRow;
						getDemotedItems = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getDemotedItems(getDemotedItemsParams, numDemote);
							}
						});
					} else {
						final int nStart = startRow;
						final int nRequested = requestedRows;
						getDemotedItems = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getDemotedItems(getDemotedItemsParams, nStart, nRequested);
							}
						});
					}
					tasks++;
				}
				
				while (tasks > 0) {
					Future<Integer> completed = completionService.take();
					if (completed.equals(getElevatedItems)) {
						logger.debug("Elevated item count: " + completed.get());
					}
					else if (completed.equals(getNormalItems)) {
						logger.debug("Normal item count: " + completed.get());
					}
					else if (completed.equals(getDemotedItems)) {
						logger.debug("Demoted item count: " + completed.get());
					}
					tasks--;
				}
			}

			/* Generate response */
			Long qtime = new Date().getTime() - start;
			solrHelper.generateServletResponse(response, qtime);
			SearchLogger.logInfo(fromSearchGui, startRow, requestedRows, originalKeyword);
		} catch (Throwable t) {
			logger.error("Failed to send solr request", t);
			throw new ServletException(t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
