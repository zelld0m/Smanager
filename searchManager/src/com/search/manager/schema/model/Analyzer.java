package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.convert.EnumConverter;

import com.search.manager.schema.analyzer.model.AnalyzerComponent;

@DataTransferObject(converter = BeanConverter.class)
public class Analyzer implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@DataTransferObject(converter = EnumConverter.class)
	public enum Type {
		INDEX,
		QUERY,
		BOTH
	}

	private Type type;
	private AnalyzerComponent tokenizer = null;
	private List<AnalyzerComponent> filters = new ArrayList<AnalyzerComponent>();

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public AnalyzerComponent getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(AnalyzerComponent tokenizer) {
		this.tokenizer = tokenizer;
	}

	public List<AnalyzerComponent> getFilters() {
		return filters;
	}

	public void setFilters(List<AnalyzerComponent> filters) {
		this.filters = filters;
	}
}