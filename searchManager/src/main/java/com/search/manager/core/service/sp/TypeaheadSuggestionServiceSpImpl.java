package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.search.manager.core.dao.TypeaheadSuggestionDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.core.service.TypeaheadSuggestionService;

@Service("typeaheadSuggestionServiceSp")
public class TypeaheadSuggestionServiceSpImpl extends GenericServiceSpImpl<TypeaheadSuggestion> implements TypeaheadSuggestionService{

	@Autowired
	public TypeaheadSuggestionServiceSpImpl(TypeaheadSuggestionDao dao) {
		super(dao);
	}

	@Override
	public TypeaheadSuggestion searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
