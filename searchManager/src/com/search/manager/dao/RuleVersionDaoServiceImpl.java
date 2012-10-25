package com.search.manager.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.file.DemoteVersionDAO;
import com.search.manager.dao.file.ElevateVersionDAO;
import com.search.manager.dao.file.ExcludeVersionDAO;
import com.search.manager.dao.file.FacetSortVersionDAO;
import com.search.manager.dao.file.QueryCleaningVersionDAO;
import com.search.manager.dao.file.RankingRuleVersionDAO;
import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleVersionInfo;

@Service("fileService")
public class RuleVersionDaoServiceImpl implements RuleVersionDaoService{

	private Logger logger = Logger.getLogger(RuleVersionDaoServiceImpl.class);

	@Autowired private ElevateVersionDAO elevateVersionDAO;
	@Autowired private ExcludeVersionDAO excludeVersionDAO;
	@Autowired private DemoteVersionDAO demoteVersionDAO;
	@Autowired private FacetSortVersionDAO facetSortVersionDAO;
	@Autowired private QueryCleaningVersionDAO queryCleaningVersionDAO;
	@Autowired private RankingRuleVersionDAO rankingRuleVersionDAO;

	@Override
	public boolean createRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String reason){
		switch (ruleEntity) {
		case ELEVATE:
			return elevateVersionDAO.createRuleVersion(store, ruleId, username, name, reason);
		case EXCLUDE:
			return excludeVersionDAO.createRuleVersion(store, ruleId, username, name, reason);
		case DEMOTE:
			return demoteVersionDAO.createRuleVersion(store, ruleId, username, name, reason);
		case FACET_SORT:
			return facetSortVersionDAO.createRuleVersion(store, ruleId, username, name, reason);
		case QUERY_CLEANING:
			return queryCleaningVersionDAO.createRuleVersion(store, ruleId, username, name, reason);
		case RANKING_RULE:
			return rankingRuleVersionDAO.createRuleVersion(store, ruleId, username, name, reason);
		}
		return false;
	}

	@Override
	public boolean deleteRuleVersion(String store, RuleEntity entity, String ruleId, String username, int version){
		boolean success = false;
		try {
			RuleVersionUtil.deleteRuleVersionFile(store, entity, ruleId);
			success = true;
		} catch (IOException e) {
			logger.equals(e.getMessage());
		} catch (Exception e) {
			logger.equals(e.getMessage());
		}
		return success;
	}

	@Override
	public boolean restoreRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, int version) {
		
		switch (ruleEntity) {
		case ELEVATE:
			return elevateVersionDAO.restoreRuleVersion(store, ruleId, username, version);
			
		case EXCLUDE:
			return excludeVersionDAO.restoreRuleVersion(store, ruleId, username, version);
			
		case DEMOTE:
			return demoteVersionDAO.restoreRuleVersion(store, ruleId, username, version);
			
		case FACET_SORT:
			return facetSortVersionDAO.restoreRuleVersion(store, ruleId, username, version);
			
		case QUERY_CLEANING:
			return queryCleaningVersionDAO.restoreRuleVersion(store, ruleId, username, version);
			
		case RANKING_RULE:
			return rankingRuleVersionDAO.restoreRuleVersion(store, ruleId, username, version);
		}
		return false;
	}

	@Override
	public List<RuleVersionInfo> getRuleVersions(String store, String ruleType, String ruleId) {
		List<RuleVersionInfo> versionList = new ArrayList<RuleVersionInfo>();

		switch (RuleEntity.find(ruleType)) {
		case ELEVATE:
			versionList = elevateVersionDAO.getRuleVersions(store, ruleId);
			break;
		case EXCLUDE:
			versionList = excludeVersionDAO.getRuleVersions(store, ruleId);
			break;
		case DEMOTE:
			versionList = demoteVersionDAO.getRuleVersions(store, ruleId);
			break;
		case FACET_SORT:
			versionList = facetSortVersionDAO.getRuleVersions(store, ruleId);
			break;
		case QUERY_CLEANING:
			versionList = queryCleaningVersionDAO.getRuleVersions(store, ruleId);
			break;
		case RANKING_RULE:
			versionList = rankingRuleVersionDAO.getRuleVersions(store, ruleId);
			break;
		}

		return versionList;
	}
}