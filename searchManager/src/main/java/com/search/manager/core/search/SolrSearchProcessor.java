package com.search.manager.core.search;

import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;

import com.search.manager.core.rule.Core;

public class SolrSearchProcessor extends SearchProcessor {

	protected SolrServer solrServer;
	protected Search search;
	
	@SuppressWarnings("unused")
	private SolrSearchProcessor() {
		
	}
	
	public SolrSearchProcessor(SolrServer solrServer) {
		this.solrServer = solrServer;
	}
	
	@Override
	public String generateStrQuery(Search search) throws Exception {
		String select = generateSelectClause(search);
		String from = generateFromClause(search);
		String where = generateWhereClause(search);
		String orderBy = generateOrderByClause(search);
		
		StringBuilder query = new StringBuilder();
		query.append(from);
		query.append(select);
		query.append(where);
		query.append(orderBy);
		
		return query.toString();
	}
	
	// Select Clause
	@Override
	protected String generateSelectClause(Search search) throws Exception {
		StringBuilder sb = null;
		boolean useOperator = false;
		boolean notUseOperator = false;
		boolean first = true;
		boolean distinct = search.isDistinct();
		List<Field> fields = checkAndCleanFields(search.getFields());
		
		if (fields != null) {
			for(Field field : fields) {
				if (first) {
					sb = new StringBuilder("q=*:*&fl=");
					first = false;
				} else {
					sb.append(", ");
				}
				
				if (field.getOperator() == Field.OP_CUSTOM) {
					
				} else {
					String prop;
					if (field.getProperty() == null || "".equals(field.getProperty())) {
						prop = "*";
					} else {
						prop = field.getProperty();
					}
					
					switch (field.getOperator()) {
					case Field.OP_AVG:
//						sb.append("avg(");
						useOperator = true;
						break;
					case Field.OP_COUNT:
//						sb.append("count(");
						useOperator = true;
						break;
					case Field.OP_COUNT_DISTINCT:
//						sb.append("count(distinct ");
						useOperator = true;
						break;
					case Field.OP_MAX:
//						sb.append("max(");
						useOperator = true;
						break;
					case Field.OP_MIN:
//						sb.append("min(");
						useOperator = true;
						break;
					case Field.OP_SUM:
//						sb.append("sum(");
						useOperator = true;
						break;
					default:
						notUseOperator = true;
						break;
					}
					
					sb.append(prop);
					if (useOperator) {
//						sb.append(")");
					}
				}
			}
		}
		if (first) {
			// there are no fields
			if (distinct) {
				// TODO faceting?
			} else {
				return "q=*:*";
			}
		}
		if (useOperator && notUseOperator) {
			throw new Error("A search can not have a mix of fields with operators and fields without operators.");
		}
		
		return sb.toString();
	}
	
	// From Clause
	@Override
	protected String generateFromClause(Search search) throws Exception {
		StringBuilder sb = new StringBuilder("/");
		
		//String core = search.getSearchClass().getAnnotation(Core.class).name();
		sb.append("core");
		sb.append("/select?");
		
		return sb.toString();
	}
	
	// Where Clause
	@Override
	protected String generateWhereClause(Search search) throws Exception {
		String whereClause = null;
		List<Filter> filters = checkAndCleanFilters(search.getFilters());
		boolean isDisjunction = search.isDisjunction();
		
		if (filters == null || filters.size() == 0) {
			return "";
		} else if(filters.size() == 1) {
			// fq
			whereClause = toFilterQuery(filters.get(0));
		} else {
			Filter junction = new Filter(null, filters, isDisjunction ? Filter.OP_OR : Filter.OP_AND);
			whereClause = toFilterQuery(junction);
		}
		return (whereClause == null) ? "" : "&fq=" + whereClause;
	}
	
