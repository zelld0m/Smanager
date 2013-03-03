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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.SearchDaoService;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.Relevancy.Parameter;
import com.search.manager.model.SearchResult;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.SearchLogger;

public class EnterpriseSearchServlet extends HttpServlet {

	@Autowired
	@Qualifier("daoService")
	SearchDaoService daoService;
	@Autowired
	@Qualifier("daoCacheService")
	SearchDaoService daoCacheService;
	@Autowired
	@Qualifier("solrService")
	SearchDaoService solrService;
	
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(SearchServlet.class);

	private ConfigManager configManager;
	private EnterpriseSearchConfigManager enterpriseSearchConfigManager;
	
	// these fields should not contain multiple entries
	private final static String[] uniqueFields = {
			SolrConstants.SOLR_PARAM_ROWS,
			SolrConstants.SOLR_PARAM_KEYWORD,
			SolrConstants.SOLR_PARAM_WRITER_TYPE ,
			SolrConstants.SOLR_PARAM_START
			};

	// TODO: transfer to config file
	private final static String[] supportedCores = {
		"pcmall",
		"macmall",
		"ecost",
		"pcmallgov",
		"enterpriseSearch"
	};
	
	public final ExecutorService execService = Executors.newCachedThreadPool();

	public void setDaoCacheService(SearchDaoService daoCacheService) {
		this.daoCacheService = daoCacheService;
	}
	
	public void setSolrService(SearchDaoService solrService) {
		this.solrService = solrService;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		super.init(config);
		configManager = ConfigManager.getInstance();
		enterpriseSearchConfigManager = EnterpriseSearchConfigManager.getInstance();
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

	private static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? null : list.get(0);
	}

	private static boolean generateFilterList(StringBuilder allValues, Collection<? extends SearchResult> list, Map<String, String> overrideMap) {
		StringBuilder edpValues = new StringBuilder();
		StringBuilder facetValues = new StringBuilder();
		boolean withFacetFlag = false;
		boolean edpFlag = false;
		boolean facetFlag = false;
		if (!(list == null || list.isEmpty())) {
			for (SearchResult result: list) {
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
					String strCondition = StringUtils.replaceEach(result.getCondition().getConditionForSolr(), 
							overrideMap.keySet().toArray(new String[0]), overrideMap.values().toArray(new String[0]));
					facetValues.append("(").append(strCondition).append(")");
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
	
	private SearchDaoService getDaoService(boolean fromSearchGui) {
		return fromSearchGui ? daoService : solrService;
	}
	
	private RedirectRule getRedirectRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRedirectRule(sk);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getRedirectRule(sk);
			}
			throw e;
		}
	}

