package com.search.properties.manager;

import com.search.properties.manager.exception.ModuleNotFoundException;
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
        StoreProperties storeProperties = getStorePropertiesFromXML();
        List<Store> stores = storeProperties.getStores();

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
     * @return a {@link StoreProperties} object read from store-properties.xml
     * @throws StorePropertiesXmlNotLoadedException thrown when store-properties.xml is
     * invalid
     */
    public StoreProperties getStorePropertiesFromXML()
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
            } catch (ModuleNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
