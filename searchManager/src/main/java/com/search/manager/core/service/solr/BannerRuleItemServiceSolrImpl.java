package com.search.manager.core.service.solr;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Filter.FilterOperator;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;

@Service("bannerRuleItemServiceSolr")
public class BannerRuleItemServiceSolrImpl implements BannerRuleItemService {

	@Autowired
	@Qualifier("bannerRuleItemDaoSolr")
	private BannerRuleItemDao bannerRuleItemDao;

	// a setter method so that the Spring container can 'inject'
	public void setBannerRuleItemDao(BannerRuleItemDao bannerRuleItemDao) {
		this.bannerRuleItemDao = bannerRuleItemDao;
	}

	@Override
	public BannerRuleItem add(BannerRuleItem model) throws CoreServiceException {
		try {
			return bannerRuleItemDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public List<BannerRuleItem> add(Collection<BannerRuleItem> models)
			throws CoreServiceException {
		try {
			return (List<BannerRuleItem>) bannerRuleItemDao.add(models);
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
	public List<BannerRuleItem> update(Collection<BannerRuleItem> models)
			throws CoreServiceException {
		try {
			return (List<BannerRuleItem>) bannerRuleItemDao.update(models);
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
	public Map<BannerRuleItem, Boolean> delete(Collection<BannerRuleItem> models)
			throws CoreServiceException {
		try {
			return bannerRuleItemDao.delete(models);
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

	@Override
	public SearchResult<BannerRuleItem> search(BannerRuleItem model)
			throws CoreServiceException {
		try {
			return bannerRuleItemDao.search(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<BannerRuleItem> search(BannerRuleItem model,
			int pageNumber, int maxRowCount) throws CoreServiceException {
		try {
			return bannerRuleItemDao.search(model, pageNumber, maxRowCount);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRuleItem searchById(String storeId, String id)
			throws CoreServiceException {
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter("store", storeId));
		search.addFilter(new Filter("memberId", id));

		SearchResult<BannerRuleItem> searchResult = search(search);

		if (searchResult.getTotalCount() > 0) {
			return searchResult.getResult().get(0);
		}

		return null;
	}

	// BannerRuleItemService specific method here...

	@Override
	public BannerRuleItem transfer(BannerRuleItem bannerRuleItem)
			throws CoreServiceException {
		throw new CoreServiceException("Unsupported Operation.");
	}

	@Override
	public List<BannerRuleItem> getActiveBannerRuleItems(String storeId,
			String keyword, DateTime currentDate) throws CoreServiceException {
		// TODO test date filter...
		Search search = new Search(BannerRuleItem.class);
		search.addFilter(new Filter("store", storeId));
		search.addFilter(new Filter("ruleName1", keyword));
		search.addFilter(new Filter("disabled", false));
		search.addFilter(new Filter("startDate", currentDate,
				FilterOperator.LESS_OR_EQUAL));
		search.addFilter(new Filter("endDate", currentDate,
				FilterOperator.GREATER_THAN));

		SearchResult<BannerRuleItem> searchResult = search(search);
		if (searchResult.getTotalCount() > 0) {
			return searchResult.getResult();
		}
		return null;
	}

	@Override
	public BannerRuleItem addRuleItem(String storeId, Map<String, String> params)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getTotalRuleItems(String storeId, String ruleId)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<BannerRuleItem> getRuleItemsByFilter(String storeId,
			String ruleId, String filter, String dateFilter, String imageSize,
			int page, int pageSize) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<BannerRuleItem> getRuleItemsByImageId(String storeId,
			String imageId, int page, int pageSize) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<BannerRuleItem> getRuleItemsByRuleId(String storeId,
			String ruleId, int page, int pageSize) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<BannerRuleItem> getAllRuleItems(String storeId,
			String ruleId) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BannerRuleItem getRuleItemByMemberId(String storeId, String ruleId,
			String memberId) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BannerRuleItem updateRuleItem(String storeId,
			Map<String, String> params) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteRuleItemsByImageSize(String storeId, String ruleId,
			String imageSize) throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean deleteRuleItemByMemberId(String storeId, String ruleId,
			String memberId, String alias, String imageSize)
			throws CoreServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
