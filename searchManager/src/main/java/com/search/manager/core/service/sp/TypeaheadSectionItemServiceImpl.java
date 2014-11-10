package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.dao.TypeaheadSectionItemDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadSectionItem;
import com.search.manager.core.service.TypeaheadSectionItemService;
import com.search.manager.service.UtilityService;

public class TypeaheadSectionItemServiceImpl extends GenericServiceSpImpl<TypeaheadSectionItem> implements TypeaheadSectionItemService{

	@Autowired
	@Qualifier("utilityService")
	private UtilityService utilityService;
	
	public TypeaheadSectionItemServiceImpl() {
		super();
	}
	
	@Autowired
	public TypeaheadSectionItemServiceImpl(@Qualifier("typeaheadSectionItemDaoSp")TypeaheadSectionItemDao dao) {
		super(dao);
	}
	
	@Override
	public TypeaheadSectionItem searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}


}
