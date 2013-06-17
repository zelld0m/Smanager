package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class FieldType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String className;
	private List<NameValuePair> attributes = new ArrayList<NameValuePair>();
	private List<Analyzer> analyzers = new ArrayList<Analyzer>();
	private GenericType genericType;
	
	public FieldType() {
	}
	
	public FieldType(String name, GenericType genericType, List<Analyzer> analyzers) {
		this.name = name;
		this.analyzers.addAll(analyzers);
		this.genericType = genericType;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<NameValuePair> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<NameValuePair> attributes) {
		this.attributes = attributes;
	}

	public List<Analyzer> getAnalyzers() {
		return analyzers;
	}

	public void setAnalyzers(List<Analyzer> analyzers) {
		this.analyzers = analyzers;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGenericType(GenericType genericType) {
		this.genericType = genericType;
	}

	public String getName() {
		return name;
	}
	
	public GenericType getGenericType() {
		return genericType;
	}
	
	public List<Analyzer> getAnalyzerChain() {
		return new ArrayList<Analyzer>(analyzers);
	}
	
}
