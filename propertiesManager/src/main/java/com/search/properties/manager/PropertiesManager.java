package com.search.properties.manager;

import com.google.common.collect.Lists;
import com.search.properties.manager.exception.NotDirectoryException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.properties.manager.exception.PropertyException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Member;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import com.search.properties.manager.util.PropertiesManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Controls the loading and reading of the store properties.
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class PropertiesManager {

    private static final Logger logger =
            LoggerFactory.getLogger(PropertiesManager.class);
    @Autowired
    private SolrXmlReader solrXmlReader;
    private String storePropertiesLocation;
    private String storePropertiesSaveLocation;

    public PropertiesManager(String storePropertiesLocation,
            String storePropertiesSaveLocation) {
        this.storePropertiesLocation = storePropertiesLocation;
        this.storePropertiesSaveLocation = storePropertiesSaveLocation;
    }

    public String getStorePropertiesLocation() {
        return storePropertiesLocation;
    }

    public void setStorePropertiesLocation(String storePropertiesLocation) {
        this.storePropertiesLocation = storePropertiesLocation;
    }

    public String getStorePropertiesSaveLocation() {
        return storePropertiesSaveLocation;
    }

    public void setStorePropertiesSaveLocation(String storePropertiesSaveLocation) {
        this.storePropertiesSaveLocation = storePropertiesSaveLocation;
    }

    /**
     * @return a {@link StoreProperties} object read from store-properties.xml
     * @throws PropertyException thrown when store-properties.xml is invalid
     */
    public StoreProperties getStoreProperties() throws PropertyException {
        StoreProperties storeProperties = getStorePropertiesFromXML();
        List<Store> stores = storeProperties.getStores();

        // removes the unexisting store in the StoreProperties object
        removeUnexistingStoreById(storeProperties);

        for (Store store : stores) {
            if (PropertiesManagerUtil.hasParent(store)) {
                Store parentStore = PropertiesManagerUtil.getParent(store,
                        storeProperties);

                // add the parent store modules
                addParentStoreModules(parentStore, store);
            }
        }

        return storeProperties;
    }

    /**
     * This helper method removes the store that is not on the store ids passed
     *
     * @param storeProperties the {@link StoreProperties} object
     */
    private void removeUnexistingStoreById(StoreProperties storeProperties) {
        List<Store> revisedStoreList = Lists.newArrayList();
        List<String> storeIds = solrXmlReader.getStoreIds();

        for (String storeId : storeIds) {
            try {
                Store store = PropertiesManagerUtil.getStoreById(storeId,
                        storeProperties);
                revisedStoreList.add(store);
            } catch (PropertyException e) {
                // NOTHING TO DO HERE!
            }
        }

        storeProperties.setStores(revisedStoreList);
    }

    /**
     * @return a {@link StoreProperties} object read from store-properties.xml
     * @throws PropertyException thrown when store-properties.xml is invalid
     */
    public StoreProperties getStorePropertiesFromXML() throws PropertyException {
        try {
            File file = new File(storePropertiesLocation);
            JAXBContext context = JAXBContext.newInstance(
                    Group.class, Member.class, Module.class, Property.class,
                    Store.class, StoreProperties.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (StoreProperties) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            logger.error("Unable to unmarshall the XML to an object", e);
        }

        throw new PropertyException("Store Properties XML cannot be loaded");
    }

    /**
     * Creates the appropriate properties files if not existing yet with initial
     * properties from store-properties.xml
     */
    public void saveStoreProperties() {
        StoreProperties storeProperties = getStoreProperties();
        List<Store> stores = storeProperties.getStores();

        for (Store store : stores) {
            // save the store properties by store modules
            savePropertiesByModule(store);
        }
    }

    /**
     * Saves the store properties files based from the {@link Module} objects of a
     * {@link Store}
     *
     * @param store the {@link Store} object
     */
    private void savePropertiesByModule(Store store) {
        String storeId = store.getId();
        List<Module> modules = store.getModules();

        for (Module module : modules) {
            Properties properties = getPropertiesByModule(module, storeId);
            String moduleName = module.getName();

            try {
                String filePath = PropertiesManagerUtil.getFormattedSaveLocation(
                        getStorePropertiesSaveLocation(), storeId, moduleName);

                // save the properties file to the appropriate directory
                savePropertiesFile(properties, filePath, false);
            } catch (NotDirectoryException e) {
                logger.error(String.format("%s is not a valid file path",
                        e.getFile().getPath()), e);
            }
        }
    }

    /**
     * Helper method for creating a {@link Properties} object based from a {@link Module}
     * object
     *
     * @param module the {@link Module} object
     * @param storeId the store id
     * @return {@link Properties} object populated with the properties of a {@link Module}
     * object
     */
    private Properties getPropertiesByModule(Module module, String storeId) {
        Properties properties = new Properties();

        List<Property> moduleProperties = module.getProperties();
        for (Property property : moduleProperties) {
            String propertyId = property.getId();
            String propertyDefaultValue = property.getDefaultValue();

            properties.put(propertyId, propertyDefaultValue);
        }

        return properties;
    }

    /**
     * Saves the store properties to their designated store specific properties file
     *
     * @param storePropertiesFiles the store properties to save
     */
    public void saveStoreProperties(List<StorePropertiesFile> storePropertiesFiles) {
        for (StorePropertiesFile storePropertiesFile : storePropertiesFiles) {
            Properties properties = getPropertiesByStorePropertiesFile(
                    storePropertiesFile);

            // save the properties file
            savePropertiesFile(properties, storePropertiesFile.getFilePath());
        }
    }

    /**
     * Helper method for creating a {@link Properties} object based from the
     * {@link Property} objects of a {@link StorePropertiesFile} object
     *
     * @param storePropertiesFile the {@link StorePropertiesFile} object
     * @return {@link Properties} object populated with the properties of a
     * {@link StorePropertiesFile} object
     */
    private Properties getPropertiesByStorePropertiesFile(
            StorePropertiesFile storePropertiesFile) {
        Properties properties = new Properties();
        List<StoreProperty> storeProperties = storePropertiesFile.getStoreProperties();

        for (StoreProperty storeProperty : storeProperties) {
            properties.setProperty(storeProperty.getName(), storeProperty.getValue());
        }

        return properties;
    }

    /**
     * <p>
     * Save the properties file
     * </p>
     * <p>
     * Note: By default, this overrides the properties file
     * </p>
     *
     * @param properties the properties file object containing the properties to save
     * @param filePath the file path of the properties file
     */
    private void savePropertiesFile(Properties properties, String filePath) {
        savePropertiesFile(properties, filePath, true);
    }

    /**
     * <p>
     * Save the properties file
     * </p>
     *
     *  @param properties the properties file object containing the properties to save
     * @param filePath the file path of the properties file
     * @param overridePropertiesFile whether to override the properties file or not
     */
    private void savePropertiesFile(Properties properties, String filePath,
            boolean overridePropertiesFile) {
        boolean isNewFile = false;
        
        try {
            isNewFile = createDirectoryAndFileIfNotExisting(filePath);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (isNewFile || overridePropertiesFile) {
            try {
                properties.store(new FileOutputStream(filePath), null);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Helper method for creating a directory and file specified if not existing
     *
     * @param filePath the file path
     * @throws IOException when the file path cannot be created
     */
    private static boolean createDirectoryAndFileIfNotExisting(String filePath)
            throws IOException {
        File file = new File(filePath);

        if (!file.isDirectory()) {
            String parent = file.getParent();

            if (parent != null) {
                new File(parent).mkdirs();
            }
        }

        return file.createNewFile();
    }

    /**
     * Helper method for adding the parent store modules to a store
     *
     * @param parentStore the parent store
     * @param childStore the child store
     */
    private void addParentStoreModules(Store parentStore, Store childStore) {
        List<Module> parentStoreModules = parentStore.getModules();

        for (Module parentModule : parentStoreModules) {
            if (!PropertiesManagerUtil.containsModule(parentModule, childStore)) {
                childStore.addModule(0, parentModule);
                continue;
            }

            // add the parent store groups
            addParentStoreGroups(parentModule, childStore);

            // add the parent store properties
            addParentStoreProperties(parentModule, childStore);
        }
    }

    /**
     * Adds the parent store groups to a child store
     *
     * @param parentModule the parent module
     * @param childStore the child store
     */
    private void addParentStoreGroups(Module parentModule, Store childStore) {
        if (PropertiesManagerUtil.containsModule(parentModule, childStore)) {
            Module module = PropertiesManagerUtil.getModuleByName(parentModule.getName(),
                    childStore);
            module.addAllGroups(parentModule.getGroups());
        }
    }

    /**
     * Adds the parent store properties to a child store
     *
     * @param parentModule the parent module
     * @param childStore the child store
     */
    private void addParentStoreProperties(Module parentModule, Store childStore) {
        List<Property> parentProperties = parentModule.getProperties();

        for (Property parentProperty : parentProperties) {
            try {
                Module childModule = PropertiesManagerUtil.getModuleByName(
                        parentModule.getName(), childStore);

                if (!PropertiesManagerUtil.containsProperty(parentProperty,
                        childModule)) {
                    childModule.addProperty(parentProperty);
                }
            } catch (PropertyException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
