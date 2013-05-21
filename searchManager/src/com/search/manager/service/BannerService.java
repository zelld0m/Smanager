package com.search.manager.service;

import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.BannerRule;
import com.search.manager.model.RecordSet;
import com.search.manager.model.SearchCriteria;
import com.search.manager.response.ServiceResponse;

@Service(value = "bannerService")
@RemoteProxy(
		name = "BannerServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "bannerService")
)

public class BannerService {
	private static final Logger logger = Logger.getLogger(BannerService.class);

	@Autowired private DaoService daoService;

	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRule>> getAllRules(String searchText, int page, int pageSize){
		String storeId = UtilityService.getStoreId();
		BannerRule model = new BannerRule(storeId, null, searchText);
		SearchCriteria<BannerRule> criteria = new SearchCriteria<BannerRule>(model, page, pageSize);

		ServiceResponse<RecordSet<BannerRule>> serviceResponse = new ServiceResponse<RecordSet<BannerRule>>();
		RecordSet<BannerRule> recordSet = new RecordSet<BannerRule>(null, 0);

		try {
			recordSet = daoService.searchBannerRule(criteria);
			serviceResponse.success(recordSet);
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(e.getMessage(), e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	public ServiceResponse<Boolean> addRule(String ruleName){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		BannerRule rule = new BannerRule(storeId, ruleName, username);
		int status = 0;
		ServiceResponse<Boolean> serviceResponse = new ServiceResponse<Boolean>();
		
		try {
			status = daoService.addBannerRule(rule);
			serviceResponse.success(status > 0, status);
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(e.getMessage(), e);
		}

		return serviceResponse;
	}
}