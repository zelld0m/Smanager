package com.search.manager.job;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.search.ws.ConfigManager;

public class TypeaheadSplunkJob {

	@Autowired
	private TypeaheadSplunkParser typeaheadSplunkParser;
	@Autowired
	private ConfigManager configManager;
	
	public void runJob() {
		List<String> storeList = configManager.getStoreNames();
		
		for(String store: storeList) {
			typeaheadSplunkParser.scanSplunkFolder(store);
		}
	}
	
}
