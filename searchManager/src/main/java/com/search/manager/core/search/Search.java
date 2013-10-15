package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.List;

public class Search {

	protected int firstResult = -1;
	protected int maxResult = -1;
	protected int page = -1;
	protected Class<?> searchClass;
	protected List<Filter> filters = new ArrayList<Filter>();
	protected List<Sort> sorts = new ArrayList<Sort>();
	protected List<Field> fields = new ArrayList<Field>();
	protected boolean distinct;
	protected boolean disjunction;

	@SuppressWarnings("unused")
	private Search() {

	}

	public Search(Class<?> searchClass) {
		this.setSearchClass(searchClass);
	}

	// Getter and Setter

	public void setSearchClass(Class<?> searchClass) {
		this.searchClass = searchClass;
	}

	public Class<?> getSearchClass() {
		return searchClass;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Field> getFields() {
		return fields;
	}
	
	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isDisjunction() {
		return disjunction;
	}

	public void setDisjunction(boolean disjunction) {
		this.disjunction = disjunction;
	}
	
	// Filters
	public Search addFilter(Filter filter) {
		SearchUtil.addFilter(this, filter);
		return this;
	}

	public Search addFilters(List<Filter> filters) {
		SearchUtil.addFilters(this, filters);
		return this;
	}

	public void removeFilter(Filter filter) {
		SearchUtil.removeFilter(this, filter);
	}

	public void clearFilters() {
		SearchUtil.clearFilters(this);
	}

	// Sorts
	public Search addSort(Sort sort) {
		SearchUtil.addSort(this, sort);
		return this;
	}

	public Search addSorts(List<Sort> sorts) {
		SearchUtil.addSorts(this, sorts);
		return this;
	}

	public void removeSort(Sort sort) {
		SearchUtil.removeSort(this, sort);
	}

	public void clearSorts() {
		SearchUtil.clearSorts(this);
	}

	// Fields
	public Search addField(Field field) {
		SearchUtil.addField(this, field);
		return this;
	}

	public Search addFields(List<Field> fields) {
		SearchUtil.addFields(this, fields);
		return this;
	}

	public void removeField(Field field) {
		SearchUtil.removeField(this, field);
	}

	public void clearFields() {
		SearchUtil.clearFields(this);
	}

}
