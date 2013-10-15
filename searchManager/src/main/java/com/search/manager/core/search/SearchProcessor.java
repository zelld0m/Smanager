package com.search.manager.core.search;

import java.util.List;

public abstract class SearchProcessor {

	protected abstract String generateStrQuery(Search search) throws Exception;

	protected abstract String generateSelectClause(Search search)
			throws Exception;

	protected abstract String generateFromClause(Search search)
			throws Exception;

	protected abstract String generateWhereClause(Search search)
			throws Exception;

	protected abstract String generateOrderByClause(Search search)
			throws Exception;

	protected abstract List<Field> checkAndCleanFields(List<Field> fields)
			throws Exception;

	protected abstract List<Filter> checkAndCleanFilters(List<Filter> filters)
			throws Exception;

	protected abstract List<Sort> checkAndCleanSorts(List<Sort> sorts)
			throws Exception;
}
