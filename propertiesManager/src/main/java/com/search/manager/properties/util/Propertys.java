package com.search.manager.properties.util;

import com.search.manager.properties.exception.PropertyException;
import com.search.manager.properties.model.Module;
import com.search.manager.properties.model.Property;

import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 7, 2013
 * @version 1.0
 */
public class Propertys {

    /**
     * Searches for a property by id
     *
     * @param id the id of the property
     * @param module the {@link Module} object
     * @return the {@link Property} object with the matching id
     * @throws PropertyException thrown when the property with the passed id does not
     * exists
     */
    public static Property getPropertyById(String id, Module module)
            throws PropertyException {
        List<Property> properties = module.getProperties();
        for (Property property : properties) {
            if (property.getId().equals(id)) {
                return property;
            }
        }

        throw new PropertyException(String.format(
                "Property with the id %s cannot be found in the module %s", id,
                module.getName()));
    }

    /**
     * Checks whether a property exists in a module
     *
     * @param propertyId the id of the property
     * @param module the module to look into
     * @return <pre>true</pre> if the module exists else <pre>false</pre>
     */
    public static boolean containsProperty(String propertyId, Module module) {
        List<Property> properties = module.getProperties();

        for (Property property : properties) {
            if (property.getId().equals(propertyId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a property exists in a module
     *
     * @param property the {@link Property} object
     * @param module the module to look into
     * @return <pre>true</pre> if the module exists else <pre>false</pre>
     */
    public static boolean containsProperty(Property property, Module module) {
        return containsProperty(property.getId(), module);
    }
}
