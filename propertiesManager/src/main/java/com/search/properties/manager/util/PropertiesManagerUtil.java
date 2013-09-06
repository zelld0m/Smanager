package com.search.properties.manager.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.search.properties.manager.PropertiesManager;
import com.search.properties.manager.exception.GroupNotFoundException;
import com.search.properties.manager.exception.ModuleNotFoundException;
import com.search.properties.manager.exception.PropertyNotFoundException;
import com.search.properties.manager.exception.StoreNotFoundException;
import com.search.properties.manager.exception.StorePropertyNotFoundException;
import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Property;
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

    /**
     * Searches for a group by name
     *
     * @param name the name of the group
     * @param module the {@link Module} object
     * @return the {@link Group} object with the matching name
     * @throws GroupNotFoundException thrown when the group with the passed name does not
     * exists
     */
    public static Group getGroupByName(String name, Module module)
            throws GroupNotFoundException {
        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            String groupName = group.getName();
            
            if (!Strings.isNullOrEmpty(groupName) && groupName.equals(name)) {
                return group;
            }
        }

        throw new GroupNotFoundException(String.format(
                "Group with the name %s cannot be found in the module %s",
                name, module.getName()));
    }

    /**
     * <p>
     * Returns all the groups in the specified module without a name
     * </p>
     *
     * <p>
     * If no group without a name if found, an empty {@link java.util.ArrayList} is
     * returned
     * </p>
     *
     * @param module the {@link Module} object
     * @return all the groups in the specified module without a name
     */
    public static List<Group> getAllGroupsWithoutAName(Module module) {
        List<Group> toReturn = Lists.newArrayList();

        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            if (Strings.isNullOrEmpty(group.getName())) {
                toReturn.add(group);
            }
        }

        return toReturn;
    }

    /**
     * Searches for a property by id
     *
     * @param id the id of the property
     * @param module the {@link Module} object
     * @return the {@link Property} object with the matching id
     * @throws PropertyNotFoundException thrown when the property with the passed id does
     * not exists
     */
    public static Property getPropertyById(String id, Module module)
            throws PropertyNotFoundException {
        List<Property> properties = module.getProperties();
        for (Property property : properties) {
            if (property.getId().equals(id)) {
                return property;
            }
        }

        throw new PropertyNotFoundException(String.format(
                "Property with the id %s cannot be found in the module %s", id,
                module.getName()));
    }

    /**
     * Checks whether a modules exists in a store
     *
     * @param moduleName the module name
     * @param store the store to look into
     * @return <pre>true</pre> if the module exists else <pre>false</pre>
     */
    public static boolean containsModule(String moduleName, Store store) {
        List<Module> modules = store.getModules();

        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a modules exists in a store
     *
     * @param module the {@link Module} object
     * @param store the store to look into
     * @return <pre>true</pre> if the module exists else <pre>false</pre>
     */
    public static boolean containsModule(Module module, Store store) {
        return containsModule(module.getName(), store);
    }

    /**
     * Checks whether a group exists in a module
     * @param name the name of the group
     * @param module the module to look into
     * @return <pre>true</pre> if the group exists else <pre>false</pre>
     */
    public static boolean containsGroup(String name, Module module) {
        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            String groupName = group.getName();
            if (!Strings.isNullOrEmpty(groupName) && groupName.equals(name)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks whether at least one group in a module has no name
     * @param module the module to look into
     * @return <pre>true</pre> if at least one group has no name else <pre>false</pre>
     */
    public static boolean containsAtLeastAGroupWithoutAName(Module module) {
        List<Group> groupsWithoutAName = Lists.newArrayList();
        List<Group> groups = module.getGroups();
        for (Group group : groups) {
            if (Strings.isNullOrEmpty(group.getName())) {
                groupsWithoutAName.add(group);
            }
        }
        
        return !groupsWithoutAName.isEmpty();
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
