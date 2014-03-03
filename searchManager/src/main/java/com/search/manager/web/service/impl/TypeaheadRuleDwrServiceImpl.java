package com.search.manager.web.service.impl;

import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.enums.MemberType;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.TypeaheadBrand;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.model.TypeaheadSuggestion;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.search.Filter.MatchType;
import com.search.manager.core.service.TypeaheadBrandService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.core.service.TypeaheadSuggestionService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.response.ServiceResponse;
import com.search.manager.service.UtilityService;
import com.search.manager.web.service.TypeaheadRuleDwrService;

@Service(value = "typeaheadRuleDwrService")
@RemoteProxy(name = "TypeaheadRuleServiceJS", creator = SpringCreator.class, creatorParams =
@Param(name = "beanName", value = "typeaheadRuleDwrService"))
public class TypeaheadRuleDwrServiceImpl implements TypeaheadRuleDwrService{

	private static final Logger logger =
			LoggerFactory.getLogger(TypeaheadRuleDwrServiceImpl.class);
	@Autowired
	private TypeaheadBrandService typeaheadBrandService;
	@Autowired
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
	public ServiceResponse<SearchResult<TypeaheadRule>> getAllRules(String name, int page, int itemsPerPage) {
		logger.info(String.format("%s %d %d", name, page, itemsPerPage));
		ServiceResponse<SearchResult<TypeaheadRule>> serviceResponse = new ServiceResponse<SearchResult<TypeaheadRule>>();
		try {

			String storeId = utilityService.getStoreId();

			Search search = new Search(TypeaheadRule.class);
			search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
	        search.addFilter(new Filter(DAOConstants.PARAM_RULE_NAME, name));
	        search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE, MatchType.LIKE_NAME.getIntValue()));
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
			serviceResponse.error("Unable to add typeahead rule.");
		}
		return serviceResponse;
	}
}
