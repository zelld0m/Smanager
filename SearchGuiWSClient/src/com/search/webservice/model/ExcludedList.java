/**
 * ExcludedList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.search.webservice.model;

public class ExcludedList  implements java.io.Serializable {
    private com.search.manager.model.ExcludeResult[] list;

    private com.search.ws.client.AnyType2AnyTypeMapEntry[] map;

    private java.lang.String store;

    private java.lang.String token;

    public ExcludedList() {
    }

    public ExcludedList(
           com.search.manager.model.ExcludeResult[] list,
           com.search.ws.client.AnyType2AnyTypeMapEntry[] map,
           java.lang.String store,
           java.lang.String token) {
           this.list = list;
           this.map = map;
           this.store = store;
           this.token = token;
    }


    /**
     * Gets the list value for this ExcludedList.
     * 
     * @return list
     */
    public com.search.manager.model.ExcludeResult[] getList() {
        return list;
    }


    /**
     * Sets the list value for this ExcludedList.
     * 
     * @param list
     */
    public void setList(com.search.manager.model.ExcludeResult[] list) {
        this.list = list;
    }


    /**
     * Gets the map value for this ExcludedList.
     * 
     * @return map
     */
    public com.search.ws.client.AnyType2AnyTypeMapEntry[] getMap() {
        return map;
    }


    /**
     * Sets the map value for this ExcludedList.
     * 
     * @param map
     */
    public void setMap(com.search.ws.client.AnyType2AnyTypeMapEntry[] map) {
        this.map = map;
    }


    /**
     * Gets the store value for this ExcludedList.
     * 
     * @return store
     */
    public java.lang.String getStore() {
        return store;
    }


    /**
     * Sets the store value for this ExcludedList.
     * 
     * @param store
     */
    public void setStore(java.lang.String store) {
        this.store = store;
    }


    /**
     * Gets the token value for this ExcludedList.
     * 
     * @return token
     */
    public java.lang.String getToken() {
        return token;
    }


    /**
     * Sets the token value for this ExcludedList.
     * 
     * @param token
     */
    public void setToken(java.lang.String token) {
        this.token = token;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExcludedList)) return false;
        ExcludedList other = (ExcludedList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.list==null && other.getList()==null) || 
             (this.list!=null &&
              java.util.Arrays.equals(this.list, other.getList()))) &&
            ((this.map==null && other.getMap()==null) || 
             (this.map!=null &&
              java.util.Arrays.equals(this.map, other.getMap()))) &&
            ((this.store==null && other.getStore()==null) || 
             (this.store!=null &&
              this.store.equals(other.getStore()))) &&
            ((this.token==null && other.getToken()==null) || 
             (this.token!=null &&
              this.token.equals(other.getToken())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMap() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMap());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMap(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getStore() != null) {
            _hashCode += getStore().hashCode();
        }
        if (getToken() != null) {
            _hashCode += getToken().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExcludedList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.webservice.search.com", "ExcludedList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("list");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.webservice.search.com", "list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://model.manager.search.com", "ExcludeResult"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://model.manager.search.com", "ExcludeResult"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("map");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.webservice.search.com", "map"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://ws.search.com/client", ">anyType2anyTypeMap>entry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ws.search.com/client", "entry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("store");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.webservice.search.com", "store"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("token");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.webservice.search.com", "token"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
