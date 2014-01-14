package com.search.manager.core.search.processor;

import java.util.Collection;
import java.util.List;

import com.search.manager.core.exception.CoreSearchException;
import com.search.manager.core.search.BaseSearchProcessor;
import com.search.manager.core.search.Field;
import com.search.manager.core.search.Field.FieldOperator;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Filter.FilterOperator;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchProcessor;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.search.Sort;

public class SqlSearchProcessor extends BaseSearchProcessor implements
		SearchProcessor {

	@SuppressWarnings("unused")
	private SqlSearchProcessor() {
		// do nothing...
	}

	public SqlSearchProcessor(String TODO) {
		// this.search = search;
	}

	@Override
	public SearchResult<?> processSearch(Search search)
			throws CoreSearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateStrQuery(Search search) throws CoreSearchException {
		String select = generateSelectClause(search);
		String from = generateFromClause(search);
		String where = generateWhereClause(search);
		String orderBy = generateOrderByClause(search);

		StringBuilder query = new StringBuilder();
		query.append(select);
		query.append(from);
		query.append(where);
		query.append(orderBy);

		return query.toString();
	}

	@Override
	protected String generateSelectClause(Search search)
			throws CoreSearchException {
		StringBuilder sb = null;
		boolean useOperator = false;
		boolean notUseOperator = false;
		boolean first = true;
		boolean distinct = search.isDistinct();
		List<Field> fields = checkAndCleanFields(search.getFields());

		if (fields != null) {
			for (Field field : fields) {
				if (first) {
					sb = new StringBuilder("select ");
					if (distinct) {
						sb.append("distinct ");
					}
					first = false;
				} else {
					sb.append(", ");
				}

				if (field.getOperator() == FieldOperator.CUSTOM) {
					if (field.getProperty() == null) {
						sb.append("null");
					} else {
						// TODO
						sb.append(field.getProperty());
					}
				} else {
					String prop;
					if (field.getProperty() == null
							|| "".equals(field.getProperty())) {
						prop = "*";
					} else {
						prop = field.getProperty();
					}

					switch (field.getOperator()) {
					case AVG:
						sb.append("avg(");
						useOperator = true;
						break;
					case COUNT:
						sb.append("count(");
						useOperator = true;
						break;
					case COUNT_DISTINCT:
						sb.append("count(distinct ");
						useOperator = true;
						break;
					case MAX:
						sb.append("max(");
						useOperator = true;
						break;
					case MIN:
						sb.append("min(");
						useOperator = true;
						break;
					case SUM:
						sb.append("sum(");
						useOperator = true;
						break;
					default:
						notUseOperator = true;
						break;
					}

					sb.append(prop);
					if (useOperator) {
						sb.append(")");
					}
					if (field.getKey() != null) {
						sb.append(" as `");
						sb.append(field.getKey());
						sb.append("` ");
					}
				}
			}
		}
		if (first) {
			// there are no fields
			if (distinct) {
				return "select distinct *";
			} else {
				return "select *";
			}
		}
		if (useOperator && notUseOperator) {
			throw new Error(
					"A search can not have a mix of fields with operators and fields without operators.");
		}

		return sb.toString();
	}

	// From Clause
	@Override
	protected String generateFromClause(Search search)
			throws CoreSearchException {
		StringBuilder sb = new StringBuilder(" from ");
		// TODO annotate table name
		sb.append(search.getSearchClass().getSimpleName());
		return sb.toString();
	}

	// Where Clause
	@Override
	protected String generateWhereClause(Search search)
			throws CoreSearchException {
		String whereClause = null;
		List<Filter> filters = checkAndCleanFilters(search.getFilters());
		boolean isDisjunction = search.isDisjunction();

		if (filters == null || filters.size() == 0) {
			return "";
		} else if (filters.size() == 1) {
			whereClause = toFilterQuery(filters.get(0));
		} else {
			Filter junction = new Filter(null, filters,
					isDisjunction ? FilterOperator.OR : FilterOperator.AND);
			whereClause = toFilterQuery(junction);
		}

		return (whereClause == null) ? "" : " where " + whereClause;
	}

	@Override
	protected String generateOrderByClause(Search search)
			throws CoreSearchException {
		List<Sort> sorts = checkAndCleanSorts(search.getSorts());

		if (sorts == null) {
			return "";
		}

		StringBuilder sb = null;
		boolean first = true;
		for (Sort sort : sorts) {
			if (first) {
				sb = new StringBuilder(" order by ");
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(sort.getProperty());
			sb.append(sort.isDesc() ? " desc" : " asc");
		}
		if (first) {
			return "";
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	protected String toFilterQuery(Filter filter) {
		String property = filter.getProperty();
		Object value = filter.getValue();
		FilterOperator operator = filter.getOperator();

		// for IN and NOT IN, if value is empty list, return false, and true
		// respectively
		if (operator == FilterOperator.IN || operator == FilterOperator.NOT_IN) {
			if (value instanceof Collection && ((Collection) value).size() == 0) {
				return operator == FilterOperator.IN ? "1 = 2" : "1 = 1";
			}
			if (value instanceof Object[] && ((Object[]) value).length == 0) {
				return operator == FilterOperator.IN ? "1 = 2" : "1 = 1";
			}
		}

		switch (operator) {
		case EQUAL:
			return property + " = " + param(value);
		case NOT_EQUAL:
			return property + " != " + param(value);
		case LESS_THAN:
			return property + " < " + param(value);
		case GREATER_THAN:
			return property + " > " + param(value);
		case LESS_OR_EQUAL:
			return property + " <= " + param(value);
		case GREATER_OR_EQUAL:
			return property + " >= " + param(value);
		case LIKE:
			return property + " like " + param(value);
		case IN:
			return property + " in (" + param(value) + ")";
		case NOT_IN:
			return property + " not in (" + param(value) + ")";
		case EMPTY:
			return null; // TODO
		case NULL:
			return property + " is null ";
		case NOT_EMPTY:
			return null; // TODO
		case NOT_NULL:
			return property + " is not null ";
		case AND:
		case OR:
			if (!(value instanceof List)) {
				return null;
			}
			String op = filter.getOperator() == FilterOperator.AND ? " and "
					: " or ";
			StringBuilder sb = new StringBuilder("(");
			boolean first = true;
			for (Object o : ((List) value)) {
				if (o instanceof Filter) {
					String filterStr = toFilterQuery((Filter) o);
					if (filterStr != null) {
						if (first) {
							first = false;
						} else {
							sb.append(op);
						}
						sb.append(filterStr);
					}
				}
			}
			if (first) {
				return null;
			}
			sb.append(")");
			return sb.toString();
		case NOT:
			if (!(value instanceof Filter)) {
				return null;
			}
			String filterStr = toFilterQuery((Filter) value);
			if (filterStr == null)
				return null;

			return "not " + filterStr;
		case SOME:
			return null; // TODO
		case ALL:
			return null; // TODO
		case NONE:
			return null; // TODO
		case CUSTOM:
			return null; // TODO
		default:
			throw new IllegalArgumentException("Filter operator ( " + operator
					+ " ) is invalid.");
		}
	}

	@SuppressWarnings("rawtypes")
	protected String param(Object value) {
		if (value instanceof Class) {
			return "\"" + ((Class<?>) value).getName() + "\"";
		}
		if (value instanceof Collection) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Object o : (Collection) value) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append("\"" + o + "\"");
			}
			return sb.toString();
		} else if (value instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Object o : (Object[]) value) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append("\"" + o + "\"");
			}
			return sb.toString();
		} else {
			return "\"" + value.toString() + "\"";
		}
	}

	@Override
	protected List<Field> checkAndCleanFields(List<Field> fields)
			throws CoreSearchException {
		// TODO Auto-generated method stub
		return fields;
	}

	@Override
	protected List<Filter> checkAndCleanFilters(List<Filter> filters)
			throws CoreSearchException {
		// TODO Auto-generated method stub
		return filters;
	}

	@Override
	protected List<Sort> checkAndCleanSorts(List<Sort> sorts)
			throws CoreSearchException {
		// TODO Auto-generated method stub
		return sorts;
	}

}
