package com.search.manager.dao.file;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.DBRuleVersion;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRules;

@Repository("spellRuleVersionDAO")
public class SpellRuleVersionDAO implements IRuleVersionDAO<SpellRules> {

	private static Logger logger = Logger.getLogger(SpellRuleVersionDAO.class);

	private static final String MAX_SUGGEST = "maxSuggest";

	private static final RuleEntity entity = RuleEntity.SPELL;

	@Autowired
	private DaoService daoService;

	protected boolean deleteDatabaseVersion(String store, String ruleId, int versionNo) {
		try {
			return daoService.deleteSpellRuleVersion(store, versionNo);
		} catch (DaoException e) {
			logger.error("Error occured on deleteDatabaseVersion.", e);
			return false;
		}
	}

	@Override
	public boolean createRuleVersion(String store, String ruleId, String username, String name, String notes) {
		RuleVersionListXml<DBRuleVersion> ruleVersions = RuleVersionUtil.getRuleVersionList(store, entity, ruleId);
		if (ruleVersions != null) {
			try {
				long nextVersion = ruleVersions.getNextVersion();

				if (daoService.addSpellRuleVersion(store, (int) nextVersion)) {
					DBRuleVersion version = new DBRuleVersion(store, nextVersion, name, notes, username, new Date(),
					        ruleId, RuleEntity.SPELL);
					version.getProps().put(MAX_SUGGEST, String.valueOf(daoService.getMaxSuggest(store)));
					ruleVersions.getVersions().add(version);

					return RuleVersionUtil.addRuleVersion(store, RuleEntity.SPELL, ruleId, ruleVersions);
				}
			} catch (DaoException e) {
				logger.error("Unable to create new version.", e);
			}
		}

		return false;
	}

	@Override
	public boolean createPublishedRuleVersion(String store, String ruleId, String username, String name, String notes) {
		RuleVersionListXml<DBRuleVersion> ruleVersions = RuleVersionUtil.getPublishedList(store, entity, ruleId);

		if (ruleVersions != null) {
			try {
				long nextVersion = ruleVersions.getNextVersion();

				DBRuleVersion version = new DBRuleVersion(store, nextVersion, name, notes, username, new Date(),
				        ruleId, RuleEntity.SPELL);
				version.getProps().put(MAX_SUGGEST, String.valueOf(daoService.getMaxSuggest(store)));
				ruleVersions.getVersions().add(version);

				return RuleVersionUtil.addPublishedVersion(store, entity, ruleId, ruleVersions);
			} catch (DaoException e) {
				logger.error("Unable to create new published version.", e);
			}
		}

		return false;
	}

	@Override
	public boolean restoreRuleVersion(RuleXml xml) {
		DBRuleVersion version = (DBRuleVersion) xml;

		try {
			daoService.restoreSpellRules(version.getStore(), (int) version.getVersion());
			daoService.setMaxSuggest(version.getStore(), Integer.parseInt(version.getProps().get(MAX_SUGGEST)));
			return true;
		} catch (DaoException e) {
			logger.error("Unable to restore version.", e);
		}

		return false;
	}

	@Override
	public boolean deleteRuleVersion(String store, String ruleId, String username, long version) {
		try {
			if (daoService.deleteSpellRuleVersion(store, (int) version)) {
				RuleVersionListXml<DBRuleVersion> ruleVersions = RuleVersionUtil.getRuleVersionList(store, entity,
				        ruleId);

				for (DBRuleVersion ruleVersion : ruleVersions.getVersions()) {
					if (ruleVersion.getVersion() == version) {
						ruleVersion.setDeleted(true);
						ruleVersion.setLastModifiedBy(username);
						ruleVersion.setLastModifiedDate(new Date());
						break;
					}
				}

				return RuleVersionUtil.saveRuleVersionList(store, entity, ruleId, ruleVersions,
				        RuleVersionUtil.BACKUP_PATH);
			}
		} catch (DaoException e) {
			logger.error("Unable to delete spell rule version.", e);
		}

		return false;
	}

	@Override
	public List<RuleXml> getPublishedRuleVersions(String store, String ruleId) {
		return RuleVersionUtil.getPublishedList(store, entity, ruleId).getVersions();
	}

	@Override
	public List<RuleXml> getRuleVersions(String store, String ruleId) {
		return RuleVersionUtil.getRuleVersionList(store, entity, ruleId).getVersions();
	}

	@Override
	public int getRuleVersionsCount(String store, String ruleId) {
		int count = 0;
		List<RuleXml> xmls = getRuleVersions(store, ruleId);

		for (RuleXml xml : xmls) {
			if (!xml.isDeleted()) {
				count++;
			}
		}

		return count;
	}
}
