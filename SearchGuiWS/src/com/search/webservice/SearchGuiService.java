package com.search.webservice;

import java.util.Map;

import com.search.webservice.model.TransportList;

public interface SearchGuiService{
	public Map<String,Boolean> deployRulesMap(TransportList list);
	public Map<String,Boolean> unDeployRulesMap(TransportList list);
}
