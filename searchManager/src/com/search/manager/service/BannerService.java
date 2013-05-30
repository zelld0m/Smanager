package com.search.manager.service;

import java.util.ArrayList;

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
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.ImagePath;
import com.search.manager.model.ImagePathType;
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

	private static final String MSG_FAILED_ADD_RULE = "Failed to add banner rule %s";
	private static final String MSG_FAILED_ADD_RULE_ITEM = "Failed to add banner rule item %s";
	private static final String MSG_FAILED_DELETE_RULE_ITEM = "Failed to delete banner item %s";
	private static final String MSG_FAILED_GET_IMAGE = "Failed to retrieve record for %s";
	private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
	private static final String MSG_FAILED_UPDATE_IMAGE_ALIAS = "Failed to update image alias to %s";
	private static final String MSG_FAILED_GET_RULE_WITH_IMAGE = "Failed to retrieve banner rule using %s";
	
	@Autowired private DaoService daoService;

	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRule>> getAllRules(String searchText, int page, int pageSize){
		String storeId = UtilityService.getStoreId();
		BannerRule model = new BannerRule(storeId, searchText);
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
	public ServiceResponse<Void> addRule(String ruleName){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		BannerRule rule = new BannerRule(storeId, ruleName, username);
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();

		try {
			if (daoService.addBannerRule(rule) > 0){
				serviceResponse.success(null);
			}else{
				serviceResponse.error(String.format(MSG_FAILED_ADD_RULE, ruleName));
			}
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_ADD_RULE, ruleName), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_ADD_RULE, ruleName), e);
		}

		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<Void> addRuleItem(String ruleId, int priority, String startDate, String endDate, String imageAlt, String linkPath, String description, String imagePathId, String imagePath, String imageAlias){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();
		BannerRule rule = new BannerRule(storeId, ruleId, null, null);
		DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(startDate, JodaPatternType.DATE);
		DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(endDate, JodaPatternType.DATE);
		
		ImagePath newImagePath = new ImagePath(storeId, imagePathId, imagePath, null, imageAlias); 
		
		if(StringUtils.isBlank(imagePathId)){
			ServiceResponse<Void> srAddImagePath = addImagePathLink(imagePath, imageAlias);
			if (srAddImagePath.getStatus() == ServiceResponse.SUCCESS){
				ServiceResponse<ImagePath> srGetImagePath =  getImagePath(imagePath);
				newImagePath = srGetImagePath.getData();
			}
		}
		
		BannerRuleItem ruleItem = new BannerRuleItem(rule, null, priority, startDT, endDT, imageAlt, linkPath, description, newImagePath, false);
		ruleItem.setCreatedBy(username);

		try {
			if (daoService.addBannerRuleItem(ruleItem) > 0){
				serviceResponse.success(null);
			}else{
				serviceResponse.error(String.format(MSG_FAILED_ADD_RULE_ITEM));
			}
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_ADD_RULE_ITEM), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_ADD_RULE_ITEM), e);
		}

		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<Integer> getTotalRuleItems(String ruleId){
		ServiceResponse<RecordSet<BannerRuleItem>> items = getRuleItems(ruleId, 1, 1);
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();
		serviceResponse.success((Integer) items.getData().getTotalSize());
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<Integer> getTotalRuleWithImage(String imagePathId, String imageAlias){
		ServiceResponse<RecordSet<BannerRule>> srAllRule = getAllRuleWithImage(imagePathId, imageAlias, 0, 0);
		RecordSet<BannerRule> rsAllRule = srAllRule.getData();
		
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();
		serviceResponse.success(rsAllRule.getTotalSize());
		
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRule>> getAllRuleWithImage(String imagePathId, String imageAlias, int page, int pageSize){
		String storeId = UtilityService.getStoreId();
		BannerRule bannerRule = new BannerRule(storeId, null);
		SearchCriteria<BannerRule> criteria = new SearchCriteria<BannerRule>(bannerRule, page, pageSize);
		ServiceResponse<RecordSet<BannerRule>> serviceResponse = new ServiceResponse<RecordSet<BannerRule>>();
		RecordSet<BannerRule> rules = new RecordSet<BannerRule>(new ArrayList<BannerRule>(), 0);
		
		try {
			rules = daoService.getBannerRuleWithImage(criteria, imagePathId);
			serviceResponse.success(rules);
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_GET_RULE_WITH_IMAGE, imageAlias), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_GET_RULE_WITH_IMAGE, imageAlias), e);
		}
		
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRuleItem>> getRuleItems(String ruleId, int page, int pageSize){
		return getItems(ruleId, null, null, null, page, pageSize);
	}

    @RemoteMethod
    public ServiceResponse<RecordSet<BannerRuleItem>> getAllRuleItems(String ruleId){
        return getItems(ruleId, null, null, 1, Integer.MAX_VALUE);
    }
	
	public ServiceResponse<RecordSet<BannerRuleItem>> getItems(String ruleId, String startDate, String endDate, int page, int pageSize){
		return getItems(ruleId, startDate, endDate, null, page, pageSize);
	}
	
	public ServiceResponse<RecordSet<BannerRuleItem>> getItems(String ruleId, String startDate, String endDate, String imagePathId, int page, int pageSize){
		String storeId = UtilityService.getStoreId();
		DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(startDate, JodaPatternType.DATE);
		DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(endDate, JodaPatternType.DATE);
		BannerRuleItem model = new BannerRuleItem(ruleId, storeId);
		SearchCriteria<BannerRuleItem> criteria = new SearchCriteria<BannerRuleItem>(model, startDT, endDT, page, pageSize);

		ServiceResponse<RecordSet<BannerRuleItem>> serviceResponse = new ServiceResponse<RecordSet<BannerRuleItem>>();
		RecordSet<BannerRuleItem> recordSet = new RecordSet<BannerRuleItem>(null, 0);

		try {
			recordSet = daoService.searchBannerRuleItem(criteria);
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
	public ServiceResponse<Void> addImagePathLink(String imageUrl, String alias){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();

		ImagePath imagePath = new ImagePath(storeId, null, imageUrl, ImagePathType.IMAGE_LINK, alias, username);

		try {
			if (daoService.addBannerImagePath(imagePath) > 0){
				serviceResponse.success(null);
			}else{
				serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE, imageUrl, alias));
			}
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE, imageUrl, alias), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE, imageUrl, alias), e);
		}

		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<ImagePath> getImagePath(String imageUrl){
		String storeId = UtilityService.getStoreId();
		ImagePath imagePath = new ImagePath(storeId, imageUrl);
		ServiceResponse<ImagePath> serviceResponse = new ServiceResponse<ImagePath>();
		
		try {
			serviceResponse.success(daoService.getBannerImagePath(imagePath));
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_GET_IMAGE, imageUrl), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_GET_IMAGE, imageUrl), e);
		}
		
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<Void> updateImagePathAlias(String imagePathId, String alias){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();

		ImagePath imagePath = new ImagePath(storeId, null, null, null, alias, null, username);

		try {
			if (daoService.updateBannerImagePathAlias(imagePath) > 0){
				serviceResponse.success(null);
			}else{
				serviceResponse.error(String.format(MSG_FAILED_UPDATE_IMAGE_ALIAS, alias));
			}
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_UPDATE_IMAGE_ALIAS, alias), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_UPDATE_IMAGE_ALIAS, alias), e);
		}

		return serviceResponse;
	}

	@RemoteMethod
	public ServiceResponse<Void> deleteAllRuleItem(String ruleId){
		return deleteItem(ruleId, null, null, ""); 
	}
	
	@RemoteMethod
	public ServiceResponse<Void> deleteRuleItem(String ruleId, String memberId, String alias){
		return deleteItem(ruleId, memberId, null, alias); 
	}
	
	@RemoteMethod
	public ServiceResponse<Void> deleteRuleItemWithImage(String ruleId, String imagePathId, String alias){
		return deleteItem(ruleId, null, imagePathId, alias); 
	}
	
	public ServiceResponse<Void> deleteItem(String ruleId, String memberId, String imagePathId, String alias){
		String storeId = UtilityService.getStoreId();
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();
		
		BannerRuleItem bannerRuleItem = new BannerRuleItem(ruleId, storeId, memberId);
		
		try {
			if (daoService.deleteBannerRuleItem(bannerRuleItem) > 0){
				serviceResponse.success(null);
			}else{
				serviceResponse.error(String.format(MSG_FAILED_DELETE_RULE_ITEM, alias));
			}
		} catch (DaoException e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_DELETE_RULE_ITEM, alias), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			serviceResponse.error(String.format(MSG_FAILED_DELETE_RULE_ITEM, alias), e);
		}
		
		return serviceResponse;
	}
}