	private Relevancy getRelevancyRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRelevancyRule(sk);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getRelevancyRule(sk);
			}
			throw e;
		}
	}

	private Relevancy getRelevancyRule(Store store, String relevancyId, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRelevancyRule(store, relevancyId);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getRelevancyRule(store, relevancyId);
			}
			throw e;
		}
	}
	
	private Relevancy getDefaultRelevancyRule(Store store, boolean fromSearchGui) throws DaoException {
		return getRelevancyRule(store, store.getStoreId() + "_default", fromSearchGui);
	}
	
	private FacetSort getFacetSortRule(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getFacetSortRule(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getFacetSortRule(storeKeyword);
			}
			throw e;
		}
	}
	
	private FacetSort getFacetSortRule(Store store, String templateName, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getFacetSortRule(store, templateName);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getFacetSortRule(store, templateName);
			}
			throw e;
		}
	}
	
	private List<ElevateResult> getElevateRules(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getElevateRules(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getElevateRules(storeKeyword);
			}
			throw e;
		}
	}
	
	private List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getExpiredElevateRules(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getExpiredElevateRules(storeKeyword);
			}
			throw e;
		}
	}

	private List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getExcludeRules(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getExcludeRules(storeKeyword);
			}
			throw e;
		}
	}
	
	private List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getExpiredExcludeRules(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getExpiredExcludeRules(storeKeyword);
			}
			throw e;
		}
	}
	
	private List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getDemoteRules(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getDemoteRules(storeKeyword);
			}
			throw e;
		}
	}
	
	private List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getExpiredDemoteRules(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getExpiredDemoteRules(storeKeyword);
			}
			throw e;
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
			NameValuePair redirectFqNvp = null; 
			NameValuePair defTypeNVP = new BasicNameValuePair("defType", "dismax");

			// get the server name, solr path, core name and do mapping for the store name to use for the search
			Pattern pathPattern = Pattern.compile("http://(.*):.*/(.*)/(.*)/select.*");
			String requestPath = "http:/" + request.getPathInfo();

			if (StringUtils.isEmpty(requestPath)) {
				response.sendError(400, "Invalid request: Invalid URL");
				return;
			}
			Matcher matcher = pathPattern.matcher(requestPath);
			if (!matcher.matches()) {
				response.sendError(400, "Invalid request: Invalid URL");
				return;
			}
			else if (StringUtils.isEmpty(request.getParameter("store"))) {
				response.sendError(400, "Invalid request: No store parameter");
				return;
			}

			String serverName = matcher.group(1);
			String solr = matcher.group(2);
			String solrCore = matcher.group(3);
			String storeName =  StringUtils.lowerCase(request.getParameter("store"));
			
			if (enterpriseSearchConfigManager.getSearchConfiguration(storeName) == null) {
				response.sendError(400, "Invalid request: Invalid store " + storeName);
				return;
			}
			else if (!ArrayUtils.contains(supportedCores, solrCore)) {
				response.sendError(400, "Invalid request: Invalid core " + solrCore);
				return;
			}
			
			if (logger.isDebugEnabled()) {
				logger.debug("Server name: " + serverName);
				logger.debug("Solr version: " + solr);
				logger.debug("Solr core: " + solrCore);
				logger.debug("store name: " + storeName);
			}

			// parse the parameters, construct POST form
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Set<String> paramNames = request.getParameterMap().keySet();

			HashMap<String, List<NameValuePair>> paramMap = new HashMap<String, List<NameValuePair>>();
			
			List<NameValuePair> spellcheckParams = new ArrayList<NameValuePair>();
			boolean performSpellCheck = false;
			
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
						
						if (StringUtils.equals("*:*", paramValue)) {
							paramValue = "";
						}
						else {
							paramValue = StringUtils.trimToEmpty(paramValue).replaceAll("[\\p{Cntrl}]", "");
						}
						
						String convertedKeyword = paramValue;
						logger.info(String.format("CLEANUP %s keyword: %s[%s] %s[%s]", storeName,
								origKeyword, HexUtils.convert(origKeyword.getBytes()),
								convertedKeyword, HexUtils.convert(convertedKeyword.getBytes())));
					}
					else if (paramName.startsWith(SolrConstants.SOLR_PARAM_SPELLCHECK)) {
						if (paramName.equals(SolrConstants.SOLR_PARAM_SPELLCHECK)) {
							performSpellCheck = true;
						}
						spellcheckParams.add(new BasicNameValuePair(paramName, paramValue));
						continue;
					}
					
					nvp = new BasicNameValuePair(paramName, paramValue);
					if (addNameValuePairToMap(paramMap, paramName, nvp)) {
						nameValuePairs.add(nvp);
					}
				}
			}
			
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
				response.sendError(500, "Unsupported writer type");
				return;
			}
			solrHelper.setForEnterpriseSearch(true);
			
			// add store flag
			String storeFlag = enterpriseSearchConfigManager.getStoreFlag(storeName);
			if (StringUtils.isNotBlank(storeFlag)) {
				nvp = new BasicNameValuePair("fq", String.format("%s:true", storeFlag));
				if (addNameValuePairToMap(paramMap, "fq", nvp)) {
					nameValuePairs.add(nvp);
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
			boolean disableFacetSort  = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_DISABLE_FACET_SORT) != null;
			List<Map<String,String>> activeRules = new ArrayList<Map<String, String>>();
			
			boolean fromSearchGui = "true".equalsIgnoreCase(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_GUI));
			String queryType = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE);
			boolean skipRelevancy = !fromSearchGui && !StringUtils.isBlank(keyword) && (StringUtils.isBlank(queryType) || StringUtils.equals(queryType, "standard"));
			if (skipRelevancy) {
				disableRelevancy = true;
			}
			
			RedirectRule appliedRedirect = null;
			
			// redirect 
			if (enterpriseSearchConfigManager.isActiveSearchRule(storeName, RuleEntity.QUERY_CLEANING)) {
				String storeOverride = enterpriseSearchConfigManager.getSearchRuleCore(storeName, RuleEntity.QUERY_CLEANING);
				StoreKeyword sk = new StoreKeyword(storeOverride, keyword);
				RedirectRule redirect = null;
				
				try {
					List<String> keywordHistory = new ArrayList<String>();
					while (true) { // look for change keyword
						if (StringUtils.isBlank(keyword)) {
							break;
						}
						keywordHistory.add(StringUtils.lowerCase(keyword));
						redirect = getRedirectRule(sk, fromSearchGui);
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
							appliedRedirect = redirect;
							
							keyword = StringUtils.trimToEmpty(redirect.getChangeKeyword());
							sk.setKeyword(new Keyword(keyword));
							// remove the original keyword
							nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
							paramMap.remove(SolrConstants.SOLR_PARAM_KEYWORD);
							
							if (StringUtils.isEmpty(keyword)) {
								sk.setKeyword(null);
								keywordPresent = false;
								break;
							} else {
								// set the new keyword
								nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, keyword);
								if (addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD, nvp)) {
									nameValuePairs.add(nvp);
								}
								if (keywordHistory.contains(StringUtils.lowerCase(keyword))) {
									logger.warn("Loop in change keywords detected. Aborting search for new keyword. Reverting to original keyword.");
									redirect = null;
									appliedRedirect = null;
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
						appliedRedirect = redirect;
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
									rr.setStoreId(storeOverride);
									Map<String, String> map = enterpriseSearchConfigManager.getFieldOverrideMap(storeName, storeOverride);
									String strCondition = StringUtils.replaceEach(rr.getConditionForSolr(), map.keySet().toArray(new String[0]), map.values().toArray(new String[0]));
									builder.append("(").append(strCondition).append(") OR ");
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
			}

			
			if (enterpriseSearchConfigManager.isActiveSearchRule(storeName, RuleEntity.RANKING_RULE)) {
				Store storeOverride = new Store(enterpriseSearchConfigManager.getSearchRuleCore(storeName, RuleEntity.RANKING_RULE));
				StoreKeyword sk = new StoreKeyword(storeOverride.getStoreId(), keyword);
				// set relevancy filters if any was specified
				String relevancyId = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_RELEVANCY_ID);
				Relevancy relevancy = null;
				if (StringUtils.isNotBlank(relevancyId)) {
					relevancy = new Relevancy();
					relevancy.setRelevancyId(relevancyId);
					relevancy = getRelevancyRule(storeOverride, relevancyId, fromSearchGui);
				}
				else if (keywordPresent) {
					relevancy = getRelevancyRule(sk, fromSearchGui);
				}
				
				if (relevancy == null) {
					relevancy = getDefaultRelevancyRule(storeOverride, fromSearchGui);
				}
				
				if (relevancy != null) {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_RELEVANCY, relevancy.getRelevancyId(), relevancy.getRelevancyName(), !disableRelevancy));				
					if (!disableRelevancy) {
						logger.debug("Applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());							
					}
					else {
						logger.debug("Relevancy disabled. Not applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());							
					}
				} else {
					logger.error("Unable to find default relevancy!");
				}
				
				if (relevancy == null) {
					relevancy = enterpriseSearchConfigManager.getRelevancy(storeName);
				}
				
				if (!disableRelevancy && relevancy != null) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
					nameValuePairs.add(defTypeNVP);
					Map<String, String> parameters = relevancy.getParameters();
					Map<String, String> map = enterpriseSearchConfigManager.getFieldOverrideMap(storeName, storeOverride.getStoreId());
					for (String paramName: parameters.keySet()) {
						String paramValue = parameters.get(paramName);
						if (StringUtils.isNotEmpty(paramValue)) {
							if (StringUtils.equals(Parameter.PARAM_QUERY_FIELDS.toString(), paramName)) {
								// insert VW into possible matches
								// TODO: put in config file?
								paramValue += " VW_DPNo_Index^0 VW_PN_Index^0";
							}
							paramValue = StringUtils.replaceEach(paramValue, map.keySet().toArray(new String[0]), map.values().toArray(new String[0]));
							logger.debug("adding " + paramName + ": " + paramValue);
							nvp = new BasicNameValuePair(paramName, paramValue);
							if (addNameValuePairToMap(paramMap, paramName, nvp)) {
								nameValuePairs.add(nvp);
							}
						}
					}
				}
				else {
					if (StringUtils.isBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE))) {
						nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_QUERY_TYPE, 
								enterpriseSearchConfigManager.getDismax(storeName)));
					}
				}
			}
			else {
				Relevancy relevancy = enterpriseSearchConfigManager.getRelevancy(storeName);
				if (relevancy != null) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
					nameValuePairs.add(defTypeNVP);
					Map<String, String> parameters = relevancy.getParameters();
					for (String paramName: parameters.keySet()) {
						String paramValue = parameters.get(paramName);
						if (StringUtils.isNotEmpty(paramValue)) {
							if (StringUtils.equals(Parameter.PARAM_QUERY_FIELDS.toString(), paramName)) {
								// insert VW into possible matches
								// TODO: put in config file?
								paramValue += " VW_DPNo_Index^0 VW_PN_Index^0";
							}
							logger.debug("adding " + paramName + ": " + paramValue);
							nvp = new BasicNameValuePair(paramName, paramValue);
							if (addNameValuePairToMap(paramMap, paramName, nvp)) {
								nameValuePairs.add(nvp);
							}
						}
					}
				}
	
				if (StringUtils.isBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE))) {
					nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_QUERY_TYPE, 
							enterpriseSearchConfigManager.getDismax(storeName)));
				}
			}
				
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}

			List<ElevateResult> elevatedList = null;
			List<ElevateResult> forceAddList = new ArrayList<ElevateResult>();
			List<String> expiredElevatedList = new ArrayList<String>();
			List<DemoteResult> demoteList = null;
			List<String> expiredDemotedList = new ArrayList<String>();
			List<ExcludeResult> excludeList = null;
			List<String> expiredExcludedList = new ArrayList<String>();

			// do not elevate/demote if sort is by price or manufacturer
			String sort = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT);
			boolean bestMatchFlag = StringUtils.isEmpty(sort) || !StringUtils.containsIgnoreCase(sort, "price") ||
							!StringUtils.containsIgnoreCase(sort, "manufacturer");

			Map<String, String> elevateOverrideMap = new HashMap<String,String>();
			Map<String, String> excludeOverrideMap = new HashMap<String,String>();
			Map<String, String> demoteOverrideMap = new HashMap<String,String>();
			
			if (keywordPresent) {
				if (enterpriseSearchConfigManager.isActiveSearchRule(storeName, RuleEntity.EXCLUDE)) {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_EXCLUDE, keyword, keyword, !disableExclude));
					if (!disableExclude) {
						String storeOverride = enterpriseSearchConfigManager.getSearchRuleCore(storeName, RuleEntity.EXCLUDE);
						StoreKeyword sk = new StoreKeyword(storeOverride, keyword);
						excludeList = getExcludeRules(sk, fromSearchGui);
						excludeOverrideMap = enterpriseSearchConfigManager.getFieldOverrideMap(storeName, storeOverride);
						if (fromSearchGui) {
							List<ExcludeResult> expiredList = getExpiredExcludeRules(sk, fromSearchGui);
							if (logger.isDebugEnabled()) {
								logger.debug("Expired Demoted List: ");
							}
							for (ExcludeResult expired: expiredList) {
								if (logger.isDebugEnabled()) {
									logger.debug("\t" + expired.getEdp());
								}
								if (MemberTypeEntity.PART_NUMBER.equals(expired.getExcludeEntity())) {
									expiredExcludedList.add(expired.getEdp());
								}
							}
						}
					}
				}

				if (bestMatchFlag && enterpriseSearchConfigManager.isActiveSearchRule(storeName, RuleEntity.DEMOTE)) {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_DEMOTE, keyword, keyword, !disableDemote));
					if (!disableDemote) {
						String storeOverride = enterpriseSearchConfigManager.getSearchRuleCore(storeName, RuleEntity.DEMOTE);
						StoreKeyword sk = new StoreKeyword(storeOverride, keyword);
						demoteList = getDemoteRules(sk, fromSearchGui);
						demoteOverrideMap = enterpriseSearchConfigManager.getFieldOverrideMap(storeName, storeOverride);
						solrHelper.setEnterpriseSearchDemoteFieldOverrides(demoteOverrideMap);
						if (fromSearchGui) {
							List<DemoteResult> expiredList = getExpiredDemoteRules(sk, fromSearchGui);
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
				
				if (enterpriseSearchConfigManager.isActiveSearchRule(storeName, RuleEntity.ELEVATE)) {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_ELEVATE, keyword, keyword, !disableElevate));
					if (!disableElevate) {
						String storeOverride = enterpriseSearchConfigManager.getSearchRuleCore(storeName, RuleEntity.ELEVATE);
						StoreKeyword sk = new StoreKeyword(storeOverride, keyword);
						elevatedList = getElevateRules(sk, fromSearchGui);
						elevateOverrideMap = enterpriseSearchConfigManager.getFieldOverrideMap(storeName, storeOverride);
						solrHelper.setEnterpriseSearchElevateFieldOverrides(elevateOverrideMap);
						if (CollectionUtils.isNotEmpty(elevatedList)) {
							// prepare force added list
							for (ElevateResult elevateResult : elevatedList) {
								if (elevateResult.isForceAdd()) {
									forceAddList.add(elevateResult);
								}
							}
						}
						if (!bestMatchFlag) {
							elevatedList.clear();
						}
						else if (fromSearchGui) { // && bestMatchFlag
							List<ElevateResult> expiredList = getExpiredElevateRules(sk, fromSearchGui);
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
				}
			}

			/* First Request */
			// run spellcheck if requested and if keyword is present
			performSpellCheck &= StringUtils.isNotBlank(originalKeyword);
			
			// set Solr URL
			solrHelper.setSolrUrl(requestPath);
			solrHelper.setSolrQueryParameters(paramMap);
			solrHelper.setElevatedItems(elevatedList);
			solrHelper.setExpiredElevatedEDPs(expiredElevatedList);
			solrHelper.setDemotedItems(demoteList);
			solrHelper.setExpiredDemotedEDPs(expiredDemotedList);
			solrHelper.setRedirectRule(appliedRedirect);
			solrHelper.setOriginalKeyword(originalKeyword);
			
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
				generateFilterList(excludeFilters, excludeList, excludeOverrideMap);
				if (excludeFilters.length() > 0) {
					excludeFilters.insert(0, "-");
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeFilters.toString());
					nameValuePairs.add(nvp);
				}
			}
			
			// collate elevate list
			StringBuilder elevateFilters = new StringBuilder();
			generateFilterList(elevateFilters, elevatedList, elevateOverrideMap);
			
			// collate demoted list
			StringBuilder demoteFilters = new StringBuilder();
			generateFilterList(demoteFilters, demoteList, demoteOverrideMap);

			Integer numFound = 0;
			Integer numElevateFound = 0;
			Integer numNormalFound = 0;
			Integer numDemoteFound = 0;

			// send solr request
			solrHelper.setActiveRules(activeRules);

			// create force add filters
			if (forceAddList.size() > 0) {
				// generate force add filter
				StringBuilder forceAddFilter = new StringBuilder();
				generateFilterList(forceAddFilter, forceAddList, elevateOverrideMap);

				if (redirectFqNvp != null) {
					nameValuePairs.remove(redirectFqNvp);
					StringBuilder newRedirectFilter = new StringBuilder();
					newRedirectFilter.append("(").append(redirectFqNvp.getValue()).append(") OR (").append(forceAddFilter.toString()).append(")");
					nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, newRedirectFilter.toString()));
				}

				NameValuePair keywordNvp = getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD);
				if (skipRelevancy) {
					if (!keywordPresent || (appliedRedirect != null && appliedRedirect.isRedirectFilter() && BooleanUtils.isNotTrue(appliedRedirect.getIncludeKeyword()))) {
						nameValuePairs.add(0, new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, "*:*"));
					}
				}
				else if (keywordNvp != null) {
					if (nameValuePairs.remove(defTypeNVP)) { // relevancy != null
						nameValuePairs.remove(keywordNvp);
						StringBuilder newQuery = new StringBuilder();
						newQuery.append("(_query_:\"{!dismax v=$searchKeyword}\") ").append(" OR (").append(forceAddFilter.toString()).append(")");
						nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, newQuery.toString()));
						nameValuePairs.add(new BasicNameValuePair("searchKeyword", keyword));
					}
				}
			}

			/* FacetSort */
			if (enterpriseSearchConfigManager.isActiveSearchRule(storeName, RuleEntity.FACET_SORT)) {
				FacetSort facetSort = null;
				boolean applyFacetSort = false;
				String storeOverride = enterpriseSearchConfigManager.getSearchRuleCore(storeName, RuleEntity.FACET_SORT);
				StoreKeyword sk = new StoreKeyword(storeOverride, keyword);
				
				String facetTemplateName = configManager.getParameterByCore(storeOverride, SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME);
				String facetTemplate = configManager.getParameterByCore(storeOverride, SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
				final ArrayList<NameValuePair> getTemplateNameParams = new ArrayList<NameValuePair>(nameValuePairs);
				for (NameValuePair param: nameValuePairs) {
					if (StringUtils.equals(SolrConstants.SOLR_PARAM_SPELLCHECK, param.getName()) || 
						StringUtils.equals(SolrConstants.TAG_FACET, param.getName()) || 
						StringUtils.equals(SolrConstants.TAG_FACET_LIMIT, param.getName()) || 
						StringUtils.equals(SolrConstants.TAG_FACET_MINCOUNT, param.getName())){
						getTemplateNameParams.remove(param);
					}
					else if (StringUtils.equals(SolrConstants.TAG_FACET_FIELD, param.getName())) {
						if (StringUtils.equals("Manufacturer", param.getValue()) ||
							StringUtils.equals("Category", param.getValue()) ||
							StringUtils.equals(facetTemplate, param.getValue())) {
							// apply facet sort only if facet.field contains Manufacturer or Category or PCMall_FacetTemplate
							applyFacetSort = true;
						}
						getTemplateNameParams.remove(param);
					}
				}
	
				if (keywordPresent) {
					facetSort = getFacetSortRule(sk, fromSearchGui);
				}
				
				if (facetSort == null) {
					// get facetSortRule based on template name
					getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET, "true"));
					getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET_MINCOUNT, "1"));
					getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET_FIELD, facetTemplateName));
					getTemplateNameParams.add(new BasicNameValuePair(SolrConstants.TAG_FACET_LIMIT, "-1"));

					facetTemplateName = solrHelper.getCommonTemplateName(facetTemplateName, getTemplateNameParams);
					if (StringUtils.isNotBlank(facetTemplateName)) {
						facetSort = getFacetSortRule(sk.getStore(), facetTemplateName, fromSearchGui);
					}
				}
				
				if (facetSort != null) {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_FACET_SORT, facetSort.getRuleId(), facetSort.getRuleName(), !disableFacetSort));				
					if (!disableFacetSort && applyFacetSort) {
						solrHelper.setFacetSortRule(facetSort);
					}
				}
			}
			
			Future<Integer> getTemplateCount = null;
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

			nameValuePairs.remove(getNameValuePairFromMap(paramMap,"facet"));
			
			// TASK 1B - get spellcheck if requested
			/* Run spellcheck if needed */
			if (performSpellCheck) {
				final ArrayList<NameValuePair> getSpellingSuggestionsParams = new ArrayList<NameValuePair>(nameValuePairs);
				getSpellingSuggestionsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, originalKeyword));
				getSpellingSuggestionsParams.add(defTypeNVP);
				getSpellingSuggestionsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, "0"));
				getSpellingSuggestionsParams.addAll(spellcheckParams);
				completionService.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						solrHelper.getSpellingSuggestion(getSpellingSuggestionsParams);
						return 0;
					}
				});
				tasks++;
			}

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

			numNormalFound = numFound - numElevateFound - numDemoteFound;
			
			// cleanup request parameters, remove start and row
			nameValuePairs.remove(paramMap.get(SolrConstants.SOLR_PARAM_START).get(0));
			nameValuePairs.remove(paramMap.get(SolrConstants.SOLR_PARAM_ROWS).get(0));

			if (requestedRows != 0 && (numFound + numElevateFound) != 0) {

				Future<Integer> getElevatedItems = null;
				Future<Integer> getNormalItems = null;
				Future<Integer> getDemotedItems = null;

				// TASK 2A - get elevated items
				// check if elevateList is to be included in this batch
				if (bestMatchFlag && numElevateFound > startRow) {
					logger.debug("total number of elevated found including force added items:" + numElevateFound);
					
					final ArrayList<NameValuePair> getElevatedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					final int nStart = startRow;
					final int nRequested = requestedRows;
					getElevatedItems = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getElevatedItems(getElevatedItemsParams, nStart, nRequested);
						}
					});
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
				if (requestedRows > 0 && numNormalFound > startRow) {
					/* Third Request */
					// set filter to not include elevate and exclude list
					// set rows parameter to original number requested minus results returned in second request
					// grab all the doc nodes <doc>

					final ArrayList<NameValuePair> getNormalItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					if (elevateFilters.length() > 0) {
						elevateFilters.insert(0, "-");
						getNormalItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateFilters.toString()));
					}
					getNormalItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(requestedRows)));
					getNormalItemsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, String.valueOf(startRow)));
					getNormalItems = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getNonElevatedItems(getNormalItemsParams);
						}
					});
					tasks++;
					
					requestedRows -= (numNormalFound - startRow);
					if (requestedRows < 0) {
						requestedRows = 0;
					}
				}

				
				// TASK 2C - get demoted items
				if (numFound > 0) {
					startRow -= numNormalFound;
					if (startRow < 0) {
						startRow = 0;
					}
				}
				
				if (bestMatchFlag && requestedRows > 0 && numDemoteFound > 0) {
					final ArrayList<NameValuePair> getDemotedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					getDemotedItemsParams.remove(excludeDemoteNameValuePair);
					final int nStart = startRow;
					final int nRequested = requestedRows;
					getDemotedItems = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getDemotedItems(getDemotedItemsParams, nStart, nRequested);
						}
					});
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
