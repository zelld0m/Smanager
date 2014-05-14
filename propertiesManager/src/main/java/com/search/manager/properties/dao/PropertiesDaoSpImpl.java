package com.search.manager.properties.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.search.manager.properties.model.DBProperty;

@Repository(value = "propertiesDAO")
public class PropertiesDaoSpImpl implements PropertiesDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PropertiesDaoSpImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> getKeys(String store) {
        return jdbcTemplate.queryForList("SELECT [KEY] FROM PROPERTIES WHERE STORE_ID = ?", String.class, store);
    }

    @Override
    public List<DBProperty> getAllProperties(String store) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT STORE_ID, [KEY], TYPE, VALUE, CREATED_BY,"
                + " CREATED_STAMP, LAST_UPDATED_BY, LAST_UPDATED_STAMP FROM PROPERTIES WHERE STORE_ID = ?", store);
        List<DBProperty> properties = new ArrayList<DBProperty>();

        for (Map<String, Object> row : rows) {
            DBProperty property = new DBProperty();

            property.setStore((String) row.get("STORE_ID"));
            property.setKey((String) row.get("KEY"));
            property.setType((Integer) row.get("TYPE"));
            property.setValue((String) row.get("VALUE"));
            property.setCreatedBy((String) row.get("CREATED_BY"));
            property.setCreatedDate((Date) row.get("CREATED_STAMP"));
            property.setLastModifiedBy((String) row.get("LAST_UPDATED_BY"));
            property.setLastModifiedDate((Date) row.get("LAST_UPDATED_STAMP"));

            properties.add(property);
        }

        return properties;
    }

    @Override
    public DBProperty getProperty(String store, String key) {
        return jdbcTemplate.query("SELECT STORE_ID, [KEY], TYPE, VALUE, CREATED_BY, CREATED_STAMP, LAST_UPDATED_BY,"
                + " LAST_UPDATED_STAMP FROM PROPERTIES WHERE STORE_ID = ? AND [KEY] = ?", new Object[] {
            store, key
        }, new ResultSetExtractor<DBProperty>() {
            @Override
            public DBProperty extractData(ResultSet res) throws SQLException, DataAccessException {
                if (res.next()) {
                    DBProperty prop = new DBProperty();

                    prop.setStore(res.getString(1));
                    prop.setKey(res.getString(2));
                    prop.setType(res.getInt(3));
                    prop.setValue(res.getString(4));
                    prop.setCreatedBy(res.getString(5));
                    prop.setCreatedDate(res.getDate(6));
                    prop.setLastModifiedBy(res.getString(7));
                    prop.setLastModifiedDate(res.getDate(8));

                    return prop;
                }

                return null;
            }
        });
    }

    @Override
    public void save(DBProperty property) {
        jdbcTemplate.update("INSERT INTO PROPERTIES ( STORE_ID, [KEY], TYPE, VALUE, CREATED_BY, CREATED_STAMP ) "
                + "VALUES ( ?, ?, ?, ?, ?, ? )", new Object[] {
            property.getStore(), property.getKey(), property.getType(), property.getValue(), property.getCreatedBy(),
            property.getCreatedDate()
        });
    }

    @Override
    public void update(DBProperty property) {
        jdbcTemplate.update("UPDATE PROPERTIES SET TYPE = ?, VALUE = ?, LAST_UPDATED_BY = ?, LAST_UPDATED_STAMP = ? "
                + "WHERE STORE_ID = ? AND [KEY] = ?", new Object[] {
            property.getType(), property.getValue(), property.getLastModifiedBy(), property.getLastModifiedDate(),
            property.getStore(), property.getKey()
        });
    }

    @Override
    public void delete(String store, String key) {
        jdbcTemplate.update("DELETE FROM PROPERTIES WHERE STORE_ID = ? AND [KEY] = ?", new Object[] {
            store, key
        });
    }

    @Override
    public void delete(DBProperty property) {
        delete(property.getStore(), property.getKey());
    }
}
