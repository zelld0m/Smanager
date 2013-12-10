package com.search.manager.solr.service.internal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.enums.RuleType;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetSort;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.SpellRule;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.dao.BannerRuleItemDao;
import com.search.manager.solr.dao.DemoteDao;
import com.search.manager.solr.dao.ElevateDao;
import com.search.manager.solr.dao.ExcludeDao;
import com.search.manager.solr.dao.FacetSortDao;
import com.search.manager.solr.dao.RedirectDao;
import com.search.manager.solr.dao.RelevancyDao;
import com.search.manager.solr.dao.SpellRuleDao;
import com.search.manager.solr.service.SolrService;
import com.search.ws.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("solrService")
public class SolrServiceImpl implements SolrService {

    @Autowired
    @Qualifier("demoteDaoSolr")
    private DemoteDao demoteDao;
    @Autowired
    @Qualifier("elevateDaoSolr")
    private ElevateDao elevateDao;
    @Autowired
    @Qualifier("excludeDaoSolr")
    private ExcludeDao excludeDao;
    @Autowired
    @Qualifier("redirectDaoSolr")
    private RedirectDao redirectDao;
    @Autowired
    @Qualifier("relevancyDaoSolr")
    private RelevancyDao relevancyDao;
    @Autowired
    @Qualifier("facetSortDaoSolr")
    private FacetSortDao facetSortDao;
    @Autowired
    @Qualifier("spellRuleDaoSolr")
    private SpellRuleDao spellRuleDao;
    @Autowired
    @Qualifier("bannerRuleItemDaoSolr")
    private BannerRuleItemDao bannerRuleItemDao;
    @Autowired
    private ConfigManager configManager;
    
    private static final Logger logger =
            LoggerFactory.getLogger(SolrServiceImpl.class);

    @Override
    public List<ElevateResult> getElevateRules(Store store) throws DaoException {
        return (List<ElevateResult>) elevateDao.getElevateRules(store);
    }

    @Override
    public List<ElevateResult> getElevateRules(StoreKeyword storeKeyword)
            throws DaoException {
        return (List<ElevateResult>) elevateDao.getElevateRules(storeKeyword);
    }

    @Override
    public boolean loadElevateRules(Store store) throws DaoException {
        return elevateDao.loadElevateRules(store);
    }

    @Override
    public boolean loadElevateRules(StoreKeyword storeKeyword)
            throws DaoException {
        return elevateDao.loadElevateRules(storeKeyword);
    }

    @Override
    public boolean resetElevateRules(Store store) throws DaoException {
        return elevateDao.resetElevateRules(store);
    }

    @Override
    public boolean resetElevateRules(StoreKeyword storeKeyword)
            throws DaoException {
        return elevateDao.resetElevateRules(storeKeyword);
    }

    @Override
    public boolean deleteElevateRules(Store store) throws DaoException {
        return elevateDao.deleteElevateRules(store);
    }

    @Override
    public boolean deleteElevateRules(StoreKeyword storeKeyword)
            throws DaoException {
        return elevateDao.deleteElevateRules(storeKeyword);
    }

    @Override
    public boolean updateElevateRule(ElevateResult elevateResult)
            throws DaoException {
        return elevateDao.updateElevateRule(elevateResult);
    }

    @Override
    public List<ExcludeResult> getExcludeRules(Store store) throws DaoException {
        return (List<ExcludeResult>) excludeDao.getExcludeRules(store);
    }

    @Override
    public List<ExcludeResult> getExcludeRules(StoreKeyword storeKeyword)
            throws DaoException {
        return (List<ExcludeResult>) excludeDao.getExcludeRules(storeKeyword);
    }

    @Override
    public boolean loadExcludeRules(Store store) throws DaoException {
        return excludeDao.loadExcludeRules(store);
    }

    @Override
    public boolean loadExcludeRules(StoreKeyword storeKeyword)
            throws DaoException {
        return excludeDao.loadExcludeRules(storeKeyword);
    }

    @Override
    public boolean resetExcludeRules(Store store) throws DaoException {
        return excludeDao.resetExcludeRules(store);
    }

    @Override
    public boolean resetExcludeRules(StoreKeyword storeKeyword)
            throws DaoException {
        return excludeDao.resetExcludeRules(storeKeyword);
    }

