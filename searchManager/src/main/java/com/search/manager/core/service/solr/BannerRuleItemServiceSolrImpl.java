package com.search.manager.core.service.solr;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.response.ServiceResponse;

@Service("bannerRuleItemServiceSolr")
public class BannerRuleItemServiceSolrImpl implements BannerRuleItemService {

	@Autowired
	@Qualifier("bannerRuleItemDaoSolr")
	private BannerRuleItemDao bannerRuleItemDao;

	@Override
	public BannerRuleItem add(BannerRuleItem model) throws CoreServiceException {
		try {
			return bannerRuleItemDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRuleItem update(BannerRuleItem model)
			throws CoreServiceException {
		try {
			return bannerRuleItemDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(BannerRuleItem model) throws CoreServiceException {
		try {
			return bannerRuleItemDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<BannerRuleItem> search(Search search)
			throws CoreServiceException {
		try {
			return bannerRuleItemDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	// BannerRuleItemService specific method here...

	@Override
	public ServiceResponse<BannerRuleItem> addRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<Integer> getTotalRuleItems(String storeId,
			String ruleId) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByFilter(
			String storeId, String ruleId, String filter, String dateFilter,
			String imageSize, int page, int pageSize)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByImageId(
			String storeId, String imageId, int page, int pageSize)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getRuleItemsByRuleId(
			String storeId, String ruleId, int page, int pageSize)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<SearchResult<BannerRuleItem>> getAllRuleItems(
			String storeId, String ruleId) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<BannerRuleItem> getRuleItemByMemberId(
			String storeId, String ruleId, String memberId)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<BannerRuleItem> updateRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<Boolean> deleteRuleItemsByImageSize(String storeId,
			String ruleId, String imageSize) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResponse<Boolean> deleteRuleItemByMemberId(String storeId,
			String ruleId, String memberId, String alias, String imageSize)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
