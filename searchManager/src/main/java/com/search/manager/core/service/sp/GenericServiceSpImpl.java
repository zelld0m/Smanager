package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.dao.GenericDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.ModelBean;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;

public abstract class GenericServiceSpImpl<T extends ModelBean> {

	protected GenericDao<T> dao;
	
	public GenericServiceSpImpl(GenericDao<T> dao) {
		this.dao = dao;
	}
	
	public T add(T model)
			throws CoreServiceException {
		
		try {
			return dao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public Collection<T> add(
			Collection<T> models) throws CoreServiceException {
		try {
			return dao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public T update(T model)
			throws CoreServiceException {
		try {
			return dao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public Collection<T> update(
			Collection<T> models) throws CoreServiceException {
		try {
			return dao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public boolean delete(T model) throws CoreServiceException {
		try {
			return dao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public Map<T, Boolean> delete(
			Collection<T> models) throws CoreServiceException {
		try {
			return dao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public SearchResult<T> search(Search search)
			throws CoreServiceException {
		try {
			return dao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public SearchResult<T> search(T model)
			throws CoreServiceException {
		try {
			return dao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	public SearchResult<T> search(T model,
			int pageNumber, int maxRowCount) throws CoreServiceException {
		try {
			return dao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

}
