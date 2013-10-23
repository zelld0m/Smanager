package com.search.ws;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.search.manager.core.processor.FacetSortRequestProcessor;
import com.search.manager.core.processor.RequestProcessorUtil;
import com.search.manager.core.processor.RequestPropertyBean;
import com.search.manager.core.processor.SearchWithinRequestProcessor;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.SearchDaoService;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SearchResult;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.SearchLogger;

public class SearchServlet extends HttpServlet {

	@Autowired
	@Qualifier("daoService")
	SearchDaoService daoService;
	@Autowired
	@Qualifier("solrService")
	SearchDaoService solrService;
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);
	protected ConfigManager configManager;
	// these fields should not contain multiple entries
	protected final static String[] uniqueFields = {
		SolrConstants.SOLR_PARAM_ROWS,
		SolrConstants.SOLR_PARAM_KEYWORD,
		SolrConstants.SOLR_PARAM_WRITER_TYPE,
		SolrConstants.SOLR_PARAM_START
	};
	protected final ExecutorService execService = Executors.newCachedThreadPool();

	public void setSolrService(SearchDaoService solrService) {
		this.solrService = solrService;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
		super.init(config);
		configManager = ConfigManager.getInstance();
	}

	protected static boolean addNameValuePairToMap(Map<String, List<NameValuePair>> map, String paramName, NameValuePair pair) {
		boolean added = true;
		if (ArrayUtils.contains(uniqueFields, paramName) && map.containsKey(paramName)) {
			logger.warn("Request contained multiple declarations for parameter {}. Discarding subsequent declarations.", paramName);
			added = false;
		} else {
			if (!map.containsKey(paramName)) {
				map.put(paramName, new ArrayList<NameValuePair>());
			}
			map.get(paramName).add(pair);
		}
		return added;
	}

	protected static String getValueFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? "" : list.get(0).getValue();
	}

	protected static List<String> getValuesFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		List<String> values = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (NameValuePair nvp : list) {
				values.add(nvp.getValue());
			}
		}
		return values;
	}

	protected static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? null : list.get(0);
	}

	protected static NameValuePair getNameValuePairFromList(List<NameValuePair> paramList, String parameterName) {
		for (NameValuePair param : paramList) {
			if (param.getName().equals(parameterName)) {
				return param;
			}
		}

		return null;
	}
	
	protected static boolean resetNameValuePairFromMapAndList(HashMap<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs, 
			String paramterName, String value) {
		nameValuePairs.remove(getNameValuePairFromMap(paramMap, paramterName));
		paramMap.remove(paramterName);
		BasicNameValuePair nvp = new BasicNameValuePair(paramterName, value);
		
		if (addNameValuePairToMap(paramMap, paramterName, nvp)) {
			return nameValuePairs.add(nvp);
		}
		
		return false;
	}

	protected void removeNameValuePairFromMapAndList(HashMap<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs, 
			String paramterName) {
		nameValuePairs.remove(getNameValuePairFromMap(paramMap, paramterName));
		paramMap.remove(paramterName);
	}
	
	protected void setFacetTemplateValues(RedirectRuleCondition condition, Map<String, String> facetMap) {
		if (condition != null) {
			condition.setFacetPrefix(facetMap.get(SolrConstants.SOLR_PARAM_FACET_NAME));
			condition.setFacetTemplate(facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE));
			condition.setFacetTemplateName(facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME));
		}
	}

	protected boolean isRegisteredKeyword(StoreKeyword storeKeyword) {
		if (daoService instanceof DaoService) {
			try {
				return ((DaoService) daoService).getKeyword(storeKeyword.getStoreId(), storeKeyword.getKeywordId()) != null;
			} catch (DaoException e) {
			}
		}
		return false;
	}

	public String applyValueOverride(String value, HttpServletRequest request, RuleEntity ruleEntity) {
		// override is only applied in enterprise search
		return value;
	}

	protected String applyRelevancyOverrides(HttpServletRequest request, String paramName, String paramValue) {
		return paramValue;
	}

	public String getOverrideMapAttributeName(RuleEntity ruleEntity) {
		String overrideMapAttribName = "";

		switch (ruleEntity) {
		case ELEVATE: overrideMapAttribName = SolrConstants.REQUEST_ATTRIB_ELEVATE_OVERRIDE_MAP; break;
		case EXCLUDE: overrideMapAttribName = SolrConstants.REQUEST_ATTRIB_EXCLUDE_OVERRIDE_MAP; break;
		case DEMOTE: overrideMapAttribName = SolrConstants.REQUEST_ATTRIB_DEMOTE_OVERRIDE_MAP; break;
		case QUERY_CLEANING: overrideMapAttribName = SolrConstants.REQUEST_ATTRIB_REDIRECT_OVERRIDE_MAP; break;
		case RANKING_RULE: overrideMapAttribName = SolrConstants.REQUEST_ATTRIB_RELEVANCY_OVERRIDE_MAP; break;
		default: overrideMapAttribName= ""; break;
		}

		return overrideMapAttribName;
	}

	/**
	 * Transform list of SearchResult class to a Solr filter query value.
	 * SearchResult can be either a part number or a facet
	 * 
	 * Return Format: (EDP:(58496 8549 856785) OR ((condition1) OR (condition2) OR (condition3)))  	  
	 *  
	 * @param request HttpServletRequest
	 * @param list list of SearchResult class to transform
	 * @return StringBuilder of Solr filter query value
	 */
	protected StringBuilder toSolrFilterQueryValue(HttpServletRequest request, List<? extends SearchResult> list) {
		List<String> edpList = new ArrayList<String>();
		List<String> facetList = new ArrayList<String>();
		StringBuilder filterQuery = new StringBuilder();

		if (CollectionUtils.isNotEmpty(list)) {
			RuleEntity ruleEntity = null;
			SearchResult tmp = list.get(0);

			if (tmp instanceof ElevateResult) {
				ruleEntity = RuleEntity.ELEVATE;
			} else if (tmp instanceof ExcludeResult) {
				ruleEntity = RuleEntity.EXCLUDE;
			} else if (tmp instanceof DemoteResult) {
				ruleEntity = RuleEntity.DEMOTE;
			}

			for (SearchResult result : list) {
				switch(result.getEntity()){
				case  PART_NUMBER: 
					edpList.add(result.getEdp());
					break;  
				case FACET: 
					facetList.add(String.format("(%s)", applyValueOverride(result.getCondition().getConditionForSolr(), request, ruleEntity)));
					break;
				}	
			}

			String edpValues = CollectionUtils.isNotEmpty(edpList) ? StringUtils.trimToEmpty(String.format("EDP:(%s)", StringUtils.join(edpList, ' '))): "";
			String facetValues = CollectionUtils.isNotEmpty(facetList) ? String.format("(%s)", StringUtils.join(facetList, " OR ")): "";

			filterQuery.append(String.format("(%s%s%s)", edpValues, StringUtils.isNotBlank(edpValues) && StringUtils.isNotBlank(facetValues)? " OR ": "", facetValues));
			logger.debug("{}:{}", RuleEntity.getValue(ruleEntity.getCode()), filterQuery.toString());
		}

		return filterQuery;
	}

	
	protected SearchDaoService getDaoService(boolean fromSearchGui) {
		return fromSearchGui ? daoService : solrService;
	}

	protected Integer getMaxSuggestCount(String storeId, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getMaxSuggest(storeId);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return 5;
			} else {
				throw e;
			}
		}
	}

	protected SpellRule getSpellRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getSpellRuleForSearchTerm(sk.getStoreId(), sk.getKeywordId());
		} catch (DaoException e) {
			if (!fromSearchGui) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return daoService.getSpellRuleForSearchTerm(sk.getStoreId(), sk.getKeywordId());
					} catch (DaoException e1) {
						logger.error("Failed to get spellRule {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	protected RedirectRule getRedirectRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRedirectRule(sk);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return daoService.getRedirectRule(sk);
					} catch (DaoException e1) {
						logger.error("Failed to get redirectRule {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	protected Relevancy getRelevancyRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRelevancyRule(sk);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return daoService.getRelevancyRule(sk);
					} catch (DaoException e1) {
						logger.error("Failed to get relevancyRule {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	protected Relevancy getRelevancyRule(Store store, String relevancyId, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRelevancyRule(store, relevancyId);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return daoService.getRelevancyRule(store, relevancyId);
					} catch (DaoException e1) {
						logger.error("Failed to get relevancyRule {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	/**
	 * Return Relevancy object with id <b>storeId</b>_default as DEFAULT if no <b>default-relevancy-rule</b> value is configured in solr.xml. 
	 * 
	 * @param store Store object
	 * @param fromSearchGui request is from simulator when true, otherwise from store site
	 * @return Relevancy object
	 * @throws DaoException the default or specified rule id is non-existent
	 */
	protected Relevancy getDefaultRelevancyRule(Store store, boolean fromSearchGui) throws DaoException {
		String storeId = store.getStoreId();
		String relevancyRuleId = StringUtils.defaultIfBlank(configManager.getStoreParameter(storeId, "default-relevancy-rule"), storeId + "_default");
		return getRelevancyRule(store, relevancyRuleId, fromSearchGui);
	}
	
	protected String getDefType(String storeId) throws DaoException {
		return StringUtils.defaultIfBlank(configManager.getStoreParameter(storeId, "defType"), "edismax");
	}

	protected void setFacetTemplateValues(List<? extends SearchResult> list, Map<String, String> facetMap) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (SearchResult e : list) {
				if (e.getEntity().equals(MemberTypeEntity.FACET)) {
					setFacetTemplateValues(e.getCondition(), facetMap);
				}
			}
		}
	}

	protected List<ElevateResult> getElevateRules(StoreKeyword storeKeyword, boolean fromSearchGui, Map<String, String> facetMap) throws DaoException {
		List<ElevateResult> list = null;
		try {
			list = getDaoService(fromSearchGui).getElevateRules(storeKeyword);
		} catch (DaoException e) {
			if (fromSearchGui) {
				throw e;
			}
			if (!configManager.isSolrImplOnly()) {
				try {
					list = daoService.getElevateRules(storeKeyword);
				} catch (DaoException e1) {
					logger.error("Failed to get elevateRules {}", e1);
					return null;
				}
			} else {
				return null;
			}
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}

	protected List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword, boolean fromSearchGui, Map<String, String> facetMap) throws DaoException {
		List<ElevateResult> list = null;
		try {
			list = getDaoService(fromSearchGui).getExpiredElevateRules(storeKeyword);
		} catch (DaoException e) {
			if (fromSearchGui) {
				throw e;
			}
			if (!configManager.isSolrImplOnly()) {
				try {
					list = daoService.getExpiredElevateRules(storeKeyword);
				} catch (DaoException e1) {
					logger.error("Failed to get expired elevateRules {}", e1);
					return null;
				}
			} else {
				return null;
			}
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}

	protected List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword, boolean fromSearchGui, Map<String, String> facetMap) throws DaoException {
		List<ExcludeResult> list = null;
		try {
			list = getDaoService(fromSearchGui).getExcludeRules(storeKeyword);
		} catch (DaoException e) {
			if (fromSearchGui) {
				throw e;
			}
			if (!configManager.isSolrImplOnly()) {
				try {
					list = daoService.getExcludeRules(storeKeyword);
				} catch (DaoException e1) {
					logger.error("Failed to get exludeRules {}", e1);
					return null;
				}
			} else {
				return null;
			}
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}

	protected List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword, boolean fromSearchGui, Map<String, String> facetMap) throws DaoException {
		List<ExcludeResult> list = null;
		try {
			list = getDaoService(fromSearchGui).getExpiredExcludeRules(storeKeyword);
		} catch (DaoException e) {
			if (fromSearchGui) {
				throw e;
			}
			if (!configManager.isSolrImplOnly()) {
				try {
					list = daoService.getExpiredExcludeRules(storeKeyword);
				} catch (DaoException e1) {
					logger.error("Failed to get expired excludeRules {}", e1);
					return null;
				}
			} else {
				return null;
			}
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}

	protected List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword, boolean fromSearchGui, Map<String, String> facetMap) throws DaoException {
		List<DemoteResult> list = null;
		try {
			list = getDaoService(fromSearchGui).getDemoteRules(storeKeyword);
		} catch (DaoException e) {
			if (fromSearchGui) {
				throw e;
			}
			if (!configManager.isSolrImplOnly()) {
				try {
					list = daoService.getDemoteRules(storeKeyword);
				} catch (DaoException e1) {
					logger.error("Failed to get demoteRules {}", e1);
					return null;
				}
			} else {
				return null;
			}
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}

	protected List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword, boolean fromSearchGui, Map<String, String> facetMap) throws DaoException {
		List<DemoteResult> list = null;
		try {
			list = getDaoService(fromSearchGui).getExpiredDemoteRules(storeKeyword);
		} catch (DaoException e) {
			if (fromSearchGui) {
				throw e;
			}
			if (!configManager.isSolrImplOnly()) {
				try {
					list = daoService.getExpiredDemoteRules(storeKeyword);
				} catch (DaoException e1) {
					logger.error("Failed to get expired demoteRules {}", e1);
					return null;
				}
			} else {
				return null;
			}
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}

	protected List<BannerRuleItem> getActiveBannerRuleItems(Store store, String keyword, boolean fromSearchGui, DateTime currentDate) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getActiveBannerRuleItems(store, keyword, currentDate);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				if (!configManager.isSolrImplOnly()) {
					try {
						return daoService.getActiveBannerRuleItems(store, keyword, currentDate);
					} catch (DaoException e1) {
						logger.error("Failed to get active bannerRuleItems {}", e1);
						return null;
					}
				} else {
					return null;
				}
			}
			throw e;
		}
	}

	protected String getRequestPath(HttpServletRequest request) {
		return "http:/" + request.getPathInfo();
	}

	protected boolean generateSearchNav(HttpServletRequest request) {
		return StringUtils.equalsIgnoreCase("true", request.getParameter(SolrConstants.SOLR_PARAM_GUI))
				&& StringUtils.equals(request.getParameter(SolrConstants.SOLR_PARAM_WRITER_TYPE), SolrConstants.SOLR_PARAM_VALUE_JSON);
	}

	protected void addDefaultParameters(String storeId, List<NameValuePair> nameValuePairs, Map<String, List<NameValuePair>> paramMap) {
		// default parameters for the store
		for (NameValuePair pair : configManager.getDefaultSolrParameters(storeId)) {
			if (addNameValuePairToMap(paramMap, pair.getName(), pair)) {
				nameValuePairs.add(pair);
			}
		}
	}

	protected void initFieldOverrideMaps(HttpServletRequest request, SolrResponseParser solrHelper, String storeId) {
	}

	/**
	 * Return Error Message if invalid else return null;
	 */
	protected String getStoreId(HttpServletRequest request) throws HttpException {
		// get the server name, solr path, core name and do mapping for the store name to use for the search
		Pattern pathPattern = Pattern.compile("http://(.*):.*/(.*)/(.*)/select.*");
		String requestPath = getRequestPath(request);
		if (StringUtils.isBlank(requestPath)) {
			throw new HttpException("Invalid request");
		}
		Matcher matcher = pathPattern.matcher(requestPath);
		if (!matcher.matches()) {
			throw new HttpException("Invalid request");
		}

		String serverName = matcher.group(1);
		String solr = matcher.group(2);
		String coreName = matcher.group(3);
		String storeId = coreName;
		String storeName = configManager.getStoreName(storeId);

		String solrSelectorParam = configManager.getSolrSelectorParam();

		if (configManager.isSharedCore() && StringUtils.isNotBlank(solrSelectorParam)) {
			// Verify if request parameter store is a valid store id
			String storeParam = request.getParameter(solrSelectorParam);
			String storeIdFromAlias = configManager.getStoreIdByAliases(storeParam);
			if (StringUtils.isNotBlank(storeParam) && StringUtils.isNotBlank(storeIdFromAlias)) {
				storeId = storeIdFromAlias;
				logger.info("Request parameter store {} -> {}", storeParam, coreName);
			} else {
				logger.info("Core as storeId: {}", coreName);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Server name: {}", serverName);
			logger.debug("Solr path: {}", solr);
			logger.debug("Core name: {}", coreName);
			logger.debug("Store name: {}", storeName);
		}

		return storeId;
	}

	protected SolrResponseParser getParser(HttpServletRequest request) throws HttpException {
		// get expected resultformat
		String writerType = request.getParameter(SolrConstants.SOLR_PARAM_WRITER_TYPE);
		if (SolrConstants.SOLR_PARAM_VALUE_JSON.equalsIgnoreCase(writerType)) {
			return new SolrJsonResponseParser();
		} else if (StringUtils.isEmpty(writerType) || SolrConstants.SOLR_PARAM_VALUE_XML.equalsIgnoreCase(writerType)
				|| SolrConstants.SOLR_PARAM_VALUE_STANDARD.equalsIgnoreCase(writerType)) {
			return new SolrXmlResponseParser();
		} else { // unsupported writer type
			throw new HttpException("Unsupported writer type");
		}
	}

	protected boolean isActiveSearchRule(String storeId, RuleEntity ruleEntity) {
		return true;
	}

	protected StoreKeyword getStoreKeywordOverride(RuleEntity entity, String storeId, String keyword) {
		return new StoreKeyword(storeId, keyword);
	}

	protected void setDefaultQueryType(HttpServletRequest request, List<NameValuePair> nameValuePairs, String storeId) {
		if (StringUtils.isBlank(request.getParameter(SolrConstants.SOLR_PARAM_QUERY_TYPE))) {
			nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_QUERY_TYPE,
					configManager.getStoreParameter(storeId, SolrConstants.SOLR_PARAM_QUERY_TYPE)));
		}
	}

	protected Relevancy getDefaultRelevancy(String storeId) {
		return null;
	}

	protected DateTime getOverrideCurrentDate(String storeId, String dateText) {
		DateTime convertedCurrentDate = DateTime.now();
		DateTime overrideCurrentDate = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, dateText, JodaPatternType.DATE);

		logger.info("Current Date: {}", convertedCurrentDate.toString());
		if (overrideCurrentDate != null) {
			convertedCurrentDate = convertedCurrentDate.withYear(overrideCurrentDate.getYear());
			convertedCurrentDate = convertedCurrentDate.withMonthOfYear(overrideCurrentDate.getMonthOfYear());
			convertedCurrentDate = convertedCurrentDate.withDayOfMonth(overrideCurrentDate.getDayOfMonth());
			logger.info("Simulate date: {} -> {}", dateText, convertedCurrentDate.toString());
		}

		return convertedCurrentDate;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected boolean isSameDomain(String storeId, String url) {
		// relative path
		List<String> relativePath = UtilityService.getStoreRelativePath(storeId);
		for(String path : relativePath) {
			if(url.startsWith(path)) {
				return true;
			}
		}
		
		// configured domain
		List<String> domains = UtilityService.getStoreSelfDomains(storeId);
		for(String domain: domains) {
			if(url.contains(domain)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getQueryKeyword(String url) {
		try {
			List<NameValuePair> args = URLEncodedUtils.parse(url, Charset.defaultCharset());
			for(NameValuePair arg : args) {
				if (arg.getName().equalsIgnoreCase("q")) {
					return arg.getValue();
				}
			}
		} catch(Exception e) {
			logger.warn("Error parsing q for redirect to page url: " + url, e);
		}
		return null;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: support for json if json.nl != map

		ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(execService);
		int tasks = 0;
		DateTime currentDate = DateTime.now();

		try {
			Long start = new Date().getTime();
			NameValuePair nvp;
			final SolrResponseParser solrHelper;
			NameValuePair redirectFqNvp = null;

			String storeId = "";
			try {
				storeId = getStoreId(request);
				solrHelper = getParser(request);
			} catch (HttpException ex) {
				response.sendError(400, ex.getMessage());
				return;
			}
			
			NameValuePair defTypeNVP = new BasicNameValuePair("defType", getDefType(storeId));
			String storeName = configManager.getStoreName(storeId);
			initFieldOverrideMaps(request, solrHelper, storeId);
			logger.debug("Config store name mapped to {}: {}", storeId, storeName);
			
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			HashMap<String, List<NameValuePair>> paramMap = new HashMap<String, List<NameValuePair>>();
			List<NameValuePair> spellcheckParams = new ArrayList<NameValuePair>();
			boolean includeEDP = false;
			boolean includeFacetTemplateFacet = false;
			boolean performSpellCheck = false;

			nvp = new BasicNameValuePair("echoParams", "explicit");
			if (addNameValuePairToMap(paramMap, "echoParams", nvp)) {
				nameValuePairs.add(nvp);
			}
			
			// parse the parameters, construct POST form
			Set<String> paramNames = (Set<String>) request.getParameterMap().keySet();
			for (String paramName : paramNames) {
				for (String paramValue : request.getParameterValues(paramName)) {
					if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_SIMULATE_DATE) && StringUtils.isNotBlank(paramValue)) {
						currentDate = getOverrideCurrentDate(storeId, paramValue);
					} else if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_KEYWORD)) {

						String origKeyword = paramValue;
						if (origKeyword == null) {
							origKeyword = "";
						}

						if (StringUtils.equals("*:*", paramValue)) {
							paramValue = "";
						} else {
							paramValue = StringUtils.trimToEmpty(paramValue).replaceAll("\\s+", " ").replaceAll("[\\p{Cntrl}]", "");
						}

						String convertedKeyword = paramValue;

						logger.info(String.format("CLEANUP %s keyword: %s[%s] %s[%s]", storeName, origKeyword, Hex.encodeHexString(origKeyword.getBytes()), convertedKeyword, Hex.encodeHexString(convertedKeyword.getBytes())));

					} else if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_FACET_FIELD)) {
						if (paramValue.endsWith("_FacetTemplate")) {
							includeFacetTemplateFacet = true;
						}
					} else if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_FIELDS)) {
						if (StringUtils.isNotBlank(paramValue)) {
							String[] fields = paramValue.split(",");
							for (String field : fields) {
								if (StringUtils.equals(field, "*") || StringUtils.equals(field, "EDP")) {
									includeEDP = true;
									break;
								}
							}
							if (!includeEDP) {
								paramValue += ",EDP";
							}
						} else {
							includeEDP = true;
						}
					} else if (paramName.startsWith(SolrConstants.SOLR_PARAM_SPELLCHECK)) {
						if (paramName.equals(SolrConstants.SOLR_PARAM_SPELLCHECK)) {
							performSpellCheck = true;
						}
						nvp = new BasicNameValuePair(paramName, paramValue);
						addNameValuePairToMap(paramMap, paramName, nvp);
						spellcheckParams.add(nvp);
						continue;
					}

					nvp = new BasicNameValuePair(paramName, paramValue);
					if (addNameValuePairToMap(paramMap, paramName, nvp)) {
						nameValuePairs.add(nvp);
					}
				}
			}
			
			//Integrate search within request processor
			new SearchWithinRequestProcessor(new RequestPropertyBean(storeId)).process(request, solrHelper, null, paramMap, nameValuePairs);
			
			boolean fromSearchGui = "true".equalsIgnoreCase(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_GUI));
			addDefaultParameters(storeId, nameValuePairs, paramMap);

			Map<String, String> facetMap = RequestProcessorUtil.getFacetMap(storeId);
			String facetTemplate = facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
			
			if (fromSearchGui) {
				nameValuePairs.add(new BasicNameValuePair(SolrConstants.TAG_FACET_LIMIT, "-1"));
			}
			
			if (generateSearchNav(request) && !includeFacetTemplateFacet) {
				nvp = new BasicNameValuePair(SolrConstants.TAG_FACET_FIELD, facetTemplate);
				if (addNameValuePairToMap(paramMap, SolrConstants.TAG_FACET_FIELD, nvp)) {
					nameValuePairs.add(nvp);
				}
			}

			// grab the keyword
			// patch for rebates search
			// sample: http://afs-pl-schmstr.afservice.org:8080/solr14/pcmall/select
			// ?q=MallIn_RebateFlag%3A1+OR+MacMallRebate_RebateFlag%3A1+OR+Manufacturer_RebateFlag%3A1
			// &facet=true&rows=0&qt=standard&facet.mincount=1&facet.limit=15
			// &facet.field=Manufacturer&facet.field=Platform&facet.field=Category
			String keyword = StringUtils.trimToEmpty(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD));
			String originalKeyword = keyword;
			
			if (StringUtils.isNotBlank(keyword)) {
				// workaround for search compare
				if (keyword.startsWith("DPNo:") || keyword.contains("RebateFlag:") || keyword.startsWith("Manufacturer:")) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD));
					nvp = new BasicNameValuePair("fq", keyword);
					if (addNameValuePairToMap(paramMap, "fq", nvp)) {
						nameValuePairs.add(nvp);
					}
					keyword = "";
				}
			}

			boolean keywordPresent = StringUtils.isNotEmpty(keyword);
			boolean disableElevate = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_ELEVATE) != null;
			boolean disableExclude = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_EXCLUDE) != null;
			boolean disableDemote = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_DEMOTE) != null;
			// Redirect flag
			boolean disableRedirect = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_REDIRECT) != null;
			boolean disableRedirectIdPresent = StringUtils.isNotBlank(request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_REDIRECT));
			String disableRedirectId = disableRedirect ? request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_REDIRECT) : "";
			boolean enableRedirectToPage = request.getParameter(SolrConstants.SOLR_PARAM_ENABLE_REDIRECT_TO_PAGE) != null;
			boolean isRedirectToPage = false;
			
			boolean disableRelevancy = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_RELEVANCY) != null;
			boolean disableFacetSort = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_FACET_SORT) != null;
			final boolean disableDidYouMean = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_DID_YOU_MEAN) != null;
			
			final List<Map<String, String>> activeRules = new ArrayList<Map<String, String>>();
			boolean disableBanner = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_BANNER) != null;

			List<ElevateResult> elevatedList = null;
			List<ElevateResult> forceAddList = new ArrayList<ElevateResult>();
			List<String> expiredElevatedList = new ArrayList<String>();
			List<String> forceAddedEDPs = new ArrayList<String>();
			List<DemoteResult> demoteList = null;
			List<String> expiredDemotedList = new ArrayList<String>();
			List<ExcludeResult> excludeList = null;
			List<String> expiredExcludedList = new ArrayList<String>();
			List<BannerRuleItem> bannerList = null;
			
			String sort = request.getParameter(SolrConstants.SOLR_PARAM_SORT);
			boolean bestMatchFlag = StringUtils.isEmpty(sort) || !(StringUtils.containsIgnoreCase(sort, "price") || StringUtils.containsIgnoreCase(sort, "manufacturer"));
			
			// REDIRECT
			try {
				if (isActiveSearchRule(storeId, RuleEntity.QUERY_CLEANING)) {
					RedirectRule redirect = null;
					RedirectRule originalRedirect = null;
					RedirectRule appliedRedirect = null;

					List<String> keywordHistory = new ArrayList<String>();
					StoreKeyword sk = getStoreKeywordOverride(RuleEntity.QUERY_CLEANING, storeId, keyword);
					while (true) { // look for change keyword
						
						if (StringUtils.isBlank(keyword)) {
							break;
						}
						
						keywordHistory.add(StringUtils.lowerCase(keyword));
						
						redirect = getRedirectRule(sk, fromSearchGui);
						
						if (redirect == null) {
							break;
						}

						if (originalRedirect == null) {
							originalRedirect = redirect;
						}
						
						boolean stop = disableRedirect && (!disableRedirectIdPresent || StringUtils.equals(disableRedirectId, redirect.getRuleId()));
						activeRules.add(RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_REDIRECT, redirect.getRuleId(), redirect.getRuleName(), !stop));

						if (stop) {
							redirect = null;
							break;
						}

						if (!redirect.isRedirectChangeKeyword()) {
							break;
						}

						logger.info("Applying Redirect Rule {} with id {}", redirect.getRuleName(), redirect.getRuleId());
						appliedRedirect = redirect;

						keyword = StringUtils.trimToEmpty(redirect.getChangeKeyword());
						sk.setKeyword(new Keyword(keyword));
						// remove the original keyword
						removeNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_KEYWORD);

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
								
								// set q to original keyword
								resetNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_KEYWORD, keyword);
								
								// tag active redirect rule as loop
								for(Map<String, String> activeRule : activeRules) {
									if(activeRule.containsValue(SolrConstants.TAG_VALUE_RULE_TYPE_REDIRECT)) {
										activeRule.put(SolrConstants.TAG_RULE_ACTIVE, "loop");
									}
								}
								break;
							}
						}
					}
					
					if (redirect != null && !redirect.isRedirectChangeKeyword()) {
						logger.info("Applying redirect rule {} with id {}", redirect.getRuleName(), redirect.getRuleId());
						appliedRedirect = redirect;
						
						if (redirect.isRedirectToPage()) { // Direct Hit
							boolean validRedirectToPage = true;
							
							if(enableRedirectToPage) {
								nvp = new BasicNameValuePair(SolrConstants.REDIRECT_URL, redirect.getRedirectToPage());
								if(addNameValuePairToMap(paramMap, SolrConstants.REDIRECT_URL, nvp)) {
									nameValuePairs.add(nvp);
								}
								originalRedirect = redirect;
								isRedirectToPage = true;
								// validate redirect to page with q parameter
								String url = redirect.getRedirectUrl();
								if(StringUtils.isNotEmpty(url) && isSameDomain(storeId, url) && url.contains("q=")) {
									String sKeyword = getQueryKeyword(url);
									if(StringUtils.isNotBlank(sKeyword) && keywordHistory.contains(StringUtils.lowerCase(sKeyword))) {
										logger.warn("Loop in redirect to page detected. Reverting to original keyword.");
										
										// tag active redirect rule as loop
										for(Map<String, String> activeRule : activeRules) {
											if(activeRule.containsValue(SolrConstants.TAG_VALUE_RULE_TYPE_REDIRECT)) {
												activeRule.put(SolrConstants.TAG_RULE_ACTIVE, "loop");
											}
										}
										
										validRedirectToPage = false;
									}
								}
							} else {
								logger.warn("Redirect To Page is disabled. Reverting to original keyword.");
								// tag active redirect rule as loop
								for(Map<String, String> activeRule : activeRules) {
									if(activeRule.containsValue(SolrConstants.TAG_VALUE_RULE_TYPE_REDIRECT)) {
										activeRule.put(SolrConstants.TAG_RULE_ACTIVE, "disabledRedirectToPage");
									}
								}
								validRedirectToPage = false;
							}
							
							if (validRedirectToPage) {
								isRedirectToPage = true;
								// set number of requested rows to 0
								resetNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_ROWS, "0");
								// set facet to false
								resetNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_FACET, "false");
								// set debugQuery to false
								resetNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_DEBUG_QUERY, "false");
							} else {
								isRedirectToPage = false;
								redirect = null;
								appliedRedirect = null;
								removeNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.REDIRECT_URL);
								
								keyword = originalKeyword;
								sk.setKeyword(new Keyword(keyword));
								
								// set q to original keyword
								resetNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_KEYWORD, keyword);
							}
						} else if (redirect.isRedirectFilter()) { // Filter
							StringBuilder builder = new StringBuilder();
							for (String condition : redirect.getConditions()) {
								if (StringUtils.isNotEmpty(condition)) {
									RedirectRuleCondition rr = new RedirectRuleCondition(condition);
									setFacetTemplateValues(rr, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
									String conditionForSolr = applyValueOverride(rr.getConditionForSolr(), request, RuleEntity.QUERY_CLEANING);
									if (StringUtils.isNotBlank(conditionForSolr)) {
										builder.append("(").append(conditionForSolr).append(") OR ");
									}
								}
							}
							if (builder.length() > 0) {
								builder.delete(builder.length() - 4, builder.length());
								redirectFqNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, builder.toString());
								nameValuePairs.add(redirectFqNvp);
							}
							if (BooleanUtils.isNotTrue(redirect.getIncludeKeyword())) {
								removeNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_KEYWORD);
							}
						}
					}

					if (appliedRedirect != null && originalRedirect != null) {
						appliedRedirect.setRedirectType(originalRedirect.getRedirectType());
						appliedRedirect.setReplaceKeywordMessageCustomText(originalRedirect.getReplaceKeywordMessageCustomText());
						appliedRedirect.setReplaceKeywordMessageType(originalRedirect.getReplaceKeywordMessageType());
						appliedRedirect.setChangeKeyword(keywordHistory.isEmpty() ? "" : keywordHistory.get(keywordHistory.size() - 1));
					}

					solrHelper.setRedirectRule(appliedRedirect);
				}
			} catch (Exception e) {
				logger.error("Failed to get redirect for keyword: {} {}", originalKeyword, e);
			}
			
			if(!isRedirectToPage) {
				// RELEVANCY
				if (isActiveSearchRule(storeId, RuleEntity.RANKING_RULE)) {
					// set relevancy filters if any was specified
					String relevancyId = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_RELEVANCY_ID);
					StoreKeyword sk = getStoreKeywordOverride(RuleEntity.RANKING_RULE, storeId, keyword);
					Relevancy relevancy = null;
					if (StringUtils.isNotBlank(relevancyId)) {
						relevancy = new Relevancy();
						relevancy.setRelevancyId(relevancyId);
						relevancy = getRelevancyRule(sk.getStore(), relevancyId, fromSearchGui);
					} else if (keywordPresent) {
						relevancy = getRelevancyRule(sk, fromSearchGui);
					}
	
					if (relevancy == null) {
						relevancy = getDefaultRelevancyRule(sk.getStore(), fromSearchGui);
					}
	
					if (relevancy != null) {
						activeRules.add(RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_RELEVANCY, relevancy.getRelevancyId(), relevancy.getRelevancyName(), !disableRelevancy));
						if (!disableRelevancy) {
							logger.debug("Applying relevancy {} with id: {}", relevancy.getRelevancyName(), relevancy.getRelevancyId());
						} else {
							logger.debug("Relevancy disabled. Not applying relevancy {} with id: {}", relevancy.getRelevancyName(), relevancy.getRelevancyId());
							relevancy = getDefaultRelevancyRule(sk.getStore(), fromSearchGui);
						}
					} else {
						logger.error("Unable to find default relevancy!");
					}
	
					if (relevancy != null) {
						nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
						nameValuePairs.add(defTypeNVP);
						Map<String, String> parameters = relevancy.getParameters();
						for (String paramName : parameters.keySet()) {
							String paramValue = parameters.get(paramName);
							if (StringUtils.isNotEmpty(paramValue)) {
								paramValue = applyRelevancyOverrides(request, paramName, paramValue);
								logger.debug("Adding Ranking Rule {}: {}", paramName, paramValue);
								nvp = new BasicNameValuePair(paramName, paramValue);
								if (addNameValuePairToMap(paramMap, paramName, nvp)) {
									nameValuePairs.add(nvp);
								}
							}
						}
					} else {
						setDefaultQueryType(request, nameValuePairs, storeId);
					}
				} else {
					Relevancy relevancy = getDefaultRelevancyRule(new Store(storeId), fromSearchGui);
					if (relevancy == null) {
						setDefaultQueryType(request, nameValuePairs, storeId);
					} else {
						nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
						nameValuePairs.add(defTypeNVP);
						Map<String, String> parameters = relevancy.getParameters();
						for (String paramName : parameters.keySet()) {
							String paramValue = parameters.get(paramName);
							if (StringUtils.isNotEmpty(paramValue)) {
								paramValue = applyRelevancyOverrides(request, paramName, paramValue);
								logger.debug("Adding Ranking Rule {}: {}", paramName, paramValue);
								nvp = new BasicNameValuePair(paramName, paramValue);
								if (addNameValuePairToMap(paramMap, paramName, nvp)) {
									nameValuePairs.add(nvp);
								}
							}
						}
					}
				}
	
				if (logger.isDebugEnabled()) {
					for (NameValuePair p : nameValuePairs) {
						logger.debug("Parameter: {}={}", p.getName(), p.getValue());
					}
				}
	
				if (logger.isDebugEnabled()) {
					logger.debug("Store config sort: {}", configManager.getStoreParameter(storeId, "sort"));
					logger.debug("Store requested sort: {}", getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
				}
				
				if (keywordPresent) {
					// EXCLUDE
					if (isActiveSearchRule(storeId, RuleEntity.EXCLUDE)) {
						StoreKeyword sk = getStoreKeywordOverride(RuleEntity.EXCLUDE, storeId, keyword);
						if (!fromSearchGui || isRegisteredKeyword(sk)) {
							activeRules.add(RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_EXCLUDE, keyword, keyword, !disableExclude));
						}
						if (!disableExclude) {
							excludeList = getExcludeRules(sk, fromSearchGui, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
	
							//TODO: refactor to method
							if (fromSearchGui) {
								List<ExcludeResult> expiredList = getExpiredExcludeRules(sk, fromSearchGui, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
								String excludeItem = "";
								for (ExcludeResult expired : expiredList) {
									switch(expired.getExcludeEntity()){
									case PART_NUMBER: excludeItem = expired.getEdp(); break;
									case FACET: excludeItem = expired.getCondition().getConditionForSolr(); break;
									default: 
									}
	
									expiredExcludedList.add(excludeItem);
									logger.debug("Expired Excluded Item: {}" + excludeItem);
								}
							}
						}
					}
	
					// DEMOTE
					if (isActiveSearchRule(storeId, RuleEntity.DEMOTE)) {
						StoreKeyword sk = getStoreKeywordOverride(RuleEntity.DEMOTE, storeId, keyword);
						if (!fromSearchGui || isRegisteredKeyword(sk)) {
							activeRules.add(RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_DEMOTE, keyword, keyword, !disableDemote));
						}
						if (!disableDemote && bestMatchFlag) {
							demoteList = getDemoteRules(sk, fromSearchGui, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
							if (fromSearchGui) {
								List<DemoteResult> expiredList = getExpiredDemoteRules(sk, fromSearchGui, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
								if (logger.isDebugEnabled()) {
									logger.debug("Expired Demoted List: ");
								}
								for (DemoteResult expired : expiredList) {
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
	
					// ELEVATE
					if (isActiveSearchRule(storeId, RuleEntity.ELEVATE)) {
						StoreKeyword sk = getStoreKeywordOverride(RuleEntity.ELEVATE, storeId, keyword);
						if (!fromSearchGui || isRegisteredKeyword(sk)) {
							activeRules.add(RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_ELEVATE, keyword, keyword, !disableElevate));
						}
						if (!disableElevate) {
							elevatedList = getElevateRules(sk, fromSearchGui, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
							if (CollectionUtils.isNotEmpty(elevatedList)) {
								// prepare force added list
								for (ElevateResult elevateResult : elevatedList) {
									if (elevateResult.isForceAdd()) {
										forceAddList.add(elevateResult);
										//TODO:
										if (MemberTypeEntity.PART_NUMBER.equals(elevateResult.getElevateEntity())) {
											forceAddedEDPs.add(elevateResult.getEdp());
										}
									}
								}
							}
							if (!bestMatchFlag) {
								elevatedList.clear();
							} else if (fromSearchGui) { // && bestMatchFlag //TODO: 
								List<ElevateResult> expiredList = getExpiredElevateRules(sk, fromSearchGui, RequestProcessorUtil.getFacetMap(sk.getStoreId()));
								if (logger.isDebugEnabled()) {
									logger.debug("Expired Elevated List: ");
								}
								for (ElevateResult expired : expiredList) {
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
	
					// BANNER
					try {
						bannerList = getActiveBannerRuleItems(new Store(storeId), keyword, fromSearchGui, currentDate);
						if (bannerList != null && bannerList.size() > 0) {
							activeRules.add((RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_BANNER, bannerList.get(0).getRule().getRuleId(), keyword, !disableBanner)));
							if (!disableBanner) {
								solrHelper.setBannerRuleItems(bannerList);
								//TODO: also log expired and non-active banner
							}
						}
					} catch (Exception e) {
						logger.error("Failed to get banner for keyword: {} {}", originalKeyword, e);
					}
				}
			}
			
			/* First Request */
			// run spellcheck if requested and if keyword is present
			performSpellCheck &= StringUtils.isNotBlank(originalKeyword);

			// set Solr URL
			solrHelper.setSolrUrl(getRequestPath(request));
			solrHelper.setSolrQueryParameters(paramMap);
			solrHelper.setElevatedItems(elevatedList);
			solrHelper.setExpiredElevatedEDPs(expiredElevatedList);
			solrHelper.setForceAddedEDPs(forceAddedEDPs);
			solrHelper.setDemotedItems(demoteList);
			solrHelper.setExpiredDemotedEDPs(expiredDemotedList);
			solrHelper.setFacetTemplate(facetTemplate);
			solrHelper.setOriginalKeyword(originalKeyword);
			solrHelper.setIncludeEDP(includeEDP);
			solrHelper.setIncludeFacetTemplateFacet(includeFacetTemplateFacet);

			// remove json.wrf parameter as this is not a JSON standard
			nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_JSON_WRAPPER_FUNCTION));

			// get start row
			int startRow = 0;
			String tmp = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_START);
			if (StringUtils.isNotEmpty(tmp)) {
				startRow = Integer.valueOf(tmp);
				removeNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_START);
			}
			nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, "0");
			addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_START, nvp);
			nameValuePairs.add(nvp);

			// get number of requested rows
			tmp = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_ROWS);
			int requestedRows = StringUtils.isNotEmpty(tmp) && StringUtils.isNumeric(tmp)? Integer.valueOf(tmp) : 25;
			solrHelper.setRequestRows(startRow, requestedRows);
			
			// set number of requested rows to 0
			resetNameValuePairFromMapAndList(paramMap, nameValuePairs, SolrConstants.SOLR_PARAM_ROWS, "0");

			// collate exclude list
			if (excludeList != null && !excludeList.isEmpty()) {
				StringBuilder excludeFilters = toSolrFilterQueryValue(request, excludeList);
				if (excludeFilters.length() > 0) {
					excludeFilters.insert(0, "-");
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeFilters.toString());
					nameValuePairs.add(nvp);
				}
			}

			// send solr request
			solrHelper.setActiveRules(activeRules);

			// create force add filters
			if (CollectionUtils.isNotEmpty(forceAddList)) {
				// generate force add filter
				StringBuilder forceAddFilter = toSolrFilterQueryValue(request, forceAddList);

				if (redirectFqNvp != null) {
					nameValuePairs.remove(redirectFqNvp);
					StringBuilder newRedirectFilter = new StringBuilder();
					newRedirectFilter.append(String.format("(%s) OR (%s)", redirectFqNvp.getValue(), forceAddFilter.toString()));
					nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, newRedirectFilter.toString()));
				}

				NameValuePair keywordNvp = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD);
				if (keywordNvp != null && nameValuePairs.remove(defTypeNVP)) {
					nameValuePairs.remove(keywordNvp);
					StringBuilder newQuery = new StringBuilder();
					newQuery.append(String.format("(_query_:\"{!%s v=$searchKeyword}\") OR (%s)", getDefType(storeId), forceAddFilter.toString()));
					nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, newQuery.toString()));
					nameValuePairs.add(new BasicNameValuePair("searchKeyword", StringUtils.isBlank(keyword) ? "*:*" : keyword));
				}
			}

			// FACETSORT
			if (!isRedirectToPage) {
				RequestPropertyBean requestPropertyBean = new RequestPropertyBean(storeId, keyword, keywordPresent, fromSearchGui, disableFacetSort);
				new FacetSortRequestProcessor(requestPropertyBean).process(request, solrHelper, activeRules, paramMap, nameValuePairs);
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

			nameValuePairs.remove(getNameValuePairFromMap(paramMap, "facet"));

			// TASK 1B - get spellcheck if requested
			/* Run spellcheck if needed */
			if (!isRedirectToPage && performSpellCheck) {
				StoreKeyword sk = getStoreKeywordOverride(RuleEntity.SPELL, storeId, originalKeyword);
				SpellRule spellRule = getSpellRule(sk, fromSearchGui);
				if (spellRule != null) {
					activeRules.add((RequestProcessorUtil.generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_DID_YOU_MEAN, spellRule.getRuleId(), originalKeyword, !disableDidYouMean)));
					if (!disableDidYouMean) {
						solrHelper.setSpellRule(spellRule);
					}
				}
				Integer suggestCount = getMaxSuggestCount(storeId, fromSearchGui);
				if (suggestCount == null) {
					suggestCount = 3;
				}
				solrHelper.setMaxSuggestCount(suggestCount);

				final ArrayList<NameValuePair> getSpellingSuggestionsParams = new ArrayList<NameValuePair>(nameValuePairs);
				getSpellingSuggestionsParams.remove(getNameValuePairFromList(getSpellingSuggestionsParams, SolrConstants.SOLR_PARAM_KEYWORD));
				getSpellingSuggestionsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, originalKeyword));
				getSpellingSuggestionsParams.add(defTypeNVP);
				getSpellingSuggestionsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, "0"));
				getSpellingSuggestionsParams.addAll(spellcheckParams);
				// override spellcheck.count sent from client
				NameValuePair spellCount = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_SPELLCHECK_COUNT);
				if (spellCount != null) {
					getSpellingSuggestionsParams.remove(spellCount);
				}
				getSpellingSuggestionsParams.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_SPELLCHECK_COUNT, String.valueOf(suggestCount)));
				completionService.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception { // TODO here...
						solrHelper.getSpellingSuggestion(getSpellingSuggestionsParams);
						return 0;
					}
				});
				tasks++;
			}

			// Collate Elevate and Demote items 
			StringBuilder elevateFilters = toSolrFilterQueryValue(request, elevatedList);
			StringBuilder demoteFilters = toSolrFilterQueryValue(request, demoteList);

			Integer numFound = 0;
			Integer numElevateFound = 0;
			Integer numNormalFound = 0;
			Integer numDemoteFound = 0;

			// NameValue pair excluding demoted items, empty fq will be added if no demote items
			String excludeDemoteSolrFQValue = (demoteFilters.length() > 0 ? "-": "") + demoteFilters.toString();
			NameValuePair excludeDemoteNameValuePair = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeDemoteSolrFQValue);

			if (bestMatchFlag) {
				nameValuePairs.add(excludeDemoteNameValuePair);

				if (requestedRows != 0 && (elevateFilters.length() > 0)) {
					/* Second Request */
					// set filter to exclude & include elevated list only
					// TASK 1C - get count of elevated items (sans excluded items)
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

				if (requestedRows != 0 && (demoteFilters.length() > 0)) {
					// TASK 1D - get count of demoted items
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

			while (tasks > 0) {
				Future<Integer> completed = completionService.take();
				if (completed.equals(getTemplateCount)) {
					numFound = completed.get();
					logger.debug("Results found: {}", numFound);
				} else if (getElevatedCount != null && completed.equals(getElevatedCount)) {
					numElevateFound += completed.get();
					logger.debug("Elevate result size: {}", numElevateFound);
				} else if (getDemotedCount != null && completed.equals(getDemotedCount)) {
					numDemoteFound = completed.get();
					logger.debug("Demote result size: {}", numDemoteFound);
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
					logger.debug("Total number of elevated found including force added items: {}", numElevateFound);

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
					startRow = startRow < 0 ? 0 : startRow;
				}

				logger.debug("[Normal items] start row: {}", startRow);
				logger.debug("[Normal items] requested rows: {}", requestedRows);

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
					requestedRows = requestedRows < 0 ? 0 : requestedRows;
				}

				// TASK 2C - get demoted items
				if (numFound > 0) {
					startRow -= numNormalFound;
					startRow = startRow < 0 ? 0 : startRow;
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
						logger.debug("Elevated item count: {}", completed.get());
					} else if (completed.equals(getNormalItems)) {
						logger.debug("Normal item count: {}", completed.get());
					} else if (completed.equals(getDemotedItems)) {
						logger.debug("Demoted item count: {}", completed.get());
					}
					tasks--;
				}
			}
			
			/* Generate response */
			Long qtime = new Date().getTime() - start;
			solrHelper.generateServletResponse(response, qtime);
			SearchLogger.logInfo(fromSearchGui, startRow, requestedRows, originalKeyword);
		} catch (Throwable t) {
			logger.error("Failed to send solr request {}", t);
			throw new ServletException(t);
		}
	}
}