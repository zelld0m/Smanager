package com.search.manager.dao.sp;

import org.apache.commons.lang.StringUtils;

import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.Store;
import com.search.manager.dao.DaoException;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Keyword;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.StoreKeyword;

public class DAOValidation {

	/* Error Messages */
	// STORE
    public final static String ERROR_MESSAGE_NO_STORE 					= "No store provided";
    public final static String ERROR_MESSAGE_NO_STORE_ID 				= "No store id provided";
    // KEYWORD
    public final static String ERROR_MESSAGE_NO_KEYWORD					= "No keyword provided";
    public final static String ERROR_MESSAGE_NO_KEYWORD_ID 				= "No keyword id provided";
    // STORE KEYWORD
    public final static String ERROR_MESSAGE_NO_STORE_KEYWORD			= "No store keyword provided";
    // ELEVATE
    public final static String ERROR_MESSAGE_NO_ELEVATE_VALUE			= "No elevate value provided";
    // EXCLUDE
    public final static String ERROR_MESSAGE_NO_EXCLUDE_VALUE			= "No exclude value provided";
    // DEMOTE
    public final static String ERROR_MESSAGE_NO_DEMOTE_VALUE			= "No demote value provided";
    // RELEVANCY
    public final static String ERROR_MESSAGE_NO_RELEVANCY 				= "No relevancy provided";
    public final static String ERROR_MESSAGE_NO_RELEVANCY_ID			= "No relevancy id provided";
    public final static String ERROR_MESSAGE_NO_RELEVANCY_FIELD			= "No relevancy field provided";
    public final static String ERROR_MESSAGE_NO_RELEVANCY_FIELD_NAME	= "No relevancy field name provided";
    public final static String ERROR_MESSAGE_NO_RELEVANCY_KEYWORD 		= "No relevancy keyword provided";
    // FACET
    public final static String ERROR_MESSAGE_NO_FACET_SORT 				= "No facet sort provided";
    public final static String ERROR_MESSAGE_NO_FACET_SORT_STORE		= "No facet sort store provided";
    public final static String ERROR_MESSAGE_NO_FACET_SORT_NAME 		= "No facet sort name provided";
    public final static String ERROR_MESSAGE_NO_FACET_SORT_ID			= "No facet sort id provided";
    public final static String ERROR_MESSAGE_NO_FACET_GROUP 			= "No facet group provided";
    public final static String ERROR_MESSAGE_NO_FACET_GROUP_ID 			= "No facet group id provided";
    public final static String ERROR_MESSAGE_NO_FACET_GROUP_ITEM		= "No facet group item provided";
    public final static String ERROR_MESSAGE_NO_FACET_GROUP_ITEM_ID		= "No facet group item id provided";
    
    // SEARCH CRITERIA
    public final static String ERROR_MESSAGE_NO_SEARCH_CRITERIA			= "No search criteria provided";
    public final static String ERROR_MESSAGE_NO_SEARCH_CRITERIA_MODEL	= "No search criteria model provided";

    // RULE STATUS
    public final static String ERROR_MESSAGE_NO_RULE_STATUS					= "No rule status provided";
    public final static String ERROR_MESSAGE_NO_RULE_STATUS_REFERENCE_ID	= "No rule status reference id provided";
    public final static String ERROR_MESSAGE_NO_RULE_STATUS_TYPE_ID			= "No rule status type id provided";

	/* Validation methods */
    public static boolean checkStringNotEmpty(String string, String errorMessage) throws DaoException {
    	if (StringUtils.isBlank(string)) {
    		throw new DaoException(errorMessage);
    	}
    	return true;
    }
    
    public static boolean checkObjectNotNull(Object object, String errorMessage) throws DaoException {
    	if (object == null) {
    		throw new DaoException(errorMessage);
    	}
    	return true;
    }
    
    public static void checkStoreId(Store store) throws DaoException {
		checkObjectNotNull(store, ERROR_MESSAGE_NO_STORE);
		checkStringNotEmpty(store.getStoreId(), ERROR_MESSAGE_NO_STORE_ID);
    }
    
    public static void checkKeywordId(Keyword keyword) throws DaoException {
		checkObjectNotNull(keyword, ERROR_MESSAGE_NO_KEYWORD);
		checkStringNotEmpty(keyword.getKeywordId(), ERROR_MESSAGE_NO_KEYWORD_ID);
    }
    
    public static void checkStoreKeywordPK(StoreKeyword storeKeyword) throws DaoException {
		checkObjectNotNull(storeKeyword, ERROR_MESSAGE_NO_STORE_KEYWORD);
    	checkStoreId(storeKeyword.getStore());
    	checkKeywordId(storeKeyword.getKeyword());
    }
    
    public static void checkElevatePK(ElevateResult elevate) throws DaoException {
    	checkObjectNotNull(elevate, ERROR_MESSAGE_NO_ELEVATE_VALUE);
    	checkStoreKeywordPK(elevate.getStoreKeyword());    	
//		checkStringNotEmpty(elevate.getEdp(), ERROR_MESSAGE_NO_ELEVATE_VALUE);
    }

