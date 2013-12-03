package com.search.manager.properties.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents a group of a store in the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@DataTransferObject(converter = BeanConverter.class)
public class Group implements java.io.Serializable {

    private static final long serialVersionUID = -7403612053638059677L;
    @XmlAttribute(required = true)
    private String name;
    @XmlElement(name = "member")
    private List<Member> members = Lists.newArrayList();

    public Group() {
    }

    public Group(String name, List<Member> groups) {
        this.name = name;
        this.members = groups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("name", name).
                add("members", members).
                toString();
    }
}
