package com.search.manager.cache.dao;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.dao.DaoException;
import com.search.manager.exception.DataException;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.StoreKeyword;

@Repository("redirectCacheDao")
@Scope("singleton")
public class RedirectCacheDao extends CacheDao<RedirectRule> {

	@Override
	protected String getCacheKey(StoreKeyword storeKeyword)
			throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CacheModel<RedirectRule> getDatabaseObject(
			StoreKeyword storeKeyword) throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean reload(RedirectRule bean) throws DataException, DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reload(List<RedirectRule> list) throws DataException,
			DaoException {
		// TODO Auto-generated method stub
		return false;
	}

}
