package com.search.manager.core.dao.sp;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.object.StoredProcedure;

import com.search.manager.core.annotation.AuditableMethod;
import com.search.manager.core.dao.GenericDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreSearchException;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchProcessor;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.search.processor.SpSearchProcessor;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.model.constants.AuditTrailConstants.Operation;

public abstract class GenericDaoSpImpl<T> implements GenericDao<T> {

	private Class<T> modelClass;

	protected abstract StoredProcedure getAddStoredProcedure()
			throws CoreDaoException;

	protected abstract StoredProcedure getUpdateStoredProcedure()
			throws CoreDaoException;

	protected abstract StoredProcedure getDeleteStoredProcedure()
			throws CoreDaoException;

	protected abstract StoredProcedure getSearchStoredProcedure()
			throws CoreDaoException;

	protected abstract Map<String, Object> generateAddInput(T model)
			throws CoreDaoException;

	protected abstract Map<String, Object> generateUpdateInput(T model)
			throws CoreDaoException;

	protected abstract Map<String, Object> generateDeleteInput(T model)
			throws CoreDaoException;

	protected abstract Search generateSearchInput(T model)
			throws CoreDaoException;

	protected abstract Map<String, Object> getDefaultInParam()
			throws CoreDaoException;

	protected abstract Search generateSearchById(String id, String storeId)
			throws CoreDaoException;

	@SuppressWarnings("unchecked")
	public GenericDaoSpImpl() {
		this.modelClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Class<T> getModelClass() {
		return this.modelClass;
	}

	@AuditableMethod(operation = Operation.add)
	@Override
	public T add(T model) throws CoreDaoException {
		Map<String, Object> inParams = generateAddInput(model);

		if (inParams != null) {
			int status = DAOUtils.getUpdateCount(getAddStoredProcedure()
					.execute(inParams));
			if (status > 0) {
				// TODO return added model in sp level
				// add primary key per model
				if (inParams.get(DAOConstants.MODEL_ID) != null) {
					SearchResult<T> searchResult = search(generateSearchById(
							inParams.get(DAOConstants.MODEL_ID).toString(),
							inParams.get(DAOConstants.PARAM_STORE_ID)
									.toString()));
					if (searchResult.getTotalCount() > 0) {
						return searchResult.getResult().get(0);
					}
				}
				return model;
			}
		}

		return null;
	}

	@AuditableMethod(operation = Operation.add)
	@Override
	public List<T> add(Collection<T> models) throws CoreDaoException {
		List<T> addedModels = new ArrayList<T>();
		for (T model : models) {
			model = add(model);
			if (model != null) {
				addedModels.add(model);
			}
		}
		return addedModels;
	}

	@AuditableMethod(operation = Operation.update)
	@Override
	public T update(T model) throws CoreDaoException {
		Map<String, Object> inParams = generateUpdateInput(model);

		if (inParams != null) {
			int status = DAOUtils.getUpdateCount(getUpdateStoredProcedure()
					.execute(inParams));
			if (status > 0) {
				// TODO return updated model in sp level
				SearchResult<T> searchResult = search(generateSearchById(
						inParams.get(DAOConstants.MODEL_ID).toString(),
						inParams.get(DAOConstants.PARAM_STORE_ID).toString()));
				if (searchResult.getTotalCount() > 0) {
					return searchResult.getResult().get(0);
				}
			}
		}

		return null;
	}

	@Override
	public List<T> update(Collection<T> models) throws CoreDaoException {
		if (models != null) {
			List<T> updatedModels = new ArrayList<T>();
			for (T model : models) {
				model = update(model);
				if (model != null) {
					updatedModels.add(model);
				}
			}
			return updatedModels;
		}
		return null;
	}

	@AuditableMethod(operation = Operation.delete)
	@Override
	public boolean delete(T model) throws CoreDaoException {
		Map<String, Object> inParams = generateDeleteInput(model);
		if (inParams != null) {
			int status = DAOUtils.getUpdateCount(getDeleteStoredProcedure()
					.execute(inParams));
			if (status > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<T, Boolean> delete(Collection<T> models) throws CoreDaoException {
		if (models != null) {
			Map<T, Boolean> deletedModelStatus = new HashMap<T, Boolean>();
			for (T model : models) {
				deletedModelStatus.put(model, delete(model));
			}
			return deletedModelStatus;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<T> search(Search search) throws CoreDaoException {
		try {
			SearchProcessor searchProcessor = new SpSearchProcessor(
					getSearchStoredProcedure(), getDefaultInParam());
			return (SearchResult<T>) searchProcessor.processSearch(search);
		} catch (CoreSearchException e) {
			throw new CoreDaoException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SearchResult<T> search(T model) throws CoreDaoException {
		try {
			SearchProcessor searchProcessor = new SpSearchProcessor(
					getSearchStoredProcedure(), getDefaultInParam());
			return (SearchResult<T>) searchProcessor
					.processSearch(generateSearchInput(model));
		} catch (CoreSearchException e) {
			throw new CoreDaoException(e);
		}
	}

}
