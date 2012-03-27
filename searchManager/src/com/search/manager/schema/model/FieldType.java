package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.analyzer.model.AnalyzerComponent;

@DataTransferObject(converter = BeanConverter.class)
public class FieldType implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<AnalyzerComponent> analyzerChain = new ArrayList<AnalyzerComponent>();
	private GenericType genericType;
	
	public FieldType(String name, GenericType genericType, List<AnalyzerComponent> analyzerChain) {
		this.name = name;
		this.analyzerChain.addAll(analyzerChain);
		this.genericType = genericType;
	}

	public String getName() {
		return name;
	}
	
	public GenericType getGenericType() {
		return genericType;
	}

	public boolean isCaseSensitive() {
		for (AnalyzerComponent analyzer: analyzerChain) {
			if (StringUtils.containsIgnoreCase(analyzer.getName(), "lowercase")) {
				return false;
			}
		}
		return true;
	}
	
	public List<AnalyzerComponent> getAnalyzerChain() {
		return new ArrayList<AnalyzerComponent>(analyzerChain);
	}
	
	
}
