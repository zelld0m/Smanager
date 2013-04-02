package com.search.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.Banner;
import com.search.manager.model.Campaign;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;

@Service(value = "campaignService")
@RemoteProxy(
		name = "CampaignServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "campaignService")
	)
public class CampaignService {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CampaignService.class);
	
	@Autowired private DaoService daoService;
		
	@RemoteMethod
	public RecordSet<Campaign> getRules(String filter, int page, int itemsPerPage){
		try{
			String storeId = UtilityService.getStoreId();
			Campaign rule = new Campaign("", filter, new Store(storeId));
			rule.setRuleName(filter);
			
			SearchCriteria<Campaign> criteria = new SearchCriteria<Campaign>(rule, page, itemsPerPage);
			return daoService.getCampaignsContainingName(criteria);
		}catch(DaoException e){
			logger.error("Failed during getRules()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public Campaign getRuleById(String ruleId){
		try {
			String store = UtilityService.getStoreId();
			Campaign rule = new Campaign(ruleId, new Store(store));
			return daoService.getCampaign(rule);
			
		} catch (DaoException e) {
			logger.error("Failed during getRuleById()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public Campaign getRuleByName(String ruleName){
		try {
			String store = UtilityService.getStoreId();
			Campaign rule = new Campaign(new Store(store), ruleName);
			SearchCriteria<Campaign> criteria = new SearchCriteria<Campaign>(rule, 1, 1);
			RecordSet<Campaign> campaignList = daoService.getCampaignsWithName(criteria);
			
			if(campaignList != null && campaignList.getTotalSize() > 0){
				return campaignList.getList().get(0);
			}
			
		} catch (DaoException e) {
			logger.error("Failed during getRuleByName()",e);
		}
		return null;
	}
	
	@RemoteMethod
	public Campaign addRule(String ruleName, String startDate, String endDate, String description) {
		String ruleId = "";
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		
		try {
			DateTime startDateTime = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, startDate, JodaPatternType.DATE);
			DateTime endDateTime = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, endDate, JodaPatternType.DATE);
			
			Campaign rule = new Campaign(new Store(storeId), ruleName, startDateTime, endDateTime, description);
			rule.setCreatedBy(username);
			ruleId = daoService.addCampaignAndGetId(rule);

			try {
				if(StringUtils.isNotEmpty(ruleId)){
					daoService.addRuleStatus(new RuleStatus(RuleEntity.CAMPAIGN, storeId, ruleId, ruleName, 
						username, username, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
				}
			} catch (DaoException de) {
				logger.error("Failed to create rule status for campaign: " + ruleName);
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
		Campaign rule = new Campaign(ruleId, ruleName, new Store(UtilityService.getStoreId()));
		
		SearchCriteria<Campaign> criteria = new SearchCriteria<Campaign>(rule, null, null, 0, 0);
		RecordSet<Campaign> set = daoService.getCampaignsWithName(criteria);
		if (set.getTotalSize() > 0) {
			for (Campaign r: set.getList()) {
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
	public int updateRule(String ruleId, String ruleName, String startDate, String endDate, String description) {
		int result = -1;
		String storeId = UtilityService.getStoreId();
		try {
			DateTime startDateTime = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, startDate, JodaPatternType.DATE);
			DateTime endDateTime = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, endDate, JodaPatternType.DATE);
			
			Campaign rule = new Campaign(new Store(storeId), ruleId, ruleName, startDateTime, endDateTime, description);
			rule.setLastModifiedBy(UtilityService.getUsername());
			result = daoService.updateCampaign(rule);
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
			Campaign rule = new Campaign(ruleId, new Store(store));
			rule.setLastModifiedBy(username);
			result = daoService.deleteCampaign(rule);
			if (result > 0) {
				RuleStatus ruleStatus = new RuleStatus();
				ruleStatus.setRuleTypeId(RuleEntity.CAMPAIGN.getCode());
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
	
	public DaoService getDaoService() {
		return daoService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}