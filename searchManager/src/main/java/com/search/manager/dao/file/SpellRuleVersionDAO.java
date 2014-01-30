package com.search.manager.dao.file;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.DBRuleVersion;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.service.UtilityService;

@Repository("spellRuleVersionDAO")
@SuppressWarnings("unchecked")
public class SpellRuleVersionDAO implements IRuleVersionDAO<SpellRules> {

	private static Logger logger = LoggerFactory.getLogger(SpellRuleVersionDAO.class);

	private static final String MAX_SUGGEST = "maxSuggest";

	private static final RuleEntity entity = RuleEntity.SPELL;

	@Autowired
	private DaoService daoService;
	@Autowired
	private RuleVersionUtil ruleVersionUtil;
	@Autowired
	private UtilityService utilityService;
	
	@Autowired
    @Qualifier("ruleStatusServiceSp")
    private RuleStatusService ruleStatusService;
	
	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

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
		RuleVersionListXml<DBRuleVersion> ruleVersions = ruleVersionUtil.getRuleVersionList(store, entity, ruleId);
        if (ruleVersions != null) {
            try {
                long nextVersion = ruleVersions.getNextVersion();

                if (daoService.addSpellRuleVersion(store, (int) nextVersion)) {
                    DBRuleVersion version = new DBRuleVersion(store, nextVersion, name, notes, username, new DateTime(),
                            ruleId, RuleEntity.SPELL);
                    version.getProps().put(MAX_SUGGEST, String.valueOf(daoService.getMaxSuggest(store)));
                    ruleVersions.getVersions().add(version);

                    return ruleVersionUtil.addRuleVersion(store, RuleEntity.SPELL, ruleId, ruleVersions);
                }
            } catch (DaoException e) {
                logger.error("Unable to create new version.", e);
            }
        }

        return false;
	}

	@Override
	public boolean createPublishedRuleVersion(String storeId, String ruleId, String username, String name, String notes) {
		  RuleVersionListXml<DBRuleVersion> ruleVersions = ruleVersionUtil.getPublishedList(storeId, entity, ruleId);

	        if (ruleVersions != null) {
	            try {
	                long nextVersion = ruleVersions.getNextVersion();

	                DBRuleVersion version = new DBRuleVersion(storeId, nextVersion, name, notes, username, new DateTime(),
	                        ruleId, RuleEntity.SPELL);
	                
	                RuleStatus ruleStatus;
                    try {
                        ruleStatus = ruleStatusService.getRuleStatus(storeId, RuleEntity.getValue(entity.getCode()), ruleId);
                        version.getProps().put(MAX_SUGGEST, String.valueOf(daoService.getMaxSuggest(storeId)));
                        version.setRuleStatus(ruleStatus);
                        ruleVersions.getVersions().add(version);

                        return ruleVersionUtil.addPublishedVersion(storeId, entity, ruleId, ruleVersions);
                    } catch (CoreServiceException e) {
                        logger.error("Error getting rule status. ", e);
                    }
	            } catch (DaoException e) {
	                logger.error("Unable to create new published version.", e);
	            }
	        }

	        return false;
	}

	@Override
	public boolean restoreRuleVersion(RuleXml xml) {
		String username = utilityService.getUsername();
		DBRuleVersion version = (DBRuleVersion) xml;

		try {
			daoService.restoreSpellRules(version.getStore(), (int) version.getVersion(), username);
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
				RuleVersionListXml<DBRuleVersion> ruleVersions = ruleVersionUtil.getRuleVersionList(store, entity,
				        ruleId);

				for (DBRuleVersion ruleVersion : ruleVersions.getVersions()) {
					if (ruleVersion.getVersion() == version) {
						ruleVersion.setDeleted(true);
						ruleVersion.setLastModifiedBy(username);
						ruleVersion.setLastModifiedDate(new DateTime());
						break;
					}
				}

				return ruleVersionUtil.saveRuleVersionList(store, entity, ruleId, ruleVersions,
				        RuleVersionUtil.BACKUP_PATH);
			}
		} catch (DaoException e) {
			logger.error("Unable to delete spell rule version.", e);
		}

		return false;
	}

	@Override
	public List<RuleXml> getPublishedRuleVersions(String store, String ruleId) {
		return ruleVersionUtil.getPublishedList(store, entity, ruleId).getVersions();
	}

	@Override
	public List<RuleXml> getRuleVersions(String store, String ruleId) {
		return ruleVersionUtil.getRuleVersionList(store, entity, ruleId).getVersions();
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
