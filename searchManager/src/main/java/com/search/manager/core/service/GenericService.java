package com.search.manager.core.service;

import java.util.Collection;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public interface GenericService<T> {

	T add(T model) throws CoreServiceException;

	Collection<T> add(Collection<T> models) throws CoreServiceException;

	T update(T model) throws CoreServiceException;

	boolean delete(T model) throws CoreServiceException;

	SearchResult<T> search(Search search) throws CoreServiceException;

	T searchById(String storeId, String id) throws CoreServiceException;

}
