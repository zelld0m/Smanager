package com.search.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.FileUtil;
import com.search.manager.utility.PropsUtils;
import com.thoughtworks.xstream.XStream;

public class FileServiceImpl implements FileService{
	
	private static Logger logger = Logger.getLogger(FileServiceImpl.class);
	private static final String path = PropsUtils.getValue("backuppath");
	
	private static DaoService daoService;
	
	public void setDaoService(DaoService daoService_) {
		daoService = daoService_;
	}
	
	@Override
	public boolean createBackup(String store, List<String> list, RuleEntity ruleEntity) throws Exception {
		
		Object xml = null;
		boolean success = false;
		
		for(String ruleId : list){		
			switch (ruleEntity) {
			case ELEVATE:
				xml = getXmlObjectForElevatedRule(store, ruleId);
				if(xml != null){
					FileUtil.fileStream(xml, getFileDirectory(String.valueOf(ruleEntity.getCode()), ruleId), ruleId+FileUtil.XML_FILE_TYPE);
					success = true;
				}
				break;
			case EXCLUDE:
				xml = getXmlObjectForExcludeRule(store, ruleId);
				if(xml != null){
					FileUtil.fileStream(xml, getFileDirectory(String.valueOf(ruleEntity.getCode()), ruleId), ruleId+FileUtil.XML_FILE_TYPE);
					success = true;
				}
				break;
			case KEYWORD: 
				break;
			case STORE_KEYWORD: 
				break;
			case CAMPAIGN:
				break;
			case BANNER:
				break;
			case QUERY_CLEANING:
				return createBackupForRedirectRule(store, ruleId, String.valueOf(ruleEntity.getCode()));
			case RANKING_RULE:
				return createBackupForRankingRule(store, ruleId, String.valueOf(ruleEntity.getCode()));
			default:
				break;
			}
		}
		return success;
	}
	
	@Override
	public Map<String,List<Object>> readBackup(String store, List<String> list, RuleEntity ruleEntity) throws Exception {
		
		Map<String,List<Object>> map = new HashedMap();
		List<Object> list_ = null;
		
		for(String ruleId : list){
			switch (ruleEntity) {
			case ELEVATE:
					list_ = convertToObjectList(getElevatedResultFromFile(getFilePath(String.valueOf(ruleEntity.getCode()), ruleId, FileUtil.XML_FILE_TYPE)));
					
					if(CollectionUtils.isNotEmpty(list_))
						map.put(ruleId, list_);
				break;
			case EXCLUDE:
				list_ = convertToObjectList(getExcludeResultFromFile(getFilePath(String.valueOf(ruleEntity.getCode()), ruleId, FileUtil.XML_FILE_TYPE)));
				
					if(CollectionUtils.isNotEmpty(list_))
						map.put(ruleId, list_);
				break;
			case KEYWORD: 
				break;
			case STORE_KEYWORD: 
				break;
			case CAMPAIGN:
				break;
			case BANNER:
				break;
			case QUERY_CLEANING:
				Object obj = getRedirectRuleResultFromFile(store, ruleId, String.valueOf(ruleEntity.getCode()));
				if(obj != null){
					list_ = new ArrayList<Object>();
					list_.add(obj);
					map.put(ruleId, list_);
				}
				break;
			case RANKING_RULE:		
				obj = getRankingRuleResultFromFile(store, ruleId, String.valueOf(ruleEntity.getCode()));
				if(obj != null){
					list_ = new ArrayList<Object>();
					list_.add(obj);
					map.put(ruleId, list_);
				}
				break;
			default:
				break;
			}
		}
		return map;
	}
	
	@Override
	public List<BackupInfo> getBackupInfo(String store, List<String> list, RuleEntity ruleEntity) throws Exception {
		
		List<BackupInfo> backupList = new ArrayList<BackupInfo>();
		BackupInfo backup = null;
		
		for(String ruleId : list){
			backup = new BackupInfo();
			String path = getFilePath(String.valueOf(ruleEntity.getCode()), ruleId, FileUtil.XML_FILE_TYPE);
			
			if(FileUtil.isExist(path)){
				backup.setRuleId(ruleId);
				backup.setFileSize(FileUtil.getSizeBytes(path));
				backup.setHasBackup(true);
				backup.setDateCreated(FileUtil.getLastModefied(path));
				backupList.add(backup);
			}
		}

		return backupList;
	}
	
	@Override
	public boolean removeBackup(String store, String ruleId, RuleEntity ruleEntity) throws Exception {
		String dir = getFileDirectory(String.valueOf(ruleEntity.getCode()), ruleId);
		String path_ = "";
		
		if(FileUtil.isDirectoryExist(dir)){
			path_ = getFilePath(String.valueOf(ruleEntity.getCode()), ruleId, FileUtil.XML_FILE_TYPE);
			if(FileUtil.isExist(path_)){
				FileUtil.deleteFile(path_);
				
				if(!FileUtil.isExist(path_))
					return true;
				
			}else
				return false;
		}
		return false;
	}
	
