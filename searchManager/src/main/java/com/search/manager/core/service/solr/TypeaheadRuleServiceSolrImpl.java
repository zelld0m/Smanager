package com.search.manager.core.service.solr;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.KeywordAttribute;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.TypeaheadRuleService;

@Service("typeaheadRuleServiceSolr")
public class TypeaheadRuleServiceSolrImpl implements TypeaheadRuleService {

	@Autowired
	@Qualifier("typeaheadRuleDaoSolr")
	private TypeaheadRuleDao typeaheadRuleDao;

	// a setter method so that the Spring container can 'inject'
	public void setTypeaheadRuleDao(TypeaheadRuleDao typeaheadRuleDao) {
		this.typeaheadRuleDao = typeaheadRuleDao;
	}

	@Override
	public TypeaheadRule add(TypeaheadRule model) throws CoreServiceException {
		try {
			return typeaheadRuleDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<TypeaheadRule> add(Collection<TypeaheadRule> models) throws CoreServiceException {
		try {
			return (List<TypeaheadRule>) typeaheadRuleDao.add(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public TypeaheadRule update(TypeaheadRule model) throws CoreServiceException {
		try {
			return typeaheadRuleDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<TypeaheadRule> update(Collection<TypeaheadRule> models) throws CoreServiceException {
		try {
			return (List<TypeaheadRule>) typeaheadRuleDao.update(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(TypeaheadRule model) throws CoreServiceException {
		try {
			return typeaheadRuleDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Map<TypeaheadRule, Boolean> delete(Collection<TypeaheadRule> models) throws CoreServiceException {
		try {
			return typeaheadRuleDao.delete(models);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<TypeaheadRule> search(Search search) throws CoreServiceException {
		try {
			return typeaheadRuleDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<TypeaheadRule> search(TypeaheadRule model) throws CoreServiceException {
		try {
			return typeaheadRuleDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<TypeaheadRule> search(TypeaheadRule model, int pageNumber, int maxRowCount)
			throws CoreServiceException {
		try {
			return typeaheadRuleDao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public TypeaheadRule searchById(String storeId, String id) throws CoreServiceException {
		try {
			TypeaheadRule rule = new TypeaheadRule();

			rule.setStoreId(storeId);
			rule.setRuleId(id);

			SearchResult<TypeaheadRule> result = typeaheadRuleDao.search(rule);

			if (CollectionUtils.isNotEmpty(result.getResult())) {
				return result.getResult().get(0);
			} else {
				return null;
			}
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public TypeaheadRule transfer(TypeaheadRule typeaheadRule)
			throws CoreServiceException {
		try {
			return typeaheadRuleDao.add(typeaheadRule);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public Boolean addSections(TypeaheadRule typeaheadRule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteSections(TypeaheadRule typeaheadRule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeTypeaheadSections(TypeaheadRule rule) {
		// TODO Auto-generated method stub
		
	}
}
