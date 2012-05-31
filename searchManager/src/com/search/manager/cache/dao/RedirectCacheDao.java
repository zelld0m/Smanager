package com.search.manager.cache.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.search.manager.cache.model.CacheModel;
import com.search.manager.cache.utility.CacheConstants;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.exception.DataException;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.UtilityService;

@Repository("redirectCacheDao")
public class RedirectCacheDao extends CacheDao<RedirectRule> {

	private static final Logger logger = Logger.getLogger(RedirectCacheDao.class);

	@Override
	protected String getCacheKeyInitials() throws DataException {
		return CacheConstants.RULE_REDIRECT_CACHE_KEY;
	}

	@Override
	protected String getCacheKey(StoreKeyword storeKeyword) throws DataException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
		} catch (Exception e) {
			throw new DataException(e);
		}
		return CacheConstants.getCacheKey(storeKeyword.getStoreId(), CacheConstants.RULE_REDIRECT_CACHE_KEY, storeKeyword.getKeywordId());
	}

	@Override
	protected CacheModel<RedirectRule> getDatabaseObject(StoreKeyword storeKeyword) throws DaoException {
		try {
			DAOValidation.checkStoreKeywordPK(storeKeyword);
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setStoreId(storeKeyword.getStoreId());
			redirectRule.setSearchTerm(storeKeyword.getKeywordId());
			RecordSet<RedirectRule> rules = daoService.getRedirectRules(new SearchCriteria<RedirectRule>(redirectRule, null, null, 0, 0));
			if (rules.getTotalSize() > 0) {
				redirectRule = rules.getList().get(0);
				if (rules.getTotalSize() > 1) {
					logger.warn("Multiple keyword mappings detected for storeKeyword: " + storeKeyword);
				}
				return new CacheModel<RedirectRule>(redirectRule);
			}
		} catch (Exception e) {
			logger.error("Failed to get redirect database object for " + storeKeyword, e);
		}
		return new CacheModel<RedirectRule>();
	}

	public CacheModel<RedirectRule> getDatabaseObject(String ruleId) throws DaoException {
		if (StringUtils.isNotBlank(ruleId)) {
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setRuleId(ruleId);
			RecordSet<RedirectRule> rules = daoService.getRedirectRules(new SearchCriteria<RedirectRule>(redirectRule, null, null, 0, 0));
			if (rules.getTotalSize() > 0) {
				redirectRule = rules.getList().get(0);
				return new CacheModel<RedirectRule>(redirectRule);
			}
		}
		return new CacheModel<RedirectRule>();
	}
	
	@Override
	public boolean reload(RedirectRule redirect) throws DataException, DaoException {
		try {
			CacheModel<RedirectRule> rule = getDatabaseObject(redirect.getRuleId());
			if (rule != null) {
				if (rule.getObj() != null) {
					redirect = rule.getObj();
					for (String keyword: redirect.getSearchTerms()) {
						reload(new StoreKeyword(redirect.getStoreId(), keyword));
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
	public boolean reload(List<RedirectRule> list) throws DataException, DaoException {
		boolean result = true;
		for (RedirectRule rule: list) {
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
