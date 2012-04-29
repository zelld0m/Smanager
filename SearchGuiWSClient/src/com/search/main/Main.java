package com.search.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;

public class Main{
	
	public static void main(String[] args) {
		SearchGuiClientService service = new SearchGuiClientServiceImpl();

		/** UNIT TESTING STARTS HERE */
		
		/** ELEVATE RULES */
		
		List<String> elevatedKeyList = new ArrayList<String>();
		elevatedKeyList.add("apple");
		elevatedKeyList.add("ipod");
	
		//System.out.println(service.deployRules("macmall", elevatedKeyList, RuleEntity.ELEVATE));
		//System.out.println(service.recallRules("macmall", elevatedKeyList, RuleEntity.ELEVATE));
		//System.out.println(service.unDeployRules("macmall", elevatedKeyList, RuleEntity.ELEVATE));
		
		//Map<String,Boolean> map = new HashMap<String,Boolean>();
		//map = service.deployRulesMap("macmall", elevatedKeyList, RuleEntity.ELEVATE);
		//map = service.recallRulesMap("macmall", elevatedKeyList, RuleEntity.ELEVATE);
//		map = service.unDeployRulesMap("macmall", elevatedKeyList, RuleEntity.ELEVATE);
//		
//		for(String key : map.keySet()){
//			System.out.println("key : "+key+" is "+map.get(key));
//		}
		
		
			
//		List<BackupInfo> list = service.getBackupInfo("macmall", elevatedKeyList, RuleEntity.ELEVATE);
//		
//		for(BackupInfo back : list){
//			System.out.println(back.getFileSize());
//			System.out.println(back.getRuleId());
//			System.out.println(back.isHasBackup());
//			System.out.println(back.getDateCreated());
//		}
		
		/** EXCLUDE RULES */
		
		List<String> excludeKeyList = new ArrayList<String>();
		excludeKeyList.add("apple");
		excludeKeyList.add("ipod");
		
	//	System.out.println(service.deployRules("macmall", excludeKeyList, RuleEntity.EXCLUDE));
	//	System.out.println(service.recallRules("macmall", excludeKeyList, RuleEntity.EXCLUDE));
	//	System.out.println(service.unDeployRules("macmall", excludeKeyList, RuleEntity.EXCLUDE));
	
	
		//Map<String,Boolean> map = new HashMap<String,Boolean>();
		//map = service.deployRulesMap("macmall", excludeKeyList, RuleEntity.EXCLUDE);
		//map = service.recallRulesMap("macmall", elevatedKeyList, RuleEntity.EXCLUDE);
		//map = service.unDeployRulesMap("macmall", elevatedKeyList, RuleEntity.EXCLUDE);
				
//		for(String key : map.keySet()){
//			System.out.println("key : "+key+" is "+map.get(key));
//		}
		
//		List<BackupInfo> list = service.getBackupInfo("macmall", excludeKeyList, RuleEntity.EXCLUDE);
		
//		for(BackupInfo back : list){
//			System.out.println(back.getFileSize());
//			System.out.println(back.getRuleId());
//			System.out.println(back.isHasBackup());
//			System.out.println(back.getDateCreated());
//		}
		
		
		/** RANKING RULES */
		
		List<String> rankingList = new ArrayList<String>();
		rankingList.add("0064dWv1p0NVUbyQkHKN");

//		System.out.println(service.deployRules("macmall", rankingList, RuleEntity.RANKING_RULE));
//		System.out.println(service.recallRules("macmall", rankingList, RuleEntity.RANKING_RULE));
//		System.out.println(service.unDeployRules("macmall", rankingList, RuleEntity.RANKING_RULE));
		
	
		Map<String,Boolean> map = new HashMap<String,Boolean>();
		//map = service.deployRulesMap("macmall", rankingList, RuleEntity.RANKING_RULE);
		//map = service.recallRulesMap("macmall", rankingList, RuleEntity.RANKING_RULE);
		//map = service.unDeployRulesMap("macmall", rankingList, RuleEntity.RANKING_RULE);
				
		for(String key : map.keySet()){
			System.out.println("key : "+key+" is "+map.get(key));
		}
		
//		List<BackupInfo> list = service.getBackupInfo("macmall", rankingList, RuleEntity.RANKING_RULE);
		
//		for(BackupInfo back : list){
//			System.out.println(back.getFileSize());
//			System.out.println(back.getRuleId());
//			System.out.println(back.isHasBackup());
//			System.out.println(back.getDateCreated());
//		}
		


		/** UNIT TESTING ENDS HERE */
	}
}