    public static void checkExcludePK(ExcludeResult exclude) throws DaoException {
    	checkObjectNotNull(exclude, ERROR_MESSAGE_NO_EXCLUDE_VALUE);
    	checkStoreKeywordPK(exclude.getStoreKeyword());    	
//		checkStringNotEmpty(exclude.getEdp(), ERROR_MESSAGE_NO_EXCLUDE_VALUE);
    }
    
    public static void checkDemotePK(DemoteResult demote) throws DaoException {
    	checkObjectNotNull(demote, ERROR_MESSAGE_NO_DEMOTE_VALUE);
    	checkStoreKeywordPK(demote.getStoreKeyword());    	
//		checkStringNotEmpty(exclude.getEdp(), ERROR_MESSAGE_NO_DEMOTE_VALUE);
    }

    public static void checkRelevancy(Relevancy relevancy) throws DaoException {
		checkObjectNotNull(relevancy, ERROR_MESSAGE_NO_RELEVANCY);
    }
    
    public static void checkRelevancyPK(Relevancy relevancy) throws DaoException {
		checkObjectNotNull(relevancy, ERROR_MESSAGE_NO_RELEVANCY);
		checkStringNotEmpty(relevancy.getRelevancyId(), ERROR_MESSAGE_NO_RELEVANCY_ID);
    }
    
    public static void checkRelevancyFieldPK(RelevancyField relevancyField) throws DaoException {
		checkObjectNotNull(relevancyField, ERROR_MESSAGE_NO_RELEVANCY_FIELD);
		checkStringNotEmpty(relevancyField.getFieldName(), ERROR_MESSAGE_NO_RELEVANCY_FIELD_NAME);
    	checkRelevancyPK(relevancyField.getRelevancy());
    }
    
    public static void checkRelevancyFieldName(String fieldName) throws DaoException {
		checkStringNotEmpty(fieldName, ERROR_MESSAGE_NO_RELEVANCY_FIELD_NAME);
    }
    
    public static void checkRelevancyKeywordPK(RelevancyKeyword relevancyKeyword) throws DaoException {
		DAOValidation.checkObjectNotNull(relevancyKeyword, DAOValidation.ERROR_MESSAGE_NO_RELEVANCY_KEYWORD);
		DAOValidation.checkRelevancyPK(relevancyKeyword.getRelevancy());
		DAOValidation.checkKeywordId(relevancyKeyword.getKeyword());
    }
    
    public static void checkFacetSort(FacetSort facetSort) throws DaoException {
		checkObjectNotNull(facetSort, ERROR_MESSAGE_NO_FACET_SORT);
    }
    
    public static void checkFacetGroup(FacetGroup facetGroup) throws DaoException {
    	checkObjectNotNull(facetGroup, ERROR_MESSAGE_NO_FACET_GROUP);
    }
    
    public static void checkFacetGroupItem(FacetGroupItem facetGroupItem) throws DaoException {
    	checkObjectNotNull(facetGroupItem, ERROR_MESSAGE_NO_FACET_GROUP_ITEM);
    }
    
    public static void checkFacetSortPK(FacetSort facetSort) throws DaoException {
		checkObjectNotNull(facetSort, ERROR_MESSAGE_NO_FACET_SORT);
		checkStringNotEmpty(facetSort.getRuleId(), ERROR_MESSAGE_NO_FACET_SORT_ID);
    }
    
    public static void checkFacetSortPK(Store store, String name) throws DaoException {
		checkObjectNotNull(store, ERROR_MESSAGE_NO_FACET_SORT_STORE);
		checkStringNotEmpty(name, ERROR_MESSAGE_NO_FACET_SORT_NAME);
    }
    
    public static void checkFacetGroupPK(FacetGroup facetGroup) throws DaoException {
		checkObjectNotNull(facetGroup, ERROR_MESSAGE_NO_FACET_GROUP);
		checkStringNotEmpty(facetGroup.getId(), ERROR_MESSAGE_NO_FACET_GROUP_ID);
    }
    
    public static void checkFacetGroupItemPK(FacetGroupItem facetGroupItem) throws DaoException {
		checkObjectNotNull(facetGroupItem, ERROR_MESSAGE_NO_FACET_GROUP_ITEM);
		checkStringNotEmpty(facetGroupItem.getId(), ERROR_MESSAGE_NO_FACET_GROUP_ITEM_ID);
    }
    
    public static void checkRuleStatusPK(RuleStatus ruleStatus) throws DaoException {
		checkObjectNotNull(ruleStatus, ERROR_MESSAGE_NO_RULE_STATUS);
		checkStringNotEmpty(ruleStatus.getStoreId(), ERROR_MESSAGE_NO_STORE_ID);
		checkStringNotEmpty(ruleStatus.getRuleRefId(), ERROR_MESSAGE_NO_RULE_STATUS_REFERENCE_ID);
		checkObjectNotNull(ruleStatus.getRuleTypeId(), ERROR_MESSAGE_NO_RULE_STATUS_TYPE_ID);
    }

    @SuppressWarnings({"rawtypes"})
    public static void checkSearchCriteria(SearchCriteria criteria) throws DaoException {
		DAOValidation.checkObjectNotNull(criteria, DAOValidation.ERROR_MESSAGE_NO_SEARCH_CRITERIA);
		DAOValidation.checkObjectNotNull(criteria.getModel(), DAOValidation.ERROR_MESSAGE_NO_SEARCH_CRITERIA_MODEL);
    }
}