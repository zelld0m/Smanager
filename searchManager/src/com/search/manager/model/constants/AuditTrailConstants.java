package com.search.manager.model.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;

public class AuditTrailConstants {
	
	public enum Entity {
		elevate,
		exclude,
		keyword,
		storeKeyword,
		campaign,
		banner,
		queryCleaning,
		relevancy
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
		// for elevate and exclude only
		updateExpiryDate,
		clear,
		// for banner only
		addToCampaign,
		removeFromCampaign,
		//for relevancy only
		addRelevancyField,
		updateRelevancyField,
		deleteRelevancyField,
		saveRelevancyField,
		// for relevancy and banners
		mapKeyword,
		unmapKeyword,
		updateKeywordMapping,
		saveKeywordMapping,		
	}

	public static Operation[] elevateOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateExpiryDate,
		Operation.updateComment,
		Operation.appendComment };
	
	public static Operation[] excludeOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
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
		Operation.delete };

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
		Operation.saveKeywordMapping,
		Operation.updateKeywordMapping };

	public static Map<Entity, Operation[]> entityOperationMap;
	
	static {
		entityOperationMap = new HashMap<Entity, Operation[]>();
		entityOperationMap.put(Entity.elevate, elevateOperations);
		entityOperationMap.put(Entity.exclude, excludeOperations);
		entityOperationMap.put(Entity.keyword, keywordOperations);
		entityOperationMap.put(Entity.storeKeyword, storeKeywordOperations);
		entityOperationMap.put(Entity.campaign, campaignOperations);
		entityOperationMap.put(Entity.banner, bannerOperations);
		entityOperationMap.put(Entity.queryCleaning, queryCleaningOperations);
		ArrayList<Operation> relevancyOperationList = new ArrayList<Operation>();
		CollectionUtils.mergeArrayIntoCollection(relevancyOperations, relevancyOperationList);
		CollectionUtils.mergeArrayIntoCollection(relevancyFieldOperations, relevancyOperationList);
		CollectionUtils.mergeArrayIntoCollection(relevancyKeywordOperations, relevancyOperationList);
		entityOperationMap.put(Entity.relevancy, relevancyOperationList.toArray(new Operation[0]));
	}
	
}
