package com.search.properties.manager.model;

import com.google.common.base.Objects;
import java.util.List;

/**
 * Represents a module of a store in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class Module {

    private String name;
    private String title;
    private List<Group> groups;
    private List<Store> stores;

    public Module(String name, String title, List<Group> groups, List<Store> stores) {
        this.name = name;
        this.title = title;
        this.groups = groups;
        this.stores = stores;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Store> getStores() {
        return stores;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("name", name).
                add("title", title).
                add("groups", groups).
                add("store", stores).
                toString();
    }
}
