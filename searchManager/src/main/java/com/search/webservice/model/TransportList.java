package com.search.webservice.model;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.xml.namespace.QName;

import org.apache.axis.description.ElementDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.encoding.ser.BeanSerializer;

public class TransportList implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String[] list;

	private RuleEntity ruleEntity;

	private String store;

	private String token;

	public TransportList() {
	}

	public TransportList(String[] list, RuleEntity ruleEntity, String store, String token) {
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
	public String[] getList() {
		return list;
	}

	/**
	 * Sets the list value for this TransportList.
	 * 
	 * @param list
	 */
	public void setList(String[] list) {
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
	public String getStore() {
		return store;
	}

	/**
	 * Sets the store value for this TransportList.
	 * 
	 * @param store
	 */
	public void setStore(String store) {
		this.store = store;
	}

	/**
	 * Gets the token value for this TransportList.
	 * 
	 * @return token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the token value for this TransportList.
	 * 
	 * @param token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	private Object __equalsCalc = null;

	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof TransportList))
			return false;

		TransportList other = (TransportList) obj;

		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
		        && ((this.list == null && other.getList() == null) || (this.list != null && Arrays.equals(this.list,
		                other.getList())))
		        && ((this.ruleEntity == null && other.getRuleEntity() == null) || (this.ruleEntity != null && this.ruleEntity
		                .equals(other.getRuleEntity())))
		        && ((this.store == null && other.getStore() == null) || (this.store != null && this.store.equals(other
		                .getStore())))
		        && ((this.token == null && other.getToken() == null) || (this.token != null && this.token.equals(other
		                .getToken())));
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
			for (int i = 0; i < Array.getLength(getList()); i++) {
				java.lang.Object obj = Array.get(getList(), i);
				if (obj != null && !obj.getClass().isArray()) {
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
	private static TypeDesc typeDesc = new TypeDesc(TransportList.class, true);

	static {
		typeDesc.setXmlType(new QName("http://model.webservice.search.com", "TransportList"));
		ElementDesc elemField = new ElementDesc();
		elemField.setFieldName("list");
		elemField.setXmlName(new QName("http://model.webservice.search.com", "list"));
		elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		elemField.setItemQName(new QName("http://ws.search.com/client", "string"));
		typeDesc.addFieldDesc(elemField);
		elemField = new ElementDesc();
		elemField.setFieldName("ruleEntity");
		elemField.setXmlName(new QName("http://model.webservice.search.com", "ruleEntity"));
		elemField.setXmlType(new QName("http://enums.manager.search.com", "RuleEntity"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new ElementDesc();
		elemField.setFieldName("store");
		elemField.setXmlName(new QName("http://model.webservice.search.com", "store"));
		elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new ElementDesc();
		elemField.setFieldName("token");
		elemField.setXmlName(new QName("http://model.webservice.search.com", "token"));
		elemField.setXmlType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setMinOccurs(0);
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
