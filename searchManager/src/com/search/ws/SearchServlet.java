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
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;

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

	private static void putNameValuePairToMap(HashMap<String, List<NameValuePair>> map, String paramName, NameValuePair pair) {
		map.put(paramName, new ArrayList<NameValuePair>());
		map.get(paramName).add(pair);
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
			logger.debug("Request path: " + requestPath);

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
			boolean keywordPresent = !StringUtils.isEmpty(keyword);

			// set relevancy filters if any was specified
			nvp = getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_RELEVANCY_ID);
			if (nvp != null) {
				// remove qt parameter
				nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_QUERY_TYPE));
				// add all relevancy fields
				Relevancy relevancy = new Relevancy();
				String relevancyId = nvp.getValue();
				if (StringUtils.isNotEmpty(StringUtils.trim(relevancyId))) {
					relevancy.setRelevancyId(nvp.getValue());
				}
				else {
					//exec usp_Search_Relevancy_Prod_Keyword_Relationship 'pcmall', 'relevancytest3',2,'apple',1,null,null,0,0;
					RelevancyKeyword rk = new RelevancyKeyword();
					Relevancy r = new Relevancy();
					r.setRelevancyName("");
					r.setStore(new Store(coreName));
					if (StringUtils.isNotEmpty(keyword)) {
						rk.setKeyword(new Keyword(keyword));
					}
					SearchCriteria<RelevancyKeyword> criteria = new SearchCriteria<RelevancyKeyword>(rk, new Date(), new Date(), 0, 0);
					
					RecordSet<RelevancyKeyword> result = daoService.searchRelevancyKeywords(criteria, MatchType.LIKE_NAME, ExactMatch.MATCH);
					if (result == null || result.getTotalSize() == 0) {
						relevancy.setRelevancyId(coreName + "_" + "default");
					}
					else {
						relevancy.setRelevancyId(result.getList().get(0).getRelevancy().getRelevancyId());
					}
				}
				logger.debug("Retrieving relevancy with id: " + relevancy.getRelevancyId());
				//relevancy = daoService.getRelevancyDetails(relevancy);
				relevancy = daoCacheService.getRelevancyDetails(relevancy,storeName);

				if (relevancy != null) {
					nameValuePairs.add(new BasicNameValuePair("defType", "dismax"));
					Map<String, String> parameters = relevancy.getParameters();
					for (String paramName: parameters.keySet()) {
						String paramValue = parameters.get(paramName);
						logger.debug("adding " + paramName + ": " + paramValue);
						nvp = new BasicNameValuePair(paramName, paramValue);
						if (addNameValuePairToMap(paramMap, paramName, nvp)) {
							nameValuePairs.add(nvp);
						}
					}
				}
				else {
					// TODO: get default dismax
				}
			}
			
			if (logger.isDebugEnabled()) {
				for (NameValuePair p: nameValuePairs) {
					logger.debug("Parameter: " + p.getName() + "=" + p.getValue());
				}
			}

			List<ElevateResult> elevatedList = null;
			List<String> expiredElevatedList = new ArrayList<String>();

			if (logger.isDebugEnabled()) {
				logger.debug(configManager.getStoreParameter(storeName, "sort"));
				logger.debug(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
				logger.info(">>>>>>>>>>>>>>" + configManager.getStoreParameter(storeName, "sort") + ">>>>>>>>>>>>>>>" + getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT));
			}

			ElevateResult elevateFilter = new ElevateResult();
			ExcludeResult excludeFilter  = new ExcludeResult();
			StoreKeyword sk = new StoreKeyword(storeName, keyword);
			elevateFilter.setStoreKeyword(sk);
			excludeFilter.setStoreKeyword(sk);
			SearchCriteria<ElevateResult> elevateCriteria = new SearchCriteria<ElevateResult>(elevateFilter,new Date(),null,0,0);
			SearchCriteria<ElevateResult> expiredElevateCriteria = new SearchCriteria<ElevateResult>(elevateFilter,null,DateAndTimeUtils.getDateYesterday(),0,0);
			SearchCriteria<ExcludeResult> excludeCriteria = new SearchCriteria<ExcludeResult>(excludeFilter,new Date(),null,0,0);
	
			if (keywordPresent && configManager.getStoreParameter(storeName, "sort").equals(getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_SORT))) {
				//elevatedList = daoService.getElevateResultList(elevateCriteria).getList();
				elevatedList = daoCacheService.getElevateResultList(elevateCriteria,storeName);
				
				List<ElevateResult> expiredList = daoCacheService.getElevateResultList(expiredElevateCriteria,storeName);
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
			if (elevatedList == null){
				elevatedList = new ArrayList<ElevateResult>();
			}

			List<ExcludeResult> excludeList = keywordPresent ? daoService.getExcludeResultList(excludeCriteria).getList() : null;

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
			}
			nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_START, "0");
			putNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_START, nvp);
			nameValuePairs.add(nvp);

			// get number of requested rows
			int requestedRows = 25;
			tmp = getValueFromNameValuePairMap(paramMap, SolrConstants.SOLR_PARAM_ROWS);
			if (!StringUtils.isEmpty(tmp)) {
				requestedRows = Integer.valueOf(tmp);
			}
			nameValuePairs.remove(getNameValuePairFromMap(paramMap, SolrConstants.SOLR_PARAM_ROWS));
			solrHelper.setRequestRows(startRow, requestedRows);

			// set number of requested rows to 0
			nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_ROWS, "0");
			putNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_ROWS, nvp);
			nameValuePairs.add(nvp);

			// collate exclude list
			StringBuilder excludeValues = new StringBuilder();
			generateExcludeList(excludeValues, excludeList);
			if (excludeValues.length() > 0) {
				excludeValues.insert(0, "-");
			}
			// collate elevate list
			StringBuilder elevateValues = new StringBuilder();
			generateElevateList(elevateValues, elevatedList);

			// set filter to not include exclude list &fq=-EDP:(5400741)
			nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, excludeValues.toString());
			nameValuePairs.add(nvp);

			// redirect 
			//TODO change to storename
			// TODO: fix
			RedirectRule redirect = daoService.getRedirectRule(new SearchCriteria<RedirectRule>(new RedirectRule(sk.getStoreId(), sk.getKeywordId()), null, null, 0, 0));
			
			if (redirect != null) {
				// TODO: implement
//				nvp = new BasicNameValuePair(SolrConstants.REDIRECT_URL, redirectUrl);
//				nameValuePairs.add(nvp);
//				
//				String redirectFQ = redirectUtility.getRedirectFQ("macmall" + keyword);
//				if (!StringUtils.isBlank(redirectFQ)) {
//					nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, redirectFQ);
//					nameValuePairs.add(nvp);
//					nameValuePairs.remove(getNameValuePairFromMap(paramMap,SolrConstants.SOLR_PARAM_KEYWORD));
//				}				
			}

			BasicNameValuePair elevateNvp = null;
			Integer numFound = 0;
			Integer numElevateFound = 0;

			// send solr request
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

				logger.debug("***************************requestedRows:" + numElevateFound);
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

				logger.debug("***************************requestedRows:" + requestedRows);
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
		} catch (Throwable t) {
			logger.error("Failed to send solr request", t);
			throw new ServletException(t);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
