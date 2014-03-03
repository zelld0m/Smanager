package com.search.manager.core.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.search.SearchResult;

public interface TypeaheadBrandService extends GenericService<TypeaheadBrand>{

	public SearchResult<TypeaheadBrand> searchByRuleId(String ruleId) throws CoreServiceException;
	
}
