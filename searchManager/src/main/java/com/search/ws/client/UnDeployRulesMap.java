package com.search.ws.client;

import java.io.Serializable;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

import com.search.webservice.model.TransportList;

public class UnDeployRulesMap implements Serializable {

	private static final long serialVersionUID = 1L;
	private TransportList in0;

	public UnDeployRulesMap() {
	}

	public UnDeployRulesMap(TransportList in0) {
		this.in0 = in0;
	}

	/**
	 * Gets the in0 value for this UnDeployRulesMap.
	 * 
	 * @return in0
	 */
	public TransportList getIn0() {
		return in0;
	}

	/**
	 * Sets the in0 value for this UnDeployRulesMap.
	 * 
	 * @param in0
	 */
	public void setIn0(TransportList in0) {
		this.in0 = in0;
	}

	private Object __equalsCalc = null;

	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof UnDeployRulesMap))
			return false;
		UnDeployRulesMap other = (UnDeployRulesMap) obj;

		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((this.in0 == null && other.getIn0() == null) || (this.in0 != null && this.in0.equals(other
		        .getIn0())));
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
	private static TypeDesc typeDesc = new TypeDesc(UnDeployRulesMap.class, true);

	static {
		typeDesc.setXmlType(new QName("http://ws.search.com/client", ">unDeployRulesMap"));
		ElementDesc elemField = new ElementDesc();
		elemField.setFieldName("in0");
		elemField.setXmlName(new QName("http://ws.search.com/client", "in0"));
		elemField.setXmlType(new QName("http://model.webservice.search.com", "TransportList"));
		elemField.setNillable(true);
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
