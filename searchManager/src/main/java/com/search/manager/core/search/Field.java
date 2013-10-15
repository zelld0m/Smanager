package com.search.manager.core.search;

public class Field {

	protected String property;
	protected String key;
	protected int operator = 0;

	public static final int OP_PROPERTY = 0;
	public static final int OP_COUNT = 1;
	public static final int OP_COUNT_DISTINCT = 2;
	public static final int OP_MAX = 3;
	public static final int OP_MIN = 4;
	public static final int OP_SUM = 5;
	public static final int OP_AVG = 6;
	public static final int OP_CUSTOM = 999;

	@SuppressWarnings("unused")
	private Field() {

	}

	public Field(String property) {
		this.property = property;
	}

	public Field(String property, String key) {
		this.property = property;
		this.key = key;
	}

	public Field(String property, int operator) {
		this.property = property;
		this.operator = operator;
	}

	public Field(String property, int operator, String key) {
		this.property = property;
		this.operator = operator;
		this.key = key;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean parens = true;
		switch (operator) {
		case OP_AVG:
			sb.append("AVG(");
			break;
		case OP_COUNT:
			sb.append("COUNT(");
			break;
		case OP_COUNT_DISTINCT:
			sb.append("COUNT_DISTINCT(");
			break;
		case OP_MAX:
			sb.append("MAX(");
			break;
		case OP_MIN:
			sb.append("MIN(");
			break;
		case OP_PROPERTY:
			parens = false;
			break;
		case OP_SUM:
			sb.append("SUM(");
			break;
		case OP_CUSTOM:
			sb.append("CUSTOM: ");
			parens = false;
			break;
		default:
			sb.append("**INVALID OPERATOR: (" + operator + ")** ");
			parens = false;
			break;
		}

		if (property == null) {
			sb.append("null");
		} else {
			sb.append("`");
			sb.append(property);
			sb.append("`");
		}
		if (parens) {
			sb.append(")");
		}
		if (key != null) {
			sb.append(" as `");
			sb.append(key);
			sb.append("`");
		}

		return sb.toString();
	}

	public String toStringSolr() {
		StringBuilder sb = new StringBuilder();
		boolean parens = true;
		
		switch (operator) {
		case OP_AVG:
			// Unknown function count in FunctionQuery
			break;
		case OP_COUNT:
			// Unknown function count in FunctionQuery
			break;
		case OP_COUNT_DISTINCT:
			// Unknown function count in FunctionQuery
			break;
		case OP_MAX:
			break;
		case OP_MIN:
			break;
		case OP_PROPERTY:
			parens = false;
			break;
		case OP_SUM:
			break;
		case OP_CUSTOM:
			parens = false;
			break;
		default:
			sb.append("**INVALID OPERATOR: (" + operator + ")** ");
			parens = false;
			break;
		}
		if (property == null) {
			
 		} else {
 			sb.append(property);
 		}
		if (parens) {
			
		}
		return sb.toString();
	}
}
