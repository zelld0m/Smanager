package com.search.manager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
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
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.ImagePath;
import com.search.manager.model.ImagePathType;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.response.ServiceResponse;

@Service(value = "bannerService")
@RemoteProxy(
		name = "BannerServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "bannerService")
)
public class BannerService extends RuleService{
	private static final Logger logger = Logger.getLogger(BannerService.class);

	private static final String MSG_FAILED_ADD_RULE = "Failed to add banner rule %s";
	private static final String MSG_FAILED_ADD_RULE_ITEM = "Failed to add banner rule item %s";
	private static final String MSG_FAILED_DELETE_RULE_ITEM = "Failed to delete banner item %s";
	private static final String MSG_FAILED_GET_IMAGE = "Failed to retrieve record for %s";
	private static final String MSG_FAILED_ADD_IMAGE = "Failed to add image link %s : %s";
	private static final String MSG_FAILED_UPDATE_IMAGE_ALIAS = "Failed to update image alias to %s";
	private static final String MSG_FAILED_GET_RULE_WITH_IMAGE = "Failed to retrieve banner rule using %s";

	@Autowired private DaoService daoService;
	@Autowired private DeploymentService deploymentService;

	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.BANNER;
	}
		
	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRule>> getAllRules(String storeId, String searchText, int page, int pageSize){
		ServiceResponse<RecordSet<BannerRule>> serviceResponse = new ServiceResponse<RecordSet<BannerRule>>();
		
		try {
			BannerRule rule = new BannerRule(storeId, searchText);
			SearchCriteria<BannerRule> criteria = new SearchCriteria<BannerRule>(rule, MatchType.LIKE_NAME, page, pageSize);
			serviceResponse.success(daoService.searchBannerRule(criteria));
		} catch (DaoException e) {
	    	logger.error(e.getMessage(), e);
	    	serviceResponse.error("Unable to retrieve all rules.");
	    }catch (Exception e) {
	    	logger.error(e.getMessage(), e);
	    	serviceResponse.error("Unable to retrieve all rules.");
		}
		
		return serviceResponse;
	}

	@RemoteMethod
	public ServiceResponse<BannerRule> getRuleByName(String storeId, String ruleName) {
		ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();

	    try {
	    	BannerRule rule = new BannerRule(storeId, ruleName);
			SearchCriteria<BannerRule> criteria = new SearchCriteria<BannerRule>(rule, MatchType.MATCH_NAME, 0, 0);
			RecordSet<BannerRule> allRulesRS = daoService.searchBannerRule(criteria);
	    	serviceResponse.success((BannerRule) CollectionUtils.get(allRulesRS.getList(), 0));
	    } catch (DaoException e) {
	    	logger.error(e.getMessage(), e);
	    	serviceResponse.error("Unable to retrieve selected rule.");
	    }catch (Exception e) {
	    	logger.error(e.getMessage(), e);
	    	serviceResponse.error("Unable to retrieve selected rule.");
	    }

	    return serviceResponse;
	}

	@RemoteMethod
	public ServiceResponse<BannerRule> getRuleById(String storeId, String ruleId) {
	    ServiceResponse<BannerRule> serviceResponse = new ServiceResponse<BannerRule>();
	    BannerRule rule = new BannerRule();
	    rule.setStoreId(storeId);
	    rule.setRuleId(ruleId);

	    try {
			SearchCriteria<BannerRule> criteria = new SearchCriteria<BannerRule>(rule, MatchType.MATCH_ID, 1, 1);
			RecordSet<BannerRule> allRulesRS = daoService.searchBannerRule(criteria);
	    	serviceResponse.success((BannerRule) CollectionUtils.get(allRulesRS.getList(), 0));
	    } catch (DaoException e) {
	    	logger.error(e.getMessage(), e);
	    	serviceResponse.error("Unable to retrieve selected rule.");
	    }catch (Exception e) {
	    	logger.error(e.getMessage(), e);
	    	serviceResponse.error("Unable to retrieve selected rule.");
	    }

	    return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRule>> getRulesByImageId(String storeId, String imagePathId, String imageAlias, int page, int pageSize){
		ServiceResponse<RecordSet<BannerRule>> serviceResponse = new ServiceResponse<RecordSet<BannerRule>>();

		BannerRule rule = new BannerRule(storeId);
		
		Map<String, Object> additionalCriteria = new HashMap<String, Object>();
		additionalCriteria.put("imagePathId", imagePathId);
		
		SearchCriteria<BannerRule> criteria = new SearchCriteria<BannerRule>(rule, page, pageSize);
		criteria.setAdditionalCriteria(additionalCriteria);
		
		try {
			serviceResponse.success(daoService.searchBannerRule(criteria));
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
	public ServiceResponse<Integer> getTotalRulesByImageId(String storeId, String imagePathId, String imageAlias){
		ServiceResponse<RecordSet<BannerRule>> srAllRule = getRulesByImageId(storeId, imagePathId, imageAlias, 0, 0);
		RecordSet<BannerRule> rsAllRule = srAllRule.getData();

		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();
		serviceResponse.success(rsAllRule.getTotalSize());

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
	public ServiceResponse<List<String>> copyToRule(String storeId, String[] keywords, Map<String, String> params){
		ServiceResponse<List<String>> serviceResponse = new ServiceResponse<List<String>>();
		List<String> copiedToKeywordList = new ArrayList<String>();
		
		for(String keyword: keywords){
			addRule(keyword);
			ServiceResponse<BannerRule> srRule = getRuleByName(storeId, keyword);
			BannerRule rule = srRule.getData();
			
			if(rule!=null){
				RuleStatus ruleStatus = deploymentService.getRuleStatus(RuleEntity.getValue(getRuleEntity().getCode()),rule.getRuleId());
				
				if(ruleStatus!=null && !ruleStatus.isLocked()){
					params.put("ruleId", rule.getRuleId());
					params.put("ruleName", rule.getRuleName());
					ServiceResponse<Void> srRuleItem = addRuleItem(params);
					if (srRuleItem.getStatus() == ServiceResponse.SUCCESS)
						copiedToKeywordList.add(keyword);
				}
			}
		}
		
		serviceResponse.success(copiedToKeywordList);
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<Void> addRuleItem(Map<String, String> params){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();

		String ruleId = params.get("ruleId"); 
		String ruleName = params.get("ruleName"); 
		Integer priority  = Integer.parseInt(params.get("priority")); 
		String startDate = params.get("startDate"); 
		String endDate = params.get("endDate");  
		String imageAlt = params.get("imageAlt"); 
		String linkPath = params.get("linkPath"); 
		String description = params.get("description"); 
		String imagePathId = params.get("imagePathId"); 
		String imagePath = params.get("imagePath"); 
		String imageAlias = params.get("imageAlias"); 
		Boolean disable = BooleanUtils.toBooleanObject(params.get("disable"));
		Boolean openNewWindow = BooleanUtils.toBooleanObject(params.get("openNewWindow")); 
		
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();
		BannerRule rule = new BannerRule(storeId, ruleId, ruleName, null);
		DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(startDate, JodaPatternType.DATE);
		DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(endDate, JodaPatternType.DATE);

		ImagePath newImagePath = new ImagePath(storeId, imagePathId, imagePath, null, imageAlias); 

		if(StringUtils.isBlank(imagePathId)){
			ServiceResponse<Void> srAddImagePath = addImagePathLink(imagePath, imageAlias);
			if (srAddImagePath.getStatus() == ServiceResponse.SUCCESS){
				ServiceResponse<ImagePath> srGetImagePath = getImagePath(imagePath);
				newImagePath = srGetImagePath.getData();
			}
			else{
				serviceResponse.error(String.format(MSG_FAILED_ADD_IMAGE, imagePath, imageAlias));
				return serviceResponse; 
			}
		}

		BannerRuleItem ruleItem = new BannerRuleItem(rule, null, priority, startDT, endDT, imageAlt, linkPath, description, newImagePath, disable, openNewWindow);
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
	public ServiceResponse<Integer> getTotalRuleItems(String storeId, String ruleId){
		ServiceResponse<RecordSet<BannerRuleItem>> items = getRuleItemsByRuleId(storeId, ruleId, 1, 1);
		ServiceResponse<Integer> serviceResponse = new ServiceResponse<Integer>();
		serviceResponse.success((Integer) items.getData().getTotalSize());
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRuleItem>> getRuleItemsByFilter(String storeId, String ruleId, String filter, String dateFilter, int page, int pageSize){
		ServiceResponse<RecordSet<BannerRuleItem>> serviceResponse = new ServiceResponse<RecordSet<BannerRuleItem>>();
		
		DateTime now = DateTime.now();
		DateTime startDate =  null;
		DateTime endDate =  null;
		Boolean disabled = null;
		
		if("active".equalsIgnoreCase(filter)){
			startDate = now;
			endDate = now;
			disabled = false;
		}else if ("expired".equalsIgnoreCase(filter)){
			endDate = now;
		}else if ("disabled".equalsIgnoreCase(filter)){
			startDate = endDate = now;
			disabled = true;
		}else if("date".equalsIgnoreCase(filter)){
			startDate = endDate = now;
			if(StringUtils.isNotBlank(dateFilter)){
				startDate = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, dateFilter, JodaPatternType.DATE);
				endDate = JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, dateFilter, JodaPatternType.DATE);
			}
			disabled = false;
		}

		BannerRuleItem ruleItem = new BannerRuleItem(ruleId, storeId);
		ruleItem.setDisabled(disabled);
		
		try {
			SearchCriteria<BannerRuleItem> criteria = new SearchCriteria<BannerRuleItem>(ruleItem, startDate, endDate, page, pageSize);
			serviceResponse.success(daoService.searchBannerRuleItem(criteria));
		} catch (DaoException e) {
			serviceResponse.error("", e);
		} catch (Exception e) {
			serviceResponse.error("", e);
		}
		
		return serviceResponse;
	}

	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRuleItem>> getRuleItemsByRuleId(String storeId, String ruleId, int page, int pageSize){
		ServiceResponse<RecordSet<BannerRuleItem>> serviceResponse = new ServiceResponse<RecordSet<BannerRuleItem>>();
		BannerRuleItem ruleItem = new BannerRuleItem(ruleId, storeId);
		
		try {
			SearchCriteria<BannerRuleItem> criteria = new SearchCriteria<BannerRuleItem>(ruleItem, page, pageSize);
			serviceResponse.success(daoService.searchBannerRuleItem(criteria));
		} catch (DaoException e) {
			serviceResponse.error("", e);
		} catch (Exception e) {
			serviceResponse.error("", e);
		}
		
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<RecordSet<BannerRuleItem>> getAllRuleItems(String storeId, String ruleId){
		return getRuleItemsByRuleId(storeId, ruleId, 0, 0);
	}
	
	@RemoteMethod
	public ServiceResponse<Void> updateRuleItem(Map<String, String> params){
		String storeId = UtilityService.getStoreId();
		String username = UtilityService.getUsername();
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();

		String ruleId = params.get("ruleId");
		String ruleName = params.get("ruleName");
		String memberId = params.get("memberId");
		String priority = params.get("priority");
		String startDate = params.get("startDate");
		String endDate = params.get("endDate");
		String imagePathId = params.get("imagePathId");
		String imagePath = params.get("imagePath");
		String imageAlias = params.get("imageAlias");
		String imageAlt = params.get("imageAlt");
		String linkPath = params.get("linkPath");
		String description = params.get("description");
		Boolean disable = BooleanUtils.toBooleanObject(params.get("disable"));
		Boolean openNewWindow = BooleanUtils.toBooleanObject(params.get("openNewWindow"));
		
		if(StringUtils.isNotBlank(ruleId) && StringUtils.isNotBlank(memberId) && StringUtils.isNotBlank(storeId) ){
			BannerRule rule = new BannerRule();
			rule.setRuleId(ruleId);
			rule.setStoreId(storeId);
		
			DateTime startDT = JodaDateTimeUtil.toDateTimeFromStorePattern(startDate, JodaPatternType.DATE);
			DateTime endDT = JodaDateTimeUtil.toDateTimeFromStorePattern(endDate, JodaPatternType.DATE);
			
			BannerRuleItem ruleItem = new BannerRuleItem();
			ruleItem.setRule(rule);
			ruleItem.setMemberId(memberId);

			if(StringUtils.isNotBlank(priority) && StringUtils.isNumeric(priority)){
				ruleItem.setPriority(Integer.parseInt(priority));			
			}
			
			ruleItem.setStartDate(startDT);
			ruleItem.setEndDate(endDT);
			ruleItem.setImageAlt(imageAlt);
			ruleItem.setLinkPath(linkPath);
			ruleItem.setDescription(description);
			ruleItem.setDisabled(disable);
			ruleItem.setOpenNewWindow(openNewWindow);
			ruleItem.setLastModifiedBy(username);
			
			ImagePath iPath =  new ImagePath();
			ServiceResponse<Void> srImagePath = new ServiceResponse<Void>();
			
			if(StringUtils.isNotBlank(imagePathId) && 
			   StringUtils.isBlank(imagePath) && 
			   StringUtils.isNotBlank(imageAlias)){
				//update alias
				logger.info(String.format("Updating banner alias", imagePathId));
				srImagePath = updateImagePathAlias(imagePathId, imageAlias);
				if (srImagePath.getStatus() == ServiceResponse.ERROR){
					return srImagePath;
				}
			}else if (StringUtils.isNotBlank(imagePathId) && 
					  StringUtils.isBlank(imagePath) && 
					  StringUtils.isBlank(imageAlias)){
				//update to existing banner
				logger.info(String.format("Updating to a existing banner ", imagePathId));
				iPath.setId(imagePathId);
			}else if (StringUtils.isBlank(imagePathId) && 
					  StringUtils.isNotBlank(imagePath) && 
					  StringUtils.isNotBlank(imageAlias)){
				//update to new banner
				logger.info(String.format("Updating to a new banner %s %s", imagePath, imageAlias));
				srImagePath = addImagePathLink(imagePath, imageAlias);
				if (srImagePath.getStatus() == ServiceResponse.ERROR){
					return srImagePath;
				}
				iPath = getImagePath(imagePath).getData();
			}else{
				// Do not update any image path assoc details
				logger.error(String.format("Image path update for %s failed %s %s %s", ruleName, imagePathId, imagePath, imageAlias));
				iPath = null;
			}
			
			ruleItem.setImagePath(iPath);
			ruleItem.setLastModifiedBy(username);
			
			// Update banner item
			try {
				daoService.updateBannerRuleItem(ruleItem);
			} catch (DaoException e) {
				logger.error(e.getMessage(), e);
				serviceResponse.error(e.getMessage(), e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				serviceResponse.error(e.getMessage(), e);
			}
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

		ImagePath imagePath = new ImagePath(storeId, imagePathId, null, null, alias, null, username);

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
	public ServiceResponse<Void> deleteAllRuleItems(String storeId, String ruleId){
		return deleteItem(storeId, ruleId, null, null, ""); 
	}

	@RemoteMethod
	public ServiceResponse<Void> deleteRuleItemByMemberId(String storeId, String ruleId, String memberId, String alias){
		return deleteItem(storeId, ruleId, memberId, null, alias); 
	}

	@RemoteMethod
	public ServiceResponse<Void> deleteRuleItemByImageId(String storeId, String ruleId, String imagePathId, String alias){
		return deleteItem(storeId, ruleId, null, imagePathId, alias); 
	}

	public ServiceResponse<Void> deleteItem(String storeId, String ruleId, String memberId, String imagePathId, String alias){
		ServiceResponse<Void> serviceResponse = new ServiceResponse<Void>();

		BannerRuleItem bannerRuleItem = new BannerRuleItem(ruleId, storeId, memberId);
		bannerRuleItem.setImagePath(new ImagePath(storeId, imagePathId, null, null, alias));

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