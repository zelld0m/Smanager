package com.search.ws;

import java.io.IOException;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
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
import com.search.manager.model.SearchResult;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.utility.SearchLogger;

public class SearchServlet extends HttpServlet {

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

	protected ConfigManager configManager;
	
	// these fields should not contain multiple entries
	protected final static String[] uniqueFields = {
			SolrConstants.SOLR_PARAM_ROWS,
			SolrConstants.SOLR_PARAM_KEYWORD,
			SolrConstants.SOLR_PARAM_WRITER_TYPE ,
			SolrConstants.SOLR_PARAM_START
			};

	protected final ExecutorService execService = Executors.newCachedThreadPool();

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
	}

	protected static boolean addNameValuePairToMap(Map<String, List<NameValuePair>> map, String paramName, NameValuePair pair) {
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

	protected static String getValueFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? "" : list.get(0).getValue();
	}

	protected static List<String> getValuesFromNameValuePairMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		List<String> values = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(list)) {
			for (NameValuePair nvp: list) {
				values.add(nvp.getValue());
			}
		}
		return values;
	}
	
	protected static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? null : list.get(0);
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
				return ((DaoService)daoService).getKeyword(storeKeyword.getStoreId(), storeKeyword.getKeywordId()) != null;
			} catch (DaoException e) { }
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
		switch (ruleEntity) {
			case ELEVATE:
				return SolrConstants.REQUEST_ATTRIB_ELEVATE_OVERRIDE_MAP;
			case EXCLUDE:
				return SolrConstants.REQUEST_ATTRIB_EXCLUDE_OVERRIDE_MAP;
			case DEMOTE:
				return SolrConstants.REQUEST_ATTRIB_DEMOTE_OVERRIDE_MAP;
			case QUERY_CLEANING:
				return SolrConstants.REQUEST_ATTRIB_REDIRECT_OVERRIDE_MAP;
			case RANKING_RULE:
				return SolrConstants.REQUEST_ATTRIB_RELEVANCY_OVERRIDE_MAP;
		}
		return "";
	}
	
	protected boolean generateFilterList(HttpServletRequest request, StringBuilder allValues, List<? extends SearchResult> list) {
		StringBuilder edpValues = new StringBuilder();
		StringBuilder facetValues = new StringBuilder();
		boolean withFacetFlag = false;
		boolean edpFlag = false;
		boolean facetFlag = false;
		if (!(list == null || list.isEmpty())) {

			RuleEntity ruleEntity = null;
			SearchResult tmp = list.get(0);
			if (tmp instanceof ElevateResult) {
				ruleEntity = RuleEntity.ELEVATE;
			}
			else if (tmp instanceof ExcludeResult) {
				ruleEntity = RuleEntity.EXCLUDE;
			}
			else if (tmp instanceof DemoteResult) {
				ruleEntity = RuleEntity.DEMOTE;
			}
			
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
					facetValues.append("(").append(applyValueOverride(result.getCondition().getConditionForSolr(), request, ruleEntity)).append(")");
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
	
	protected static Map<String,String> generateActiveRule(String type, String id, String name, boolean active) {
		Map<String,String> activeRule = new HashMap<String,String>();
		activeRule.put(SolrConstants.TAG_RULE_TYPE, type);
		activeRule.put(SolrConstants.TAG_RULE_ID, id);
		activeRule.put(SolrConstants.TAG_RULE_NAME, name);
		activeRule.put(SolrConstants.TAG_RULE_ACTIVE, String.valueOf(active));			
		return activeRule;
	}
	
	protected SearchDaoService getDaoService(boolean fromSearchGui) {
		return fromSearchGui ? daoService : solrService;
	}
	
	protected RedirectRule getRedirectRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRedirectRule(sk);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getRedirectRule(sk);
			}
			throw e;
		}
	}

	protected Relevancy getRelevancyRule(StoreKeyword sk, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRelevancyRule(sk);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getRelevancyRule(sk);
			}
			throw e;
		}
	}

	protected Relevancy getRelevancyRule(Store store, String relevancyId, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getRelevancyRule(store, relevancyId);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getRelevancyRule(store, relevancyId);
			}
			throw e;
		}
	}
	
	protected Relevancy getDefaultRelevancyRule(Store store, boolean fromSearchGui) throws DaoException {
		return getRelevancyRule(store, store.getStoreId() + "_default", fromSearchGui);
	}
	
	protected FacetSort getFacetSortRule(StoreKeyword storeKeyword, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getFacetSortRule(storeKeyword);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getFacetSortRule(storeKeyword);
			}
			throw e;
		}
	}
	
	protected FacetSort getFacetSortRule(Store store, String templateName, boolean fromSearchGui) throws DaoException {
		try {
			return getDaoService(fromSearchGui).getFacetSortRule(store, templateName);
		} catch (DaoException e) {
			if (!fromSearchGui) {
				return daoCacheService.getFacetSortRule(store, templateName);
			}
			throw e;
		}
	}
	
	protected void setFacetTemplateValues(List<? extends SearchResult> list, Map<String, String> facetMap) {
		if (CollectionUtils.isNotEmpty(list)) {
			for (SearchResult e: list) {
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
			list = daoCacheService.getElevateRules(storeKeyword);
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
			list = daoCacheService.getExpiredElevateRules(storeKeyword);
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
			list = daoCacheService.getExcludeRules(storeKeyword);
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
			list = daoCacheService.getExpiredExcludeRules(storeKeyword);
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
			list = daoCacheService.getDemoteRules(storeKeyword);
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
			list = daoCacheService.getExpiredDemoteRules(storeKeyword);
		}
		setFacetTemplateValues(list, facetMap);
		return list;
	}
	
	protected Map<String, String> getFacetMap(String storeId) {
		Map<String, String> facetMap = new HashMap<String, String>();
		facetMap.put(SolrConstants.SOLR_PARAM_FACET_NAME, configManager.getParameterByStoreId(storeId, SolrConstants.SOLR_PARAM_FACET_NAME));
		facetMap.put(SolrConstants.SOLR_PARAM_FACET_TEMPLATE, configManager.getParameterByStoreId(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE));
		facetMap.put(SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME, configManager.getParameterByStoreId(storeId, SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME));
		return facetMap;
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
		for (NameValuePair pair: configManager.getDefaultSolrParameters(storeId)) {
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

		// Verify if request parameter store is a valid store id
		String storeParam = request.getParameter("store");
		String storeIdFromAlias =  configManager.getStoreIdByAliases(storeParam);
		if (StringUtils.isNotBlank(storeParam) && StringUtils.isNotBlank(storeIdFromAlias)) {
			storeId = storeIdFromAlias;
			logger.info(String.format("Request parameter store %s -> %s", storeParam, coreName));
		}
		else {
			logger.info(String.format("Core as storeId: %s", coreName));
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Server name: " + serverName);
			logger.debug("Solr path: " + solr);
			logger.debug("Core name: " + coreName);
			logger.debug("store name: " + storeName);
		}

		return storeId;
	}
	
	protected SolrResponseParser getParser(HttpServletRequest request) throws HttpException {
		// get expected resultformat
		String writerType = request.getParameter(SolrConstants.SOLR_PARAM_WRITER_TYPE);
		if (SolrConstants.SOLR_PARAM_VALUE_JSON.equalsIgnoreCase(writerType)) {
			return new SolrJsonResponseParser();
		}
		else if (StringUtils.isEmpty(writerType) || SolrConstants.SOLR_PARAM_VALUE_XML.equalsIgnoreCase(writerType)
				|| SolrConstants.SOLR_PARAM_VALUE_STANDARD.equalsIgnoreCase(writerType)) {
			return new SolrXmlResponseParser();
		}
		else { // unsupported writer type
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
					configManager.getParameterByStoreId(storeId, SolrConstants.SOLR_PARAM_QUERY_TYPE)));
		}
	}

	protected Relevancy getDefaultRelevancy(String storeId) {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: 
		// remove the exclude list from the result header, update fl to not include EDP if it's not originally part of the request
		// fix if EDP not part of fl
		// support for json if json.nl != map

		ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(execService);
		int tasks = 0;

		try {
			Long start = new Date().getTime();
			NameValuePair nvp;
			final SolrResponseParser solrHelper;
			NameValuePair redirectFqNvp = null; 
			NameValuePair defTypeNVP = new BasicNameValuePair("defType", "dismax");

			String storeId = "";
			try {
				storeId = getStoreId(request);
				solrHelper = getParser(request);
			}
			catch (HttpException ex) {
				response.sendError(400, ex.getMessage());
				return;
			}
			
			String storeName = configManager.getStoreName(storeId);
			initFieldOverrideMaps(request, solrHelper, storeId);
			logger.debug("store name: " + storeName);
			
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
			Set<String> paramNames = request.getParameterMap().keySet();
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
					else if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_FACET_FIELD)) {
						if (paramValue.endsWith("_FacetTemplate")) {
							includeFacetTemplateFacet = true;
						}
					}
					else if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_FIELDS)) {
						if (StringUtils.isNotBlank(paramValue)) {
							String[] fields = paramValue.split(",");
							for (String field: fields) {
								if (StringUtils.equals(field, "*") || StringUtils.equals(field, "EDP")) {
									includeEDP = true;
								}
							}
							if (!includeEDP) {
								paramValue += ",EDP";
							}
						}
						else {
							includeEDP = true;
						}
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

			boolean fromSearchGui = "true".equalsIgnoreCase(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_GUI));
			addDefaultParameters(storeId, nameValuePairs, paramMap);
						
			Map<String, String> facetMap = getFacetMap(storeId);
			String facetTemplate = facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE);
			String facetTemplateName = facetMap.get(SolrConstants.SOLR_PARAM_FACET_TEMPLATE_NAME);
			
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
				if (keyword.startsWith("DPNo:") || keyword.contains("RebateFlag:")) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
					nvp = new BasicNameValuePair("fq", keyword);
					if (addNameValuePairToMap(paramMap, "fq", nvp)) {
						nameValuePairs.add(nvp);
					}
					keyword = "";
				}
			}
			
			boolean keywordPresent = !StringUtils.isEmpty(keyword);
			boolean disableElevate    = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_ELEVATE) != null;
			boolean disableExclude    = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_EXCLUDE) != null;
			boolean disableDemote     = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_DEMOTE) != null;
			boolean disableRedirect   = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_REDIRECT) != null;
			boolean disableRedirectIdPresent = StringUtils.isNotBlank(request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_REDIRECT));
			String  disableRedirectId = disableRedirect ? request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_REDIRECT): "";
			boolean disableRelevancy  = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_RELEVANCY) != null;
			boolean disableFacetSort  = request.getParameter(SolrConstants.SOLR_PARAM_DISABLE_FACET_SORT) != null;
			List<Map<String,String>> activeRules = new ArrayList<Map<String, String>>();
			
			// redirect 
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
						} else {
							if(originalRedirect == null){
								originalRedirect = redirect;
							}
							
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
									setFacetTemplateValues(rr, getFacetMap(sk.getStoreId()));
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
								nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
								paramMap.remove(SolrConstants.SOLR_PARAM_KEYWORD);							
							}
						}
					}
					
					if (appliedRedirect != null && originalRedirect != null) {
						appliedRedirect.setRedirectType(originalRedirect.getRedirectType());
						appliedRedirect.setReplaceKeywordMessageCustomText(originalRedirect.getReplaceKeywordMessageCustomText());
						appliedRedirect.setReplaceKeywordMessageType(originalRedirect.getReplaceKeywordMessageType());
						appliedRedirect.setChangeKeyword(keywordHistory.isEmpty() ? "" : keywordHistory.get(keywordHistory.size()-1));
					}
					
					solrHelper.setRedirectRule(appliedRedirect);
				}

			} catch (Exception e) {
				logger.error("Failed to get redirect for keyword: " + originalKeyword, e);
			}
			
			if (isActiveSearchRule(storeId, RuleEntity.RANKING_RULE)) {
				// set relevancy filters if any was specified
				String relevancyId = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_RELEVANCY_ID);
				StoreKeyword sk = getStoreKeywordOverride(RuleEntity.QUERY_CLEANING, storeId, keyword);
				Relevancy relevancy = null;
				if (StringUtils.isNotBlank(relevancyId)) {
					relevancy = new Relevancy();
					relevancy.setRelevancyId(relevancyId);
					relevancy = getRelevancyRule(sk.getStore(), relevancyId, fromSearchGui);
				}
				else if (keywordPresent) {
					relevancy = getRelevancyRule(sk, fromSearchGui);
				}
				
				if (relevancy == null) {
					relevancy = getDefaultRelevancyRule(sk.getStore(), fromSearchGui);
				}
				
				if (relevancy != null) {
					activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_RELEVANCY, relevancy.getRelevancyId(), relevancy.getRelevancyName(), !disableRelevancy));				
					if (!disableRelevancy) {
						logger.debug("Applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());							
					}
					else {
						logger.debug("Relevancy disabled. Not applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());							
						relevancy = getDefaultRelevancyRule(sk.getStore(), fromSearchGui);
					}
				} else {
					logger.error("Unable to find default relevancy!");
				}

				if (relevancy != null) {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
					nameValuePairs.add(defTypeNVP);
					Map<String, String> parameters = relevancy.getParameters();
					for (String paramName: parameters.keySet()) {
						String paramValue = parameters.get(paramName);
						if (StringUtils.isNotEmpty(paramValue)) {
							paramValue = applyRelevancyOverrides(request, paramName, paramValue);
							logger.debug("adding " + paramName + ": " + paramValue);
							nvp = new BasicNameValuePair(paramName, paramValue);
							if (addNameValuePairToMap(paramMap, paramName, nvp)) {
								nameValuePairs.add(nvp);
							}						
						}
					}
				}
				else {
					setDefaultQueryType(request, nameValuePairs, storeId);
				}
			}
			else {
				
				Relevancy relevancy = getDefaultRelevancy(storeId);
				if (relevancy == null) {
					setDefaultQueryType(request, nameValuePairs, storeId);
				}
				else {
					nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
					nameValuePairs.add(defTypeNVP);
					Map<String, String> parameters = relevancy.getParameters();
					for (String paramName: parameters.keySet()) {
						String paramValue = parameters.get(paramName);
						if (StringUtils.isNotEmpty(paramValue)) {
							paramValue = applyRelevancyOverrides(request, paramName, paramValue);
							logger.debug("adding " + paramName + ": " + paramValue);
							nvp = new BasicNameValuePair(paramName, paramValue);
							if (addNameValuePairToMap(paramMap, paramName, nvp)) {
								nameValuePairs.add(nvp);
							}
						}
					}
				}
			}
			
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(configManager.getStoreParameter(storeId, "sort"));
				logger.debug(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
				logger.debug(">>>>>>>>>>>>>>" + configManager.getStoreParameter(storeId, "sort") + ">>>>>>>>>>>>>>>" + getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
			}
			
			List<ElevateResult> elevatedList = null;
			List<ElevateResult> forceAddList = new ArrayList<ElevateResult>();
			List<String> expiredElevatedList = new ArrayList<String>();
			List<String> forceAddedEDPs = new ArrayList<String>();
			List<DemoteResult> demoteList = null;
			List<String> expiredDemotedList = new ArrayList<String>();
			List<ExcludeResult> excludeList = null;
			List<String> expiredExcludedList = new ArrayList<String>();
			String sort = request.getParameter(SolrConstants.SOLR_PARAM_SORT);
			boolean bestMatchFlag = StringUtils.isEmpty(sort) || !(StringUtils.containsIgnoreCase(sort, "price") ||
							StringUtils.containsIgnoreCase(sort, "manufacturer"));

			if (keywordPresent) {
				
				if (isActiveSearchRule(storeId, RuleEntity.EXCLUDE)) {
					StoreKeyword sk = getStoreKeywordOverride(RuleEntity.EXCLUDE, storeId, keyword);
					if (!fromSearchGui || isRegisteredKeyword(sk)) {
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_EXCLUDE, keyword, keyword, !disableExclude));
					}
					if (!disableExclude) {
						excludeList = getExcludeRules(sk, fromSearchGui, getFacetMap(sk.getStoreId()));
						if (fromSearchGui) {
							List<ExcludeResult> expiredList = getExpiredExcludeRules(sk, fromSearchGui, getFacetMap(sk.getStoreId()));
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
				
				if (isActiveSearchRule(storeId, RuleEntity.DEMOTE)) {
					StoreKeyword sk = getStoreKeywordOverride(RuleEntity.DEMOTE, storeId, keyword);
					if (!fromSearchGui || isRegisteredKeyword(sk)) {
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_DEMOTE,  keyword, keyword, !disableDemote));				
					}
					if (!disableDemote && bestMatchFlag) {
						demoteList = getDemoteRules(sk, fromSearchGui, getFacetMap(sk.getStoreId()));
						if (fromSearchGui) {
							List<DemoteResult> expiredList = getExpiredDemoteRules(sk, fromSearchGui, getFacetMap(sk.getStoreId()));
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
				
				if (isActiveSearchRule(storeId, RuleEntity.ELEVATE)) {
					StoreKeyword sk = getStoreKeywordOverride(RuleEntity.ELEVATE, storeId, keyword);
					if (!fromSearchGui || isRegisteredKeyword(sk)) {
						activeRules.add(generateActiveRule(SolrConstants.TAG_VALUE_RULE_TYPE_ELEVATE, keyword, keyword, !disableElevate));
					}
					if (!disableElevate) {
						elevatedList = getElevateRules(sk, fromSearchGui, getFacetMap(sk.getStoreId()));
						if (CollectionUtils.isNotEmpty(elevatedList)) {
							// prepare force added list
							for (ElevateResult elevateResult : elevatedList) {
								if (elevateResult.isForceAdd()) {
									forceAddList.add(elevateResult);
									if(MemberTypeEntity.PART_NUMBER.equals(elevateResult.getElevateEntity())) {
										forceAddedEDPs.add(elevateResult.getEdp());
									}
								}
							}
						}
						if (!bestMatchFlag) {
							elevatedList.clear();
						}
						else if (fromSearchGui) { // && bestMatchFlag
							List<ElevateResult> expiredList = getExpiredElevateRules(sk, fromSearchGui, getFacetMap(sk.getStoreId()));
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
				generateFilterList(request, excludeFilters, excludeList);
				if (excludeFilters.length() > 0) {
					excludeFilters.insert(0, "-");
					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeFilters.toString());
					nameValuePairs.add(nvp);
				}
			}
			
			// collate elevate list
			StringBuilder elevateFilters = new StringBuilder();
			generateFilterList(request, elevateFilters, elevatedList);
			
			// collate demoted list
			StringBuilder demoteFilters = new StringBuilder();
			generateFilterList(request, demoteFilters, demoteList);

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
				generateFilterList(request, forceAddFilter, forceAddList);

				if (redirectFqNvp != null) {
					nameValuePairs.remove(redirectFqNvp);
					StringBuilder newRedirectFilter = new StringBuilder();
					newRedirectFilter.append("(").append(redirectFqNvp.getValue()).append(") OR (").append(forceAddFilter.toString()).append(")");
					nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, newRedirectFilter.toString()));
				}

				NameValuePair keywordNvp = getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD);
				if (keywordNvp != null) {
					if (nameValuePairs.remove(defTypeNVP)) { // relevancy != null
						nameValuePairs.remove(keywordNvp);
						StringBuilder newQuery = new StringBuilder();
						newQuery.append("(_query_:\"{!dismax v=$searchKeyword}\") ").append(" OR (").append(forceAddFilter.toString()).append(")");
						nameValuePairs.add(new BasicNameValuePair(SolrConstants.SOLR_PARAM_KEYWORD, newQuery.toString()));
						nameValuePairs.add(new BasicNameValuePair("searchKeyword", StringUtils.isBlank(keyword) ? "*:*" : keyword));
					}
				}
			}

			/* FacetSort */
			if (isActiveSearchRule(storeId, RuleEntity.FACET_SORT)) {
				FacetSort facetSort = null;
				boolean applyFacetSort = false;
				StoreKeyword sk = getStoreKeywordOverride(RuleEntity.FACET_SORT, storeId, keyword);
				final ArrayList<NameValuePair> getTemplateNameParams = new ArrayList<NameValuePair>(nameValuePairs);
				for (NameValuePair param: nameValuePairs) {
					if (StringUtils.equals(SolrConstants.SOLR_PARAM_SPELLCHECK, param.getName()) || 
						StringUtils.equals(SolrConstants.TAG_FACET, param.getName()) || 
						StringUtils.equals(SolrConstants.TAG_FACET_MINCOUNT, param.getName()) ||
						StringUtils.equals(SolrConstants.TAG_FACET_LIMIT, param.getName())){
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
					
					try{
						facetTemplateName = solrHelper.getCommonTemplateName(facetTemplateName, getTemplateNameParams);
						if (StringUtils.isNotBlank(facetTemplateName)) {
							facetSort = getFacetSortRule(sk.getStore(), facetTemplateName, fromSearchGui);
						}
					}catch(Exception e){
						logger.error("Failed to get template name", e);
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
