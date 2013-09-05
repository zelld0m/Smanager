package com.search.properties.manager;

import com.search.properties.manager.exception.NotDirectoryException;
import com.search.properties.manager.exception.StorePropertiesXmlNotLoadedException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Member;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import com.search.properties.manager.model.StorePropertiesFile;
import com.search.properties.manager.model.StoreProperty;
import com.search.properties.manager.util.PropertiesManagerUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @throws StorePropertiesXmlNotLoadedException thrown when store-properties.xml is
     * invalid
     */
    public StoreProperties getStoreProperties()
            throws StorePropertiesXmlNotLoadedException {
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

        throw new StorePropertiesXmlNotLoadedException(
                "Store Properties XML cannot be loaded");
    }

    /**
     * Saves the store properties to their designated store specific properties file
     *
     * @param storePropertiesFiles the store properties to save
     */
    public void saveStoreProperties(List<StorePropertiesFile> storePropertiesFiles) {
        for (StorePropertiesFile storePropertiesFile : storePropertiesFiles) {
            List<StoreProperty> storeProperties = storePropertiesFile.
                    getStoreProperties();
            Properties properties = new Properties();
            
            for (StoreProperty storeProperty : storeProperties) {
                properties.setProperty(storeProperty.getName(), storeProperty.getValue());
            }
            
            // save the properties file
            savePropertiesFile(properties, storePropertiesFile.getFilePath());
        }
    }

    /**
     * Creates the store properties to the appropriate directory
     *
     * @throws NotDirectoryException thrown when the <i>storePropertiesSaveLocation</i>
     * provided in the beans configuration is not a directory
     */
    public void createStoreSpecificProperties() throws NotDirectoryException {

        File file = new File(storePropertiesSaveLocation);
        if (!file.isDirectory()) {
            throw new NotDirectoryException(String.format("%s is not a directory",
                    storePropertiesSaveLocation));
        }

        StoreProperties storeProperties = getStoreProperties();
        List<Store> stores = storeProperties.getStores();

        for (Store store : stores) {
            String storeId = store.getId();
            boolean overrideProperties = false;

            if (PropertiesManagerUtil.hasParent(store)) {
                Store parentStore = PropertiesManagerUtil.getParent(store,
                        storeProperties);
                // load and save the store modules of the parent store
                loadAndSaveStoreModules(parentStore.getModules(), storeId);

                overrideProperties = true;
            }

            // load and save the store modules of the store
            loadAndSaveStoreModules(store.getModules(), storeId, overrideProperties);
        }
    }

    /**
     * Helper method for formatting the properties file file path
     *
     * @param store the store object
     * @param module the module object
     * @return the formatted file path of the properties file
     */
    private String getFormattedPropertiesFilePath(String storeId, Module module) {
        String moduleName = module.getName();
        return String.format("%s%s%s.%s.properties", storePropertiesSaveLocation,
                File.separator, storeId, moduleName);
    }

    /**
     * Helper method for loading the properties file
     *
     * @param properties the properties object
     * @param filePath the filePath of the properties file
     */
    private void loadPropertiesFile(Properties properties, String filePath) {
        File fileNameFile = new File(filePath);

        if (fileNameFile.exists()) {
            try {
                properties.load(new FileInputStream(filePath));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Save the properties file
     *
     * @param properties the properties file object containing the properties to save
     * @param filePath the file path of the properties file
     */
    private void savePropertiesFile(Properties properties, String filePath) {
        try {
            properties.store(new FileOutputStream(filePath), null);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    /**
     * Adds the properties file read from store-properties.xml to the properties object.
     *
     * @param propertyList the list of {@link Property} objects
     * @param properties the properties object to save on
     * @param overrideProperties determines if the properties should be overridden if
     * already existing
     */
    private void addEachProperty(List<Property> propertyList, Properties properties,
            boolean overrideProperties) {
        for (Property property : propertyList) {
            String id = property.getId();
            String defaultValue = property.getDefaultValue();

            if (overrideProperties || properties.getProperty(id) == null) {
                properties.setProperty(id, defaultValue);
            }
        }
    }

    /**
     * Loads and saves the store modules
     *
     * @param modules the {@link Store} object
     * @param storeId the store id
     */
    private void loadAndSaveStoreModules(List<Module> modules, String storeId) {
        loadAndSaveStoreModules(modules, storeId, false);
    }

    /**
     * Loads and saves the store modules
     *
     * @param modules the {@link Store} object
     * @param storeId the store id
     * @param overrideProperties determines if the properties should be overridden if
     * already existing
     */
    private void loadAndSaveStoreModules(List<Module> modules, String storeId,
            boolean overrideProperties) {

        for (Module module : modules) {
            List<Property> propertyList = module.getProperties();
            String filePath = getFormattedPropertiesFilePath(storeId, module);

            Properties properties = new Properties();

            // load the properties file
            loadPropertiesFile(properties, filePath);

            // save each property file in store-properties.xml
            addEachProperty(propertyList, properties, overrideProperties);

            // save the properties file
            savePropertiesFile(properties, filePath);
        }
    }
}
