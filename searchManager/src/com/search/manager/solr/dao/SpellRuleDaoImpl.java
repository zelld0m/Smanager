package com.search.manager.solr.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.report.model.xml.RuleFileXml;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.solr.constants.Constants;
import com.search.manager.solr.model.SpellRuleSolr;
import com.search.manager.solr.util.SolrDocUtil;
import com.search.manager.solr.util.SolrResultUtil;
import com.search.manager.utility.PropsUtils;
import com.search.manager.xml.file.RuleXmlUtil;

@Repository("spellRuleDaoSolr")
public class SpellRuleDaoImpl extends BaseDaoSolr implements SpellRuleDao {

	private static final Logger logger = Logger
			.getLogger(SpellRuleDaoImpl.class);

	@Autowired
	private RuleXmlUtil ruleXmlUtil;
	private static final String BASE_RULE_DIR = PropsUtils
			.getValue("publishedfilepath");
	private static final String SPELL_FILE = PropsUtils.getValue("spellfile");

	@Override
	public SpellRule getSpellRuleForSearchTerm(String storeId, String searchTerm)
			throws DaoException {
		List<SpellRule> spellRules = null;

		try {
			storeId = StringUtils.lowerCase(StringUtils.trim(storeId));
			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			searchTerm = StringUtils.lowerCase(StringUtils.trim(searchTerm));
			strQuery.append(" AND keywords1:"
					+ ClientUtils.escapeQueryChars(StringUtils.trim(searchTerm)));

			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setQuery(strQuery.toString());
			logger.debug(solrQuery.toString());
			QueryResponse queryResponse = null;

			queryResponse = solrServers.getCoreInstance(
					Constants.Core.SPELL_RULE_CORE.getCoreName()).query(
					solrQuery);

			if (queryResponse != null) {
				spellRules = SolrResultUtil.toSpellRule(queryResponse
						.getBeans(SpellRuleSolr.class));

				if (spellRules != null && spellRules.size() > 0) {
					return spellRules.get(0);
				}
			}

		} catch (Exception e) {
			logger.error("Failed to get spell rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public boolean loadSpellRules(Store store) throws DaoException {
		RuleFileXml xml = new RuleFileXml();
		String dirPath = new StringBuilder().append(BASE_RULE_DIR)
				.append(File.separator).append(store.getStoreId())
				.append(File.separator).append("Did You Mean")
				.append(File.separator).append(SPELL_FILE).toString();
		xml.setPath(dirPath);

		@SuppressWarnings("static-access")
		SpellRules spellRules = (SpellRules) ruleXmlUtil.loadVersion(xml);
		List<SpellRuleXml> spellRulesXml = null;

		if (spellRules != null) {
			spellRulesXml = spellRules.getSpellRule();

			if (spellRulesXml != null && spellRulesXml.size() > 0) {
				List<SolrInputDocument> solrInputDocuments = null;
				boolean hasError = false;

				try {
					solrInputDocuments = SolrDocUtil.composeSolrDocsSpell(
							spellRulesXml, spellRules.getStore());
				} catch (Exception e) {
					hasError = true;
					logger.error("Failed to load spell rules by store", e);
				}

				if (!hasError && solrInputDocuments != null
						&& solrInputDocuments.size() > 0) {
					try {
						solrServers.getCoreInstance(
								Constants.Core.SPELL_RULE_CORE.getCoreName())
								.addDocs(solrInputDocuments);
						solrServers.getCoreInstance(
								Constants.Core.SPELL_RULE_CORE.getCoreName())
								.softCommit();
					} catch (Exception e) {
						logger.error("Failed to load spell rules by store", e);
						throw new DaoException(e.getMessage(), e);
					}
				}
				return !hasError;
			}
		}

		return false;
	}

	@Override
	public boolean loadSpellRules(StoreKeyword storeKeyword)
			throws DaoException {

		String store = StringUtils.lowerCase(StringUtils.trim(storeKeyword
				.getStoreId()));
		String keyword = StringUtils.lowerCase(StringUtils.trim(storeKeyword
				.getKeywordId()));

		RuleFileXml xml = new RuleFileXml();
		String dirPath = new StringBuilder().append(BASE_RULE_DIR)
				.append(File.separator).append(store).append(File.separator)
				.append("Did You Mean").append(File.separator)
				.append(SPELL_FILE).toString();
		xml.setPath(dirPath);

		@SuppressWarnings("static-access")
		SpellRules spellRules = (SpellRules) ruleXmlUtil.loadVersion(xml);
		List<SpellRuleXml> spellRulesXml = null;

		if (spellRules != null) {
			spellRulesXml = new ArrayList<SpellRuleXml>();
			for (int i = 0; i < spellRules.getSpellRule().size(); i++) {
				SpellRuleXml temp = spellRules.getSpellRule().get(i);
				for (String key : temp.getRuleKeyword()) {
					if (keyword.equalsIgnoreCase(StringUtils.trim(key))) {
						spellRulesXml.add(temp);
						break;
					}
				}
			}
		}

		if (spellRulesXml != null && spellRulesXml.size() > 0) {
			List<SolrInputDocument> solrInputDocuments = null;
			boolean hasError = false;

			try {
				solrInputDocuments = SolrDocUtil.composeSolrDocsSpell(
						spellRulesXml, spellRules.getStore());
			} catch (Exception e) {
				hasError = true;
				logger.error("Failed to load spell rules by store", e);
			}

			if (!hasError && solrInputDocuments != null
					&& solrInputDocuments.size() > 0) {
				try {
					solrServers.getCoreInstance(
							Constants.Core.SPELL_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.SPELL_RULE_CORE.getCoreName())
							.softCommit();
				} catch (Exception e) {
					logger.error("Failed to load spell rules by store", e);
					throw new DaoException(e.getMessage(), e);
				}
			}
			return !hasError;
		}

		return false;
	}

	@Override
	public boolean loadSpellRuleById(Store store, String ruleId)
			throws DaoException {
		String storeId = StringUtils.lowerCase(StringUtils.trim(store
				.getStoreId()));
		ruleId = StringUtils.trim(ruleId);

		RuleFileXml xml = new RuleFileXml();
		String dirPath = new StringBuilder().append(BASE_RULE_DIR)
				.append(File.separator).append(storeId).append(File.separator)
				.append("Did You Mean").append(File.separator)
				.append(SPELL_FILE).toString();
		xml.setPath(dirPath);

		@SuppressWarnings("static-access")
		SpellRules spellRules = (SpellRules) ruleXmlUtil.loadVersion(xml);
		List<SpellRuleXml> spellRulesXml = null;

		if (spellRules != null) {
			spellRulesXml = new ArrayList<SpellRuleXml>();
			for (int i = 0; i < spellRules.getSpellRule().size(); i++) {
				SpellRuleXml temp = spellRules.getSpellRule().get(i);
				if (ruleId.equals(temp.getRuleId())) {
					spellRulesXml.add(temp);
					break;
				}
			}
		}

		if (spellRulesXml != null && spellRulesXml.size() > 0) {
			List<SolrInputDocument> solrInputDocuments = null;
			boolean hasError = false;

			try {
				solrInputDocuments = SolrDocUtil.composeSolrDocsSpell(
						spellRulesXml, spellRules.getStore());
			} catch (Exception e) {
				hasError = true;
				logger.error("Failed to load spell rule by id", e);
			}

			if (!hasError && solrInputDocuments != null
					&& solrInputDocuments.size() > 0) {
				try {
					solrServers.getCoreInstance(
							Constants.Core.SPELL_RULE_CORE.getCoreName())
							.addDocs(solrInputDocuments);
					solrServers.getCoreInstance(
							Constants.Core.SPELL_RULE_CORE.getCoreName())
							.softCommit();
				} catch (Exception e) {
					logger.error("Failed to load spell rule by id", e);
					throw new DaoException(e.getMessage(), e);
				}
			}
			return !hasError;
		}

		return false;
	}

	@Override
	public boolean loadSpellRules(Store store, String dirPath, String fileName)
			throws DaoException {
		RuleFileXml xml = new RuleFileXml();
		xml.setPath(new StringBuilder().append(dirPath).append(File.separator)
				.append(fileName).toString());

		@SuppressWarnings("static-access")
		SpellRules spellRules = (SpellRules) ruleXmlUtil.loadVersion(xml);
		List<SpellRuleXml> spellRulesXml = null;

		if (spellRules != null) {
			spellRulesXml = spellRules.getSpellRule();

			if (spellRulesXml != null && spellRulesXml.size() > 0) {
				List<SolrInputDocument> solrInputDocuments = null;
				boolean hasError = false;

				try {
					solrInputDocuments = SolrDocUtil.composeSolrDocsSpell(
							spellRulesXml, spellRules.getStore());
				} catch (Exception e) {
					hasError = true;
					logger.error("Failed to load spell rules", e);
				}

				if (!hasError && solrInputDocuments != null
						&& solrInputDocuments.size() > 0) {
					try {
						solrServers.getCoreInstance(
								Constants.Core.SPELL_RULE_CORE.getCoreName())
								.addDocs(solrInputDocuments);
						solrServers.getCoreInstance(
								Constants.Core.SPELL_RULE_CORE.getCoreName())
								.softCommit();
					} catch (Exception e) {
						logger.error("Failed to load spell rules", e);
						throw new DaoException(e.getMessage(), e);
					}
				}
				return !hasError;
			}
		}

		return false;
	}

	@Override
	public boolean resetSpellRules(Store store) throws DaoException {
		try {
			if (deleteSpellRules(store)) {
				return loadSpellRules(store);
			}
		} catch (Exception e) {
			logger.error("Failed to reset spell rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetSpellRules(StoreKeyword storeKeyword)
			throws DaoException {
		try {
			if (deleteSpellRules(storeKeyword)) {
				return loadSpellRules(storeKeyword);
			}
		} catch (Exception e) {
			logger.error("Failed to reset spell rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean resetSpellRuleById(Store store, String ruleId)
			throws DaoException {
		try {
			if (deleteSpellRuleById(store, ruleId)) {
				return loadSpellRuleById(store, ruleId);
			}
		} catch (Exception e) {
			logger.error("Failed to reset spell rule by id", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteSpellRules(Store store) throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));

			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.SPELL_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete spell rules by store", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteSpellRules(StoreKeyword storeKeyword)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getStoreId()));

			String keyword = StringUtils.lowerCase(StringUtils
					.trim(storeKeyword.getKeywordId()));

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));
			strQuery.append(" AND keywords1:"
					+ ClientUtils.escapeQueryChars(keyword));
			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.SPELL_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete spell rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean deleteSpellRuleById(Store store, String ruleId)
			throws DaoException {
		try {
			String storeId = StringUtils.lowerCase(StringUtils.trim(store
					.getStoreId()));

			ruleId = StringUtils.trim(ruleId);

			StringBuffer strQuery = new StringBuffer();
			strQuery.append("store:" + ClientUtils.escapeQueryChars(storeId));
			strQuery.append(" AND ruleId:"
					+ ClientUtils.escapeQueryChars(ruleId));
			UpdateResponse updateResponse = solrServers.getCoreInstance(
					Constants.Core.SPELL_RULE_CORE.getCoreName())
					.deleteByQuery(strQuery.toString());
			if (updateResponse.getStatus() == 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Failed to delete spell rule by id", e);
			throw new DaoException(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateSpellRule(SpellRule spellRule) throws DaoException {
		if (spellRule == null) {
			return false;
		}

		try {
			List<SpellRule> spellRules = new ArrayList<SpellRule>();
			spellRules.add(spellRule);

			List<SolrInputDocument> solrInputDocuments = SolrDocUtil
					.composeSolrDocs(spellRules);
			solrServers.getCoreInstance(
					Constants.Core.SPELL_RULE_CORE.getCoreName()).addDocs(
					solrInputDocuments);
			solrServers.getCoreInstance(
					Constants.Core.SPELL_RULE_CORE.getCoreName()).softCommit();
		} catch (Exception e) {
			logger.error("Failed to update spell rules by storeKeyword", e);
			throw new DaoException(e.getMessage(), e);
		}

		return true;
	}

	@Override
	public boolean commitSpellRule() throws DaoException {
		try {
			return commit(solrServers
					.getCoreInstance(Constants.Core.SPELL_RULE_CORE
							.getCoreName()));
		} catch (SolrServerException e) {
			logger.error("Failed to commit spell rules", e);
			return false;
		}
	}

}