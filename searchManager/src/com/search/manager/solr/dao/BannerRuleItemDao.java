package com.search.manager.solr.dao;

import java.util.Collection;

import com.search.manager.dao.DaoException;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.Store;

public interface BannerRuleItemDao {

	Collection<BannerRuleItem> getBannerRuleItems(Store store)
			throws DaoException;

	BannerRuleItem getBannerRuleItemMemberId(Store store, String memberId)
			throws DaoException;

	BannerRuleItem getBannerRuleItemRuleName(Store store, String ruleName)
			throws DaoException;

	boolean loadBannerRuleItems(Store store) throws DaoException;

	boolean loadBannerRuleItem(Store store, String memberId)
			throws DaoException;

	boolean resetBannerRuleItems(Store store) throws DaoException;

	boolean resetBannerRuleItem(Store store, String memberId)
			throws DaoException;

	boolean deleteBannerRuleItems(Store store) throws DaoException;

	boolean deleteBannerRuleItem(Store store, String memberId)
			throws DaoException;

	boolean updateBannerRuleItem(BannerRuleItem bannerRuleItem)
			throws DaoException;

	boolean commitBannerRuleItem() throws DaoException;

}