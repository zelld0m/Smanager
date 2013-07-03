package com.search.manager.schema.model.bf;

import java.util.ArrayList;
import java.util.List;

import jxl.common.Logger;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.RelevancyConfig;
import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.GenericType;
import com.search.manager.schema.model.bf.Function.ArgumentConstraint;

@DataTransferObject(converter = BeanConverter.class)
public class FunctionQuery implements FunctionModelComponent {
	
	private final static long serialVersionUID = 1L;
	
	private final static Logger logger = Logger.getLogger(FunctionQuery.class);
	
	private Function function;
	private List<FunctionModelComponent> arguments = new ArrayList<FunctionModelComponent>();

	public FunctionQuery(Function function) {
		this.function = function;
	}
	
	public FunctionQuery(Function function, FunctionModelComponent ... args) {
		this.function = function;
		for (FunctionModelComponent arg: args) {
			addArgument(arg);
		}
	}
	
	public FunctionQuery(String functionName) throws SchemaException {
		Function function = RelevancyConfig.getInstance().getFunction(functionName);
		if (function == null) {
			throw new SchemaException("Unknown function: " + functionName);
		}
		this.function = function;
	}
	
//	public FunctionQuery(Schema schema, String functionName, String ... args) throws SchemaException {
//		this(functionName);
//
//		for (String arg: args) {
//			FunctionModel model = null;
//			Function function = RelevancyConfig.getInstance().getFunction(functionName);
//			Field field = schema.getField(arg);
//			if (function != null) {
//				model = new FunctionQuery(function);
//			}
//			else if (field != null) {
//				model = field;
//			}
//			else {
//				// Constant
//				if (StringUtils.isNumericSpace(arg0))
//			}
//			if (model == null) {
//				throw new SchemaException("Unrecognized argument: " + arg);
//			}
//			addArgument(model);
//		}
//	}
	
	public Function getFunction() {
		return function;
	}
	
	public void setFunction(Function function) {
		this.function = function;
	}
	
	public List<FunctionModelComponent> getArguments() {
		return arguments;
	}

	public void addArguments(FunctionModelComponent ... models) {
		for (FunctionModelComponent model: models) {
			arguments.add(model);
		}
	}
	
	public void addArgument(FunctionModelComponent model) {
		arguments.add(model);
	}

	public void insertArgument(int index, FunctionModelComponent model) {
		if (index >= 0 && index < arguments.size()) {
			arguments.add(index, model);
		}
		else {
			arguments.add(model);
		}
	}
	
	/**
	 * Remove the argument specified at i
	 * @param index zero-based index
	 */
	public FunctionModelComponent removeArgument(int index) {
		return (index >= 0 && index < arguments.size()) ? arguments.remove(index) : null;
	}

	public FunctionModelComponent getArgument(int index) {
		return (index >= 0 && index < arguments.size()) ? arguments.get(index) : null;
	}
	
	public List<FunctionModelComponent> getArguments(int index) {
		return new ArrayList<FunctionModelComponent>(arguments);
	}
	
	private StringBuilder getErrorStringBuilder() {
		StringBuilder error = new StringBuilder();
		error.append("Error in function[").append(function.getName()).append("]: ");
		return error;
	}
	
	@Override
	public boolean validate() throws SchemaException {
		StringBuilder error = getErrorStringBuilder();
		if (arguments.size() < function.getMinArgs() || function.getMaxArgs() != null && arguments.size() > function.getMaxArgs()) {
			error.append("Incorrect number of arguments. Expected ");
			if (function.getMinArgs() != null) {
				error.append(function.getMinArgs());
				if (function.getMaxArgs() != null) {
					error.append(" to ");
				}
			}
			if (function.getMaxArgs() != null) {
				error.append("function.getMaxArgs()");
			}
			error.append(".");
			throw new SchemaException(error.toString());
		}
		
		boolean valid = true;
		for (int i = 0; i < arguments.size(); i++) {
			FunctionModelComponent argument = arguments.get(i);
			ArgumentConstraint constraint = function.getArgumentConstraint(i+1);
			if (constraint != null) {
				switch(constraint) {
					case DATE:
						valid = argument instanceof DateConstant || argument instanceof Field && ((Field)argument).getGenericType() == GenericType.DATE;
						break;
					case NUMERIC_CONSTANT:
						valid = argument instanceof NumericConstant;
						break;
					case NORMAL:
						valid = argument instanceof FunctionQuery || argument instanceof NumericConstant || argument instanceof Field && ((Field)argument).getGenericType() == GenericType.NUMERIC;
						break;
					default:
						logger.warn("Unknown argument constraint: " + constraint);
						break;
				}
			}
			
			if (!valid) {
				error.append(" Expected ").append(constraint).append(" got ").append(argument.toString());
				throw new SchemaException(error.toString());
			}
			
			try {
				valid &= argument.validate();
			} catch (Exception e) {
				error.append(" Argument[").append(i+1).append("](").append(e.getMessage()).append(")");
				throw new SchemaException(error.toString());
			}
			
			if (!valid) {
				break;
			}
		}
		return valid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(function.getName())
			   .append("(");
		int size = arguments.size();
		if (size > 0) {
			builder.append(arguments.get(0).toString());
			for (int i = 1; i < size; i++) {
				builder.append(",")
					   .append(arguments.get(i).toString());
			}
		}
		builder.append(")");
		return builder.toString();
	}
	
}
