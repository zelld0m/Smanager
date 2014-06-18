package com.search.manager.core.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadRule;

public interface TypeaheadRuleService extends GenericService<TypeaheadRule>{

	public TypeaheadRule transfer(TypeaheadRule typeaheadRule)
			throws CoreServiceException;

	
}