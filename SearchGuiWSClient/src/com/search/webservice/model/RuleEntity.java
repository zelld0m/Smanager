package com.search.webservice.model;

public class RuleEntity implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    public RuleEntity(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _ELEVATE = "ELEVATE";
    public static final java.lang.String _EXCLUDE = "EXCLUDE";
    public static final java.lang.String _DEMOTE = "DEMOTE";
    public static final java.lang.String _FACET_SORT = "FACET_SORT";
    public static final java.lang.String _KEYWORD = "KEYWORD";
    public static final java.lang.String _STORE_KEYWORD = "STORE_KEYWORD";
    public static final java.lang.String _CAMPAIGN = "CAMPAIGN";
    public static final java.lang.String _BANNER = "BANNER";
    public static final java.lang.String _QUERY_CLEANING = "QUERY_CLEANING";
    public static final java.lang.String _RANKING_RULE = "RANKING_RULE";
    public static final RuleEntity ELEVATE = new RuleEntity(_ELEVATE);
    public static final RuleEntity EXCLUDE = new RuleEntity(_EXCLUDE);
    public static final RuleEntity DEMOTE = new RuleEntity(_DEMOTE);
    public static final RuleEntity FACET_SORT = new RuleEntity(_FACET_SORT);
    public static final RuleEntity KEYWORD = new RuleEntity(_KEYWORD);
    public static final RuleEntity STORE_KEYWORD = new RuleEntity(_STORE_KEYWORD);
    public static final RuleEntity CAMPAIGN = new RuleEntity(_CAMPAIGN);
    public static final RuleEntity BANNER = new RuleEntity(_BANNER);
    public static final RuleEntity QUERY_CLEANING = new RuleEntity(_QUERY_CLEANING);
    public static final RuleEntity RANKING_RULE = new RuleEntity(_RANKING_RULE);
    public java.lang.String getValue() { return _value_;}
    public static RuleEntity fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        RuleEntity enumeration = (RuleEntity)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static RuleEntity fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RuleEntity.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://enums.manager.search.com", "RuleEntity"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
