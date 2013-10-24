package com.search.manager.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Filter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String property;
	private Object value;
	private FilterOperator operator;

	public enum FilterOperator {
		EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL, LIKE, IN, NOT_IN, NULL, NOT_NULL, EMPTY, NOT_EMPTY, AND, OR, NOT, SOME, ALL, NONE, CUSTOM;
	}

	@SuppressWarnings("unused")
	private Filter() {
		// do nothing...
	}

	public Filter(String property, Object value) {
		this.property = property;
		this.value = value;
		this.operator = FilterOperator.EQUAL;
	}

	public Filter(String property, Object value, FilterOperator operator) {
		this.property = property;
		this.value = value;
		this.operator = operator;
	}

	public static Filter equal(String property, Object value) {
		return new Filter(property, value, FilterOperator.EQUAL);
	}

	public static Filter lessThan(String property, Object value) {
		return new Filter(property, value, FilterOperator.LESS_THAN);
	}

	public static Filter greaterThan(String property, Object value) {
		return new Filter(property, value, FilterOperator.GREATER_THAN);
	}

	public static Filter lessOrEqual(String property, Object value) {
		return new Filter(property, value, FilterOperator.LESS_OR_EQUAL);
	}

	public static Filter greaterOrEqual(String property, Object value) {
		return new Filter(property, value, FilterOperator.GREATER_OR_EQUAL);
	}

	public static Filter in(String property, Collection<?> value) {
		return new Filter(property, value, FilterOperator.IN);
	}

	public static Filter notIn(String property, Collection<?> value) {
		return new Filter(property, value, FilterOperator.NOT_IN);
	}

	public static Filter like(String property, String value) {
		return new Filter(property, value, FilterOperator.LIKE);
	}

	public static Filter notEqual(String property, Object value) {
		return new Filter(property, value, FilterOperator.NOT_EQUAL);
	}

	public static Filter isNull(String property) {
		return new Filter(property, true, FilterOperator.NULL);
	}

	public static Filter isNotNull(String property) {
		return new Filter(property, true, FilterOperator.NOT_NULL);
	}

	public static Filter isEmpty(String property) {
		return new Filter(property, true, FilterOperator.EMPTY);
	}

	public static Filter isNotEmpty(String property) {
		return new Filter(property, true, FilterOperator.NOT_EMPTY);
	}

	public static Filter and(Filter... filters) {
		Filter filter = new Filter("AND", null, FilterOperator.AND);
		for (Filter f : filters) {
			filter.add(f);
		}
		return filter;
	}

	public static Filter or(Filter... filters) {
		Filter filter = and(filters);
		filter.property = "OR";
		filter.operator = FilterOperator.OR;
		return filter;
	}

	public static Filter not(Filter filter) {
		return new Filter("NOT", filter, FilterOperator.NOT);
	}

	public static Filter some(String property, Filter filter) {
		return new Filter(property, filter, FilterOperator.SOME);
	}

	public static Filter all(String property, Filter filter) {
		return new Filter(property, filter, FilterOperator.ALL);
	}

	public static Filter none(String property, Filter filter) {
		return new Filter(property, filter, FilterOperator.NONE);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void add(Filter filter) {
		if (value == null || !(value instanceof List)) {
			value = new ArrayList();
		}
		((List) value).add(filter);
	}

	@SuppressWarnings("rawtypes")
	public void remove(Filter filter) {
		if (value == null || !(value instanceof List)) {
			return;
		}
		((List) value).remove(filter);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public void setOperator(FilterOperator operator) {
		this.operator = operator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String toString() {
		switch (operator) {
		case EQUAL:
			return "`" + property + "` = "
					+ SearchUtil.paramDisplayString(value);
		case NOT_EQUAL:
			return "`" + property + "` != "
					+ SearchUtil.paramDisplayString(value);
		case LESS_THAN:
			return "`" + property + "` < "
					+ SearchUtil.paramDisplayString(value);
		case GREATER_THAN:
			return "`" + property + "` > "
					+ SearchUtil.paramDisplayString(value);
		case LESS_OR_EQUAL:
			return "`" + property + "` <= "
					+ SearchUtil.paramDisplayString(value);
		case GREATER_OR_EQUAL:
			return "`" + property + "` >= "
					+ SearchUtil.paramDisplayString(value);
		case LIKE:
			return "`" + property + "` LIKE "
					+ SearchUtil.paramDisplayString(value);
		case IN:
			return "`" + property + "` IN ("
					+ SearchUtil.paramDisplayString(value) + ")";
		case NOT_IN:
			return "`" + property + "` NOT IN ("
					+ SearchUtil.paramDisplayString(value) + ")";
		case NULL:
			return "`" + property + "` IS NULL";
		case NOT_NULL:
			return "`" + property + "` IS NOT NULL";
		case EMPTY:
			return "`" + property + "` IS EMPTY";
		case NOT_EMPTY:
			return "`" + property + "` IS NOT EMPTY";
		case AND:
		case OR:
			if (!(value instanceof List)) {
				return (operator == FilterOperator.AND ? "AND: " : "OR: ")
						+ "Invalid value - NOT a List: (" + value + ")";
			}
			String op = operator == FilterOperator.AND ? " AND " : " OR ";
			StringBuilder sb = new StringBuilder("(");
			boolean first = true;
			for (Object o : ((List) value)) {
				if (first) {
					first = false;
				} else {
					sb.append(op);
				}
				if (o instanceof Filter) {
					sb.append(o.toString());
				} else {
					sb.append("Invalid value - NOT a Filter: (" + o + ").");
				}
			}

			if (first) {
				return (operator == FilterOperator.AND ? "AND: " : "OR: ")
						+ " Invalid value - null/empty List.";
			} else {
				sb.append(")");
				return sb.toString();
			}
		case NOT:
			if (!(value instanceof Filter)) {
				return "NOT: Invalid value - NOT a Filter: (" + value + ").";
			}
			return "NOT " + value.toString();
		case SOME:
			if (!(value instanceof Filter)) {
				return "SOME: Invalid value - NOT a Filter: (" + value + ").";
			}
			return "SOME `" + property + "` {" + value.toString() + "}";
		case ALL:
			if (!(value instanceof Filter)) {
				return "ALL: Invalid value - NOT a Filter: (" + value + ").";
			}
			return "ALL `" + property + "` {" + value.toString() + "}";
		case NONE:
			if (!(value instanceof Filter)) {
				return "NONE: Invalid value - NOT a Filter: (" + value + ").";
			}
			return "NONE `" + property + "` {" + value.toString() + "}";
		case CUSTOM:
			// TODO
		default:
			return "Invalid filter Operator: " + operator + " Value: "
					+ SearchUtil.paramDisplayString(value);
		}
	}

}
