package com.search.manager.core.processor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;

import com.search.ws.SolrResponseParser;

public interface RequestProcessor {
	public boolean isEnabled();
	public void process(HttpServletRequest request, SolrResponseParser solrHelper, List<Map<String, String>> activeRules, Map<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs);
}