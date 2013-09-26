package com.search.manager.core;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;

public interface RequestProcessor {
	public boolean isEnabled();
	public void process(HttpServletRequest request, Map<String, List<NameValuePair>> paramMap, List<NameValuePair> nameValuePairs);
}