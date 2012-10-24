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
import com.search.manager.model.DemoteProduct;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.FacetSort;
import com.search.manager.model.Product;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RuleVersionInfo;
import com.search.manager.utility.StringUtil;

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
	public boolean createRuleVersion(String store, RuleEntity ruleEntity, String ruleId, String username, String name, String reason) throws Exception {

		boolean success = false;

		switch (ruleEntity) {
		case ELEVATE:
			success = elevateVersionDAO.createElevateRuleVersion(store, ruleId, username, name, reason);
			break;
		case EXCLUDE:
			success = excludeVersionDAO.createExcludeRuleVersion(store, ruleId, username, name, reason);
			break;
		case DEMOTE:
			success = demoteVersionDAO.createDemoteRuleVersion(store, ruleId, username, name, reason);
			break;
		case FACET_SORT:
			success = facetSortVersionDAO.createFacetSortRuleVersion(store, ruleId, username, name, reason);
			break;
		case QUERY_CLEANING:
			success = queryCleaningVersionDAO.createQueryCleaningRuleVersion(store, ruleId, username, name, reason);
			break;
		case RANKING_RULE:
			success = rankingRuleVersionDAO.createRankingRuleVersion(store, ruleId, username, name, reason);
			break;
		default:
			break;
		}
		return success;
	}

	@Override
	public boolean deleteRuleVersion(String store, RuleEntity entity, String ruleId, int version) throws Exception{
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
	public boolean restoreRuleVersion(String store, RuleEntity ruleEntity, String ruleId, int version) {
		boolean success = false;

		switch (ruleEntity) {
		case ELEVATE:
			break;
		case EXCLUDE:
			break;
		case DEMOTE:
			break;
		case FACET_SORT:
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
	public List<ElevateProduct> readElevateRuleVersion(String store, String ruleId, int version, String server) {
		String filePath = RuleVersionUtil.getFileName(store, RuleEntity.ELEVATE, StringUtil.escapeKeyword(ruleId));
		return elevateVersionDAO.readElevatedVersion(filePath, store, server);
	}

	@Override
	public List<Product> readExcludeRuleVersion(String store, String ruleId, int version, String server) {
		String filePath = RuleVersionUtil.getFileName(store, RuleEntity.EXCLUDE, StringUtil.escapeKeyword(ruleId));
		return excludeVersionDAO.readExcludeRuleVersion(filePath, store, server);
	}

	@Override
	public List<DemoteProduct> readDemoteRuleVersion(String store, String ruleId, int version, String server) {
		String filePath = RuleVersionUtil.getFileName(store, RuleEntity.DEMOTE, StringUtil.escapeKeyword(ruleId));
		return demoteVersionDAO.readDemotedVersion(filePath, store, server);
	}

	@Override
	public List<FacetSort> readFacetSortRuleVersion(String store, String ruleId, int version, String server) {
		String filePath = RuleVersionUtil.getFileName(store, RuleEntity.FACET_SORT, StringUtil.escapeKeyword(ruleId));
		return facetSortVersionDAO.readFacetSortVersion(filePath, store, server);
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
	public List<RuleVersionInfo> getRuleVersionList(String store, String ruleType, String ruleId) {
		List<RuleVersionInfo> versionList = new ArrayList<RuleVersionInfo>();


		return versionList;
	}
}