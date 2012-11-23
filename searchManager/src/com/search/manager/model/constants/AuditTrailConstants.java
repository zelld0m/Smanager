package com.search.manager.model.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

public class AuditTrailConstants {
	
	public enum Entity {
		elevate,
		exclude,
		demote,
		keyword,
		storeKeyword,
		campaign,
		banner,
		queryCleaning,
		relevancy,
		ruleStatus,
		security,
		facetSort
	}
	
	public enum Operation {
		// basic
		add,
		update,
		delete,
		updateComment,
		appendComment,
		// for campaign only
		addBanner,
		removeBanner,
		// for elevate, demote and exclude only
		clear,
		updateExpiryDate,
		// for banner only
		addToCampaign,
		removeFromCampaign,
		//for relevancy only
		addRelevancyField,
		updateRelevancyField,
		deleteRelevancyField,
		saveRelevancyField,
		// for redirect, relevancy and banners
		mapKeyword,
		unmapKeyword,
		// for relevancy only
		updateKeywordMapping,
		// for redirect only
		addCondition,
		updateCondition,
		removeCondition, 
		resetPassword,
		//for facet sort only
		updateGroup,
		updateGroupItem,
		// for import and export
		importRule,
		exportRule
	}

	public static Operation[] elevateOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.clear,
		Operation.updateExpiryDate,
		Operation.updateComment,
		Operation.appendComment };
	
	public static Operation[] excludeOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.clear,
		Operation.updateExpiryDate,
		Operation.updateComment,
		Operation.appendComment };
	
	public static Operation[] demoteOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.clear,
		Operation.updateExpiryDate,
		Operation.updateComment,
		Operation.appendComment };
	
	public static Operation[] keywordOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment };
	
	public static Operation[] storeKeywordOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment,
		Operation.appendComment };
	
	public static Operation[] campaignOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment,
		Operation.appendComment };
	
	public static Operation[] bannerOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment,
		Operation.appendComment };

	public static Operation[] queryCleaningOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
	};
	
	public static Operation[] facetSortOperations = {
		Operation.add,
		Operation.update,
		Operation.delete
	};
	
	public static Operation[] facetSortGroupOperations = {
		Operation.updateGroup
	};
	
	public static Operation[] facetSortGroupItemOperations = {
		Operation.updateGroupItem
	};
		
	public static Operation[] queryCleaningKeywordOperations = {
		Operation.mapKeyword,
		Operation.unmapKeyword,
	};

	public static Operation[] queryCleaningConditionOperations = {
		Operation.addCondition,
		Operation.updateCondition,
		Operation.removeCondition,
	};

	public static Operation[] relevancyOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment,
		Operation.appendComment };

	public static Operation[] relevancyFieldOperations = {
		Operation.addRelevancyField,
		Operation.updateRelevancyField,
		Operation.saveRelevancyField,
		Operation.deleteRelevancyField, };

	public static Operation[] relevancyKeywordOperations = {
		Operation.mapKeyword,
		Operation.unmapKeyword,
		Operation.updateKeywordMapping };

	public static Operation[] ruleStatusOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.exportRule,
		Operation.importRule };

	public static Operation[] securityOperations = {
		Operation.add,
		Operation.update,
		Operation.delete, 
		Operation.resetPassword
		};

	public static Map<Entity, Operation[]> entityOperationMap;
	
	static {
		entityOperationMap = new HashMap<Entity, Operation[]>();
		entityOperationMap.put(Entity.elevate, elevateOperations);
		entityOperationMap.put(Entity.exclude, excludeOperations);
		entityOperationMap.put(Entity.demote, demoteOperations);
		entityOperationMap.put(Entity.keyword, keywordOperations);
		entityOperationMap.put(Entity.storeKeyword, storeKeywordOperations);
		entityOperationMap.put(Entity.campaign, campaignOperations);
		entityOperationMap.put(Entity.banner, bannerOperations);
		entityOperationMap.put(Entity.facetSort, facetSortOperations);
		ArrayList<Operation> queryCleaningOperationList = new ArrayList<Operation>();
		CollectionUtils.mergeArrayIntoCollection(queryCleaningOperations, queryCleaningOperationList);
		CollectionUtils.mergeArrayIntoCollection(queryCleaningKeywordOperations, queryCleaningOperationList);
		CollectionUtils.mergeArrayIntoCollection(queryCleaningConditionOperations, queryCleaningOperationList);
		entityOperationMap.put(Entity.queryCleaning, queryCleaningOperationList.toArray(new Operation[0]));
		entityOperationMap.put(Entity.ruleStatus, ruleStatusOperations);
		entityOperationMap.put(Entity.security, securityOperations);
		ArrayList<Operation> relevancyOperationList = new ArrayList<Operation>();
		CollectionUtils.mergeArrayIntoCollection(relevancyOperations, relevancyOperationList);
		CollectionUtils.mergeArrayIntoCollection(relevancyFieldOperations, relevancyOperationList);
		CollectionUtils.mergeArrayIntoCollection(relevancyKeywordOperations, relevancyOperationList);
		entityOperationMap.put(Entity.relevancy, relevancyOperationList.toArray(new Operation[0]));
	}
	
}
