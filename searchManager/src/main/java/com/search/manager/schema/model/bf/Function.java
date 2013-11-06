package com.search.manager.schema.model.bf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.convert.EnumConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Function implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@DataTransferObject(converter = EnumConverter.class)
	public enum ArgumentConstraint {
		NORMAL, 			// can be a NumericField, Function, or NumericConstant
		FIELD, 				// should be a field
//		FUNCTION, 			// should be another function
//		DATE_FIELD, 		// should be a date field
//		NUMERIC_FIELD, 		// should be a numeric field
		NUMERIC_CONSTANT,	// should be a numeric constant
		DATE 				// can be a date constant or a date field
		};
	
	private String name;
	private String displayText;
	private String description;
	private Integer minArgs;
	private Integer maxArgs;
	private Map<Integer, ArgumentConstraint> argConstraints = new HashMap<Integer, ArgumentConstraint>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayText() {
		return displayText;
	}
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getMinArgs() {
		return minArgs;
	}
	
	public void setMinArgs(Integer minArgs) {
		this.minArgs = minArgs;
	}
	
	public Integer getMaxArgs() {
		return maxArgs;
	}
	
	public void setMaxArgs(Integer maxArgs) {
		this.maxArgs = maxArgs;
	}
	
	public void setArgumentConstraint(int argNumber, ArgumentConstraint constraint) {
		argConstraints.put(argNumber, constraint);
	}
	
	public ArgumentConstraint getArgumentConstraint(int argNumber) {
		return argConstraints.get(argNumber);
	}
	
	public Map<Integer, ArgumentConstraint> getArgumentConstraints() {
		return new HashMap<Integer, ArgumentConstraint>(argConstraints);
	}

	@Override
	public String toString() {
		return name;
	}
	
}
