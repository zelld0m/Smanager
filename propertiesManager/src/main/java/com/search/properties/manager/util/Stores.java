package com.search.properties.manager.util;

import com.google.common.base.Strings;
import com.search.properties.manager.exception.NotDirectoryException;
import com.search.properties.manager.exception.PropertyException;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import java.io.File;
import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 7, 2013
 * @version 1.0
 */
public class Stores {

    /**
     * @param storeSaveLocation the save store location
     * @param storeId the store id
     * @param moduleName the module name
     * @throws NotDirectoryException thrown when <i>storeSaveLocation</i> is not a
     * directory
     * @return the formatted store properties file location based on the parameters passed
     */
    public static String getFormattedSaveLocation(String storeSaveLocation,
            String storeId, String moduleName)
            throws NotDirectoryException {

        File file = new File(storeSaveLocation);
        if (!file.isDirectory()) {
            throw new NotDirectoryException(file, String.format("%s is not a directory",
                    storeSaveLocation));
        }

        return String.format("%s/%s/%2$s.%s.properties", storeSaveLocation, storeId, 
                moduleName);
    }

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
     * @throws PropertyException thrown when the parent of the passed {@link Store} object
     * cannot be found
     */
    public static Store getParent(Store store, StoreProperties storeProperties)
            throws PropertyException {
        String passedStoreParentId = store.getParent();

        if (hasParent(store)) {
            return getStoreById(passedStoreParentId, storeProperties);
        }

        throw new PropertyException(String.format(
                "The parent of %s cannot be found.", passedStoreParentId));
    }

    /**
     * @param storeId the store id
     * @param storeProperties the {@link StoreProperties} object
     * @return the {@link Store} object based from the store id passed
     * @throws PropertyException
     */
    public static Store getStoreById(String storeId, StoreProperties storeProperties)
            throws PropertyException {

        List<Store> stores = storeProperties.getStores();

        for (Store store : stores) {
            if (store.getId().equals(storeId)) {
                return store;
            }
        }

        throw new PropertyException(String.format("Cannot find the store %s",
                storeId));
    }

    /**
     *
     * @param name the name of the store property to find
     * @param storePropertiesFile the {@link StorePropertiesFile} object
     * @return the {@link StoreProperty} object based from a name
     * @throws PropertyException thrown when the store property cannot be found
     */
    public static StoreProperty getStorePropertyByName(String name,
            StorePropertiesFile storePropertiesFile)
            throws PropertyException {
        List<StoreProperty> storeProperties = storePropertiesFile.getStoreProperties();
        for (StoreProperty storeProperty : storeProperties) {
            if (storeProperty.getName().equals(name)) {
                return storeProperty;
            }
        }

        throw new PropertyException(String.format(
                "Store property with the name %s not found on neither "
                + "store-properties.xml nor on the properties file %s",
                name, storePropertiesFile.getFilePath()));
    }
}
