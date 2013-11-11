package com.search.properties.manager.model;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents a member of a group in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@DataTransferObject(converter = BeanConverter.class)
public class Member implements java.io.Serializable {

    private static final long serialVersionUID = -4536630867321550383L;
    @XmlAttribute(name = "property-id", required = true)
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
