package com.search.properties.manager.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.StoreNotFoundException;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import java.util.List;

/**
 * Utility class for {@link PropertiesManager}
 *
 * @author Philip Mark Gutierrez
 * @since September 02, 2013
 * @version 1.0
 */
public class PropertiesManagerUtil {

    /**
     * Checks if the {@link Store} object has a parent
     *
     * @param store the {@link Store} object
     * @return <pre>true</pre> if the store has a parent else <pre>false</pre>
     * @throws NullPointerException when the argument pass is <pre>null</pre>
     */
    public static boolean hasParent(Store store) throws NullPointerException {
        checkNotNull(store);

        return !Strings.isNullOrEmpty(store.getParent());
    }

    /**
     * @param store the {@link Store} object
     * @return the parent of the {@link Store} object provided
     * @throws StoreNotFoundException thrown when the parent of the passed {@link Store}
     * object cannot be found
     */
    public static Store getParent(StoreProperties storeProperties, Store store)
            throws StoreNotFoundException {
        String passedStoreParentId = store.getParent();

        if (hasParent(store)) {
            List<Store> stores = storeProperties.getStores();
            for (Store theStore : stores) {
                if (theStore.getId().equals(passedStoreParentId)) {
                    return theStore;
                }
            }
        }

        throw new StoreNotFoundException(String.format(
                "The parent of %s cannot be found.", passedStoreParentId));
    }
}
