package com.search.properties.manager.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * Represents the store contents of the store-properties.xml file
 *
 * @author Philip Mark Gutierrez
 * @since August 29, 2013
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "store-properties")
@DataTransferObject(converter = BeanConverter.class)
public class StoreProperties implements java.io.Serializable {

    private static final long serialVersionUID = 4191205693999658584L;
    @XmlElement(name = "store")
    private List<Store> stores = Lists.newArrayList();

    public StoreProperties() {
    }

    public StoreProperties(List<Store> stores) {
        this.stores = stores;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("stores", stores).
                toString();
    }
}
