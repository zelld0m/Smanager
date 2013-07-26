package com.search.webservice.model;

import com.search.webservice.model.RuleEntity;

public class TransportList  implements java.io.Serializable {
    private java.lang.String[] list;

    private RuleEntity ruleEntity;

    private java.lang.String store;

    private java.lang.String token;

    public TransportList() {
    }

    public TransportList(
           java.lang.String[] list,
           RuleEntity ruleEntity,
           java.lang.String store,
           java.lang.String token) {
           this.list = list;
           this.ruleEntity = ruleEntity;
           this.store = store;
           this.token = token;
    }


    /**
     * Gets the list value for this TransportList.
     * 
     * @return list
     */
    public java.lang.String[] getList() {
        return list;
    }


    /**
     * Sets the list value for this TransportList.
     * 
     * @param list
     */
    public void setList(java.lang.String[] list) {
        this.list = list;
    }


    /**
     * Gets the ruleEntity value for this TransportList.
     * 
     * @return ruleEntity
     */
    public RuleEntity getRuleEntity() {
        return ruleEntity;
    }


    /**
     * Sets the ruleEntity value for this TransportList.
     * 
     * @param ruleEntity
     */
    public void setRuleEntity(RuleEntity ruleEntity) {
        this.ruleEntity = ruleEntity;
    }


    /**
     * Gets the store value for this TransportList.
     * 
     * @return store
     */
    public java.lang.String getStore() {
        return store;
    }


    /**
     * Sets the store value for this TransportList.
     * 
     * @param store
     */
    public void setStore(java.lang.String store) {
        this.store = store;
    }


    /**
     * Gets the token value for this TransportList.
     * 
     * @return token
     */
    public java.lang.String getToken() {
        return token;
    }


    /**
     * Sets the token value for this TransportList.
     * 
     * @param token
     */
    public void setToken(java.lang.String token) {
        this.token = token;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TransportList)) return false;
        TransportList other = (TransportList) obj;
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
            ((this.ruleEntity==null && other.getRuleEntity()==null) || 
             (this.ruleEntity!=null &&
              this.ruleEntity.equals(other.getRuleEntity()))) &&
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
        if (getRuleEntity() != null) {
            _hashCode += getRuleEntity().hashCode();
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
        new org.apache.axis.description.TypeDesc(TransportList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.webservice.search.com", "TransportList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("list");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.webservice.search.com", "list"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setItemQName(new javax.xml.namespace.QName("http://ws.search.com/client", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ruleEntity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.webservice.search.com", "ruleEntity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://enums.manager.search.com", "RuleEntity"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
