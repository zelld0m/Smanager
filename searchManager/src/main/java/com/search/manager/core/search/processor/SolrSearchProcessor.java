package com.search.manager.core.search.processor;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.SolrCore;
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
import com.search.manager.solr.util.SolrServerFactory;

@Service("solrSearchProcessor")
public class SolrSearchProcessor extends BaseSearchProcessor implements
		SearchProcessor {

	@Autowired
	private SolrServerFactory solrServerFactory;

	@SuppressWarnings("unused")
	private SolrSearchProcessor() {
		// do nothing...
	}

	public SolrSearchProcessor(SolrServerFactory solrServerFactory) {
		this.solrServerFactory = solrServerFactory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public SearchResult<?> processSearch(Search search)
			throws CoreSearchException {
		SearchResult searchResult = new SearchResult();
		QueryResponse queryResponse = null;
		SolrServer solrServer = null;

		try {
			SolrCore solrCore = (SolrCore) search.getSearchClass()
					.getAnnotation(SolrCore.class);

			if (solrCore != null) {
				SolrQuery solrQuery = new SolrQuery();
				solrServer = solrServerFactory.getCoreInstance(solrCore.name())
						.getSolrServer();

				if (solrServer != null) {
					// String select = generateSelectClause(search);
					String where = generateWhereClause(search);
					// String orderBy = generateOrderByClause(search);

					// fl
					List<Field> fields = checkAndCleanFields(search.getFields());
					if (fields != null && fields.size() > 0) {
						for (Field field : fields) {
							if (field.getOperator() != FieldOperator.PROPERTY) {
								throw new Exception(
										"Field operation not supported in solr.");
							}
							solrQuery.addField(field.getProperty());
						}
					} else {
						solrQuery.setQuery("*:*");
					}

					// fq
					if (StringUtils.isNotEmpty(where)) {
						solrQuery.addFilterQuery(where.substring(3,
								where.length()));
					}

					// sort
					List<Sort> sorts = checkAndCleanSorts(search.getSorts());
					for (Sort sort : sorts) {
						solrQuery.addSort(sort.getProperty(),
								sort.isDesc() ? ORDER.desc : ORDER.asc);
					}

					// start
					if (search.getPageNumber() > -1) {
						solrQuery.setStart(search.getPageNumber());
					}
					
					// rows
					if (search.getMaxRowCount() > -1) {
						solrQuery.setRows(search.getMaxRowCount());
					}
					
					queryResponse = solrServer.query(solrQuery);

					if (queryResponse != null && queryResponse.getStatus() == 0) {
						searchResult.setResult(queryResponse.getBeans(search
								.getSearchClass()));
						searchResult.setTotalCount(queryResponse.getBeans(
								search.getSearchClass()).size());
						return searchResult;
					}

				}
			}
		} catch (Exception e) {
			throw new CoreSearchException(e);
		}

		return searchResult;
	}

	@Override
	public String generateStrQuery(Search search) throws CoreSearchException {
		String select = generateSelectClause(search);
		String from = generateFromClause(search);
		String where = generateWhereClause(search);
		String orderBy = generateOrderByClause(search);

		StringBuilder query = new StringBuilder();
		query.append(from);
		query.append(select);

		if (StringUtils.isNotEmpty(where)) {
			query.append("&" + where);
		}
		if (StringUtils.isNotEmpty(orderBy)) {
			query.append("&" + orderBy);
		}

		if (search.getMaxRowCount() > -1) {
			query.append("&rows=" + search.getMaxRowCount());
		}

		if (search.getPageNumber() > -1) {
			query.append("&start=" + search.getPageNumber());
		}

		return query.toString();
	}

	// Select Clause
	@Override
	protected String generateSelectClause(Search search)
			throws CoreSearchException {
		List<Field> fields = checkAndCleanFields(search.getFields());
		StringBuilder sb = null;
		boolean first = true;

		if (fields != null && fields.size() > 0) {
			for (Field field : fields) {
				if (field.getOperator() != FieldOperator.PROPERTY) {
					throw new CoreSearchException(
							"Field operation not supported in solr.");
				}
				if (first) {
					sb = new StringBuilder("q=*:*&fl=");
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(field.getProperty());
			}
		}
		if (first) {
			return "q=*:*";
		}

		return sb.toString();
	}

	// From Clause
	@Override
	protected String generateFromClause(Search search)
			throws CoreSearchException {
		SolrCore solrCore = (SolrCore) search.getSearchClass().getAnnotation(
				SolrCore.class);

		if (solrCore != null) {
			return solrCore.name() + "/select?";
		}

		return null;
	}

	// Where Clause
	@Override
	protected String generateWhereClause(Search search)
			throws CoreSearchException {
		List<Filter> filters = checkAndCleanFilters(search.getFilters());
		boolean isDisjunction = search.isDisjunction();
		String whereClause = "";

		if (filters == null || filters.size() == 0) {
			return whereClause;
		} else if (filters.size() == 1) {
			whereClause = toFilterQuery(filters.get(0));
		} else {
			Filter junction = new Filter(null, filters,
					isDisjunction ? FilterOperator.OR : FilterOperator.AND);
			whereClause = toFilterQuery(junction);
		}

		return (whereClause == null) ? "" : "fq=" + whereClause;
	}

	// Order By Clause
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
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	protected String toFilterQuery(Filter filter) {
		String property = filter.getProperty();
		Object value = filter.getValue();
		FilterOperator operator = filter.getOperator();

		switch (operator) {
		case EQUAL:
			return property + ":" + param(value);
		case NOT_EQUAL:
			return "-" + property + ":(" + param(value) + ")";
		case LESS_THAN:
			return property + ":[* TO " + param(value) + "}";
		case GREATER_THAN:
			return property + ":{" + param(value) + " TO *]";
		case LESS_OR_EQUAL:
			return property + ":[* TO " + param(value) + "]";
		case GREATER_OR_EQUAL:
			return property + ":[" + param(value) + " TO *]";
		case LIKE:
			return property + ":*" + value + "*";
		case IN:
			return property + ":(" + param(value) + ")";
		case NOT_IN:
			return "-" + property + ":(" + param(value) + ")";
		case EMPTY:
		case NULL:
			return "-" + property + ":['' TO *]";
		case NOT_EMPTY:
		case NOT_NULL:
			return property + ":['' TO *]";
		case AND:
		case OR:
			if (!(value instanceof List)) {
				return null;
			}
			String op = filter.getOperator() == FilterOperator.AND ? " AND "
					: " OR ";
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
			return null; // TODO
		case SOME:
			return null; // TODO
		case ALL:
			return null; // TODO
		case NONE:
			return null; // TODO
		case CUSTOM:
			return null; // TODO
		default:
			throw new IllegalArgumentException("Filter comparison ( "
					+ operator + " ) is invalid.");
		}
	}

	@SuppressWarnings("rawtypes")
	protected String param(Object value) {
		if (value instanceof Class) {
			return "\"" + ((Class<?>) value).getName() + "\"";
		}
		if (value instanceof DateTime) {
			// TODO Ignore .SSS on comparison
			return "\""
					+ ((DateTime) value).withZone(DateTimeZone.UTC).toString(
							"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") + "\"";
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
		} else if (value instanceof Object[]) {
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
