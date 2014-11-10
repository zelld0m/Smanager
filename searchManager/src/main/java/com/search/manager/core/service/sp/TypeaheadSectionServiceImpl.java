package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.dao.TypeaheadSectionDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadSection;
import com.search.manager.core.service.TypeaheadSectionService;
import com.search.manager.service.UtilityService;

public class TypeaheadSectionServiceImpl extends GenericServiceSpImpl<TypeaheadSection> implements TypeaheadSectionService{

	@Autowired
	@Qualifier("utilityService")
	private UtilityService utilityService;
	
	public TypeaheadSectionServiceImpl() {
		super();
	}
	
	@Autowired
	public TypeaheadSectionServiceImpl(@Qualifier("typeaheadSectionDaoSp") TypeaheadSectionDao dao) {
		super(dao);
	}
	
	
	@Override
	public TypeaheadSection searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}


}
