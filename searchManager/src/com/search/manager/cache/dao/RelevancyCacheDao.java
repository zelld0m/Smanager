package com.search.manager.cache.dao;

import java.util.List;

import jxl.common.Logger;

import org.apache.commons.lang.StringUtils;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;

public class RelevancyCacheDao extends CacheDao<Relevancy> {

	private static final Logger logger = Logger.getLogger(RelevancyCacheDao.class);

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
	protected CacheModel<Relevancy> getDatabaseObject(StoreKeyword storeKeyword) throws DaoException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			RecordSet<RelevancyKeyword> rk = daoService.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
					new RelevancyKeyword(storeKeyword.getKeyword(), new Relevancy("", "")), null, null, 0, 0),
					MatchType.LIKE_NAME, ExactMatch.MATCH);
			if (rk.getTotalSize() > 0) {
				Relevancy relevancy = daoService.getRelevancyDetails(rk.getList().get(0).getRelevancy());
				return new CacheModel<Relevancy>(relevancy);
			}
		} catch (Exception e) {
			logger.error("Failed to get redirect database object for " + storeKeyword, e);
		}
		return null;
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
		} catch (Exception e) {
			logger.error("Problem accessing cache.", e);
			cacheError = true;
		}
		
		if (cache == null) {
			try {
				Relevancy relevancy = daoService.getRelevancyDetails(new Relevancy());
				logger.info("Retrieved rule from database.");
				if (!cacheError) {
					try {
						cacheObject(key, new CacheModel<Relevancy>(relevancy));
					} catch (Exception e) {
						logger.error("Cannot cache object", e);						
					}					
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
							cacheService.put(getCacheKey(new StoreKeyword(relevancy.getStore().getStoreId(), rk.getKeyword().getKeywordId())), rule);
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
