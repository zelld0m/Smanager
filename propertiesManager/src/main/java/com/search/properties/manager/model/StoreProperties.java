package com.search.properties.manager.model;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.search.properties.manager.exception.StoreNotFoundException;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * Represents the store contents of the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class StoreProperties {

    private List<Store> stores = ImmutableList.of();
    private static StoreProperties instance;
    public static final String EMPTY_STORE_EXCEPTION_MESSAGE =
            "There seems to be no store loaded. Try calling "
            + "StoreProperties.getInstance().loadStores() first";

    /**
     * Prevent instantiation by other classes.
     */
    private StoreProperties() {
       // NOTHING
    }

    /**
     *
     * @return the singleton instance of this class
     */
    public static StoreProperties getInstance() {
        if (instance == null) {
           instance = new StoreProperties();
        }
        
        return instance;
    }

    /**
     * Load the stores in the store-properties.xml
     */
    public void loadStores() {
        // TODO add code here
        throw new UnsupportedOperationException("No implementation for loadStores() yet");
    }

    /**
     *
     * @return the stores defined in store-properties.xml
     */
    public List<Store> getStores() {
        return stores;
    }

    /**
     * Retrieves the {@link Store} object by storeId
     *
     * @param storeId the storeId
     * @return the store object
     * @throws StoreNotFoundException thrown when the store cannot be found
     */
    public Store getStore(String storeId) throws StoreNotFoundException {

        for (Store store : stores) {
            if (StringUtils.equals(store.getId(), storeId)) {
                return store;
            }
        }

        throw new StoreNotFoundException(String.format("Store %s cannot be found",
                storeId));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("stores", stores).
                toString();
    }
}
