package com.search.manager.core.service;

import java.util.List;

import com.search.manager.properties.model.DBProperty;


public interface PropertiesService {

	public List<String> getKeys(String store);
	
	public DBProperty getProperty(String store, String key);
	
	public void save(DBProperty property);
	
	public void update(DBProperty property);
	
	public void delete(String store, String key);

	public void delete(DBProperty property);

	
	public List<DBProperty> getAllProperties(String store);
}
