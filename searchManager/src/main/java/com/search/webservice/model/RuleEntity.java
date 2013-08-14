package com.search.webservice.model;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.EnumDeserializer;
import org.apache.axis.encoding.ser.EnumSerializer;

public class RuleEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String _value_;
	private static HashMap<String, RuleEntity> _table_ = new HashMap<String, RuleEntity>();

	// Constructor
	public RuleEntity(String value) {
		_value_ = value;
		_table_.put(_value_, this);
	}

	public static final String _ELEVATE = "ELEVATE";
	public static final String _EXCLUDE = "EXCLUDE";
	public static final String _DEMOTE = "DEMOTE";
	public static final String _FACET_SORT = "FACET_SORT";
	public static final String _KEYWORD = "KEYWORD";
	public static final String _STORE_KEYWORD = "STORE_KEYWORD";
	public static final String _CAMPAIGN = "CAMPAIGN";
	public static final String _BANNER = "BANNER";
	public static final String _QUERY_CLEANING = "QUERY_CLEANING";
	public static final String _RANKING_RULE = "RANKING_RULE";
	public static final String _SPELL = "SPELL";
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
	public static final RuleEntity SPELL = new RuleEntity(_SPELL);

	public String getValue() {
		return _value_;
	}

	public static RuleEntity fromValue(String value) throws IllegalArgumentException {
		RuleEntity enumeration = _table_.get(value);
		if (enumeration == null)
			throw new IllegalArgumentException();
		return enumeration;
	}

	public static RuleEntity fromString(String value) throws IllegalArgumentException {
		return fromValue(value);
	}

	public boolean equals(Object obj) {
		return (obj == this);
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return _value_;
	}

	public Object readResolve() throws ObjectStreamException {
		return fromValue(_value_);
	}

	public static Serializer getSerializer(String mechType, Class<?> _javaType, QName _xmlType) {
		return new EnumSerializer(_javaType, _xmlType);
	}

	public static Deserializer getDeserializer(String mechType, Class<?> _javaType, QName _xmlType) {
		return new EnumDeserializer(_javaType, _xmlType);
	}

	// Type metadata
	private static TypeDesc typeDesc = new TypeDesc(RuleEntity.class);

	static {
		typeDesc.setXmlType(new QName("http://enums.manager.search.com", "RuleEntity"));
	}

	/**
	 * Return type metadata object
	 */
	public static TypeDesc getTypeDesc() {
		return typeDesc;
	}

}
