package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Filter {

	private String property;
	private Object value;
	private int operator;

	// TODO convert to enum
	public static final int OP_EQUAL = 0;
	public static final int OP_NOT_EQUAL = 1;
	public static final int OP_LESS_THAN = 2;
	public static final int OP_GREATER_THAN = 3;
	public static final int OP_LESS_OR_EQUAL = 4;
	public static final int OP_GREATER_OR_EQUAL = 5;
	public static final int OP_LIKE = 6;
	public static final int OP_IN = 8;
	public static final int OP_NOT_IN = 9;
	public static final int OP_NULL = 10;
	public static final int OP_NOT_NULL = 11;
	public static final int OP_EMPTY = 12;
	public static final int OP_NOT_EMPTY = 13;
	public static final int OP_AND = 100;
	public static final int OP_OR = 101;
	public static final int OP_NOT = 102;
	public static final int OP_SOME = 200;
	public static final int OP_ALL = 201;
	public static final int OP_NONE = 202;
	public static final int OP_CUSTOM = 999;

	@SuppressWarnings("unused")
	private Filter() {

	}

	public Filter(String property, Object value) {
		this.property = property;
		this.value = value;
		this.operator = OP_EQUAL;
	}

	public Filter(String property, Object value, int operator) {
		this.property = property;
		this.value = value;
		this.operator = operator;
	}

	public static Filter equal(String property, Object value) {
		return new Filter(property, value, OP_EQUAL);
	}

	public static Filter lessThan(String property, Object value) {
		return new Filter(property, value, OP_LESS_THAN);
	}

	public static Filter greaterThan(String property, Object value) {
		return new Filter(property, value, OP_GREATER_THAN);
	}

	public static Filter lessOrEqual(String property, Object value) {
		return new Filter(property, value, OP_LESS_OR_EQUAL);
	}

	public static Filter greaterOrEqual(String property, Object value) {
		return new Filter(property, value, OP_GREATER_OR_EQUAL);
	}

	public static Filter in(String property, Collection<?> value) {
		return new Filter(property, value, OP_IN);
	}

	public static Filter notIn(String property, Collection<?> value) {
		return new Filter(property, value, OP_NOT_IN);
	}

	public static Filter like(String property, String value) {
		return new Filter(property, value, OP_LIKE);
	}

	public static Filter notEqual(String property, Object value) {
		return new Filter(property, value, OP_NOT_EQUAL);
	}

	public static Filter isNull(String property) {
		return new Filter(property, true, OP_NULL);
	}

	public static Filter isNotNull(String property) {
		return new Filter(property, true, OP_NOT_NULL);
	}

	public static Filter isEmpty(String property) {
		return new Filter(property, true, OP_EMPTY);
	}

	public static Filter isNotEmpty(String property) {
		return new Filter(property, true, OP_NOT_EMPTY);
	}

	public static Filter and(Filter... filters) {
		Filter filter = new Filter("AND", null, OP_AND);
		for (Filter f : filters) {
			filter.add(f);
		}
		return filter;
	}

	public static Filter or(Filter... filters) {
		Filter filter = and(filters);
		filter.property = "OR";
		filter.operator = OP_OR;
		return filter;
	}

	public static Filter not(Filter filter) {
		return new Filter("NOT", filter, OP_NOT);
	}

	public static Filter some(String property, Filter filter) {
		return new Filter(property, filter, OP_SOME);
	}

	public static Filter all(String property, Filter filter) {
		return new Filter(property, filter, OP_ALL);
	}

	public static Filter none(String property, Filter filter) {
		return new Filter(property, filter, OP_NONE);
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

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

}
