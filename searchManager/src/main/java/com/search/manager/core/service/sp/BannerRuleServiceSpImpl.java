package com.search.manager.core.service.sp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.BannerRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleService;

@Service("bannerRuleServiceSp")
public class BannerRuleServiceSpImpl implements BannerRuleService {

	@Autowired
	@Qualifier("bannerRuleDaoSp")
	private BannerRuleDao bannerRuleDao;

	@Override
	public BannerRule add(BannerRule model) throws CoreServiceException {
		try {
			return bannerRuleDao.add(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public BannerRule update(BannerRule model) throws CoreServiceException {
		try {
			return bannerRuleDao.update(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public boolean delete(BannerRule model) throws CoreServiceException {
		try {
			return bannerRuleDao.delete(model);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

	@Override
	public SearchResult<BannerRule> search(Search search)
			throws CoreServiceException {
		try {
			return bannerRuleDao.search(search);
		} catch (CoreDaoException e) {
			throw new CoreServiceException(e);
		}
	}

}
