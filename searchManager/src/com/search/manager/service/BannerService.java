package com.search.manager.service;

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
	private static final String MSG_FAILED_GET_IMAGE = "Failed to retrieve record for %s";
	private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
	private static final String MSG_FAILED_UPDATE_IMAGE_ALIAS = "Failed to update image alias to %s";
	
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
	public ServiceResponse<RecordSet<BannerRuleItem>> getAllRuleItem(String ruleId, int page, int pageSize){
		return getAllRuleItem(ruleId, null, null, page, pageSize);
	}
	
	public ServiceResponse<RecordSet<BannerRuleItem>> getAllRuleItem(String ruleId, String startDate, String endDate, int page, int pageSize){
		String storeId = UtilityService.getStoreId();
		DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(startDate, JodaPatternType.DATE);
		DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(endDate, JodaPatternType.DATE);
		BannerRuleItem model = new BannerRuleItem(ruleId, storeId, startDT, endDT);
		SearchCriteria<BannerRuleItem> criteria = new SearchCriteria<BannerRuleItem>(model, page, pageSize);

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
		
		return null;
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
}