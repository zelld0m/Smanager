package com.search.properties.manager.model;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a member of a group in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Member {

    @XmlAttribute(name="property-id", required = true)
    private String propertyId;

    public Member() {
    }

    public Member(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("propertyId", propertyId).
                toString();
    }
}
