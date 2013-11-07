package com.search.manager.core.service.solr;

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

}
