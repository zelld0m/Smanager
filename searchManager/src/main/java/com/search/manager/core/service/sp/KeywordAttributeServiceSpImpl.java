package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.KeywordAttributeDao;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.KeywordAttribute;
import com.search.manager.core.service.KeywordAttributeService;

@Service("keywordAttributeServiceSp")
public class KeywordAttributeServiceSpImpl extends GenericServiceSpImpl<KeywordAttribute> implements KeywordAttributeService{

	@Autowired
	@Qualifier("keywordAttributeDaoSp")
	private KeywordAttributeDao dao;
	
	public KeywordAttributeServiceSpImpl() {
		super();
	}
	
	@Autowired
	public KeywordAttributeServiceSpImpl(@Qualifier("keywordAttributeDaoSp") KeywordAttributeDao dao) {
		super(dao);
	}
	
	@Override
	public KeywordAttribute searchById(String storeId, String id)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
