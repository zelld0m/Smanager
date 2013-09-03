package com.search.properties.manager.model;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents a property of a module in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@DataTransferObject(converter = BeanConverter.class)
public class Property implements java.io.Serializable {

    private static final long serialVersionUID = 3848487761515404464L;
    @XmlAttribute(required = true)
    private String id;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private boolean multiValued;
    @XmlAttribute
    private boolean required;
    @XmlElement
    private String label;
    @XmlElement
    private String description;
    @XmlElement
    private String defaultValue;

    public Property() {
    }

    public Property(String id, String type, boolean multiValued, boolean required,
            String label, String description, String defaultValue) {
        this.id = id;
        this.type = type;
        this.multiValued = multiValued;
        this.required = required;
        this.label = label;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("type", type).
                add("multiValued", multiValued).
                add("required", required).
                add("label", label).
                add("description", description).
                add("defaultValue", defaultValue).
                toString();
    }
}
