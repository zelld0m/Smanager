package com.search.manager.schema.analyzer.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.convert.EnumConverter;


/**
 * Can either be a tokenizer or analyzer, that are declared to be part of the analyzer chain of a specific field type
 */
@DataTransferObject(converter = BeanConverter.class)
public class AnalyzerComponent implements Serializable {

	@DataTransferObject(converter = EnumConverter.class)
	public enum AnalyzerComponentType { Tokenizer, Filter };
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String displayText;
	private String description;
	private AnalyzerComponentType type;
	
	public AnalyzerComponent(String name, String displayText, String description, AnalyzerComponentType linkType) {
		this.name = name;
		this.displayText = displayText;
		this.description = description;
		this.type = linkType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayText() {
		return displayText;
	}
	
	public String getDescription() {
		return description;
	}

	public AnalyzerComponentType getAnalyzerComponentType() {
		return type;
	}

}
