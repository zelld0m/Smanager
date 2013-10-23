package com.search.manager.core.search;

public interface SearchProcessor {

	SearchResult<?> processSearch(Search search) throws Exception;

	String generateStrQuery(Search search) throws Exception;
	
}
