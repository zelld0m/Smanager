package com.search.properties.manager.model;

import com.google.common.collect.Lists;
import java.util.List;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents a store specific properties file
 *
 * @author Philip Mark Gutierrez
 * @since September 04, 2013
 * @version 1.0
 */
@DataTransferObject(converter = BeanConverter.class)
public class StorePropertiesFile implements java.io.Serializable {

    private static final long serialVersionUID = -4015343634641306977L;
    private List<StoreProperty> storeProperties = Lists.newArrayList();

    public StorePropertiesFile() {
    }

    public StorePropertiesFile(List<StoreProperty> storeProperties) {
        this.storeProperties = storeProperties;
    }

    public List<StoreProperty> getStoreProperties() {
        return storeProperties;
    }

    public void setStoreProperties(List<StoreProperty> storeProperties) {
        this.storeProperties = storeProperties;
    }
}
