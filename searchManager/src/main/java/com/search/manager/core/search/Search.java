package com.search.manager.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Search implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int maxRowCount = -1;
    protected int pageNumber = -1;
    protected Class<?> searchClass;
    protected List<Filter> filters = new ArrayList<Filter>();
    protected List<Sort> sorts = new ArrayList<Sort>();
    protected List<Field> fields = new ArrayList<Field>();
    protected boolean distinct;
    protected boolean disjunction;

    @SuppressWarnings("unused")
    private Search() {
        // do nothing...
    }

    public Search(Class<?> searchClass) {
        this.setSearchClass(searchClass);
    }

    // Getter and Setter

    public int getMaxRowCount() {
        return maxRowCount;
    }

    public void setMaxRowCount(int maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

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
    
    private void ensureFiltersNotNull() {
        if (filters == null) {
            filters = new ArrayList<Filter>();
        }
    }

    // Filters
    public void addFilter(Filter filter) {
        if (filter != null) {
            ensureFiltersNotNull();
            filters.add(filter);
        }
    }

    public void addFilters(List<Filter> filters) {
        if (filters != null) {
            ensureFiltersNotNull();
            this.filters.addAll(filters);
        }
    }

    public void removeFilter(Filter filter) {
        if (filters != null) {
            filters.remove(filter);
        }
    }

    public void clearFilters() {
        if (filters != null) {
            filters.clear();
        }
    }
    
    private void ensureSortsNotNull() {
        if (sorts == null) {
            sorts = new ArrayList<Sort>();
        }
    }

    // Sorts
    public void addSort(Sort sort) {
        if (sort != null) {
            ensureSortsNotNull();
            sorts.add(sort);
        }
    }

    public void addSorts(List<Sort> sorts) {
        if (sorts != null) {
            ensureSortsNotNull();
            this.sorts.addAll(sorts);
        }
    }

    public void removeSort(Sort sort) {
        if (sorts != null) {
            sorts.remove(sort);
        }
    }

    public void clearSorts() {
        if (sorts != null) {
            sorts.clear();
        }
    }
    private void ensureFieldsNotNull() {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }
    }

    // Fields
    public void addField(Field field) {
        if (field != null) {
            ensureFieldsNotNull();
            fields.add(field);
        }
    }


    public void addFields(List<Field> fields) {
        if (fields != null) {
            ensureFieldsNotNull();
            this.fields.addAll(fields);
        }
    }

    public void removeField(Field field) {
        if (fields != null) {
            fields.remove(field);
        }
    }

    public void clearFields() {
        if (fields != null) {
            fields.clear();
        }
    }

}