	@Override
	public boolean removeBackup(String store, List<String> list, RuleEntity ruleEntity) throws Exception {
		
		String path_ = "";
		
		for(String ruleId : list){
			path_ = getFilePath(String.valueOf(ruleEntity.getCode()), ruleId, FileUtil.XML_FILE_TYPE);
			if(FileUtil.isExist(path_)){
				FileUtil.deleteFile(path_);
			}else
				return false;
		}
		return true;
	}
	
	private List<ElevateResult> getElevatedResultFromFile(String filePath){
		XStream xstream = null;
		List<ElevateResult> list = null;
		try {
			if(FileUtil.isExist(filePath.toString())){
				
				Object object = FileUtil.fileStream(filePath.toString());
				xstream = new XStream();
				xstream.alias("elevate", ElevateResult.class);
				xstream.alias("list", List.class);
				list = (List<ElevateResult>)xstream.fromXML(object.toString());
				if(list != null && list.size() > 0)
					return list;
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.EMPTY_LIST;
	}
	
	private List<ExcludeResult> getExcludeResultFromFile(String filePath){
		XStream xstream = null;
		List<ExcludeResult> list = null;
		try {
			if(FileUtil.isExist(filePath.toString())){
				
				Object object = FileUtil.fileStream(filePath.toString());
				xstream = new XStream();
				xstream.alias("exclude", ExcludeResult.class);
				xstream.alias("list", List.class);
				list = (List<ExcludeResult>)xstream.fromXML(object.toString());
				if(list != null && list.size() > 0)
					return list;
			}
		} catch (Exception e) {
			logger.error(e,e);
		}
		return Collections.EMPTY_LIST;
	}
	
	private Object getXmlObjectForElevatedRule(String storeName, String ruleId){
		
		ElevateResult elevateFilter = new ElevateResult();
		List<ElevateResult> elevatedList = null;
		
		try{
			StoreKeyword sk = new StoreKeyword(storeName, ruleId);
			elevateFilter.setStoreKeyword(sk); 
			SearchCriteria<ElevateResult> criteria = new SearchCriteria<ElevateResult>(elevateFilter,null,null,0,0);
			
			elevatedList = daoService.getElevateResultList(criteria).getList();	
			
			if(CollectionUtils.isNotEmpty(elevatedList)){
				XStream xstream = new XStream();
				xstream.alias("elevate", ElevateResult.class);
				xstream.alias("list", List.class);

			    String xml = xstream.toXML(elevatedList);
			    return xml;
			}
		}catch (Exception e) {
			logger.error(e,e);
		} 
		return null;	
	}
	
	private Object getRankingRuleResultFromFile(String storeName, String ruleId, String ruleType){
		// TODO: fix
		Map<String,Object> bck = new HashMap<String,Object>();
		String file = getFilePath(ruleType, ruleId, FileUtil.XML_FILE_TYPE);
		String keywords =  getFilePath(ruleType, ruleId, "keywords", FileUtil.XML_FILE_TYPE);
		
		XStream xstream = null;
		Relevancy relevancy = null;
		List<RelevancyKeyword> kwList = null;
		
		try {
			if(FileUtil.isExist(file)){
				
				Object object = FileUtil.fileStream(file);
				xstream = new XStream();
				xstream.alias("relevancy", Relevancy.class);
			
				relevancy = (Relevancy)xstream.fromXML(object.toString());
				if(relevancy != null && StringUtils.isNotEmpty(relevancy.getRelevancyId())){
					bck.put("relevancy", relevancy);
					
					if(FileUtil.isExist(keywords)){
						object = FileUtil.fileStream(keywords);
						xstream = new XStream();
						xstream.alias("relevancyKeyword", RelevancyKeyword.class);
						xstream.alias("list", List.class);
						
						kwList = (List<RelevancyKeyword>)xstream.fromXML(object.toString());
						
						if(CollectionUtils.isNotEmpty(kwList))
							bck.put("keywords", kwList);
					}
				}
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
		return bck;
	}
	
	private boolean createBackupForRankingRule(String storeName, String ruleId, String ruleType){
		
		boolean success = false;
		
		try{
			List<RelevancyKeyword> relKWList = null;
			Relevancy relevancy = new Relevancy();
			relevancy.setRelevancyId(ruleId);
			relevancy.setStore(new Store(storeName));
			relevancy = daoService.getRelevancyDetails(relevancy);
			
			if(relevancy != null){
				XStream xstream = new XStream();
				xstream.alias("relevancy", Relevancy.class);
			    String xml = xstream.toXML(relevancy);
			    FileUtil.fileStream(xml, getFileDirectory(ruleType, ruleId), ruleId+FileUtil.XML_FILE_TYPE);  
	
			    relKWList = daoService.getRelevancyKeywords(relevancy).getList();
			    
			    if(CollectionUtils.isNotEmpty(relKWList)){
			    	xstream = new XStream();
					xstream.alias("relevancyKeyword", RelevancyKeyword.class);
					xstream.alias("list", List.class);
				    xml = xstream.toXML(relKWList);
				    FileUtil.fileStream(xml, getFileDirectory(ruleType, ruleId, "keywords"), ruleId+FileUtil.XML_FILE_TYPE);
			    }

			    success = true;
			}
		} catch (Exception e) {
			logger.error(e,e);
		} 
		return success;	
	}
	
	private Object getRedirectRuleResultFromFile(String storeName, String ruleId, String ruleType){
		// TODO: fix
		Map<String,Object> bck = new HashMap<String,Object>();
		String file = getFilePath(ruleType, ruleId, FileUtil.XML_FILE_TYPE);
		String keywords = getFilePath(ruleType, ruleId, "keywords", FileUtil.XML_FILE_TYPE);
		
		XStream xstream = null;
		RedirectRule redirectRule = null;
		
		try {
			if(FileUtil.isExist(file)){
				
				Object object = FileUtil.fileStream(file);
				xstream = new XStream();
				xstream.alias("redirect", RedirectRule.class);
			
				redirectRule = (RedirectRule)xstream.fromXML(object.toString());
				if(redirectRule != null && StringUtils.isNotEmpty(redirectRule.getRuleId())){
					bck.put("redirect", redirectRule);
				}
			}
		}catch (Exception e) {
			logger.error(e,e);
		}
		return bck;
	}
	
	private boolean createBackupForRedirectRule(String storeName, String ruleId, String ruleType) {

		boolean success = false;

		try {
			List<RedirectRule> relKWList = null;
			RedirectRule redirectRule = new RedirectRule();
			redirectRule.setRuleId(ruleId);
			redirectRule.setStoreId(storeName);
			redirectRule = daoService.getRedirectRule(redirectRule);

			if (redirectRule != null) {
				XStream xstream = new XStream();
				xstream.alias("redirect", RedirectRule.class);
				String xml = xstream.toXML(redirectRule);
				FileUtil.fileStream(xml, getFileDirectory(ruleType, ruleId),
						ruleId + FileUtil.XML_FILE_TYPE);
				success = true;
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return success;
	}
	
	private Object getXmlObjectForExcludeRule(String storeName, String ruleId){
		
		ExcludeResult excludeFilter = new ExcludeResult();
		List<ExcludeResult> excludeList = null;
		
		try{
			StoreKeyword sk = new StoreKeyword(storeName, ruleId);
			excludeFilter.setStoreKeyword(sk); 
			SearchCriteria<ExcludeResult> criteria = new SearchCriteria<ExcludeResult>(excludeFilter,null,null,0,0);
			
			excludeList = daoService.getExcludeResultList(criteria).getList();	
			
			if(CollectionUtils.isNotEmpty(excludeList)){
				XStream xstream = new XStream();
				xstream.alias("exclude", ExcludeResult.class);
				xstream.alias("list", List.class);

			    String xml = xstream.toXML(excludeList);
			    return xml;
			}
		}catch (Exception e) {
			logger.error(e,e);
		} 
		return null;	
	}
	
	private List<Object> convertToObjectList(List<? extends Object> list){
		
		List<Object> objList = new ArrayList<Object>();
		
		for(Object obj : list){
			objList.add(obj);
		}

		return objList;
	}
	
	private String getFilePath(String type ,String file, String xtn){
		StringBuilder filePath = new StringBuilder(getFileDirectory(type, file)).append(File.separator).append(file+xtn);
		return filePath.toString();
	}
	
	private String getFilePath(String type ,String file, String child, String xtn){
		StringBuilder filePath = new StringBuilder(getFileDirectory(type, file, child)).append(File.separator).append(file+xtn);
		return filePath.toString();
	}
	
	private String getFileDirectory(String type ,String file){
		StringBuilder dir = new StringBuilder();
		dir.append(path).append(File.separator).append(type).append(File.separator).append(file);
		return dir.toString();
	}
	
	private String getFileDirectory(String type ,String file, String child){
		StringBuilder dir = new StringBuilder();
		dir.append(path).append(File.separator).append(type).append(File.separator).append(file).append(File.separator).append(child);
		return dir.toString();
	}
}
