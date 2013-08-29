package com.search.properties.manager.model;

import com.google.common.base.Objects;

/**
 * Represents a member of a group in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
public class Member {

    private Property property;

    public Member(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("property", property).
                toString();
    }
}
