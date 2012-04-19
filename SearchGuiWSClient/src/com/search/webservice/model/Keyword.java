package com.search.webservice.model;

public class Keyword  implements java.io.Serializable {
    private java.lang.String keyword;

    private java.lang.String keywordId;

    public Keyword() {
    }

    public Keyword(
           java.lang.String keyword,
           java.lang.String keywordId) {
           this.keyword = keyword;
           this.keywordId = keywordId;
    }


    /**
     * Gets the keyword value for this Keyword.
     * 
     * @return keyword
     */
    public java.lang.String getKeyword() {
        return keyword;
    }


    /**
     * Sets the keyword value for this Keyword.
     * 
     * @param keyword
     */
    public void setKeyword(java.lang.String keyword) {
        this.keyword = keyword;
    }


    /**
     * Gets the keywordId value for this Keyword.
     * 
     * @return keywordId
     */
    public java.lang.String getKeywordId() {
        return keywordId;
    }


    /**
     * Sets the keywordId value for this Keyword.
     * 
     * @param keywordId
     */
    public void setKeywordId(java.lang.String keywordId) {
        this.keywordId = keywordId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Keyword)) return false;
        Keyword other = (Keyword) obj;
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
              this.keywordId.equals(other.getKeywordId())));
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Keyword.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.manager.search.com", "Keyword"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("keyword");
        elemField.setXmlName(new javax.xml.namespace.QName("http://model.manager.search.com", "keyword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
