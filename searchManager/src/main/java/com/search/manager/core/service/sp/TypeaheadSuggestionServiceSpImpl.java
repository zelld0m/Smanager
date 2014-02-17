package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.dao.TypeaheadSuggestionDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.TypeaheadSuggestionService;

public class TypeaheadSuggestionServiceSpImpl implements TypeaheadSuggestionService{

	private TypeaheadSuggestionDao dao;
	
	@Override
	public TypeaheadSuggestion add(TypeaheadSuggestion model)
			throws CoreServiceException {
		try {
			return dao.add(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<TypeaheadSuggestion> add(
			Collection<TypeaheadSuggestion> models) throws CoreServiceException {
		try {
			return dao.add(models);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TypeaheadSuggestion update(TypeaheadSuggestion model)
			throws CoreServiceException {
		try {
			return dao.update(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<TypeaheadSuggestion> update(
			Collection<TypeaheadSuggestion> models) throws CoreServiceException {
		try {
			return dao.update(models);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(TypeaheadSuggestion model)
			throws CoreServiceException {
		throw new CoreServiceException("Method does not have an implementation.");
	}

	@Override
	public Map<TypeaheadSuggestion, Boolean> delete(
			Collection<TypeaheadSuggestion> models) throws CoreServiceException {
		throw new CoreServiceException("Method does not have an implementation.");
	}

	@Override
	public SearchResult<TypeaheadSuggestion> search(Search search)
			throws CoreServiceException {
		try {
			return dao.search(search);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult<TypeaheadSuggestion> search(TypeaheadSuggestion model)
			throws CoreServiceException {
		try {
			return dao.search(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult<TypeaheadSuggestion> search(TypeaheadSuggestion model,
			int pageNumber, int maxRowCount) throws CoreServiceException {
		try {
			return dao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TypeaheadSuggestion searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
