package com.search.manager.properties;

import java.io.*;
import java.util.*;

import javax.xml.bind.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.search.manager.properties.dao.PropertiesDao;
import com.search.manager.properties.exception.NotDirectoryException;
import com.search.manager.properties.exception.PropertyException;
import com.search.manager.properties.model.*;
import com.search.manager.properties.util.*;

/**
 * Controls the loading and reading of the store properties.
 * 
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class PropertiesManager {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesManager.class);
    @Autowired
    private SolrXmlReader solrXmlReader;
    private String storePropertiesFile;
    private String storePropertiesFolder;
    @Autowired
    private PropertiesDao propertiesDao;

    public PropertiesManager(String storePropertiesFile, String storePropertiesFolder) {
        this.storePropertiesFile = storePropertiesFile;
        this.storePropertiesFolder = storePropertiesFolder;
    }

    public String getStorePropertiesFile() {
        return storePropertiesFile;
    }

    public void setStorePropertiesFile(String storePropertiesFile) {
        this.storePropertiesFile = storePropertiesFile;
    }

    public String getStorePropertiesFolder() {
        return storePropertiesFolder;
    }

    public void setStorePropertiesFolder(String storePropertiesFolder) {
        this.storePropertiesFolder = storePropertiesFolder;
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
            if (Stores.hasParent(store)) {
                Store parentStore = Stores.getParent(store, storeProperties);

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
                Store store = Stores.getStoreById(storeId, storeProperties);
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
            File file = new File(storePropertiesFile);
            JAXBContext context = JAXBContext.newInstance(Group.class, Member.class, Module.class, Property.class,
                    Store.class, StoreProperties.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (StoreProperties) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            logger.error("Unable to unmarshall the XML to an object", e);
        }

        throw new PropertyException("Store Properties XML cannot be loaded");
    }

    /**
     * Creates the appropriate properties files if not existing yet with initial properties from store-properties.xml
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
     * Saves the store properties files based from the {@link Module} objects of a {@link Store}
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
                String filePath = Stores.getFormattedSaveLocation(getStorePropertiesFolder(), storeId, moduleName);

                saveToDB(store.getId(), module.getName(), properties, null);
                // save the properties file to the appropriate directory
                savePropertiesFile(properties, filePath, false);
            } catch (NotDirectoryException e) {
                logger.error(String.format("%s is not a valid file path", e.getFile().getPath()), e);
            }
        }
    }

    private Map<String, DBProperty> toMap(List<DBProperty> properties) {
        Map<String, DBProperty> map = new HashMap<String, DBProperty>();

        for (DBProperty prop : properties) {
            map.put(prop.getKey(), prop);
        }

        return map;
    }

    private void saveToDB(String store, String module, Properties properties, String user) {
        String username = StringUtils.defaultIfBlank(user, "system");
        Map<String, DBProperty> propMap = toMap(propertiesDao.getAllProperties(store));

        for (String key : properties.stringPropertyNames()) {
            String actualKey = module + "." + key;
            String value = properties.getProperty(key);
            DBProperty prop = propMap.get(actualKey);

            if (prop != null) {
                if (!prop.getValue().equals(value)) {
                    prop.setValue(value);
                    prop.setLastModifiedDate(new Date());
                    prop.setLastModifiedBy(username);

                    propertiesDao.update(prop);
                }
            } else {
                prop = new DBProperty();
                prop.setStore(store);
                prop.setKey(actualKey);
                prop.setValue(value);
                prop.setCreatedBy(username);
                prop.setCreatedDate(new Date());

                propertiesDao.save(prop);
            }
        }
    }

    /**
     * Helper method for creating a {@link Properties} object based from a {@link Module} object
     * 
     * @param module the {@link Module} object
     * @param storeId the store id
     * @return {@link Properties} object populated with the properties of a {@link Module} object
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
    public void saveStoreProperties(String store, List<StorePropertiesFile> storePropertiesFiles, String username) {
        for (StorePropertiesFile storePropertiesFile : storePropertiesFiles) {
            Properties properties = getPropertiesByStorePropertiesFile(storePropertiesFile);
            saveToDB(store, storePropertiesFile.getModuleName(), properties, username);
            // save the properties file
            savePropertiesFile(properties, storePropertiesFile.getFilePath());
        }
    }

    /**
     * Helper method for creating a {@link Properties} object based from the {@link Property} objects of a
     * {@link StorePropertiesFile} object
     * 
     * @param storePropertiesFile the {@link StorePropertiesFile} object
     * @return {@link Properties} object populated with the properties of a {@link StorePropertiesFile} object
     */
    private Properties getPropertiesByStorePropertiesFile(StorePropertiesFile storePropertiesFile) {
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
     * @param properties the properties file object containing the properties to save
     * @param filePath the file path of the properties file
     * @param overridePropertiesFile whether to override the properties file or not
     */
    private void savePropertiesFile(Properties properties, String filePath, boolean overridePropertiesFile) {
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
    private static boolean createDirectoryAndFileIfNotExisting(String filePath) throws IOException {
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
            if (!Modules.containsModule(parentModule, childStore)) {
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
        if (Modules.containsModule(parentModule, childStore)) {
            Module module = Modules.getModuleByName(parentModule.getName(), childStore);
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
                Module childModule = Modules.getModuleByName(parentModule.getName(), childStore);

                if (!Propertys.containsProperty(parentProperty, childModule)) {
                    childModule.addProperty(parentProperty);
                }
            } catch (PropertyException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
