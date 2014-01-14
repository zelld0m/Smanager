package com.search.manager.core.search;

public class Field {

	private String property;
	private String key;
	private FieldOperator operator = FieldOperator.PROPERTY; // default no operator

	public enum FieldOperator {
		PROPERTY, AVG, COUNT, COUNT_DISTINCT, MAX, MIN, SUM, CUSTOM;
	}

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

	public Field(String property, FieldOperator operator) {
		this.property = property;
		this.operator = operator;
	}

	public Field(String property, FieldOperator operator, String key) {
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

	public FieldOperator getOperator() {
		return operator;
	}

	public void setOperator(FieldOperator operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean parens = true;

		switch (operator) {
		case PROPERTY:
			parens = false;
			break;
		case AVG:
			sb.append("AVG(");
			break;
		case COUNT:
			sb.append("COUNT(");
			break;
		case COUNT_DISTINCT:
			sb.append("COUNT_DISTINCT(");
			break;
		case MAX:
			sb.append("MAX(");
			break;
		case MIN:
			sb.append("MIN(");
			break;
		case SUM:
			sb.append("SUM(");
			break;
		case CUSTOM:
			sb.append("CUSTOM: ");
			parens = false;
			break;
		default:
			sb.append("Invalid field operator: " + operator);
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

}