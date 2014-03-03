package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadCategoryDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadCategory;
import com.search.manager.core.service.TypeaheadCategoryService;

@Service("typeaheadCategoryServiceSp")
public class TypeaheadCategoryServiceSpImpl extends GenericServiceSpImpl<TypeaheadCategory> implements TypeaheadCategoryService{

	@Autowired
	public TypeaheadCategoryServiceSpImpl(TypeaheadCategoryDao dao) {
		super(dao);
		
	}

	@Override
	public TypeaheadCategory searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
