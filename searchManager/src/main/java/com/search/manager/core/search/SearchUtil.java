package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.List;

public class SearchUtil {

	// Filters
	public static void addFilter(Search search, Filter filter) {
		List<Filter> filters = search.getFilters();
		if (filters == null) {
			filters = new ArrayList<Filter>();
			search.setFilters(filters);
		}
		filters.add(filter);
	}

	public static void addFilters(Search search, List<Filter> filters) {
		if (filters != null) {
			for (Filter filter : filters) {
				addFilter(search, filter);
			}
		}
	}

	public static void removeFilter(Search search, Filter filter) {
		List<Filter> filters = search.getFilters();
		if (filters != null) {
			filters.remove(filter);
		}
	}

	public static void clearFilters(Search search) {
		if (search.getFilters() != null) {
			search.getFilters().clear();
		}
	}

	// Sorts
	public static void addSort(Search search, Sort sort) {
		List<Sort> sorts = search.getSorts();
		if (sorts == null) {
			sorts = new ArrayList<Sort>();
			search.setSorts(sorts);
		}
		sorts.add(sort);
	}

	public static void addSorts(Search search, List<Sort> sorts) {
		if (sorts != null) {
			for (Sort sort : sorts) {
				addSort(search, sort);
			}
		}
	}

	public static void removeSort(Search search, Sort sort) {
		List<Sort> sorts = search.getSorts();
		if (sorts != null) {
			sorts.remove(sort);
		}
	}

	public static void clearSorts(Search search) {
		if (search.getSorts() != null) {
			search.getSorts().clear();
		}
	}

	// Fields
	public static void addField(Search search, Field field) {
		List<Field> fields = search.getFields();
		if (field == null) {
			fields = new ArrayList<Field>();
			search.setFields(fields);
		}
		fields.add(field);
	}

	public static void addFields(Search search, List<Field> fields) {
		if (fields != null) {
			for (Field field : fields) {
				addField(search, field);
			}
		}
	}

	public static void removeField(Search search, Field field) {
		List<Field> fields = search.getFields();
		if (fields != null) {
			fields.remove(field);
		}
	}

	public static void clearFields(Search search) {
		if (search.getFields() != null) {
			search.getFields().clear();
		}
	}

}
