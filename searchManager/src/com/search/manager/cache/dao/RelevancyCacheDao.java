package com.search.manager.cache.dao;

import java.util.List;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.dao.DaoException;
import com.search.manager.exception.DataException;
import com.search.manager.model.Relevancy;
import com.search.manager.model.StoreKeyword;

public class RelevancyCacheDao extends CacheDao<Relevancy> {

	@Override
	protected String getCacheKey(StoreKeyword storeKeyword)
			throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CacheModel<Relevancy> getDatabaseObject(StoreKeyword storeKeyword)
			throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean reload(Relevancy bean) throws DataException, DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reload(List<Relevancy> list) throws DataException,
			DaoException {
		// TODO Auto-generated method stub
		return false;
	}

}
