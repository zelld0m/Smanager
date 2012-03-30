package com.search.manager.model.constants;

import java.util.HashMap;
import java.util.Map;

public class AuditTrailConstants {
	
	public enum Entity {
		elevate,
		exclude,
		keyword,
		storeKeyword,
		campaign,
		banner,
		queryCleaning
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
		// for banner only
		addToCampaign,
		removeFromCampaign,
		mapKeyword,
	}

	public static Operation[] elevateOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateExpiryDate,
		Operation.updateComment };
	
	public static Operation[] excludeOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateExpiryDate,
		Operation.updateComment };
	
	public static Operation[] keywordOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment };
	
	public static Operation[] storeKeywordOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment };
	
	public static Operation[] campaignOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment };
	
	public static Operation[] bannerOperations = {
		Operation.add,
		Operation.update,
		Operation.delete,
		Operation.updateComment };

	public static Operation[] queryCleaningOperations = {
		Operation.add,
		Operation.update,
		Operation.delete };

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
	}
	
}
