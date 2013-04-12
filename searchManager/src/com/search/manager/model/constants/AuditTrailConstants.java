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
		facetSort,
		spell
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

	public static Entity[] ENTITY_LIST_ASC = {
		Entity.banner,
		Entity.campaign,
		Entity.demote,
		Entity.elevate,
		Entity.exclude,
		Entity.facetSort,
		Entity.keyword,
		Entity.queryCleaning,
		Entity.relevancy,
		Entity.ruleStatus,
		Entity.security,
		Entity.spell,
		Entity.storeKeyword
	};
	
	public static Operation[] elevateOperations = {
		Operation.add,
		Operation.appendComment,
		Operation.clear,
		Operation.delete,
		Operation.update,
		Operation.updateComment,
		Operation.updateExpiryDate
		 };
	
	public static Operation[] excludeOperations = {
		Operation.add,
		Operation.appendComment, 
		Operation.update,
		Operation.clear,
		Operation.delete,
		Operation.updateComment,
		Operation.updateExpiryDate
		};
	
	public static Operation[] demoteOperations = {
		Operation.add,
		Operation.appendComment, 
		Operation.update,
		Operation.clear,
		Operation.delete,
		Operation.updateComment,
		Operation.updateExpiryDate
		};
	
	public static Operation[] keywordOperations = {
		Operation.add,
		Operation.delete,
		Operation.update,
		Operation.updateComment };
	
	public static Operation[] storeKeywordOperations = {
		Operation.add,
		Operation.appendComment,
		Operation.delete,
		Operation.update,
		Operation.updateComment
		 };
	
	public static Operation[] campaignOperations = {
		Operation.add,
		Operation.appendComment, 
		Operation.delete,
		Operation.update,
		Operation.updateComment,
		};
	
	public static Operation[] bannerOperations = {
		Operation.add,
		Operation.appendComment,
		Operation.delete,
		Operation.update,
		Operation.updateComment
		 };
	
	public static Operation[] campaignBannerOperations = {
		Operation.addBanner,
		Operation.removeBanner,
	};
	
	public static Operation[] campaignKeywordOperations = {
		
	};
	
	public static Operation[] bannerToCampaignOperations = {
		Operation.addToCampaign,
		Operation.removeFromCampaign
	};

	public static Operation[] queryCleaningOperations = {
		Operation.add,
		Operation.delete,
		Operation.update,
	};
	
	public static Operation[] facetSortOperations = {
		Operation.add,
		Operation.delete,
		Operation.update,
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
		Operation.removeCondition,
		Operation.updateCondition,
	};

	public static Operation[] relevancyOperations = {
		Operation.add,
		Operation.appendComment,
		Operation.delete,
		Operation.update,
		Operation.updateComment,
		 };

	public static Operation[] relevancyFieldOperations = {
		Operation.addRelevancyField,
		Operation.deleteRelevancyField,
		Operation.saveRelevancyField,
		Operation.updateRelevancyField,
		};

	public static Operation[] relevancyKeywordOperations = {
		Operation.mapKeyword,
		Operation.updateKeywordMapping,
		Operation.unmapKeyword,
		};

	public static Operation[] ruleStatusOperations = {
		Operation.add,
		Operation.delete,
		Operation.exportRule,
		Operation.importRule,
		Operation.update,
		};

	public static Operation[] securityOperations = {
		Operation.add,
		Operation.delete, 
		Operation.resetPassword,
		Operation.update,
		};
	
	public static Operation[] spellOperations = {
		Operation.add,
		Operation.delete, 
		Operation.update,
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
		entityOperationMap.put(Entity.spell, spellOperations);
		ArrayList<Operation> relevancyOperationList = new ArrayList<Operation>();
		CollectionUtils.mergeArrayIntoCollection(relevancyOperations, relevancyOperationList);
		CollectionUtils.mergeArrayIntoCollection(relevancyFieldOperations, relevancyOperationList);
		CollectionUtils.mergeArrayIntoCollection(relevancyKeywordOperations, relevancyOperationList);
		entityOperationMap.put(Entity.relevancy, relevancyOperationList.toArray(new Operation[0]));
	}
	
}
