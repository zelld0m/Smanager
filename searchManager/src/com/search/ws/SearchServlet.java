package com.search.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

	private static StringBuffer getAllValuesFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		StringBuffer value = new StringBuffer();
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

	private static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? null : list.get(0);
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

			NameValuePair fqNvp = null;	
			// default parameters for the core
			for (NameValuePair pair: ConfigManager.getInstance().getDefaultSolrParameters(coreName)) {
				if (addNameValuePairToMap(paramMap, pair.getName(), pair)) {
					if(pair.getName().equalsIgnoreCase(SolrConstants.SOLR_PARAM_FIELD_QUERY)) {
						fqNvp = pair;
					} 
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
						fqNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, builder.toString());
						nameValuePairs.add(fqNvp);
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
			Integer numDemoteFound = 0;

			// send solr request

			// TODO: workaround for spellchecker
			if (StringUtils.isNotBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD))) {
				requestPath = requestPath.replaceFirst("select", "spellCheckCompRH");
				solrHelper.setSolrUrl(requestPath);
			}
			
			solrHelper.setActiveRules(activeRules);


			StringBuffer fqBuff = new StringBuffer();
			StringBuffer kwBuff = null;
			NameValuePair fqJoinQueryNvp = null;
			NameValuePair kwBackupNvp = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD);
			NameValuePair kwJoinQueryNvp = null;
			if (forceAddList.size() > 0) {
				for (ElevateResult e : forceAddList) {
					if (fqBuff.length() > 0) {
						fqBuff.append(" OR ");
					}
					if (e.getElevateEntity() == MemberTypeEntity.PART_NUMBER) {
						fqBuff.append("EDP:").append(e.getEdp());
					} else {
						fqBuff.append(e.getCondition().getConditionForSolr());
					}
				}
				nameValuePairs.remove(dtNvp);
				nameValuePairs.remove(fqNvp);
				fqJoinQueryNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, fqNvp.getValue() + " OR " + fqBuff.toString());
				nameValuePairs.add(fqJoinQueryNvp);
				nameValuePairs.remove(kwBackupNvp);
				NameValuePair qfNvp = getNameValuePairFromMap(paramMap, "qf");
				kwBuff = new StringBuffer("_query_:\"{!dismax qf='").append(qfNvp.getValue()).append("' v='").append(kwBackupNvp != null?kwBackupNvp.getValue():originalKeyword).append("'}\"");
				kwBuff.append(" OR ").append(fqBuff);
				if (redirect !=null && redirect.isRedirectFilter()) {
					kwBuff.append(" OR ").append(fqNvp.getValue());
				} 

				kwJoinQueryNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, kwBuff.toString());
				nameValuePairs.add(kwJoinQueryNvp);
			}

			Future<Integer> getTemplateCount = null;
			Future<Integer> getForceAddTemplateCount = null;
			Future<Integer> getElevatedCount = null;
			Future<Integer> getDemotedCount = null;

			// TASK 1A - get total number of items
			final ArrayList<NameValuePair> getTemplateCountParams = new ArrayList<NameValuePair>(nameValuePairs);
			getTemplateCount = completionService.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return solrHelper.getTemplateCounts(getTemplateCountParams);
				}
			});
			tasks++;

			// TASK 1B - get count of force added items (should be merged with task 1A) 
			if (forceAddList.size() > 0) {
				nameValuePairs.remove(fqJoinQueryNvp);
				nameValuePairs.remove(kwJoinQueryNvp);
				nameValuePairs.add(kwBackupNvp);
				nameValuePairs.add(dtNvp);
				nameValuePairs.add(fqNvp);
				
			}

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
					nameValuePairs.remove(kwBackupNvp);
					if (redirect != null && redirect.isRedirectFilter()) {
						nameValuePairs.remove(fqNvp);
					}
					final ArrayList<NameValuePair> getForceAddTemplateCountParams = new ArrayList<NameValuePair>(nameValuePairs);
					getForceAddTemplateCount = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getForceAddTemplateCounts(getForceAddTemplateCountParams);
						}
					});
					tasks++;
					if (kwBackupNvp != null) {
						nameValuePairs.add(kwBackupNvp);
					}
					if (redirect != null && redirect.isRedirectFilter()) {
						nameValuePairs.add(fqNvp);
					}
				}
				
				if (requestedRows != 0 && (elevateValues.length() > 0 || elevateFacetValues.length() > 0)) {
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

					// TASK 1D - get count of demoted items
					if (demoteFilters.length() > 0) {
						final ArrayList<NameValuePair> getDemotedCountParams = new ArrayList<NameValuePair>(nameValuePairs);
						getDemotedCountParams.remove(excludeDemoteNameValuePair);
						getDemotedCountParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, demoteFilters.toString()));
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
			
			if (requestedRows != 0 && (numFound + numElevateFound) != 0) {

				Future<Integer> getElevatedItems = null;
				Future<Integer> getNonElevatedItems = null;

				logger.debug("number of elevated found:" + numElevateFound);

				// check if elevateList is to be included in this batch
				if (bestMatchFlag && numElevateFound > startRow) {

					nvp = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_ROWS);
					paramMap.remove(SolrConstants.SOLR_PARAM_ROWS);
					nameValuePairs.remove(nvp);

					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(startRow + requestedRows));
					nameValuePairs.add(nvp);
					addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_ROWS, nvp);

					nvp = paramMap.get(SolrConstants.SOLR_PARAM_START).get(0);
					nameValuePairs.remove(nvp);
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(0));
					nameValuePairs.add(nvp);
					
					if (redirect != null && redirect.isRedirectFilter()) {
						nameValuePairs.remove(fqNvp);
					}

					final ArrayList<NameValuePair> getElevatedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					if (withFacetFlag) {
						final List<ElevateResult> fElevatedList = elevatedList;
						final int numElevate = numElevateFound - startRow > requestedRows? requestedRows:numElevateFound - startRow;
						getElevatedItems = completionService.submit(new Callable<Integer>() {
							@Override
							public Integer call() throws Exception {
								return solrHelper.getElevatedItems(getElevatedItemsParams, fElevatedList, numElevate);
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
					if (redirect != null && redirect.isRedirectFilter()) {
						nameValuePairs.add(fqNvp);
					}
					tasks++;
					requestedRows -= (numElevateFound - startRow);
				}

				logger.debug("requested rows:" + requestedRows);
				if (requestedRows > 0) {
					/* Third Request */
					// set filter to not include elevate and exclude list
					// set rows parameter to original number requested minus results returned in second request
					// grab all the doc nodes <doc>
					if (elevateValues.length() > 0) {
						elevateValues.insert(0, "-");
						nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateValues.toString()));
					}
					if (elevateFacetValues.length() > 0) {
						elevateFacetValues.insert(0, "-");
						nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateFacetValues.toString()));
					}

					nvp = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_ROWS);
					paramMap.remove(SolrConstants.SOLR_PARAM_ROWS);
					nameValuePairs.remove(nvp);
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(requestedRows));
					nameValuePairs.add(nvp);
					addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_ROWS, nvp);

					// update start row
					if (numElevateFound > 0) {
						startRow -= numElevateFound;
						if (startRow < 0) {
							startRow = 0;
						}
					}
					nvp = paramMap.get(SolrConstants.SOLR_PARAM_START).get(0);
					nameValuePairs.remove(nvp);
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(startRow));
					nameValuePairs.add(nvp);
					
					if (!bestMatchFlag) {
						if (forceAddList.size() > 0) {
							nameValuePairs.remove(dtNvp);
							nameValuePairs.remove(fqNvp);
							fqNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, fqNvp.getValue() + " OR " + fqBuff.toString());
							nameValuePairs.add(fqNvp);
							nameValuePairs.remove(kwBackupNvp);
							NameValuePair qfNvp = getNameValuePairFromMap(paramMap, "qf");
							kwBuff = new StringBuffer("_query_:\"{!dismax qf='").append(qfNvp.getValue()).append("' v='").append(kwBackupNvp != null?kwBackupNvp.getValue():originalKeyword).append("'}\"");
							kwBuff.append(" OR ").append(fqBuff);
							if (redirect !=null && redirect.isRedirectFilter()) {
								kwBuff.append(" OR ").append(fqNvp.getValue());
							} 
							kwJoinQueryNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, kwBuff.toString());
							nameValuePairs.add(kwJoinQueryNvp);
						}
					}

					// TASK 2B
					getNonElevatedItems = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							final ArrayList<NameValuePair> getNonElevatedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
							return solrHelper.getNonElevatedItems(getNonElevatedItemsParams);
						}
					});
					tasks++;
				}

				while (tasks > 0) {
					Future<Integer> completed = completionService.take();
					if (completed.equals(getElevatedItems)) {
						numFound = completed.get();
						logger.debug("Elevated item count: " + numFound);
					}
					else if (completed.equals(getNonElevatedItems)) {
						numElevateFound = completed.get();
						logger.debug("Non-elevated item count: " + numElevateFound);
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
