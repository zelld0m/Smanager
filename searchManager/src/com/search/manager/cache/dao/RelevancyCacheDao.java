package com.search.manager.cache.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;

@Repository("relevancyCacheDao")
public class RelevancyCacheDao extends CacheDao<Relevancy> {

	private static final Logger logger = Logger.getLogger(RelevancyCacheDao.class);

	@Override
	protected String getCacheKeyInitials() throws DataException {
		return CacheConstants.RELEVANCY_LIST_CACHE_KEY;
	}

	@Override
	protected String getCacheKey(StoreKeyword storeKeyword) throws DataException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.RELEVANCY_LIST_CACHE_KEY, storeKeyword.getKeywordId());
	}
	
	@Override
	protected String getCacheKey(Store store, String name) throws DataException {
		StoreKeyword storeKeyword = new StoreKeyword(store, new Keyword(name));
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.RELEVANCY_LIST_CACHE_KEY, storeKeyword.getKeywordId());
	}

	@Override
	protected CacheModel<Relevancy> getDatabaseObject(StoreKeyword storeKeyword) throws DaoException {
		DAOValidation.checkStoreKeywordPK(storeKeyword);
		Relevancy relevancy = new Relevancy("", "");
		relevancy.setStore(new Store(storeKeyword.getStoreId()));
		RecordSet<RelevancyKeyword> rk = daoService.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
				new RelevancyKeyword(storeKeyword.getKeyword(), relevancy), DateTime.now(), DateTime.now(), 0, 0),
				MatchType.LIKE_NAME, ExactMatch.MATCH);
		if (rk.getTotalSize() > 0) {
			relevancy = daoService.getRelevancyDetails(rk.getList().get(0).getRelevancy());
			return new CacheModel<Relevancy>(relevancy);
		}
		return new CacheModel<Relevancy>();
	}
	
	@Override
	protected CacheModel<Relevancy> getDatabaseObject(Store store, String name) throws DaoException {
		StoreKeyword storeKeyword = new StoreKeyword(store, new Keyword(name));
		DAOValidation.checkStoreKeywordPK(storeKeyword);
		Relevancy relevancy = new Relevancy("", "");
		relevancy.setStore(new Store(storeKeyword.getStoreId()));
		RecordSet<RelevancyKeyword> rk = daoService.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
				new RelevancyKeyword(storeKeyword.getKeyword(), relevancy), DateTime.now(), DateTime.now(), 0, 0),
				MatchType.LIKE_NAME, ExactMatch.MATCH);
		if (rk.getTotalSize() > 0) {
			relevancy = daoService.getRelevancyDetails(rk.getList().get(0).getRelevancy());
			return new CacheModel<Relevancy>(relevancy);
		}
		return new CacheModel<Relevancy>();
	}
	
	public CacheModel<Relevancy> getDatabaseObject(String ruleId) throws DaoException {
		try {
			if (StringUtils.isNotBlank(ruleId)) {
				Relevancy relevancy = new Relevancy();
				relevancy.setRelevancyId(ruleId);
				return new CacheModel<Relevancy>(daoService.getRelevancyDetails(relevancy));
			}
		} catch (Exception e) {
			logger.error("Failed to load cache for redirect rule id " + ruleId, e);
		}
		return null;
	}
	
	public CacheModel<Relevancy> getDefaultRelevancy(Store store) {
		// exemption to the keyword rule. default relevancies are not mapped to keywords.
		CacheModel<Relevancy> cache = null;
		boolean cacheError = false;
		String key = "";
		try {
			key = CacheConstants.getCacheKey(store.getStoreId(), CacheConstants.RELEVANCY_DEFAULT_CACHE_KEY, "");
			cache = getCachedObject(key);
			if (cache != null && isNeedReloadCache(store, cache)) { // obsolete data, force a reload
				Date date = getforceUpdateCacheDate(store);
				if (date != null && cache.getUploadedDate().before(date)) { // obsolete data, force a reload
					cache = null;
					reset(key);
				}
			}
		} catch (Exception e) {
			logger.error("Problem accessing cache.", e);
			cacheError = true;
		}
		
		if (cache == null) {
			try {
				Relevancy relevancy = new Relevancy();
				relevancy.setRelevancyId(store.getStoreId() + "_default");
				relevancy = daoService.getRelevancyDetails(relevancy);
				if (relevancy != null) {
					cache = new CacheModel<Relevancy>(relevancy);
					logger.info("Retrieved rule from database.");
					if (!cacheError) {
						try {
							cacheObject(key, cache);
						} catch (Exception e) {
							logger.error("Cannot cache object", e);						
						}					
					}					
				}
				else {
					logger.warn("Cannot find default relevancy for " + store);					
				}
			} catch (Exception e) {
				logger.error("Cannot retrieve rule from database.", e);
			}
		}
		return cache;
	}

	@Override
	public boolean reload(Relevancy relevancy) throws DataException, DaoException {
		try {
			CacheModel<Relevancy> rule = getDatabaseObject(relevancy.getRelevancyId());
			if (rule != null) {
				if (rule.getObj() != null) {
					relevancy = rule.getObj();
					RecordSet<RelevancyKeyword> relevancyKeywords = daoService.getRelevancyKeywords(relevancy);
					if (relevancyKeywords.getTotalSize() > 0) {
						for (RelevancyKeyword rk: relevancyKeywords.getList()) {
							reload(new StoreKeyword(relevancy.getStore().getStoreId(), rk.getKeyword().getKeywordId()));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error during loading of redirect rule to cache", e);
			throw new DataException(e);
		}
		return false;
	}

	@Override
	public boolean reload(List<Relevancy> list) throws DataException, DaoException {
		boolean result = true;
		for (Relevancy rule: list) {
			try {
				result &= reload(rule);
			} catch (Exception e) {
				result = false;
				continue;
			}
		}
		return result;
	}

}
