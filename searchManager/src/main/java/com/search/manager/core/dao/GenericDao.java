package com.search.manager.core.dao;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public interface GenericDao<T> {

	Class<T> getModelClass() throws CoreDaoException;

	T add(T model) throws CoreDaoException;

	Collection<T> add(Collection<T> models) throws CoreDaoException;

	T update(T model) throws CoreDaoException;

	Collection<T> update(Collection<T> models) throws CoreDaoException;

	boolean delete(T model) throws CoreDaoException;

	Map<T, Boolean> delete(Collection<T> models) throws CoreDaoException;

	SearchResult<T> search(Search search) throws CoreDaoException;

	SearchResult<T> search(T model) throws CoreDaoException;

	SearchResult<T> search(T model, int pageNumber, int maxRowCount)
			throws CoreDaoException;

}
