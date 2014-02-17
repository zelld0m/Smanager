package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.Map;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.TypeaheadRuleService;

public class TypeaheadRuleServiceSpImpl implements TypeaheadRuleService{

	private TypeaheadRuleDao dao;
	
	@Override
	public TypeaheadRule add(TypeaheadRule model) throws CoreServiceException {
		try {
			return dao.add(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Collection<TypeaheadRule> add(Collection<TypeaheadRule> models)
			throws CoreServiceException {
		try {
			return dao.add(models);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TypeaheadRule update(TypeaheadRule model)
			throws CoreServiceException {
		try {
			return dao.update(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<TypeaheadRule> update(Collection<TypeaheadRule> models)
			throws CoreServiceException {
		try {
			return dao.update(models);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(TypeaheadRule model) throws CoreServiceException {
		throw new CoreServiceException("Method does not have an implementation.");
	}

	@Override
	public Map<TypeaheadRule, Boolean> delete(Collection<TypeaheadRule> models)
			throws CoreServiceException {
		throw new CoreServiceException("Method does not have an implementation.");
	}

	@Override
	public SearchResult<TypeaheadRule> search(Search search)
			throws CoreServiceException {
		try {
			return dao.search(search);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult<TypeaheadRule> search(TypeaheadRule model)
			throws CoreServiceException {
		try {
			return dao.search(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public SearchResult<TypeaheadRule> search(TypeaheadRule model,
			int pageNumber, int maxRowCount) throws CoreServiceException {
		try {
			return dao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public TypeaheadRule searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
