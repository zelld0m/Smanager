package com.search.properties.manager.model;

import com.google.common.base.Objects;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a module of a store in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Module {

    @XmlAttribute(required = true)
    private String name;
    @XmlAttribute
    private String title;
    @XmlElementWrapper(name = "groups")
    @XmlElement(name = "group")
    private List<Group> groups;

    public Module() {
    }

    public Module(String name, String title, List<Group> groups) {
        this.name = name;
        this.title = title;
        this.groups = groups;
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

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("name", name).
                add("title", title).
                add("groups", groups).
                toString();
    }
}
