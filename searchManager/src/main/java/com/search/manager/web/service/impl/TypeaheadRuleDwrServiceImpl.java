package com.search.manager.web.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.*;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.constant.TypeaheadDaoConstant;
import com.search.manager.core.enums.MemberType;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.*;
import com.search.manager.core.search.*;
import com.search.manager.core.search.Filter;
import com.search.manager.core.service.*;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Product;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.UtilityService;
import com.search.manager.web.service.TypeaheadRuleDwrService;
import com.search.ws.SearchHelper;

@Service(value = "typeaheadRuleDwrService")
@RemoteProxy(name = "TypeaheadRuleServiceJS", creator = SpringCreator.class, creatorParams =
@Param(name = "beanName", value = "typeaheadRuleDwrService"))
public class TypeaheadRuleDwrServiceImpl implements TypeaheadRuleDwrService{

	private static final Logger logger =
			LoggerFactory.getLogger(TypeaheadRuleDwrServiceImpl.class);
	@Autowired
	private RuleStatusService ruleStatusService;
	@Autowired
	private SearchHelper searchHelper;
	@Autowired
	private TypeaheadBrandService typeaheadBrandService;
	@Autowired
	@Qualifier("typeaheadRuleServiceSp")
	private TypeaheadRuleService typeaheadRuleService;
	@Autowired
	private TypeaheadSuggestionService typeaheadSuggestionService;
	@Autowired
	private UtilityService utilityService;

	@RemoteMethod
	public ServiceResponse<TypeaheadRule> addRule(String storeId, String keyword) {
		TypeaheadRule typeaheadRule = new TypeaheadRule();

		typeaheadRule.setRuleName(keyword);
		typeaheadRule.setStoreId(storeId);

		ServiceResponse<TypeaheadRule> response = new ServiceResponse<TypeaheadRule>();

		try {

			if(typeaheadRuleService.search(typeaheadRule).getTotalCount() > 0) {
				response.error("The keyword '" + keyword +"' already exists.");
				return response;
			}

			typeaheadRule.setCreatedBy(utilityService.getUsername());
			typeaheadRule.setCreatedDate(new DateTime());


			typeaheadRule = typeaheadRuleService.add(typeaheadRule);

			if(typeaheadRule != null)
				response.success(typeaheadRule);
			else
				response.error("Unable to add keyword. Please contact your system administrator.");


		} catch (CoreServiceException e) {
			e.printStackTrace();
			response.error("Unable to add keyword. Please contact your system administrator.");
		}

		return response;
	}
	
	@RemoteMethod
	public ServiceResponse<Map<String, ServiceResponse<TypeaheadRule>>> updateRules(TypeaheadRule[] rules) {
		
		Map<String, ServiceResponse<TypeaheadRule>> ruleIdResponseMap = new HashMap<String, ServiceResponse<TypeaheadRule>>();
		ServiceResponse<Map<String, ServiceResponse<TypeaheadRule>>> result = new ServiceResponse<Map<String,ServiceResponse<TypeaheadRule>>>();
		
		for(TypeaheadRule rule : rules) {
			ruleIdResponseMap.put(rule.getRuleId(), updateRule(rule));
		}
		
		result.success(ruleIdResponseMap);
		
		return result;
	}
	
	@RemoteMethod
	public ServiceResponse<TypeaheadRule> updateRule(TypeaheadRule typeaheadRule) {
		
		ServiceResponse<TypeaheadRule> response = new ServiceResponse<TypeaheadRule>();
		String ruleName = null;
		try {

			TypeaheadRule existingRule = typeaheadRuleService.searchById(typeaheadRule.getStoreId(), typeaheadRule.getRuleId());
			ruleName = existingRule.getRuleName();
			existingRule.setPriority(typeaheadRule.getPriority());
			existingRule.setDisabled(typeaheadRule.getDisabled());
			//Update will not work if rule name is the same.
			if(existingRule.getRuleName().equals(typeaheadRule.getRuleName())) {
				existingRule.setRuleName(null);
			}
			existingRule.setLastModifiedDate(new DateTime());
			existingRule.setLastModifiedBy(utilityService.getUsername());

			existingRule = typeaheadRuleService.update(existingRule);

			if(existingRule != null)
				response.success(existingRule);
			else
				response.error("Unable to update the rule '"+typeaheadRule.getRuleName()+"'.");


		} catch (Exception e) {
			logger.error("failed at TypeaheadRuleServiceDwr.updateRule", e);
			response.error("Unable to update the rule '"+ruleName+"'. Please contact your system administrator.");
		}

		return response;
	}
	
	@RemoteMethod
	public ServiceResponse<Boolean> deleteRule(TypeaheadRule typeaheadRule) {
		ServiceResponse<Boolean> response = new ServiceResponse<Boolean>();
		String ruleName = null;
		try {
			TypeaheadRule existingRule = typeaheadRuleService.searchById(typeaheadRule.getStoreId(), typeaheadRule.getRuleId());
			RuleStatus ruleStatus = ruleStatusService.getRuleStatus(existingRule.getStoreId(), RuleEntity.TYPEAHEAD.getName(), existingRule.getRuleId());
			
			boolean ruleStatusUpdated = ruleStatusService.updateRuleStatusDeletedInfo(ruleStatus, utilityService.getUsername());
//			if(ruleStatus.getp)
			//			ruleName = existingRule.getRuleName();
			
			Boolean success = ruleStatusUpdated ? typeaheadRuleService.delete(existingRule) : false;
			
			if(success) {
				response.success(success);
			} else {
				response.error("Unable to delete the rule '"+ruleName+"'", success);
			}
			
		} catch (Exception e) {
			logger.error("failed at TypeaheadRuleServiceDwr.deleteRule", e);
			response.error("Unable to delete the rule '"+ruleName+"'");
		}
		
		return response;
	}
	
