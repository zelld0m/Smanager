package com.search.webservice.model;

public class StoreKeyword  implements java.io.Serializable {
    private com.search.manager.model.Keyword keyword;

    private java.lang.String keywordId;

    private java.lang.String keywordTerm;

    private com.search.manager.model.Store store;

    private java.lang.String storeId;

    private java.lang.String storeName;

    public StoreKeyword() {
    }

    public StoreKeyword(
           com.search.manager.model.Keyword keyword,
           java.lang.String keywordId,
           java.lang.String keywordTerm,
           com.search.manager.model.Store store,
           java.lang.String storeId,
           java.lang.String storeName) {
           this.keyword = keyword;
           this.keywordId = keywordId;
           this.keywordTerm = keywordTerm;
           this.store = store;
           this.storeId = storeId;
           this.storeName = storeName;
    }


    /**
     * Gets the keyword value for this StoreKeyword.
     * 
     * @return keyword
     */
    public com.search.manager.model.Keyword getKeyword() {
        return keyword;
    }


    /**
     * Sets the keyword value for this StoreKeyword.
     * 
     * @param keyword
     */
    public void setKeyword(com.search.manager.model.Keyword keyword) {
        this.keyword = keyword;
    }


    /**
     * Gets the keywordId value for this StoreKeyword.
     * 
     * @return keywordId
     */
    public java.lang.String getKeywordId() {
        return keywordId;
    }


    /**
     * Sets the keywordId value for this StoreKeyword.
     * 
     * @param keywordId
     */
    public void setKeywordId(java.lang.String keywordId) {
        this.keywordId = keywordId;
    }


    /**
     * Gets the keywordTerm value for this StoreKeyword.
     * 
     * @return keywordTerm
     */
    public java.lang.String getKeywordTerm() {
        return keywordTerm;
    }


    /**
     * Sets the keywordTerm value for this StoreKeyword.
     * 
     * @param keywordTerm
     */
    public void setKeywordTerm(java.lang.String keywordTerm) {
        this.keywordTerm = keywordTerm;
    }


    /**
     * Gets the store value for this StoreKeyword.
     * 
     * @return store
     */
    public com.search.manager.model.Store getStore() {
        return store;
    }


    /**
     * Sets the store value for this StoreKeyword.
     * 
     * @param store
     */
    public void setStore(com.search.manager.model.Store store) {
        this.store = store;
    }


    /**
     * Gets the storeId value for this StoreKeyword.
     * 
     * @return storeId
     */
    public java.lang.String getStoreId() {
        return storeId;
    }


    /**
     * Sets the storeId value for this StoreKeyword.
     * 
     * @param storeId
     */
    public void setStoreId(java.lang.String storeId) {
        this.storeId = storeId;
    }


    /**
     * Gets the storeName value for this StoreKeyword.
     * 
     * @return storeName
     */
    public java.lang.String getStoreName() {
        return storeName;
    }


    /**
     * Sets the storeName value for this StoreKeyword.
     * 
     * @param storeName
     */
    public void setStoreName(java.lang.String storeName) {
        this.storeName = storeName;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StoreKeyword)) return false;
        StoreKeyword other = (StoreKeyword) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.keyword==null && other.getKeyword()==null) || 
             (this.keyword!=null &&
              this.keyword.equals(other.getKeyword()))) &&
            ((this.keywordId==null && other.getKeywordId()==null) || 
             (this.keywordId!=null &&
              this.keywordId.equals(other.getKeywordId()))) &&
            ((this.keywordTerm==null && other.getKeywordTerm()==null) || 
             (this.keywordTerm!=null &&
              this.keywordTerm.equals(other.getKeywordTerm()))) &&
            ((this.store==null && other.getStore()==null) || 
             (this.store!=null &&
              this.store.equals(other.getStore()))) &&
            ((this.storeId==null && other.getStoreId()==null) || 
             (this.storeId!=null &&
              this.storeId.equals(other.getStoreId()))) &&
            ((this.storeName==null && other.getStoreName()==null) || 
             (this.storeName!=null &&
              this.storeName.equals(other.getStoreName())));
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
        if (getKeyword() != null) {
            _hashCode += getKeyword().hashCode();
        }
        if (getKeywordId() != null) {
            _hashCode += getKeywordId().hashCode();
        }
        if (getKeywordTerm() != null) {
            _hashCode += getKeywordTerm().hashCode();
        }
        if (getStore() != null) {
            _hashCode += getStore().hashCode();
        }
        if (getStoreId() != null) {
            _hashCode += getStoreId().hashCode();
        }
        if (getStoreName() != null) {
            _hashCode += getStoreName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StoreKeyword.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.manager.search.com", "StoreKeyword"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keyword");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "keyword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://model.manager.search.com", "Keyword"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keywordId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "keywordId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keywordTerm");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "keywordTerm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("store");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "store"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://model.manager.search.com", "Store"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("storeId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "storeId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("storeName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "storeName"));
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
