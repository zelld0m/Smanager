package com.search.manager.core.search;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SearchBuilder {

    private Search search;

    private SearchBuilder(Class<?> searchClass) {
        this.search = new Search(searchClass);
    }

    public static SearchBuilder forClass(Class<?> searchClass) {
        return new SearchBuilder(searchClass);
    }

    public SearchBuilder setMaxRowCount(int maxRowCount) {
        search.setMaxRowCount(maxRowCount);
        return this;
    }

    public SearchBuilder setPageNumber(int pageNumber) {
        search.setPageNumber(pageNumber);
        return this;
    }

    public SearchBuilder setSearchClass(Class<?> searchClass) {
        search.setSearchClass(searchClass);
        return this;
    }

    public SearchBuilder setFilters(List<Filter> filters) {
        search.setFilters(filters);
        return this;
    }

    public SearchBuilder setSorts(List<Sort> sorts) {
        search.setSorts(sorts);
        return this;
    }

    public SearchBuilder setFields(List<Field> fields) {
        search.setFields(fields);
        return this;
    }

    public SearchBuilder setDistinct(boolean distinct) {
        search.setDistinct(distinct);
        return this;
    }

    public SearchBuilder setDisjunction(boolean disjunction) {
        search.setDisjunction(disjunction);
        return this;
    }

    public SearchBuilder addFilter(Filter filter) {
        search.addFilter(filter);
        return this;
    }

    public SearchBuilder addFilters(List<Filter> filters) {
        search.addFilters(filters);
        return this;
    }

    public SearchBuilder addSort(Sort sort) {
        search.addSort(sort);
        return this;
    }

    public SearchBuilder addSorts(List<Sort> sorts) {
        search.addSorts(sorts);
        return this;
    }

    public SearchBuilder addField(Field field) {
        search.addField(field);
        return this;
    }

    public SearchBuilder addFields(List<Field> fields) {
        search.addFields(fields);
        return this;
    }

    public SearchBuilder addFilterIfPresent(String name, Object value) {
        if (StringUtils.isNotBlank(name) && value != null) {
            search.addFilter(new Filter(name, value));
        }
        
        return this;
    }

    public SearchBuilder addFilterIfPresent(String name, String value) {
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
            search.addFilter(new Filter(name, value));
        }
        
        return this;
    }

    public Search get() {
        return search;
    }
}
