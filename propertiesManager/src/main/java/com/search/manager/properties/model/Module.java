package com.search.manager.properties.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents a module of a store in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@DataTransferObject(converter = BeanConverter.class)
public class Module implements java.io.Serializable {

    private static final long serialVersionUID = -4574433095922350090L;
    @XmlAttribute(required = true)
    private String name;
    @XmlAttribute
    private String title;
    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    private List<Group> groups = Lists.newArrayList();
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    private List<Property> properties = Lists.newArrayList();

    public Module() {
    }

    public Module(String name, String title, LinkedList<Group> groups, List<Property> properties) {
        this.name = name;
        this.title = title;
        this.groups = groups;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(LinkedList<Group> groups) {
        this.groups = groups;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    
    public void addGroup(Group group) {
        groups.add(group);
    }
    
    public void addAllGroups(List<Group> groupsToAdd) {
        groups.addAll(0, groupsToAdd);
    }
    
    public void addProperty(Property property) {
        properties.add(property);
    }
    
    public void addAllProperties(List<Property> properties) {
        properties.addAll(properties);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("name", name).
                add("title", title).
                add("groups", groups).
                add("properties", properties).
                toString();
    }
}
