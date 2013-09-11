package com.search.ws.client;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class DeployRulesMapResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private AnyType2AnyTypeMapEntry[] out;

	public DeployRulesMapResponse() {
	}

	public DeployRulesMapResponse(AnyType2AnyTypeMapEntry[] out) {
		this.out = out;
	}

	/**
	 * Gets the out value for this DeployRulesMapResponse.
	 * 
	 * @return out
	 */
	public AnyType2AnyTypeMapEntry[] getOut() {
		return out;
	}

	/**
	 * Sets the out value for this DeployRulesMapResponse.
	 * 
	 * @param out
	 */
	public void setOut(AnyType2AnyTypeMapEntry[] out) {
		this.out = out;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof DeployRulesMapResponse))
			return false;
		DeployRulesMapResponse other = (DeployRulesMapResponse) obj;

		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.out == null && other.getOut() == null) || (this.out != null && Arrays.equals(this.out,
		        other.getOut())));
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
		if (getOut() != null) {
			for (int i = 0; i < Array.getLength(getOut()); i++) {
				Object obj = Array.get(getOut(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static TypeDesc typeDesc = new TypeDesc(DeployRulesMapResponse.class, true);

	static {
		typeDesc.setXmlType(new QName("http://ws.search.com/client", ">deployRulesMapResponse"));
		ElementDesc elemField = new ElementDesc();
		elemField.setFieldName("out");
		elemField.setXmlName(new QName("http://ws.search.com/client", "out"));
		elemField.setXmlType(new QName("http://ws.search.com/client", ">anyType2anyTypeMap>entry"));
		elemField.setNillable(true);
		elemField.setItemQName(new QName("http://ws.search.com/client", "entry"));
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
