package com.search.manager.dao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.file.ElevateVersionDAO;
import com.search.manager.dao.file.ExcludeVersionDAO;
import com.search.manager.dao.file.QueryCleaningVersionDAO;
import com.search.manager.dao.file.RankingRuleVersionDAO;
import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BackupInfo;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.utility.StringUtil;

@Service("fileService")
public class FileDaoServiceImpl implements FileDaoService{
	
	private Logger logger = Logger.getLogger(FileDaoServiceImpl.class);
	
	@Autowired private ElevateVersionDAO elevateVersionDAO;
	@Autowired private ExcludeVersionDAO excludeVersionDAO;
	@Autowired private QueryCleaningVersionDAO queryCleaningVersionDAO;
	@Autowired private RankingRuleVersionDAO rankingRuleVersionDAO;
	
	@Override
	public boolean createBackup(String store, String ruleId, RuleEntity ruleEntity, String userName, String reason) throws Exception {
		
		boolean success = false;
		
		switch (ruleEntity) {
		case ELEVATE:
			success = elevateVersionDAO.createElevateRuleVersion(store, ruleId, reason);
			break;
		case EXCLUDE:
			success = excludeVersionDAO.createExcludeRuleVersion(store, ruleId, reason);
			break;
		case QUERY_CLEANING:
			success = queryCleaningVersionDAO.createQueryCleaningRuleVersion(store, ruleId, reason);
			break;
		case RANKING_RULE:
			success = rankingRuleVersionDAO.createRankingRuleVersion(store, ruleId, reason);
			break;
		default:
			break;
		}
		return success;
	}
	
	
	@Override
	public List<ElevateProduct> readElevateRuleVersion(String store, String ruleId, int version, String server) {
		String filePath = RuleVersionUtil.getFileName(store, RuleEntity.ELEVATE.getCode(), StringUtil.escapeKeyword(ruleId), version);
		return elevateVersionDAO.readElevatedVersion(filePath, store, server);
	}
	
	@Override
	public List<Product> readExcludeRuleVersion(String store, String ruleId, int version, String server) {
		String filePath = RuleVersionUtil.getFileName(store, RuleEntity.EXCLUDE.getCode(), StringUtil.escapeKeyword(ruleId), version);
		return excludeVersionDAO.readExcludeRuleVersion(filePath, store, server);
	}

	@Override
	public RedirectRule readQueryCleaningRuleVersion(String store, String ruleId, int version) {
		return queryCleaningVersionDAO.readQueryCleaningVersion(store, ruleId, version);
	}
	
	@Override
	public Relevancy readRankingRuleVersion(String store, String ruleId, int version) {
		return rankingRuleVersionDAO.readRankingRuleVersion(store, ruleId, version);
	}
	
	@Override
	public boolean restoreRuleVersion(String store, String ruleId, int version, RuleEntity ruleEntity) {
		boolean success = false;
		
		switch (ruleEntity) {
		case ELEVATE:
			break;
		case EXCLUDE:
			break;
		case QUERY_CLEANING:
			break;
		case RANKING_RULE:
			success = rankingRuleVersionDAO.restoreRankingRuleVersion(store, ruleId, version);
			break;
		default:
			break;
		}
		return success;
	}
	
	@Override
	public List<BackupInfo> getBackupInfo(String store, String ruleType, String ruleId) {
		
		File[] files = RuleVersionUtil.getBackupInfo(store, RuleEntity.find(ruleType).getCode(), ruleId);
		
		List<BackupInfo> backupList = new ArrayList<BackupInfo>();
		if (files !=null && files.length > 0) {
			for(File file : files){
				BackupInfo backup = new BackupInfo();
				backup.setRuleId(ruleId);
				backup.setDateCreated(new Date(file.lastModified()));
				Matcher matcher = RuleVersionUtil.PATTERN.matcher(file.getName());
		        if(matcher.find()){
		        	backup.setVersion(Integer.valueOf(matcher.group(1)));
		        }
		        String reason = null;
				switch (RuleEntity.find(ruleType)) {
				case ELEVATE:
					reason = elevateVersionDAO.readElevatedVersion(file);
					break;
				case EXCLUDE:
					reason = excludeVersionDAO.readExcludeRuleVersion(file);
					break;
				case QUERY_CLEANING:
					reason = queryCleaningVersionDAO.readQueryCleaningVersion(file);
					break;
				case RANKING_RULE:
					reason = rankingRuleVersionDAO.readRankingRuleVersion(file);
					break;
				default:
					break;
				}
		        
		        backup.setReason(reason);
				backupList.add(backup);
			}

		}

		return backupList;
	}


	@Override
	public boolean deleteBackup(String storeName, String ruleId, RuleEntity find, int version) {
		boolean success = false;
		try {
			RuleVersionUtil.deleteFile(RuleVersionUtil.getFileName(storeName, find.getCode(), ruleId, version));
			success = true;
		} catch (IOException e) {
			logger.equals(e.getMessage());
		}
		return success;
	}
	

}
