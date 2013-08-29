package com.search.properties.manager.model.builder;

import com.search.properties.manager.model.Property;

/**
 * Builder for creating {@link Property} objects.
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class PropertyBuilder {
    private String id;
    private String type;
    private boolean required;
    private String label;
    private String description;
    private String defaultValue;
    
    private PropertyBuilder(String id, String type, boolean required) {
        this.id = id;
        this.type = type;
        this.required = required;
    }
    
    public PropertyBuilder label(String label) {
        this.label = label;
        return this;
    }
    
    public PropertyBuilder description(String description) {
        this.description = description;
        return this;
    }
    
    public PropertyBuilder defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
    
    public static PropertyBuilder create(String id, String type, boolean required) {
        return new PropertyBuilder(id, type, required);
    }
    
    public Property build() {
        return new Property(id, type, required, label, description, defaultValue);
    }
}
