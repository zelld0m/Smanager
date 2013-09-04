package com.search.properties.manager.util;

import com.google.common.base.Strings;
import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.ModuleNotFoundException;
import com.search.properties.manager.exception.StoreNotFoundException;
import com.search.properties.manager.exception.StorePropertyNotFoundException;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
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
     */
    public static boolean hasParent(Store store) {
        return !Strings.isNullOrEmpty(store.getParent());
    }

    /**
     * @param store the {@link Store} object
     * @param storeProperties the {@link StoreProperties} object
     * @return the parent of the {@link Store} object provided
     * @throws StoreNotFoundException thrown when the parent of the passed {@link Store}
     * object cannot be found
     */
    public static Store getParent(Store store, StoreProperties storeProperties)
            throws StoreNotFoundException {
        String passedStoreParentId = store.getParent();

        if (hasParent(store)) {
            return getStoreById(passedStoreParentId, storeProperties);
        }

        throw new StoreNotFoundException(String.format(
                "The parent of %s cannot be found.", passedStoreParentId));
    }

    /**
     * @param storeId the store id
     * @param storeProperties the {@link StoreProperties} object
     * @return the {@link Store} object based from the store id passed
     * @throws StoreNotFoundException
     */
    public static Store getStoreById(String storeId, StoreProperties storeProperties)
            throws StoreNotFoundException {

        List<Store> stores = storeProperties.getStores();

        for (Store store : stores) {
            if (store.getId().equals(storeId)) {
                return store;
            }
        }

        throw new StoreNotFoundException(String.format("Cannot find the store %s",
                storeId));
    }

    /**
     *
     * @param name the name of the store property to find
     * @param storePropertiesFile the {@link StorePropertiesFile} object
     * @return the {@link StoreProperty} object based from a name
     * @throws StorePropertyNotFoundException thrown when the store property cannot be
     * found
     */
    public static StoreProperty getStorePropertyByName(String name,
            StorePropertiesFile storePropertiesFile)
            throws StorePropertyNotFoundException {
        List<StoreProperty> storeProperties = storePropertiesFile.getStoreProperties();
        for (StoreProperty storeProperty : storeProperties) {
            if (storeProperty.getName().equals(name)) {
                return storeProperty;
            }
        }

        throw new StorePropertyNotFoundException(String.format(
                "Store property with the name %s not found on neither "
                + "store-properties.xml nor on the properties file %s", 
                name, storePropertiesFile.getFilePath()));
    }

    /**
     *
     * @param name the name of the module to find
     * @param store the {@link Store} object
     * @return the {@link Module} object based from a name
     * @throws ModuleNotFoundException thrown when the module with the passed name cannot
     * be found
     */
    public static Module getModuleByName(String name, Store store)
            throws ModuleNotFoundException {

        List<Module> modules = store.getModules();
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }

        throw new ModuleNotFoundException(String.format(
                "module with the name %s cannot be found on %s", name, store.getId()));
    }
}
