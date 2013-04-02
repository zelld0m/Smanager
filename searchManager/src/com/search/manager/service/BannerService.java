package com.search.manager.service;

import java.util.ArrayList;
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
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;

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
	public Banner addKeywordsToBanner(String ruleId, String[] keywordList){
		return null;
	}
	
	@RemoteMethod
	public List<Campaign> getAllCampaignUsingThisBanner(String bannerId, String campaignNameFilter, int page, int pageSize){
		/*try {
			RecordSet<Campaign> campaignList = daoService.getCampaignsUsingBanner(bannerId);
			
			if(campaignList != null && campaignList.getTotalSize() > 0){
				return campaignList.getList();
			}
			
		} catch (DaoException e) {
			logger.error("Failed during getRuleById()",e);
		}*/
		return null;
	}
	
	@RemoteMethod
	public Banner getRuleById(String ruleId){
		try {
			String store = UtilityService.getStoreId();
			Banner rule = new Banner(ruleId, new Store(store));
			return daoService.getBanner(rule);
			
		} catch (DaoException e) {
			logger.error("Failed during getRuleById()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public Banner getRuleByName(String ruleName){
		try {
			String store = UtilityService.getStoreId();
			Banner rule = new Banner(new Store(store), ruleName);
			SearchCriteria<Banner> criteria = new SearchCriteria<Banner>(rule, 1, 1);
			RecordSet<Banner> bannerList = daoService.getBannerListWithNameMatching(criteria);
			
			if(bannerList != null && bannerList.getTotalSize() > 0){
				return bannerList.getList().get(0);
			}
			
			
			
		} catch (DaoException e) {
			logger.error("Failed during getRuleByName()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public RecordSet<Banner> getRules(String filter, int page, int itemsPerPage){
		try{
			String storeId = UtilityService.getStoreId();
			Banner banner = new Banner("", filter, new Store(storeId));
			banner.setRuleName(filter);
			
			SearchCriteria<Banner> criteria = new SearchCriteria<Banner>(banner, page, itemsPerPage);
			return daoService.getBannerListWithNameLike(criteria);
		}catch(DaoException e){
			logger.error("Failed during getRules()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public List<String> checkForRuleNameDuplicates(String[] ruleIds, String[] ruleNames) throws DaoException {
		List<String> duplicateRuleNames = new ArrayList<String>();
		for (int i = 0; i < ruleIds.length; i++) {
			String ruleName = ruleNames[i];
			if (checkForRuleNameDuplicate(ruleIds[i], ruleName)) {
				duplicateRuleNames.add(ruleName);				
			}
		}
		return duplicateRuleNames;
	}

	@RemoteMethod
	public boolean checkForRuleNameDuplicate(String ruleId, String ruleName) throws DaoException {
		Banner rule = new Banner(ruleId, ruleName, new Store(UtilityService.getStoreId()));
		
		SearchCriteria<Banner> criteria = new SearchCriteria<Banner>(rule, null, null, 0, 0);
		RecordSet<Banner> set = daoService.getBannerListWithNameMatching(criteria);
		if (set.getTotalSize() > 0) {
			for (Banner r: set.getList()) {
				if (StringUtils.equals(StringUtils.trim(ruleName), StringUtils.trim(r.getRuleName()))) {
					if (StringUtils.isBlank(ruleId) || !StringUtils.equals(ruleId, r.getRuleId())){
						return true;						
					}
				}
			}
		}
		return false;
	}
	
	@RemoteMethod
	public Banner addRule(String bannerName, String linkPath, String imagePath, String imageAlt, String description) {
		String ruleId = "";
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		try {
			Banner rule = new Banner(storeId, bannerName, linkPath, imagePath, description, username);
			rule.setCreatedBy(username);
			ruleId = daoService.addBannerAndGetId(rule);

			try {
				if(StringUtils.isNotEmpty(ruleId)){
					daoService.addRuleStatus(new RuleStatus(RuleEntity.BANNER, storeId, ruleId, bannerName, 
						username, username, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
				}
			} catch (DaoException de) {
				logger.error("Failed to create rule status for search banner: " + bannerName);
			}
			
			if (StringUtils.isNotBlank(ruleId)){
				return getRuleById(ruleId);
			}
		} catch (DaoException e) {
			logger.error("Failed during addRule()",e);
		} catch (Exception e) {
			logger.error("Failed during addRule()",e);
		}

		return null;
	}
	
	@RemoteMethod
	public int updateRule(String ruleId, String ruleName, String description) {
		int result = -1;
		String storeId = UtilityService.getStoreId();
		try {
			Banner rule = new Banner(new Store(storeId), ruleId, ruleName, description);
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateBanner(rule);
		} catch (DaoException e) {
			logger.error("Failed during updateRule()",e);
		}
		return result;
	}
	
	@RemoteMethod
	public int deleteRule(String ruleId) {
		int result = -1;
		
		try {
			String store = UtilityService.getStoreId();
			String username = UtilityService.getUsername();
			Banner rule = new Banner(ruleId, new Store(store));
			rule.setLastModifiedBy(username);
			result = daoService.deleteBanner(rule);
			if (result > 0) {
				RuleStatus ruleStatus = new RuleStatus();
				ruleStatus.setRuleTypeId(RuleEntity.BANNER.getCode());
				ruleStatus.setRuleRefId(ruleId);
				ruleStatus.setStoreId(store);
				daoService.updateRuleStatusDeletedInfo(ruleStatus, username);
			}
		} catch (DaoException e) {
			logger.error("Failed during deleteRule()",e);
		} catch (Exception e) {
			logger.error("Failed during deleteRule()",e);
		}
		
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