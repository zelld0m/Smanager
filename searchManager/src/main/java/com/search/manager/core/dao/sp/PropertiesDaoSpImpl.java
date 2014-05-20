package com.search.manager.core.dao.sp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.PropertiesDao;
import com.search.manager.core.model.Property;

@Repository(value = "propertiesDAO")
public class PropertiesDaoSpImpl implements PropertiesDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PropertiesDaoSpImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> getKeys(String store) {
        return jdbcTemplate.queryForList("SELECT KEY FROM PROPERTIES WHERE STORE_ID = ?", String.class, store);
    }

    @Override
    public Property getProperty(String store, String key) {
        return jdbcTemplate.query("SELECT STORE_ID, KEY, TYPE, VALUE, CREATED_BY, CREATED_STAMP, LAST_MODIFIED_BY,"
                + " LAST_MODIFIED_STAMP FROM PROPERTIES WHERE STORE_ID = ? AND KEY = ?", new Object[] {
            store, key
        }, new ResultSetExtractor<Property>() {
            @Override
            public Property extractData(ResultSet res) throws SQLException, DataAccessException {
                if (res.next()) {
                    Property prop = new Property();

                    prop.setStore(res.getString(1));
                    prop.setKey(res.getString(2));
                    prop.setType(res.getInt(3));
                    prop.setValue(res.getString(4));
                    prop.setCreatedBy(res.getString(5));
                    prop.setCreatedDate(new DateTime(res.getDate(6)));
                    prop.setLastModifiedBy(res.getString(7));
                    prop.setLastModifiedDate(new DateTime(res.getDate(8)));

                    return prop;
                }

                return null;
            }
        });
    }

    @Override
    public void save(Property property) {
        jdbcTemplate.update("INSERT INTO PROPERTIES ( STORE_ID, KEY, TYPE, VALUE, CREATED_BY, CREATED_DATE ) "
                + "VALUES ( ?, ?, ?, ?, ?, ? )", new Object[] {
            property.getStore(), property.getKey(), property.getType(), property.getValue(), property.getCreatedBy(),
            property.getCreatedDate().toDate()
        });
    }

    @Override
    public void update(Property property) {
        jdbcTemplate.update("UPDATE PROPERTIES SET TYPE = ?, VALUE = ?, LAST_MODIFIED_BY = ?, LAST_MODIFIED_STAMP = ? "
                + "WHERE STORE_ID = ? AND KEY = ?", new Object[] {
            property.getType(), property.getValue(), property.getLastModifiedBy(),
            property.getLastModifiedDate().toDate(), property.getStore(), property.getKey()
        });
    }

    @Override
    public void delete(String store, String key) {
        jdbcTemplate.update("DELETE FROM PROPERTIES WHERE STORE_ID = ? AND KEY = ?", new Object[] {
            store, key
        });
    }

    @Override
    public void delete(Property property) {
        delete(property.getStore(), property.getKey());
    }
}
