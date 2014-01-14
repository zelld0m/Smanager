package com.search.manager.core.search;

import java.util.List;

import com.search.manager.core.exception.CoreSearchException;

public abstract class BaseSearchProcessor {

	protected abstract String generateStrQuery(Search search)
			throws CoreSearchException;

	protected abstract String generateSelectClause(Search search)
			throws CoreSearchException;

	protected abstract String generateFromClause(Search search)
			throws CoreSearchException;

	protected abstract String generateWhereClause(Search search)
			throws CoreSearchException;

	protected abstract String generateOrderByClause(Search search)
			throws CoreSearchException;

	protected abstract List<Field> checkAndCleanFields(List<Field> fields)
			throws CoreSearchException;

	protected abstract List<Filter> checkAndCleanFilters(List<Filter> filters)
			throws CoreSearchException;

	protected abstract List<Sort> checkAndCleanSorts(List<Sort> sorts)
			throws CoreSearchException;

}
