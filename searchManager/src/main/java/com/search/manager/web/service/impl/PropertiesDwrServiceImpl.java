package com.search.manager.web.service.impl;

import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.directwebremoting.annotations.*;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.service.PropertiesService;
import com.search.manager.properties.model.DBProperty;
import com.search.manager.service.UtilityService;
import com.search.manager.web.service.PropertiesDwrService;
import com.search.ws.ConfigManager;

@Service(value = "propertiesDwrService")
@RemoteProxy(
		name = "PropertiesServiceJS",
		creator = SpringCreator.class,
		creatorParams =
		@Param(name = "beanName", value = "propertiesDwrService"))
public class PropertiesDwrServiceImpl implements PropertiesDwrService{

	@Autowired
	private UtilityService utilityService;
	@Autowired
	private PropertiesService propertiesServce;
	@Autowired
	private ConfigManager configManager;
	
	@RemoteMethod
	public String getStoreProperties() {
		String store = utilityService.getStoreId();
		List<DBProperty> properties = propertiesServce.getAllProperties(store);
		
		JSONObject json = new JSONObject();
		
		for(DBProperty property : properties) {
			json.put(property.getKey(), property.getValue());
			
		}
		
		return json.toString();
	}
	
	@RemoteMethod
	public String getAllStoreProperties() {
		Set<String> storeList = configManager.getAllStoresDisplayName().keySet();
		
		JSONObject storeMap = new JSONObject();
		
		for(String store: storeList) {
			List<DBProperty> properties = propertiesServce.getAllProperties(store);
			JSONObject storeProperties = new JSONObject();
			JSONObject solrConfig = new JSONObject();
			JSONObject json = new JSONObject();
			
			for(DBProperty property : properties) {
				storeProperties.put(property.getKey(), property.getValue());
			}
			
			for(NameValuePair pair : configManager.getDefaultSolrParameters(store)) {
				solrConfig.put(pair.getName(), pair.getValue());
			}
			
			json.put("storeProperties", storeProperties);
			json.put("storeSolrParameters", solrConfig);
			json.put("storeParameters", utilityService.getSourceStoreParameters(store));
			json.put("solrConfig", utilityService.getSourceSolrConfig(store));
			storeMap.put(store, json);
		}
		
		
		
		return storeMap.toString();
	}
	
	@RemoteMethod
	public String getDefaultSolrParameters() {
		String store = utilityService.getStoreId();
		

		JSONObject json = new JSONObject();
		
		for(NameValuePair pair : configManager.getDefaultSolrParameters(store)) {
			json.put(pair.getName(), pair.getValue());
		}
						
		return json.toString();
	}
}
