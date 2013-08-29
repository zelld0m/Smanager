package com.search.properties.manager.model;

import com.google.common.base.Objects;

/**
 * Represents a property of a module in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class Property {

    private String id;
    private String type;
    private boolean required;
    private String label;
    private String description;
    private String defaultValue;

    public Property(String id, String type, boolean required, 
            String label, String description, String defaultValue) {
        this.id = id;
        this.type = type;
        this.required = required;
        this.label = label;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("type", type).
                add("required", required).
                add("label", label).
                add("description", description).
                add("defaultValue", defaultValue).
                toString();
    }
}
