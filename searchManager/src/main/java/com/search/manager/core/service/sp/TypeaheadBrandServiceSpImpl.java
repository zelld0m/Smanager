package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadBrandDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.service.TypeaheadBrandService;

@Service("typeaheadBrandServiceSp")
public class TypeaheadBrandServiceSpImpl extends GenericServiceSpImpl<TypeaheadBrand> implements TypeaheadBrandService{

	@Autowired
	public TypeaheadBrandServiceSpImpl(TypeaheadBrandDao dao) {
		super(dao);
	}

	@Override
	public TypeaheadBrand searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
