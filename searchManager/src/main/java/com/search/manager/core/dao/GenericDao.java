package com.search.manager.core.dao;

import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public interface GenericDao<T> {

	Class<T> getModelClass() throws CoreDaoException;

	T add(T model) throws CoreDaoException;

	T update(T model) throws CoreDaoException;

	boolean delete(T model) throws CoreDaoException;

	SearchResult<T> search(Search search) throws CoreDaoException;

}
