package com.search.manager.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.core.processor.RequestProcessor;
import com.search.manager.core.processor.RequestPropertyBean;
import com.search.manager.utility.ParameterUtils;
import com.search.manager.utility.PropertiesUtils;
import com.search.ws.SolrConstants;
import com.search.ws.SolrResponseParser;

@Controller
public class SearchController extends AbstractSearchController {

    @Autowired
    @Qualifier("bannerRequestProcessor")
    private RequestProcessor bannerRequestProcessor;
    private final String ORDER_ENTRY_CONDITION = "orderEntryCondition";
    private final String DISCONTINUED_ITEM_FILTER = "discontinuedFilter";

    @RequestMapping("/search/**")
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        super.handleRequest(request, response);
    }

    protected String getRequestPath(HttpServletRequest request) {
        String start = request.getContextPath() + "/search";
        int idx = request.getRequestURI().indexOf(start);
        return "http:/" + request.getRequestURI().substring(start.length() + idx);
    }

    @Override
    protected void bannerRequestProcessor(HttpServletRequest request, SolrResponseParser solrHelper,
            RequestPropertyBean requestPropertyBean, List<Map<String, String>> activeRules,
            Map<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs) {

        bannerRequestProcessor.process(request, solrHelper, requestPropertyBean, activeRules, paramMap, nameValuePairs);

    }

    @Override
    protected void removeDiscontinuedItems(List<NameValuePair> nameValuePairs, HashMap<String, List<NameValuePair>> paramMap, String fqVerify) {
    	String condition = PropertiesUtils.getValue(ORDER_ENTRY_CONDITION);
    	String filter = PropertiesUtils.getValue(DISCONTINUED_ITEM_FILTER);
    	if (condition != null && filter != null && fqVerify.toLowerCase().contains(condition.toLowerCase())) {
    		NameValuePair nvp = new BasicNameValuePair(SolrConstants.SOLR_PARAM_FIELD_QUERY, filter);
    		if (ParameterUtils.addNameValuePairToMap(paramMap, SolrConstants.SOLR_PARAM_FIELD_QUERY, nvp, uniqueFields)) {
				nameValuePairs.add(nvp);
			}
		}
    }
}