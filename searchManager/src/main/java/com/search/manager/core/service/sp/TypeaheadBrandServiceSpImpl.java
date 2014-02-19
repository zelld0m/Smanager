package com.search.manager.core.service.sp;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadBrandDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.TypeaheadBrandService;

@Service("typeaheadBrandServiceSp")
public class TypeaheadBrandServiceSpImpl implements TypeaheadBrandService{

	@Autowired
	private TypeaheadBrandDao dao;
	
	@Override
	public TypeaheadBrand add(TypeaheadBrand model) throws CoreServiceException {
		
		try {
			return dao.add(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<TypeaheadBrand> add(Collection<TypeaheadBrand> models)
			throws CoreServiceException {
		try {
			return dao.add(models);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TypeaheadBrand update(TypeaheadBrand model)
			throws CoreServiceException {
		try {
			return dao.update(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<TypeaheadBrand> update(Collection<TypeaheadBrand> models)
			throws CoreServiceException {
		try {
			return dao.update(models);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean delete(TypeaheadBrand model) throws CoreServiceException {
		throw new CoreServiceException("Method does not have an implementation.");
	}

	@Override
	public Map<TypeaheadBrand, Boolean> delete(Collection<TypeaheadBrand> models)
			throws CoreServiceException {
		throw new CoreServiceException("Method does not have an implementation.");
	}

	@Override
	public SearchResult<TypeaheadBrand> search(Search search)
			throws CoreServiceException {
		try {
			return dao.search(search);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult<TypeaheadBrand> search(TypeaheadBrand model)
			throws CoreServiceException {
		try {
			return dao.search(model);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SearchResult<TypeaheadBrand> search(TypeaheadBrand model,
			int pageNumber, int maxRowCount) throws CoreServiceException {
		try {
			return dao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TypeaheadBrand searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
