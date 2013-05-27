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
                List<SpellRule> spellRuleList = daoService.getSpellRules(store, null);
                long nextVersion = ruleVersionListXml.getNextVersion();
                Date now = new Date();

                if (spellRuleList != null) {
                    // create version for current rule
                    SpellRules version = new SpellRules(store, nextVersion, name, notes, username, now, ruleId,
                            daoService.getMaxSuggest(store), Lists.transform(spellRuleList, SpellRule.transformer));
                    RuleFileXml fileXml = new RuleFileXml(store, nextVersion, name, notes, username, now, ruleId,
                            RuleEntity.SPELL, version);

                    if (isVersion) {
                        fileXml.setContentFileName(ruleId + "-" + nextVersion);
                    } else {
                        fileXml.setContentFileName(ruleId + DateUtils.formatDate(new Date(), "_yyyyMMdd_hhmmss"));
                    }

                    xmlList.add(fileXml);

                    return true;
                }
            } catch (DaoException e) {
                logger.error("Error occurred on addLatestVersion.", e);
            }
        }
        return false;
    }
}
