package com.search.properties.manager.model;

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
 * Represents the store of the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@DataTransferObject(converter = BeanConverter.class)
public class Store implements java.io.Serializable {

    private static final long serialVersionUID = 4703673883683467685L;
    @XmlAttribute(required = true)
    private String id;
    @XmlAttribute(name = "extends")
    private String parent;
    @XmlElement(name = "module")
    private List<Module> modules = Lists.newArrayList();

    public Store() {
    }

    public Store(String id, String parent, List<Module> modules) {
        this.id = id;
        this.parent = parent;
        this.modules = modules;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public void addModule(Module module) {
        modules.add(module);
    }
    
    public void addModule(int position, Module module) {
        modules.add(position, module);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("id", id).
                add("parent", parent).
                add("modules", modules).
                toString();
    }
}