	// Order By Clause
	protected String generateOrderByClause(Search search) throws Exception {
		List<Sort> sorts = checkAndCleanSorts(search.getSorts());
		
		if (sorts == null) {
			return "";
		}
		
		StringBuilder sb = null;
		boolean first = true;
		for (Sort sort : sorts) {
			if (first) {
				sb = new StringBuilder("sort=");
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
		return "&" + sb.toString();
	}
	
	@SuppressWarnings("rawtypes")
	protected String toFilterQuery(Filter filter) {
		String property = filter.getProperty();
		Object value = filter.getValue();
		int operator = filter.getOperator();
		
		// for IN and NOT IN, if value is empty list, return false, and true
		// respectively
		if (operator == Filter.OP_IN || operator == Filter.OP_NOT_IN) {
			if (value instanceof Collection && ((Collection) value).size() == 0) {
				return operator == Filter.OP_IN ? "1 = 2" : "1 = 1";
			}
			if (value instanceof Object[] && ((Object[]) value).length == 0) {
				return operator == Filter.OP_IN ? "1 = 2" : "1 = 1";
			}
		}
		
		switch (operator) {
		case Filter.OP_EQUAL:
			return property + ":(" + param(value) + ")";
		case Filter.OP_NOT_EQUAL:
			return "-" + property + ":(" + param(value) + ")";
		case Filter.OP_LESS_THAN:
			return property + ":[* TO " + param(value) + "}";
		case Filter.OP_GREATER_THAN:
			return property + ":{" + param(value) + " TO *]";
		case Filter.OP_LESS_OR_EQUAL:
			return property + ":[* TO " + param(value) + "]";
		case Filter.OP_GREATER_OR_EQUAL:
			return property + ":[" + param(value) + " TO *]";
		case Filter.OP_LIKE:
			return property + ":*" + param(value) + "*";
		case Filter.OP_IN:
			return property + ":(" + param(value) + ")";
		case Filter.OP_NOT_IN:
			return "-" + property + ":(" + param(value) + ")";
		case Filter.OP_EMPTY:
		case Filter.OP_NULL:
			return "-" + property + ":['' TO *]";
		case Filter.OP_NOT_EMPTY:
		case Filter.OP_NOT_NULL:
			return property + ":['' TO *]";
		case Filter.OP_AND:
		case Filter.OP_OR:
			if (!(value instanceof List)) {
				return null;
			}
			String op = filter.getOperator() == Filter.OP_AND ? " AND " : " OR ";
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
		case Filter.OP_NOT:
			return null; // TODO
		case Filter.OP_SOME:
			return null; // TODO
		case Filter.OP_ALL:
			return null; // TODO
		case Filter.OP_NONE:
			return null; // TODO
		case Filter.OP_CUSTOM:
			return null; // TODO
		default:
			throw new IllegalArgumentException("Filter comparison ( " + operator + " ) is invalid.");
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
					sb.append(" OR ");
				}
				sb.append("\"" + o + "\"");
			}
			return sb.toString();
		}else if (value instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (Object o : (Object[]) value) {
				if (first) {
					first = false;
				} else {
					sb.append(" OR ");
				}
				sb.append("\"" + o + "\"");
			}
			return sb.toString();
		} else {
			return "\"" + value.toString() + "\"";
		}
	}
	
	@Override
	protected List<Field> checkAndCleanFields(List<Field> fields) throws Exception {
		if (fields == null) {
			return null;
		}
		
		for (Field field : fields) {
			if (field == null) {
				throw new IllegalArgumentException("The search contains a null field.");
			}
//			if (field.getProperty() != null && field.getOperator() != Field.OP_CUSTOM) {
//				
//			}
		}
		
		return fields;
	}
	
	@Override
	protected List<Filter> checkAndCleanFilters(List<Filter> filters) throws Exception {
		// If the operator needs a value and no value is specified, ignore this filter.
		// Only NULL, NOT_NULL, EMPTY, NOT_EMPTY and CUSTOM do not need a value.
		
		// Remove operators that take sub filters but have none
		// assigned. Replace conjunctions that only have a single
		// sub-filter with that sub-filter.
		return filters;
	}
	
	@Override
	protected List<Sort> checkAndCleanSorts(List<Sort> sorts) throws Exception {
		
		return sorts;
	}
	
}
