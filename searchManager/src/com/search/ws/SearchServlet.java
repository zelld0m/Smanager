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
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.DaoService;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
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

	private static NameValuePair getNameValuePairFromMap(HashMap<String, List<NameValuePair>> paramMap, String paramterName) {
		List<NameValuePair> list = paramMap.get(paramterName);
		return list == null || list.size() == 0 ? null : list.get(0);
	}

	private static void generateExcludeList(StringBuilder stringBuilder, Collection<ExcludeResult> excludeList) {
		if (!(excludeList == null || excludeList.isEmpty())) {
			stringBuilder.append("EDP:(");
			for (ExcludeResult edp: excludeList) {
				stringBuilder.append(" ").append(edp.getEdp());
			}
			stringBuilder.append(")");
		}
	}

	private static void generateElevateList(StringBuilder stringBuilder, Collection<ElevateResult> elevateList) {
		if (!(elevateList == null || elevateList.isEmpty())) {
			stringBuilder.append("EDP:(");
			for (ElevateResult edp: elevateList) {
				stringBuilder.append(" ").append(edp.getEdp());
			}
			stringBuilder.append(")");
		}
	}


	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: find some design pattern (strategy or factory) to reduce code complexity. One for XML, another for JSON format
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
			
			// TODO: workaround for spellchecker
			nvp = new BasicNameValuePair("echoParams", "explicit");
			if (addNameValuePairToMap(paramMap, "echoParams", nvp)) {
				nameValuePairs.add(nvp);
			}

			for (String paramName: paramNames) {
				for (String paramValue: request.getParameterValues(paramName)) {
					nvp = new BasicNameValuePair(paramName, paramValue);
					if (addNameValuePairToMap(paramMap, paramName, nvp)) {
						nameValuePairs.add(nvp);
					}
				}
			}

			// default parameters for the core
			for (NameValuePair pair: ConfigManager.getInstance().getDefaultSolrParameters(coreName)) {
				if (addNameValuePairToMap(paramMap, pair.getName(), pair)) {
					nameValuePairs.add(pair);
				}
			}
			
			// grab the keyword
			String keyword = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD);
			if (StringUtils.isNotBlank(keyword)) {
				// TODO: workaround for search compare
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

			StoreKeyword sk = new StoreKeyword(storeName, keyword);

			boolean fromSearchGui = "true".equalsIgnoreCase(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_GUI));
			
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
					relevancy.setStore(new Store(UtilityService.getStoreName()));
					RecordSet<RelevancyKeyword>relevancyKeywords = daoService.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
							new RelevancyKeyword(new Keyword(keyword), relevancy), new Date(), new Date(), 0, 0),
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
						logger.debug("Applying relevancy " + relevancy.getRelevancyName() + " with id: " + relevancy.getRelevancyId());
					}
					else {
						logger.error("Unable to find default relevancy!");
					}
				}
			}
			
			if (relevancy != null) {
				nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
				nameValuePairs.add(new BasicNameValuePair("defType", "dismax"));
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
				logger.debug(configManager.getStoreParameter(storeName, "sort"));
				logger.debug(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
				logger.info(">>>>>>>>>>>>>>" + configManager.getStoreParameter(storeName, "sort") + ">>>>>>>>>>>>>>>" + getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
			}
	

			List<ElevateResult> elevatedList = null;
			List<String> expiredElevatedList = new ArrayList<String>();
			List<ExcludeResult> excludeList = null;
			
			if (keywordPresent) {
				if (fromSearchGui) {
					if (configManager.getStoreParameter(storeName, "sort").equals(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT))) {
						ElevateResult elevateFilter = new ElevateResult();
						elevateFilter.setStoreKeyword(sk);
						SearchCriteria<ElevateResult> elevateCriteria = new SearchCriteria<ElevateResult>(elevateFilter,new Date(),null,0,0);
						SearchCriteria<ElevateResult> expiredElevateCriteria = new SearchCriteria<ElevateResult>(elevateFilter,null,DateAndTimeUtils.getDateYesterday(),0,0);
		
						if (keywordPresent && configManager.getStoreParameter(storeName, "sort").equals(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT))) {
							elevatedList = daoService.getElevateResultList(elevateCriteria).getList();
							List<ElevateResult> expiredList = daoService.getElevateResultList(expiredElevateCriteria).getList();
							if (logger.isDebugEnabled()) {
								logger.debug("Expired List: ");
							}
							for (ElevateResult expired: expiredList) {
								if (logger.isDebugEnabled()) {
									logger.debug("\t" + expired.getEdp());
								}
								expiredElevatedList.add(expired.getEdp());
							}
						}
					}
					
					ExcludeResult excludeFilter  = new ExcludeResult();
					excludeFilter.setStoreKeyword(sk);
					SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(excludeFilter,new Date(),null,0,0);
					excludeList = daoService.getExcludeResultList(excludeCriteria).getList();
				}
				else {
					elevatedList = daoCacheService.getElevateRules(sk);	
					excludeList = daoCacheService.getExcludeRules(sk);
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
				StringBuilder excludeValues = new StringBuilder();
				generateExcludeList(excludeValues, excludeList);
				if (excludeValues.length() > 0) {
					excludeValues.insert(0, "-");
				}
				// set filter to not include exclude list &fq=-EDP:(5400741)
				nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeValues.toString());
				nameValuePairs.add(nvp);
			}
			
			// collate elevate list
			StringBuilder elevateValues = new StringBuilder();
			generateElevateList(elevateValues, elevatedList);

			// redirect 
			try {
				// TODO: follow up with dba on errors
				if (StringUtils.isNotBlank(sk.getKeywordId())) {
					RedirectRule redirect = (fromSearchGui) ? daoService.getRedirectRule(new RedirectRule(sk.getStoreId(), sk.getKeywordId()))
							: daoCacheService.getRedirectRule(sk);
					if (redirect != null) {
						logger.info("Applying redirect rule " + redirect.getRuleName() + " with id " + redirect.getRuleId());
						if (redirect.isRedirectToPage()) {
							// TODO: fix redirect to page implementation
							nvp = new BasicNameValuePair(SolrConstants.REDIRECT_URL, redirect.getRedirectToPage());
							nameValuePairs.add(nvp);					
						}
						else if (redirect.isRedirectFilter()) {
							nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, redirect.getRedirectFilter());
							nameValuePairs.add(nvp);
							nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
							paramMap.remove(SolrConstants.SOLR_PARAM_KEYWORD);							
						}
					}					
				}
			} catch (Exception e) {
				logger.error("Failed to get redirect for keyword: " + keyword, e);
			}

			BasicNameValuePair elevateNvp = null;
			Integer numFound = 0;
			Integer numElevateFound = 0;

			// send solr request

			// TODO: workaround for spellchecker
			if (StringUtils.isNotBlank(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_KEYWORD))) {
				requestPath = requestPath.replaceFirst("select", "spellCheckCompRH");
			}
			
			// TASK 1A
			final ArrayList<NameValuePair> getTemplateCountParams = new ArrayList<NameValuePair>(nameValuePairs);
			Future<Integer> getTemplateCount = completionService.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return solrHelper.getTemplateCounts(getTemplateCountParams);
				}
			});
			tasks++;

			// TODO: optional remove the spellcheck parameters for succeeding requests
			Future<Integer> getElevatedCount = null;
			if (requestedRows != 0 && elevateValues.length() > 0) {
				/* Second Request */
				// set filter to exclude & include elevated list only
				elevateNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateValues.toString());
				nameValuePairs.add(elevateNvp);

				// TASK 1B
				final ArrayList<NameValuePair> getElevatedCountParams = new ArrayList<NameValuePair>(nameValuePairs);
				getElevatedCount = completionService.submit(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						return solrHelper.getElevatedCount(getElevatedCountParams);
					}
				});
				tasks++;
			}


			while (tasks > 0) {
				Future<Integer> completed = completionService.take();
				if (completed.equals(getTemplateCount)) {
					numFound = completed.get();
					logger.debug("Results found: " + numFound);
				}
				else if (completed.equals(getElevatedCount)) {
					numElevateFound = completed.get();
					logger.debug("Elevate result size: " + numElevateFound);
				}
				tasks--;
			}


			if (requestedRows != 0 && numFound != 0) {

				Future<Integer> getElevatedItems = null;
				Future<Integer> getNonElevatedItems = null;

				logger.debug("number of elevated found:" + numElevateFound);
				nvp = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_ROWS);
				paramMap.remove(SolrConstants.SOLR_PARAM_ROWS);
				nameValuePairs.remove(nvp);
				// TODO: retrieve EDP list from task 1B, then just pass those that fit the criteria as fq
				nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, String.valueOf(numElevateFound));
				nameValuePairs.add(nvp);
				addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_ROWS, nvp);

				// check if elevateList is to be included in this batch
				if (numElevateFound > startRow) {
					// retrieve the elevate list
					// TASK 2A
					final ArrayList<NameValuePair> getElevatedItemsParams = new ArrayList<NameValuePair>(nameValuePairs);
					getElevatedItems = completionService.submit(new Callable<Integer>() {
						@Override
						public Integer call() throws Exception {
							return solrHelper.getElevatedItems(getElevatedItemsParams);
						}
					});
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
						nameValuePairs.remove(elevateNvp);
						elevateValues.insert(0, "-");
						elevateNvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, elevateValues.toString());
						nameValuePairs.add(elevateNvp);
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
			SearchLogger.logInfo(fromSearchGui, startRow, requestedRows, keyword);
		} catch (Throwable t) {
			logger.error("Failed to send solr request", t);
			throw new ServletException(t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
