package com.search.properties.manager.model;

import com.google.common.base.Objects;
import java.util.List;

/**
 * Represents the store of the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class Store {

    private String id;
    private String storeExtendingTo;
    private List<Module> modules;

    public Store(String id, String storeExtendingTo, List<Module> modules) {
        this.id = id;
        this.storeExtendingTo = storeExtendingTo;
        this.modules = modules;
    }

    public String getId() {
        return id;
    }

    public String getStoreExtendingTo() {
        return storeExtendingTo;
    }

    public List<Module> getModules() {
        return modules;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("storeExtendingTo", storeExtendingTo).
                add("modules", modules).
                toString();
    }
}
