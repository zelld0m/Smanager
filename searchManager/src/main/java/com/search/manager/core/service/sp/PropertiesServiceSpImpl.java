package com.search.manager.core.service.sp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.service.PropertiesService;
import com.search.manager.properties.dao.PropertiesDao;
import com.search.manager.properties.model.DBProperty;

@Service(value = "propertiesService")
public class PropertiesServiceSpImpl implements PropertiesService{
	
	@Autowired
	private PropertiesDao propertiesDao;
	
	@Override
	public List<String> getKeys(String store) {
		return propertiesDao.getKeys(store);
	}

	@Override
	public DBProperty getProperty(String store, String key) {
		return propertiesDao.getProperty(store, key);
	}
	
	@Override
	public void save(DBProperty property) {
		propertiesDao.save(property);
	}

	@Override
	public void update(DBProperty property) {
		propertiesDao.update(property);
	}

	@Override
	public void delete(String store, String key) {
		propertiesDao.delete(store, key);
	}

	@Override
	public void delete(DBProperty property) {
		propertiesDao.delete(property);
	}

	@Override
	public List<DBProperty> getAllProperties(String store) {
		return propertiesDao.getAllProperties(store);
	}
	
	
}
