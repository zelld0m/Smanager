package com.search.properties.manager;

import com.search.properties.manager.exception.StorePropertiesXmlNotLoadedException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Member;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
import com.search.properties.manager.model.Store;
import com.search.properties.manager.model.StoreProperties;
import java.io.File;
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

    public PropertiesManager(String storePropertiesLocation) {
        this.storePropertiesLocation = storePropertiesLocation;
    }

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
}
