package com.search.manager.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.enums.RuleType;
import com.search.manager.model.Banner;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.SearchCriteria.MatchType;

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
	public Banner getRuleById(String ruleId){
		return null;
	}
	
	@RemoteMethod
	public Banner getRuleByName(String ruleName){
		return null;
	}
	
	@RemoteMethod
	public RecordSet<Banner> getRules(String filter, int page, int itemsPerPage){
		try{
			String storeId = UtilityService.getStoreId();
			Banner banner = new Banner("", filter, new Store(storeId));
			banner.setBannerName(filter);
			
			SearchCriteria<Banner> criteria = new SearchCriteria<Banner>(banner, page, itemsPerPage);
			return daoService.getBannerListWithNameLike(criteria);
		}catch(DaoException e){
			logger.error("Failed during getRules()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public Banner addKeywordsToBanner(String ruleId, String[] keywordList){
		return null;
	}
	
	@RemoteMethod
	public Banner addRule(String bannerName, String linkPath, String imagePath, String imageAlt, String description) {
		int result = -1;
		String ruleId = "";
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		try {
			Banner rule = new Banner(storeId, bannerName, linkPath, imagePath, description, username);
			rule.setCreatedBy(username);
			ruleId = daoService.addBannerAndGetId(rule);

			try {
				daoService.addRuleStatus(new RuleStatus(RuleEntity.BANNER, storeId, ruleId, bannerName, 
						username, username, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
			} catch (DaoException de) {
				logger.error("Failed to create rule status for search banner: " + bannerName);
			}
			
			if (result>0){
				return getRuleById(ruleId);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRule()",e);
			try {
				daoService.deleteFacetSort(new FacetSort(ruleId, storeId));
			} catch (DaoException de) {
				logger.error("Unable to complete process, need to manually delete rule", de);
			}
		}

		return null;
	}
	
	@RemoteMethod
	public int deleteRule(String ruleId) {
		int result = -1;
		return result;
	}
	
	@RemoteMethod
	public int addFeature(String storeId, String keyword, String productId) {
		logger.info(String.format("%s %s %s", storeId, keyword, productId));
		return 0;
	}
	
	@RemoteMethod
	public int removeFeature(String storeId, String keyword, String productId) {
		logger.info(String.format("%s %s %s", storeId, keyword, productId));
		return 0;
	}

	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}