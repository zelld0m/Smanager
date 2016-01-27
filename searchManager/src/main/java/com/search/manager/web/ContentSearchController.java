package com.search.manager.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.core.processor.RequestPropertyBean;
import com.search.manager.core.service.PropertiesService;
import com.search.manager.utility.ParameterUtils;
import com.search.manager.utility.QueryValidator;
import com.search.ws.ContentSearchHelper;
import com.search.ws.SearchException;
import com.search.ws.SolrConstants;
import com.search.ws.SolrResponseParser;

@Controller
public class ContentSearchController extends AbstractSearchController {

	private static final Logger logger = LoggerFactory.getLogger(ContentSearchController.class);
	private final String sections[] = {"one", "two", "three", "four", "five"};
	private final String MODULE = "contents.section.";
	private final String NAME = "name";
	private final String TYPE = "type";
	private final String SHOW_DATE = "showCreatedDate";
	private final String SHOW_IMAGE = "showImage";
	private final String SHOW_AUTHOR = "showAuthor";
	private final String POSITION = "position";
	private final String DISPLAY = "maxDisplay";
	private final String STORE_PATTERN = "http://(.*):.*/(.*)/(.*)_content/contentSearch.*";
	private final String CORE_PATTERN = "http://(.*):.*/(.*)/(.*)/contentSearch.*";
	private String handle = "";

	@Autowired
	private PropertiesService propertiesServce;

	@RequestMapping("/sectionSearch/**")
    public void handleSectionRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

		NameValuePair nvp;
		HashMap<String, List<NameValuePair>> paramMap = new HashMap<String, List<NameValuePair>>();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String storeId = "";
		String wrf = "";
		final ContentSearchHelper searchHelper = new ContentSearchHelper();
		this.handle = "/sectionSearch";

		try {
			storeId = getStoreCore(request, STORE_PATTERN);
			if (!QueryValidator.accept(getStoreCore(request, CORE_PATTERN), request)) {
				response.sendError(400, "Invalid solr query.");
				return;
			}
		} catch (HttpException ex) {
			response.sendError(400, ex.getMessage());
			return;
		}
		searchHelper.setSolrUrl(getRequestPath(request));

