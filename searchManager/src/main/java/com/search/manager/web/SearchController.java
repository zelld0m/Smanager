package com.search.manager.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.search.manager.core.processor.RequestProcessor;
import com.search.manager.core.processor.RequestPropertyBean;
import com.search.ws.SolrResponseParser;

@Controller
public class SearchController extends AbstractSearchController {

    @Autowired
    @Qualifier("bannerRequestProcessor")
    private RequestProcessor bannerRequestProcessor;

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

}