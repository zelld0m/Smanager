package com.search.manager.web.service.impl;

import java.util.List;

import net.sf.json.JSONObject;

import org.directwebremoting.annotations.*;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.service.PropertiesService;
import com.search.manager.properties.model.DBProperty;
import com.search.manager.service.UtilityService;
import com.search.manager.web.service.PropertiesDwrService;

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
}