	@RemoteMethod
	public ServiceResponse<Map<String, ServiceResponse<Boolean>>> deleteRules(TypeaheadRule[] rules) {
		Map<String, ServiceResponse<Boolean>> ruleIdResponseMap = new HashMap<String, ServiceResponse<Boolean>>();
		ServiceResponse<Map<String, ServiceResponse<Boolean>>> result = new ServiceResponse<Map<String,ServiceResponse<Boolean>>>();
		
		for(TypeaheadRule rule : rules) {
			ruleIdResponseMap.put(rule.getRuleId(), deleteRule(rule));
		}
		
		result.success(ruleIdResponseMap);
		
		return result;
	}

	@RemoteMethod
	public ServiceResponse<TypeaheadBrand> addBrand(String ruleId, String brandName, String vendorId, Integer productCount, Integer sortOrder) {
		ServiceResponse<TypeaheadBrand> response = new ServiceResponse<TypeaheadBrand>();
		try {
			TypeaheadBrand brand = new TypeaheadBrand();

			brand.setRuleId(ruleId);
			brand.setBrandName(brandName);
			brand.setVendorId(vendorId);
			brand.setProductCount(productCount);
			brand.setSortOrder(sortOrder);
			brand.setCreatedBy(utilityService.getUsername());

			response.success(typeaheadBrandService.add(brand));

		} catch (CoreServiceException e) {
			logger.error("Error in TypeaheadRuleDwrService.addBrand", e);
			response.error("An error occured while adding the brand '"+brandName+"'. Please contact your system administrator.");
		}


		return response;
	}

	@RemoteMethod
	public ServiceResponse<TypeaheadSuggestion> addSuggestion(String ruleId, String memberType, String memberValue, Integer sortOrder) {

		ServiceResponse<TypeaheadSuggestion> response = new ServiceResponse<TypeaheadSuggestion>();
		try {

			TypeaheadSuggestion suggestion = new TypeaheadSuggestion();

			suggestion.setRuleId(ruleId);
			suggestion.setMemberType(MemberType.valueOf(memberType));
			suggestion.setMemberValue(memberValue);
			suggestion.setSortOrder(sortOrder);
			suggestion.setCreatedBy(utilityService.getUsername());

			response.success(typeaheadSuggestionService.add(suggestion));
		} catch (CoreServiceException e) {
			logger.error("Error in TypeaheadRuleDwrService.addSuggestion", e);
			response.error("An error occured while adding the suggestion '"+memberValue+"'. Please contact your system administrator.");
		} catch (Throwable e) {
			logger.error("Error in TypeaheadRuleDwrService.addSuggestion", e);
		}

		return response;
	}

	@RemoteMethod
	public ServiceResponse<SearchResult<TypeaheadRule>> getAllRules(String storeId, String name, int matchType, int orderBy, int page, int itemsPerPage) {
		logger.info(String.format("%s %d %d", name, page, itemsPerPage));
		ServiceResponse<SearchResult<TypeaheadRule>> serviceResponse = new ServiceResponse<SearchResult<TypeaheadRule>>();
		try {

			Search search = new Search(TypeaheadRule.class);
			search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
	        search.addFilter(new Filter(DAOConstants.PARAM_RULE_NAME, name));
	        search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE, matchType));
	        search.addFilter(new Filter(TypeaheadDaoConstant.PARAM_ORDER_BY, orderBy));
	        search.setPageNumber(page);
	        search.setMaxRowCount(itemsPerPage);
			
			serviceResponse.success(typeaheadRuleService.search(search));

		} catch (CoreServiceException e) {
			logger.error("getAllRule() failed.", e);
			serviceResponse.error("Unable to add typeahead rule.");
		}
		return serviceResponse;
	}
	
	@RemoteMethod
	public ServiceResponse<TypeaheadRule> getByRuleId(String ruleId) {
		ServiceResponse<TypeaheadRule> serviceResponse = new ServiceResponse<TypeaheadRule>();
		try {

			String storeId = utilityService.getStoreId();

			Search search = new Search(TypeaheadRule.class);
			search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
	        search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, ruleId));
	        search.setPageNumber(1);
	        search.setMaxRowCount(1);
			
			serviceResponse.success(typeaheadRuleService.search(search).getList().get(0));

		} catch (CoreServiceException e) {
			logger.error("getAllRule() failed.", e);
			serviceResponse.error("Unable to retrieve typeahead rule(s).");
		}
		return serviceResponse;
	}
	
	@RemoteMethod
	public Map<String, ? extends Product> getProducts(String storeId, String[] dpNumberList) {
		
		Map<String, Product> dpNumEdpMap = new LinkedHashMap<String, Product>();
		
		for(String partNumber : dpNumberList) {
			String edp = searchHelper.getEdpByPartNumber(utilityService.getServerName(), utilityService.getStoreId(), StringUtils.trim(partNumber));
			Product product = new Product();
			product.setEdp(edp);
			product.setMemberTypeEntity(MemberTypeEntity.PART_NUMBER);
			dpNumEdpMap.put(edp, product);
		}
		
		searchHelper.getProducts(dpNumEdpMap, storeId, utilityService.getServerName(), null);
		
		return dpNumEdpMap;
	}
}
