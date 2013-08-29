package com.search.properties.manager.model.builder;

import com.search.properties.manager.model.Group;
import com.search.properties.manager.model.Module;
import com.search.properties.manager.model.Store;
import java.util.List;

/**
 * Builder for creating {@link Module} objects.
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class ModuleBuilder {

    private String name;
    private String title;
    private List<Group> groups;
    private List<Store> stores;
    
    private ModuleBuilder(String name) {
        this.name = name;
    }
    
    public ModuleBuilder title(String title) {
        this.title = title;
        return this;
    }
    
    public ModuleBuilder groups(List<Group> groups) {
        this.groups = groups;
        return this;
    }
    
    public ModuleBuilder stores(List<Store> stores) {
        this.stores = stores;
        return this;
    }
    
    public static ModuleBuilder create(String name) {
        return new ModuleBuilder(name);
    }
    
    public Module build() {
        return new Module(name, title, groups, stores);
    }
}
