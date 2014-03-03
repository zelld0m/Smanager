package com.search.manager.core.service;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.core.search.SearchResult;

public interface TypeaheadSuggestionService extends GenericService<TypeaheadSuggestion>{

	public SearchResult<TypeaheadSuggestion> searchByRuleId(String ruleId) throws CoreServiceException;
}
