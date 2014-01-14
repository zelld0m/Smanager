package com.search.manager.core.service;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public interface GenericService<T> {

	T add(T model) throws CoreServiceException;

	Collection<T> add(Collection<T> models) throws CoreServiceException;

	T update(T model) throws CoreServiceException;

	Collection<T> update(Collection<T> models) throws CoreServiceException;

	boolean delete(T model) throws CoreServiceException;

	Map<T, Boolean> delete(Collection<T> models) throws CoreServiceException;

	SearchResult<T> search(Search search) throws CoreServiceException;

	SearchResult<T> search(T model) throws CoreServiceException;

	SearchResult<T> search(T model, int pageNumber, int maxRowCount)
			throws CoreServiceException;

	T searchById(String storeId, String id) throws CoreServiceException;

}
