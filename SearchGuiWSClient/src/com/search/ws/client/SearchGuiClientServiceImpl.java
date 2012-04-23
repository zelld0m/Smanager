package com.search.ws.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.rpc.Stub;
import org.apache.log4j.Logger;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;
import com.search.manager.utility.PropsUtils;
import com.search.webservice.model.TransportList;

public class SearchGuiClientServiceImpl implements SearchGuiClientService{
	
	private static Logger logger = Logger.getLogger(SearchGuiClientServiceImpl.class);
	private static String WS_CLIENT = PropsUtils.getValue("guiwsclient");
	private static String TOKEN = PropsUtils.getValue("token");
	
	// for testing only: do not use in prod
	static{
		//WS_CLIENT = "http://10.17.12.67:8080/searchguiws/services/SearchGuiService";	 // staging
		WS_CLIENT = "http://localhost:8081/SearchGuiWS/services/SearchGuiService";	
		TOKEN = "Hzwviq%2FMwKMpephPCMpavg%3D%3D";
	}
	
	@Override
	public boolean recallRules(String store, List<String> ruleRefIdList, RuleEntity entity) {
		
		try{
			if(ruleRefIdList != null && ruleRefIdList.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;

				TransportList list_ = new TransportList();
				list_.setToken(TOKEN);
				list_.setStore(store);
				
				com.search.webservice.model.RuleEntity entity_ = new com.search.webservice.model.RuleEntity(getRuleName(entity.getCode()));
	
				list_.setRuleEntity(entity_);
				
				String[] entArr = new String[ruleRefIdList.size()];
				int arrCnt = 0;

				for(String key : ruleRefIdList){
					entArr[arrCnt] = key;
					arrCnt++;
				}	

				list_.setList(entArr);
				return search.recallRules(list_);
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public boolean deployRules(String store, List<String> ruleRefIdList, RuleEntity entity) {
	
		try{
			if(ruleRefIdList != null && ruleRefIdList.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;

				TransportList list_ = new TransportList();
				list_.setToken(TOKEN);
				list_.setStore(store);
				
				com.search.webservice.model.RuleEntity entity_ = new com.search.webservice.model.RuleEntity(getRuleName(entity.getCode()));
	
				list_.setRuleEntity(entity_);
				
				String[] entArr = new String[ruleRefIdList.size()];
				int arrCnt = 0;

				for(String key : ruleRefIdList){
					entArr[arrCnt] = key;
					arrCnt++;
				}	

				list_.setList(entArr);
				return search.deployRules(list_);
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return false;
	}
	
	@Override
	public List<BackupInfo> getBackupInfo(String store, List<String> ruleRefIdList, RuleEntity entity) {
	
		List<BackupInfo> backupList_ = new ArrayList<BackupInfo>();
		
		try{
			if(ruleRefIdList != null && ruleRefIdList.size() > 0){
				Stub stub = createStoreProxy();
				stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY, WS_CLIENT);
				SearchGuiServicePortType search = (SearchGuiServicePortType) stub;

				TransportList list_ = new TransportList();
				list_.setToken(TOKEN);
				list_.setStore(store);
				
				com.search.webservice.model.RuleEntity entity_ = new com.search.webservice.model.RuleEntity(getRuleName(entity.getCode()));
	
				list_.setRuleEntity(entity_);
				
				String[] entArr = new String[ruleRefIdList.size()];
				int arrCnt = 0;

				for(String key : ruleRefIdList){
					entArr[arrCnt] = key;
					arrCnt++;
				}	

				list_.setList(entArr);
				com.search.webservice.model.BackupInfo[] backupList = search.getBackupInfo(list_);
				
				if(backupList != null && backupList.length > 0){
					for(com.search.webservice.model.BackupInfo backup : backupList){
						BackupInfo backup_ = new BackupInfo();
						backup_.setDateCreated(backup.getDateCreated().getTime());
						backup_.setFileSize(backup.getFileSize());
						backup_.setHasBackup(backup.getHasBackup());
						backup_.setRuleId(backup.getRuleId());
						backupList_.add(backup_);
					}	
				}
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return backupList_;
	}
	
	private static Stub createStoreProxy() {
		return (Stub) (new SearchGuiServicePortTypeProxy().getSearchGuiServicePortType());
	}
	
	private String getRuleName(int code){
		
		switch (code) {
		case 1:
			return "ELEVATE";
		case 2:
			return "EXCLUDE";
		case 3:
			return "KEYWORD";
		case 4:
			return "STORE_KEYWORD";	
		case 5:
			return "CAMPAIGN";		
		case 6:
			return "BANNER";			
		case 7:
			return "QUERY_CLEANING";	
		case 8:
			return "RANKING_RULE";	
		default:
			break;
		}
		return "";
	}
}

