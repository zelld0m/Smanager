package com.search.manager.properties.dao;

import java.util.List;

import com.search.manager.properties.model.DBProperty;

public interface PropertiesDao {

    List<String> getKeys(String store);

    List<DBProperty> getAllProperties(String store);

    DBProperty getProperty(String store, String key);

    void save(DBProperty property);

    void update(DBProperty property);

    void delete(String store, String key);

    void delete(DBProperty property);
}