		// parse the original parameters
		@SuppressWarnings("unchecked")
		Set<String> paramNames = (Set<String>) request.getParameterMap().keySet();
		for (String paramName : paramNames) {
			for (String paramValue : request.getParameterValues(paramName)) {
				nvp = new BasicNameValuePair(paramName, paramValue);
				if (ParameterUtils.addNameValuePairToMap(paramMap, paramName, nvp, uniqueFields)) {
					nameValuePairs.add(nvp);
					if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_JSON_WRAPPER_FUNCTION)) {
						wrf = paramValue;
					}
				}
			}
		}

		// filter tags as facets
    	String tagSettings = propertiesServce.getProperty(storeId, "contents.filters.tags").getValue();
        List<String> tagList = Arrays.asList(tagSettings.split("\\s*,\\s*"));
        boolean addFacet = false;
    	for (String tag : tagList) {
    		nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FACET_FIELD, tag);
			if (ParameterUtils.addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_FACET_FIELD, nvp, uniqueFields)) {
				nameValuePairs.add(nvp);
				addFacet = true;
			}
		}
    	if (addFacet) {
    		nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FACET, Boolean.toString(addFacet));
			if (ParameterUtils.addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_FACET, nvp, uniqueFields)) {
				nameValuePairs.add(nvp);
			}
    	}

		// get global setting for the content sections
		List<Map<String, String>> sectionProps = new ArrayList<Map<String, String>>();
		for (String section : sections) {
			if (propertiesServce.getProperty(storeId, MODULE + section +".enable").getValue().equalsIgnoreCase("true")) {
				Map<String, String> prop = new HashMap<String, String>();
				prop.put(NAME, propertiesServce.getProperty(storeId, MODULE + section +".name").getValue());
				prop.put(TYPE, propertiesServce.getProperty(storeId, MODULE + section +".type").getValue());
				prop.put(SHOW_DATE, propertiesServce.getProperty(storeId, MODULE + section +".showCreatedDate").getValue());
				prop.put(SHOW_IMAGE, propertiesServce.getProperty(storeId, MODULE + section +".showImage").getValue());
				prop.put(SHOW_AUTHOR, propertiesServce.getProperty(storeId, MODULE + section +".showAuthor").getValue());
				prop.put(POSITION, section);
				prop.put(DISPLAY, propertiesServce.getProperty(storeId, MODULE + DISPLAY).getValue());
				sectionProps.add(prop);
			}
		}

		// generate response
		try {
			searchHelper.generateSectionsResponse(storeId, paramMap, nameValuePairs, sectionProps, response, wrf);
		} catch (SearchException e) {
			logger.error("Failed to send solr request {}", e);
		}
    }

	@RequestMapping("/contentSearch/**")
    public void handleContentRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

		NameValuePair nvp;
		HashMap<String, List<NameValuePair>> paramMap = new HashMap<String, List<NameValuePair>>();
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		String storeId = "";
		String wrf = "";
		final ContentSearchHelper searchHelper = new ContentSearchHelper();
		this.handle = "/contentSearch";

		try {
			storeId = getStoreCore(request, STORE_PATTERN);
			if (!QueryValidator.accept(getStoreCore(request, CORE_PATTERN), request)) {
				response.sendError(400, "Invalid solr query.");
				return;
			}
		} catch (HttpException ex) {
			response.sendError(400, ex.getMessage());
			return;
		}
		searchHelper.setSolrUrl(getRequestPath(request));
		
		// parse the original parameters
		@SuppressWarnings("unchecked")
		Set<String> paramNames = (Set<String>) request.getParameterMap().keySet();
		for (String paramName : paramNames) {
			for (String paramValue : request.getParameterValues(paramName)) {
				nvp = new BasicNameValuePair(paramName, paramValue);
				if (ParameterUtils.addNameValuePairToMap(paramMap, paramName, nvp, uniqueFields)) {
					nameValuePairs.add(nvp);
					if (paramName.equalsIgnoreCase(SolrConstants.SOLR_PARAM_JSON_WRAPPER_FUNCTION)) {
						wrf = paramValue;
					}
				}
			}
		}

		// filter tags as facets
    	String tagSettings = propertiesServce.getProperty(storeId, "contents.filters.tags").getValue();
        List<String> tagList = Arrays.asList(tagSettings.split("\\s*,\\s*"));
        boolean addFacet = false;
    	for (String tag : tagList) {
    		nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FACET_FIELD, tag);
			if (ParameterUtils.addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_FACET_FIELD, nvp, uniqueFields)) {
				nameValuePairs.add(nvp);
				addFacet = true;
			}
		}
    	if (addFacet) {
    		nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FACET, Boolean.toString(addFacet));
			if (ParameterUtils.addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_FACET, nvp, uniqueFields)) {
				nameValuePairs.add(nvp);
			}
    	}

		// generate response
		try {
			searchHelper.generateContentSearchResponse(storeId, paramMap, nameValuePairs, response, wrf);
		} catch (SearchException e) {
			logger.error("Failed to send solr request {}", e);
		}
    }

	protected String getStoreCore(HttpServletRequest request, String pattern) throws HttpException {
		// get the server name, solr path, core name and do mapping for the store name to use for the search
		Pattern pathPattern = Pattern.compile(pattern);
		String requestPath = getRequestPath(request);
		if (StringUtils.isBlank(requestPath)) {
			throw new HttpException("Invalid request");
		}
		Matcher matcher = pathPattern.matcher(requestPath);
		if (!matcher.matches()) {
			throw new HttpException("Invalid request");
		}
		return matcher.group(3);
	}

	@Override
	protected void bannerRequestProcessor(HttpServletRequest request,
			SolrResponseParser solrHelper,
			RequestPropertyBean requestPropertyBean,
			List<Map<String, String>> activeRules,
			Map<String, List<NameValuePair>> paramMap,
			List<NameValuePair> nameValuePairs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getRequestPath(HttpServletRequest request) {
		String start = request.getContextPath() + handle;
        int idx = request.getRequestURI().indexOf(start);
        return "http:/" + request.getRequestURI().substring(start.length() + idx);
	}

}
