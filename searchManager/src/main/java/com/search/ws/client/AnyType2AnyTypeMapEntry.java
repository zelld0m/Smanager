package com.search.ws.client;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class AnyType2AnyTypeMapEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object key;

	private Object value;

	public AnyType2AnyTypeMapEntry() {
	}

	public AnyType2AnyTypeMapEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the key value for this AnyType2AnyTypeMapEntry.
	 * 
	 * @return key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Sets the key value for this AnyType2AnyTypeMapEntry.
	 * 
	 * @param key
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	/**
	 * Gets the value value for this AnyType2AnyTypeMapEntry.
	 * 
	 * @return value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value value for this AnyType2AnyTypeMapEntry.
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	private Object __equalsCalc = null;

	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof AnyType2AnyTypeMapEntry))
			return false;

		AnyType2AnyTypeMapEntry other = (AnyType2AnyTypeMapEntry) obj;

		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
		        && ((this.key == null && other.getKey() == null) || (this.key != null && this.key
		                .equals(other.getKey())))
		        && ((this.value == null && other.getValue() == null) || (this.value != null && this.value.equals(other
		                .getValue())));
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
		if (getKey() != null) {
			_hashCode += getKey().hashCode();
		}
		if (getValue() != null) {
			_hashCode += getValue().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static TypeDesc typeDesc = new TypeDesc(AnyType2AnyTypeMapEntry.class, true);

	static {
		typeDesc.setXmlType(new QName("http://ws.search.com/client", ">anyType2anyTypeMap>entry"));
		ElementDesc elemField = new ElementDesc();
		elemField.setFieldName("key");
		elemField.setXmlName(new QName("http://ws.search.com/client", "key"));
		elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new ElementDesc();
		elemField.setFieldName("value");
		elemField.setXmlName(new QName("http://ws.search.com/client", "value"));
		elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		elemField.setMinOccurs(0);
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	public static Serializer getSerializer(String mechType, Class<?> _javaType, QName _xmlType) {
		return new BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static Deserializer getDeserializer(String mechType, Class<?> _javaType, QName _xmlType) {
		return new BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

}
