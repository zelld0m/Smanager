package com.search.manager.core.dao;

import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public interface GenericDao<T> {

	Class<T> getModelClass() throws Exception;

	T add(T model) throws Exception;

	T update(T model) throws Exception;

	boolean delete(T model) throws Exception;

	SearchResult<T> search(Search search) throws Exception;

}