    @Override
    public boolean deleteExcludeRules(Store store) throws DaoException {
        return excludeDao.deleteExcludeRules(store);
    }

    @Override
    public boolean deleteExcludeRules(StoreKeyword storeKeyword)
            throws DaoException {
        return excludeDao.deleteExcludeRules(storeKeyword);
    }

    @Override
    public boolean updateExcludeRule(ExcludeResult excludeResult)
            throws DaoException {
        return excludeDao.updateExcludeRule(excludeResult);
    }

    @Override
    public List<DemoteResult> getDemoteRules(Store store) throws DaoException {
        return (List<DemoteResult>) demoteDao.getDemoteRules(store);
    }

    @Override
    public List<DemoteResult> getDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        return (List<DemoteResult>) demoteDao.getDemoteRules(storeKeyword);
    }

    @Override
    public boolean loadDemoteRules(Store store) throws DaoException {
        return demoteDao.loadDemoteRules(store);
    }

    @Override
    public boolean loadDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        return demoteDao.loadDemoteRules(storeKeyword);
    }

    @Override
    public boolean resetDemoteRules(Store store) throws DaoException {
        return demoteDao.resetDemoteRules(store);
    }

    @Override
    public boolean resetDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        return demoteDao.resetDemoteRules(storeKeyword);
    }

    @Override
    public boolean deleteDemoteRules(Store store) throws DaoException {
        return demoteDao.deleteDemoteRules(store);
    }

    @Override
    public boolean deleteDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        return demoteDao.deleteDemoteRules(storeKeyword);
    }

    @Override
    public boolean updateDemoteRule(DemoteResult demoteResult)
            throws DaoException {
        return demoteDao.updateDemoteRule(demoteResult);
    }

    @Override
    public List<FacetSort> getFacetSortRules(Store store) throws DaoException {
        return (List<FacetSort>) facetSortDao.getFacetSortRules(store);
    }

    @Override
    public FacetSort getFacetSortRule(Store store, String name,
            RuleType ruleType) throws DaoException {
        return facetSortDao.getFacetSortRule(store, name, ruleType);
    }

    @Override
    public FacetSort getFacetSortRuleById(Store store, String id)
            throws DaoException {
        return facetSortDao.getFacetSortRuleById(store, id);
    }

    @Override
    public boolean loadFacetSortRules(Store store) throws DaoException {
        return facetSortDao.loadFacetSortRules(store);
    }

    @Override
    public boolean loadFacetSortRuleByName(Store store, String name,
            RuleType ruleType) throws DaoException {
        return facetSortDao.loadFacetSortRuleByName(store, name, ruleType);
    }

    @Override
    public boolean loadFacetSortRuleById(Store store, String id)
            throws DaoException {
        return facetSortDao.loadFacetSortRuleById(store, id);
    }

    @Override
    public boolean resetFacetSortRules(Store store) throws DaoException {
        return facetSortDao.resetFacetSortRules(store);
    }

    @Override
    public boolean resetFacetSortRuleByName(Store store, String name,
            RuleType ruleType) throws DaoException {
        return facetSortDao.resetFacetSortRulesByName(store, name, ruleType);
    }

    @Override
    public boolean resetFacetSortRuleById(Store store, String id)
            throws DaoException {
        return facetSortDao.resetFacetSortRulesById(store, id);
    }

    @Override
    public boolean deleteFacetSortRules(Store store) throws DaoException {
        return facetSortDao.deleteFacetSortRules(store);
    }

    @Override
    public boolean deleteFacetSortRuleByName(Store store, String name,
            RuleType ruleType) throws DaoException {
        return facetSortDao.deleteFacetSortRuleByName(store, name, ruleType);
    }

    @Override
    public boolean deleteFacetSortRuleById(Store store, String id)
            throws DaoException {
        return facetSortDao.deleteFacetSortRuleById(store, id);
    }

    @Override
    public boolean updateFacetSortRule(FacetSort facetSort) throws DaoException {
        return facetSortDao.updateFacetSortRule(facetSort);
    }

    @Override
    public List<RedirectRule> getRedirectRules(Store store) throws DaoException {
        return (List<RedirectRule>) redirectDao.getRedirectRules(store);
    }

    @Override
    public RedirectRule getRedirectRule(StoreKeyword storeKeyword)
            throws DaoException {
        return redirectDao.getRedirectRule(storeKeyword);
    }

    @Override
    public RedirectRule getRedirectRuleByName(Store store, String name)
            throws DaoException {
        return redirectDao.getRedirectRuleByName(store, name);
    }

    @Override
    public RedirectRule getRedirectRuleById(Store store, String id)
            throws DaoException {
        return redirectDao.getRedirectRuleById(store, id);
    }

    @Override
    public boolean loadRedirectRules(Store store) throws DaoException {
        return redirectDao.loadRedirectRules(store);
    }

    @Override
    public boolean loadRedirectRuleByName(Store store, String name)
            throws DaoException {
        return redirectDao.loadRedirectRuleByName(store, name);
    }

    @Override
    public boolean loadRedirectRuleById(Store store, String id)
            throws DaoException {
        return redirectDao.loadRedirectRuleById(store, id);
    }

    @Override
    public boolean resetRedirectRules(Store store) throws DaoException {
        return redirectDao.resetRedirectRules(store);
    }

    @Override
    public boolean resetRedirectRuleByName(Store store, String name)
            throws DaoException {
        return redirectDao.resetRedirectRuleByName(store, name);
    }

    @Override
    public boolean resetRedirectRuleById(Store store, String id)
            throws DaoException {
        return redirectDao.resetRedirectRuleById(store, id);
    }

    @Override
    public boolean deleteRedirectRules(Store store) throws DaoException {
        return redirectDao.deleteRedirectRules(store);
    }

    @Override
    public boolean deleteRedirectRuleByName(Store store, String name)
            throws DaoException {
        return redirectDao.deleteRedirectRuleByName(store, name);
    }

    @Override
    public boolean deleteRedirectRuleById(Store store, String id)
            throws DaoException {
        return redirectDao.deleteRedirectRuleById(store, id);
    }

    @Override
    public boolean updateRedirectRule(RedirectRule redirectRule)
            throws DaoException {
        return redirectDao.updateRedirectRule(redirectRule);
    }

    @Override
    public List<Relevancy> getRelevancyRules(Store store) throws DaoException {
        return (List<Relevancy>) relevancyDao.getRelevancyRules(store);
    }

    @Override
    public Relevancy getDefaultRelevancyRule(Store store) throws DaoException {
        return relevancyDao.getDefaultRelevancyRule(store);
    }

    @Override
    public Relevancy getRelevancyRule(StoreKeyword storeKeyword)
            throws DaoException {
        return relevancyDao.getRelevancyRule(storeKeyword);
    }

    @Override
    public Relevancy getRelevancyRuleByName(Store store, String name)
            throws DaoException {
        return relevancyDao.getRelevancyRuleByName(store, name);
    }

    @Override
    public Relevancy getRelevancyRuleById(Store store, String id)
            throws DaoException {
        return relevancyDao.getRelevancyRuleById(store, id);
    }

    @Override
    public boolean loadRelevancyRules(Store store) throws DaoException {
        return relevancyDao.loadRelevancyRules(store);
    }

    @Override
    public boolean loadRelevancyRuleByName(Store store, String name)
            throws DaoException {
        return relevancyDao.loadRelevancyRuleByName(store, name);
    }

    @Override
    public boolean loadRelevancyRuleById(Store store, String id)
            throws DaoException {
        return relevancyDao.loadRelevancyRuleById(store, id);
    }

    @Override
    public boolean resetRelevancyRules(Store store) throws DaoException {
        return relevancyDao.resetRelevancyRules(store);
    }

    @Override
    public boolean resetRelevancyRuleByName(Store store, String name)
            throws DaoException {
        return relevancyDao.resetRelevancyRuleByName(store, name);
    }

    @Override
    public boolean resetRelevancyRuleById(Store store, String id)
            throws DaoException {
        return relevancyDao.resetRelevancyRuleById(store, id);
    }

    @Override
    public boolean deleteRelevancyRules(Store store) throws DaoException {
        return relevancyDao.deleteRelevancyRules(store);
    }

    @Override
    public boolean deleteRelevancyRuleByName(Store store, String name)
            throws DaoException {
        return relevancyDao.deleteRelevancyRuleByName(store, name);
    }

    @Override
    public boolean deleteRelevancyRuleById(Store store, String id)
            throws DaoException {
        return relevancyDao.deleteRelevancyRuleById(store, id);
    }

    @Override
    public boolean updateRelevancyRule(Relevancy relevancy) throws DaoException {
        return relevancyDao.updateRelevancyRule(relevancy);
    }

    @Override
    public Map<String, Boolean> resetElevateRules(Store store,
            Collection<String> keywords) throws DaoException {
        return elevateDao.resetElevateRules(store, keywords);
    }

    @Override
    public Map<String, Boolean> resetExcludeRules(Store store,
            Collection<String> keywords) throws DaoException {
        return excludeDao.resetExcludeRules(store, keywords);
    }

    @Override
    public Map<String, Boolean> resetDemoteRules(Store store,
            Collection<String> keywords) throws DaoException {
        return demoteDao.resetDemoteRules(store, keywords);
    }

    @Override
    public Map<String, Boolean> resetFacetSortRuleById(Store store,
            Collection<String> ids) throws DaoException {
        return facetSortDao.resetFacetSortRulesById(store, ids);
    }

    @Override
    public Map<String, Boolean> resetRedirectRulesById(Store store,
            Collection<String> ids) throws DaoException {
        return redirectDao.resetRedirectRulesById(store, ids);
    }

    @Override
    public Map<String, Boolean> resetRelevancyRulesById(Store store,
            Collection<String> ids) throws DaoException {
        return redirectDao.resetRedirectRulesById(store, ids);
    }

    @Override
    public boolean commitElevateRule() throws DaoException {
        return elevateDao.commitElevateRule();
    }

    @Override
    public boolean commitExcludeRule() throws DaoException {
        return excludeDao.commitExcludeRule();
    }

    @Override
    public boolean commitDemoteRule() throws DaoException {
        return demoteDao.commitDemoteRule();
    }

    @Override
    public boolean commitFacetSortRule() throws DaoException {
        return facetSortDao.commitFacetSortRule();
    }

    @Override
    public boolean commitRedirectRule() throws DaoException {
        return redirectDao.commitRedirectRule();
    }

    @Override
    public boolean commitRelevancyRule() throws DaoException {
        return relevancyDao.commitRelevancyRule();
    }

    @Override
    public List<ElevateResult> getExpiredElevateRules(StoreKeyword storeKeyword)
            throws DaoException {
        return (List<ElevateResult>) elevateDao
                .getExpiredElevateRules(storeKeyword);
    }

    @Override
    public List<ExcludeResult> getExpiredExcludeRules(StoreKeyword storeKeyword)
            throws DaoException {
        return (List<ExcludeResult>) excludeDao
                .getExpiredExcludeRules(storeKeyword);
    }

    @Override
    public List<DemoteResult> getExpiredDemoteRules(StoreKeyword storeKeyword)
            throws DaoException {
        return (List<DemoteResult>) demoteDao
                .getExpiredDemoteRules(storeKeyword);
    }

    @Override
    public FacetSort getFacetSortRule(StoreKeyword storeKeyword)
            throws DaoException {
        return getFacetSortRule(storeKeyword.getStore(),
                storeKeyword.getKeywordId(), RuleType.KEYWORD);
    }

    @Override
    public FacetSort getFacetSortRule(Store store, String templateName)
            throws DaoException {
        return getFacetSortRule(store, templateName, RuleType.TEMPLATE);
    }

    @Override
    public Relevancy getRelevancyRule(Store store, String relevancyId)
            throws DaoException {
        return getRelevancyRuleById(store, relevancyId);
    }

    // @Override
    public SpellRule getSpellRuleForSearchTermx(String storeId,
            String searchTerm) throws DaoException {
        // TODO: place in utility. check DaoCacheServiceImpl
        SpellRule spellRule = null;
        String os = System.getProperty("os.name");
        String fileName = configManager.getPublishedDidYouMeanPath(storeId);
        String rule = null;
        if (StringUtils.containsIgnoreCase(os, "window")) {
            // assume this is dev workstation
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(fileName));
                while ((rule = reader.readLine()) != null) {
                    if (StringUtils.containsIgnoreCase(rule, "\t" + searchTerm
                            + "\t")) {
                        // found a match
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("Error occured while readig file " + fileName, e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                    }
                }
                ;
            }
        } else {
            try {
                String[] shellCommand = {
                    "/bin/sh",
                    "-c",
                    String.format("grep -Pi \"\\\t%s\\\t\" \"%s\"",
                    searchTerm, fileName)};
                Process p = Runtime.getRuntime().exec(shellCommand);
                try {
                    p.waitFor();
                    if (logger.isDebugEnabled()) {
                        StringBuilder command = new StringBuilder();
                        for (String arg : shellCommand) {
                            command.append(arg).append(" ");
                        }
                        logger.debug("Shell command: " + command.toString());
                    }
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(
                                p.getInputStream()));
                        rule = reader.readLine();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (Exception e) {
                            }
                        }
                        ;
                    }
                } catch (InterruptedException e) {
                    logger.error("Error occured while readig file " + fileName,
                            e);
                }
            } catch (IOException e) {
                logger.error("Error occured while readig file " + fileName, e);
            }
        }
        if (StringUtils.isEmpty(rule)) {
            logger.debug("No matching rule found.");
        } else {
            int keywordPos = rule.indexOf('\t');
            int spellingPos = rule.indexOf((char) 0x0B);
            String ruleId = rule.substring(0, keywordPos);
            String[] searchTerms = StringUtils.split(
                    rule.substring(keywordPos, spellingPos), "\t");
            String[] suggestions = StringUtils.split(
                    rule.substring(spellingPos), (char) 0x0B);
            spellRule = new SpellRule(ruleId, storeId, null, searchTerms,
                    suggestions);
        }
        return spellRule;
    }

    @Override
    public Integer getMaxSuggest(String storeId) throws DaoException {
        Integer value = null;
        try {
            String val = configManager.getPublishedStoreLinguisticSetting(storeId,
                    "maxSpellSuggestions");
            value = Integer.parseInt(val);
        } catch (Exception e) {
        }
        return value;
    }

    @Override
    public SpellRule getSpellRuleForSearchTerm(String storeId, String searchTerm)
            throws DaoException {
        return spellRuleDao.getSpellRuleForSearchTerm(storeId, searchTerm);
    }

    @Override
    public boolean loadSpellRules(Store store) throws DaoException {
        return spellRuleDao.loadSpellRules(store);
    }

    @Override
    public boolean loadSpellRules(StoreKeyword storeKeyword)
            throws DaoException {
        return spellRuleDao.loadSpellRules(storeKeyword);
    }

    @Override
    public boolean loadSpellRuleById(Store store, String ruleId)
            throws DaoException {
        return spellRuleDao.loadSpellRuleById(store, ruleId);
    }

    @Override
    public boolean loadSpellRules(Store store, String dirPath, String fileName)
            throws DaoException {
        return spellRuleDao.loadSpellRules(store, dirPath, fileName);
    }

    @Override
    public boolean resetSpellRules(Store store) throws DaoException {
        return spellRuleDao.resetSpellRules(store);
    }

    @Override
    public boolean resetSpellRules(StoreKeyword storeKeyword)
            throws DaoException {
        return spellRuleDao.resetSpellRules(storeKeyword);
    }

    @Override
    public boolean resetSpellRuleById(Store store, String ruleId)
            throws DaoException {
        return spellRuleDao.resetSpellRuleById(store, ruleId);
    }

    @Override
    public boolean deleteSpellRules(Store store) throws DaoException {
        return spellRuleDao.deleteSpellRules(store);
    }

    @Override
    public boolean deleteSpellRules(StoreKeyword storeKeyword)
            throws DaoException {
        return spellRuleDao.deleteSpellRules(storeKeyword);
    }

    @Override
    public boolean deleteSpellRuleById(Store store, String ruleId)
            throws DaoException {
        return spellRuleDao.deleteSpellRuleById(store, ruleId);
    }

    @Override
    public boolean updateSpellRule(SpellRule spellRule) throws DaoException {
        return spellRuleDao.updateSpellRule(spellRule);
    }

    @Override
    public boolean commitSpellRule() throws DaoException {
        return spellRuleDao.commitSpellRule();
    }

    @Override
    public List<BannerRuleItem> getBannerRuleItems(Store store)
            throws DaoException {
        return (List<BannerRuleItem>) bannerRuleItemDao
                .getBannerRuleItems(store);
    }

    @Override
    public List<BannerRuleItem> getBannerRuleItemsByRuleId(Store store,
            String ruleId) throws DaoException {
        return (List<BannerRuleItem>) bannerRuleItemDao
                .getBannerRuleItemsByRuleId(store, ruleId);
    }

    @Override
    public List<BannerRuleItem> getBannerRuleItemsByRuleName(Store store,
            String ruleName) throws DaoException {
        return (List<BannerRuleItem>) bannerRuleItemDao
                .getBannerRuleItemsByRuleName(store, ruleName);
    }

    @Override
    public BannerRuleItem getBannerRuleItemByMemberId(Store store,
            String memberId) throws DaoException {
        return bannerRuleItemDao.getBannerRuleItemByMemberId(store, memberId);
    }

    @Override
    public boolean loadBannerRuleItems(Store store) throws DaoException {
        return bannerRuleItemDao.loadBannerRuleItems(store);
    }

    @Override
    public boolean loadBannerRuleItemsByRuleId(Store store, String ruleId)
            throws DaoException {
        return bannerRuleItemDao.loadBannerRuleItemsByRuleId(store, ruleId);
    }

    @Override
    public boolean loadBannerRuleItemsByRuleName(Store store, String ruleName)
            throws DaoException {
        return bannerRuleItemDao.loadBannerRuleItemsByRuleName(store, ruleName);
    }

    @Override
    public boolean loadBannerRuleItemByMemberId(Store store, String memberId)
            throws DaoException {
        return bannerRuleItemDao.loadBannerRuleItemByMemberId(store, memberId);
    }

    @Override
    public boolean resetBannerRuleItems(Store store) throws DaoException {
        return bannerRuleItemDao.resetBannerRuleItems(store);
    }

    @Override
    public boolean resetBannerRuleItemsByRuleId(Store store, String ruleId)
            throws DaoException {
        return bannerRuleItemDao.resetBannerRuleItemsByRuleId(store, ruleId);
    }

    @Override
    public boolean resetBannerRuleItemsByRuleName(Store store, String ruleName)
            throws DaoException {
        return bannerRuleItemDao
                .resetBannerRuleItemsByRuleName(store, ruleName);
    }

    @Override
    public boolean resetBannerRuleItemByMemberId(Store store, String memberId)
            throws DaoException {
        return bannerRuleItemDao.resetBannerRuleItemByMemberId(store, memberId);
    }

    @Override
    public boolean deleteBannerRuleItems(Store store) throws DaoException {
        return bannerRuleItemDao.deleteBannerRuleItems(store);
    }

    @Override
    public boolean deleteBannerRuleItemsByRuleId(Store store, String ruleId)
            throws DaoException {
        return bannerRuleItemDao.deleteBannerRuleItemsByRuleId(store, ruleId);
    }

    @Override
    public boolean deleteBannerRuleItemsByRuleName(Store store, String ruleName)
            throws DaoException {
        return bannerRuleItemDao.deleteBannerRuleItemsByRuleName(store,
                ruleName);
    }

    @Override
    public boolean deleteBannerRuleItemByMemberId(Store store, String memberId)
            throws DaoException {
        return bannerRuleItemDao
                .deleteBannerRuleItemByMemberId(store, memberId);
    }

    @Override
    public boolean updateBannerRuleItem(BannerRuleItem bannerRuleItem)
            throws DaoException {
        return bannerRuleItemDao.updateBannerRuleItem(bannerRuleItem);
    }

    @Override
    public boolean commitBannerRuleItem() throws DaoException {
        return bannerRuleItemDao.commitBannerRuleItem();
    }

    @Override
    public List<BannerRuleItem> getActiveBannerRuleItems(Store store,
            String keyword, DateTime currentDate) throws DaoException {
        return (List<BannerRuleItem>) bannerRuleItemDao
                .getActiveBannerRuleItemsByRuleName(store, keyword,
                currentDate, currentDate);
    }
}
