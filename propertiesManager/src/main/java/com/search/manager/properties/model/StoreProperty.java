package com.search.manager.properties.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents a property in a store specific properties file
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
@DataTransferObject(converter = BeanConverter.class)
public class StoreProperty implements java.io.Serializable {

    private static final long serialVersionUID = -5781626632295619121L;
    private String name;
    private String value;

    public StoreProperty() {
    }

    public StoreProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
