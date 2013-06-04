package com.search.manager.dao.file;

import java.util.Date;
import java.util.List;

import org.apache.http.impl.cookie.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SpellRule;
import com.search.manager.report.model.xml.RuleFileXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRules;

@Repository("spellRuleVersionDAO")
public class SpellRuleVersionDAO extends RuleVersionDAO<SpellRules> {

    private static Logger logger = Logger.getLogger(SpellRuleVersionDAO.class);

    @Autowired
    private DaoService daoService;

    @Override
    protected RuleEntity getRuleEntity() {
        return RuleEntity.SPELL;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId,
            String username, String name, String notes, boolean isVersion) {
        if (ruleVersionListXml != null) {
            try {
                List<RuleXml> xmlList = ((RuleVersionListXml<RuleXml>) ruleVersionListXml).getVersions();
                List<SpellRule> spellRuleList = daoService.getSpellRule(
                        new SearchCriteria<SpellRule>(new SpellRule(null, store), 1, 1)).getList();
                long nextVersion = ruleVersionListXml.getNextVersion();
                Date now = new Date();

                if (spellRuleList != null && spellRuleList.size() > 0) {
                    if (isVersion) {
                        RuleFileXml fileXml = new RuleFileXml(store, nextVersion, name, notes, username, now, ruleId,
                                RuleEntity.SPELL, null);

                        daoService.addSpellRuleVersion(store, (int) nextVersion);
                        fileXml.getProps().put("maxSuggest", String.valueOf(daoService.getMaxSuggest(store)));
                        fileXml.setStoredInDB(true);
                        xmlList.add(fileXml);
                    } else {
                        daoService.publishSpellRules(store);
                        SpellRules version = new SpellRules(store, nextVersion, name, notes, username, now, ruleId,
                                daoService.getMaxSuggest(store), Lists.transform(
                                        daoService.getSpellRuleVersion(store, 0), SpellRule.transformer));

                        RuleFileXml fileXml = new RuleFileXml(store, nextVersion, name, notes, username, now, ruleId,
                                RuleEntity.SPELL, version);

                        fileXml.getProps().put("maxSuggest", String.valueOf(daoService.getMaxSuggest(store)));
                        fileXml.setStoredInDB(true);
                        fileXml.setContentFileName(ruleId + DateUtils.formatDate(new Date(), "_yyyyMMdd_hhmmss"));
                        xmlList.add(fileXml);
                    }

                    return true;
                }
            } catch (DaoException e) {
                logger.error("Error occurred on addLatestVersion.", e);
            }
        }
        return false;
    }
    
    @Override
    protected boolean deleteDatabaseVersion(String store, String ruleId, int versionNo) {
        try {
            return daoService.deleteSpellRuleVersion(store, versionNo);
        } catch (DaoException e) {
            logger.error("Error occured on deleteDatabaseVersion.", e);
            return false;
        }
    }
}
