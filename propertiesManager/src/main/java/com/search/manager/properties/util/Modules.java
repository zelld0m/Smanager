package com.search.manager.properties.util;

import com.search.manager.properties.exception.PropertyException;
import com.search.manager.properties.model.Module;
import com.search.manager.properties.model.Store;

import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 7, 2013
 * @version 1.0
 */
public class Modules {

    /**
     *
     * @param name the name of the module to find
     * @param store the {@link Store} object
     * @return the {@link Module} object based from a name
     * @throws PropertyException thrown when the module with the passed name cannot be
     * found
     */
    public static Module getModuleByName(String name, Store store)
            throws PropertyException {

        List<Module> modules = store.getModules();
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }

        throw new PropertyException(String.format(
                "module with the name %s cannot be found on %s", name, store.getId()));
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
}
