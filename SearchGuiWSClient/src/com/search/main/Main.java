package com.search.main;

import java.util.ArrayList;
import java.util.List;
import com.search.manager.enums.RuleEntity;
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
	
		System.out.println(service.deployRules("macmall", elevatedKeyList, RuleEntity.ELEVATE));
		//System.out.println(service.recallRules("macmall", elevatedKeyList, RuleEntity.ELEVATE));
		
//		List<BackupInfo> list = service.getBackupInfo("macmall", elevatedKeyList, RuleEntity.ELEVATE);
//		
//		for(BackupInfo back : list){
//			System.out.println(back.getFileSize());
//			System.out.println(back.getRuleId());
//			System.out.println(back.isHasBackup());
//			System.out.println(back.getDateCreated());
//		}
		
		/** EXCLUDE RULES */
		
//		List<String> excludeKeyList = new ArrayList<String>();
//		excludeKeyList.add("apple");
//		excludeKeyList.add("ipod");
		
	//	System.out.println(service.deployRules("macmall", excludeKeyList, RuleEntity.EXCLUDE));
	//	System.out.println(service.recallRules("macmall", excludeKeyList, RuleEntity.EXCLUDE));
		
//		List<BackupInfo> list = service.getBackupInfo("macmall", excludeKeyList, RuleEntity.EXCLUDE);
//		
//		for(BackupInfo back : list){
//			System.out.println(back.getFileSize());
//			System.out.println(back.getRuleId());
//			System.out.println(back.isHasBackup());
//			System.out.println(back.getDateCreated());
//		}
		
		/** UNIT TESTING ENDS HERE */
	}
}
