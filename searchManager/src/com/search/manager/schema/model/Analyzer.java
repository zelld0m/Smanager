package com.search.manager.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.search.manager.schema.analyzer.model.AnalyzerComponent;

public class Analyzer {
	
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