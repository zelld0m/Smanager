package com.search.manager.solr.dao;

import java.util.Collection;

import org.joda.time.DateTime;

import com.search.manager.dao.DaoException;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.Store;

public interface BannerRuleItemDao {

	Collection<BannerRuleItem> getBannerRuleItems(Store store)
			throws DaoException;

	Collection<BannerRuleItem> getBannerRuleItemsByRuleId(Store store,
			String ruleId) throws DaoException;

	Collection<BannerRuleItem> getBannerRuleItemsByRuleName(Store store,
			String ruleName) throws DaoException;

	Collection<BannerRuleItem> getActiveBannerRuleItemsByRuleName(Store store,
			String ruleName, DateTime startDate, DateTime endDate)
			throws DaoException;

	BannerRuleItem getBannerRuleItemByMemberId(Store store, String memberId)
			throws DaoException;

	boolean loadBannerRuleItems(Store store) throws DaoException;

	boolean loadBannerRuleItemsByRuleId(Store store, String ruleId)
			throws DaoException;

	boolean loadBannerRuleItemsByRuleName(Store store, String ruleName)
			throws DaoException;

	boolean loadBannerRuleItemByMemberId(Store store, String memberId)
			throws DaoException;

	boolean resetBannerRuleItems(Store store) throws DaoException;

	boolean resetBannerRuleItemsByRuleId(Store store, String ruleId)
			throws DaoException;

	boolean resetBannerRuleItemsByRuleName(Store store, String ruleName)
			throws DaoException;

	boolean resetBannerRuleItemByMemberId(Store store, String memberId)
			throws DaoException;

	boolean deleteBannerRuleItems(Store store) throws DaoException;

	boolean deleteBannerRuleItemsByRuleId(Store store, String ruleId)
			throws DaoException;

	boolean deleteBannerRuleItemsByRuleName(Store store, String ruleName)
			throws DaoException;

	boolean deleteBannerRuleItemByMemberId(Store store, String memberId)
			throws DaoException;

	boolean updateBannerRuleItem(BannerRuleItem bannerRuleItem)
			throws DaoException;

	boolean commitBannerRuleItem() throws DaoException;

}