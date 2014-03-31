package com.search.manager.job;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.search.ws.ConfigManager;

public class TypeaheadSplunkJob {

	@Autowired
	private TypeaheadSplunkParser typeaheadSplunkParser;
	@Autowired
	private ConfigManager configManager;
	
	public void runJob() {
		Set<String> storeList = configManager.getAllStoresDisplayName().keySet();
		
		for(String store: storeList) {
			typeaheadSplunkParser.scanSplunkFolder(store);
		}
	}
	
}
