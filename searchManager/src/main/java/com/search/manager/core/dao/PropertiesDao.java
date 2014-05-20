package com.search.manager.core.dao;

import java.util.List;

import com.search.manager.core.model.Property;

public interface PropertiesDao {

    List<String> getKeys(String store);

    Property getProperty(String store, String key);

    void save(Property property);

    void update(Property property);

    void delete(String store, String key);

    void delete(Property property);
}
