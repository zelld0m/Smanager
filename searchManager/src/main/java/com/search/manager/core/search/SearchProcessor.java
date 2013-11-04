package com.search.manager.core.search;

import com.search.manager.core.exception.CoreSearchException;

public interface SearchProcessor {

	SearchResult<?> processSearch(Search search) throws CoreSearchException;

	String generateStrQuery(Search search) throws CoreSearchException;

}
