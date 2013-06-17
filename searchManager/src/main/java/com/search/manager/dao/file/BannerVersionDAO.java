package com.search.manager.dao.file;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.SearchCriteria;
import com.search.manager.report.model.xml.BannerRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.utility.Transformers;

@Component("bannerVersionDAO")
public class BannerVersionDAO extends RuleVersionDAO<BannerRuleXml> {

    private static final Logger logger = LoggerFactory.getLogger(BannerVersionDAO.class);

    @Autowired
    private DaoService daoService;

    @Override
    protected RuleEntity getRuleEntity() {
        return RuleEntity.BANNER;
    }

    @Override
    protected boolean addLatestVersion(RuleVersionListXml<?> ruleVersionListXml, String store, String ruleId,
            String username, String name, String notes, boolean isVersion) {
        if (ruleVersionListXml != null) {
            @SuppressWarnings("unchecked")
            List<BannerRuleXml> ruleXmlList = ((RuleVersionListXml<BannerRuleXml>) ruleVersionListXml).getVersions();
            long version = ruleVersionListXml.getNextVersion();

            try {
                // Get all items
                SearchCriteria<BannerRuleItem> criteria = new SearchCriteria<BannerRuleItem>(new BannerRuleItem(ruleId,
                        store));

                BannerRule rule = daoService.getBannerRuleById(store, ruleId);
                List<BannerRuleItem> ruleItems = daoService.searchBannerRuleItem(criteria).getList();

                ruleXmlList.add(new BannerRuleXml(store, ruleId, rule.getRuleName(), name, notes, username, Lists
                        .transform(ruleItems, Transformers.bannerItemRuleToXml), version));
                ruleVersionListXml.setRuleId(ruleId);
                ruleVersionListXml.setRuleName(rule.getRuleName());

                return true;
            } catch (DaoException e) {
                logger.error("Error occurred in addLatestVersion.", e);
            }
        }
        return false;
    }

}
