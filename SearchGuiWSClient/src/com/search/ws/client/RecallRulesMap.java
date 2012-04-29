package com.search.ws.client;

public class RecallRulesMap  implements java.io.Serializable {
    private com.search.webservice.model.TransportList in0;

    public RecallRulesMap() {
    }

    public RecallRulesMap(
           com.search.webservice.model.TransportList in0) {
           this.in0 = in0;
    }


    /**
     * Gets the in0 value for this RecallRulesMap.
     * 
     * @return in0
     */
    public com.search.webservice.model.TransportList getIn0() {
        return in0;
    }


    /**
     * Sets the in0 value for this RecallRulesMap.
     * 
     * @param in0
     */
    public void setIn0(com.search.webservice.model.TransportList in0) {
        this.in0 = in0;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RecallRulesMap)) return false;
        RecallRulesMap other = (RecallRulesMap) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.in0==null && other.getIn0()==null) || 
             (this.in0!=null &&
              this.in0.equals(other.getIn0())));
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
        if (getIn0() != null) {
            _hashCode += getIn0().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecallRulesMap.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.search.com/client", ">recallRulesMap"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("in0");
        elemField.setXmlName(new javax.xml.namespace.QName("http://ws.search.com/client", "in0"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://model.webservice.search.com", "TransportList"));
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
