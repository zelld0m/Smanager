package com.search.manager.core.service;

import org.joda.time.DateTime;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadRule;

public interface TypeaheadRuleService extends GenericService<TypeaheadRule>{

	public TypeaheadRule transfer(TypeaheadRule typeaheadRule)
			throws CoreServiceException;

	Boolean addSections(TypeaheadRule typeaheadRule);

	Boolean deleteSections(TypeaheadRule typeaheadRule);

	void initializeTypeaheadSections(TypeaheadRule rule);

	Boolean updatePrioritySection(TypeaheadRule rule, String lastModifiedBy,
			DateTime lastModifiedDate, Boolean disabled);
	
}
