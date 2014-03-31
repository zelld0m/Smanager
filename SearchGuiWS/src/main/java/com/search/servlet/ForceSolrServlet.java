package com.search.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.search.manager.core.model.Store;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.sp.DAOValidation;
import com.search.manager.enums.RuleType;
import com.search.manager.model.StoreKeyword;
import com.search.manager.solr.service.SolrService;
import com.search.service.solr.BannerRuleItemMigratorService;
import com.search.service.solr.TypeaheadRuleMigratorService;

/**
 * Servlet implementation class ForceSolrServlet
 */
@Component
public class ForceSolrServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Autowired
    private SolrService solrService;

    @Autowired
    private BannerRuleItemMigratorService bannerRuleItemMigratorService;

    @Autowired
    private TypeaheadRuleMigratorService typeaheadRuleMigratorService;

    @Override
    public void init() {
        WebApplicationContextUtils.getWebApplicationContext(super.getServletContext()).getAutowireCapableBeanFactory()
                .autowireBean(this);
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ForceSolrServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String strStore = request.getParameter("store");
        String action = request.getParameter("action");
        String rule = request.getParameter("rule");
        String keyword = request.getParameter("keyword");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String type = request.getParameter("ruleType");
        Store store = null;
        String msg = "";
        long timeStart = System.currentTimeMillis();

        out.println("START...");

        if (StringUtils.isNotEmpty(strStore)) {
            store = new Store(strStore);
            try {
                DAOValidation.checkStoreId(store);
            } catch (DaoException e) {
                store = null;
            }
        }

        if (store != null && StringUtils.isNotEmpty(action) && StringUtils.isNotEmpty(rule)) {

            action = StringUtils.trim(action);
            rule = StringUtils.lowerCase(StringUtils.trim(rule));

            if (action.equalsIgnoreCase("loadByStore")) {
                out.println("Calling method: loadBy(Store)");
                msg = loadByStore(rule, store);
            } else if (action.equalsIgnoreCase("loadByStoreKeyword")) {
                out.println("Calling method: loadByStoreKeyword(StoreKeyword)");
                try {
                    StoreKeyword storeKeyword = new StoreKeyword(strStore, keyword);
                    DAOValidation.checkStoreKeywordPK(storeKeyword);
                    msg = loadByStoreKeyword(rule, storeKeyword);
                } catch (DaoException e) {
                    msg = "Invalid paramter!";
                    msg += "\nError: Invalid 'store' or 'keyword' parameter.";
                }
            } else if (action.equalsIgnoreCase("loadByName")) {
                out.println("Calling method: loadByName(Store, RuleName)");
                if (StringUtils.isNotEmpty(name)) {
                    msg = loadByName(rule, store, name, type);
                } else {
                    msg = "Invalid paramter!";
                    msg += "\nError: You need to specify 'name=' parameter.";
                }
            } else if (action.equalsIgnoreCase("loadById")) {
                out.println("Calling method: loadById(Store, RuleId)");
                if (StringUtils.isNotEmpty(id)) {
                    msg = loadById(rule, store, id);
                } else {
                    msg = "Invalid paramter!";
                    msg += "\nError: You need to specify 'id=' parameter.";
                }
            } else if (action.equalsIgnoreCase("resetByStore")) {
                out.println("Calling method: resetByStore(Store)");
                msg = resetByStore(rule, store);
            } else if (action.equalsIgnoreCase("resetByStoreKeyword")) {
                out.println("Calling method: reset(StoreKeyword)");
                try {
                    StoreKeyword storeKeyword = new StoreKeyword(strStore, keyword);
                    DAOValidation.checkStoreKeywordPK(storeKeyword);
                    msg = resetByStoreKeyword(rule, storeKeyword);
                    msg += "\n" + commit(rule, store);
                } catch (DaoException e) {
                    msg = "Invalid paramter!";
                    msg += "\nError: Invalid 'store' or 'keyword' parameter.";
                }
            } else if (action.equalsIgnoreCase("resetById")) {
                out.println("Calling method: resetById(Store, RuleId)");
                if (StringUtils.isNotEmpty(id)) {
                    msg = resetById(rule, store, id);
                } else {
                    msg = "Invalid paramter!";
                    msg += "\nError: You need to specify 'id=' parameter.";
                }
            } else if (action.equalsIgnoreCase("resetByName")) {
                out.println("Calling method: resetByName(Store, RuleName)");
                if (StringUtils.isNotEmpty(name)) {
                    msg = resetByName(rule, store, name, type);
                } else {
                    msg = "Invalid paramter!";
                    msg += "\nError: You need to specify 'name=' parameter.";
                }
            } else if (action.equalsIgnoreCase("deleteByStore")) {
                out.println("Calling method: deleteByStore(Store)");
                msg = deleteByStore(rule, store);
            } else if (action.equalsIgnoreCase("deleteByStoreKeyword")) {
                out.println("Calling method: delete(StoreKeyword)");
                try {
                    StoreKeyword storeKeyword = new StoreKeyword(strStore, keyword);
                    DAOValidation.checkStoreKeywordPK(storeKeyword);
                    msg = deleteByStoreKeyword(rule, storeKeyword);
                    msg += "\n" + commit(rule, store);
                } catch (DaoException e) {
                    msg = "Invalid paramter!";
                }
            } else if (action.equalsIgnoreCase("deleteById")) {
                out.println("Calling method: deleteById(Store, RuleId)");
                if (StringUtils.isNotEmpty(id)) {
                    msg = deleteById(rule, store, id);
                } else {
                    msg = "Invalid paramter!";
                    msg += "\nError: You need to specify 'id=' parameter.";
                }
            } else if (action.equalsIgnoreCase("deleteByName")) {
                out.println("Calling method: deleteByName(Store, RuleName)");
                if (StringUtils.isNotEmpty(name)) {
                    msg = deleteByName(rule, store, name, type);
                } else {
                    msg = "Invalid paramter!";
                    msg += "\nError: You need to specify 'name=' parameter.";
                }
            } else if (action.equalsIgnoreCase("commit")) {
                out.println("Calling method: commit()");
                msg = commit(rule, store);
            } else {
                msg = "Invalid paramter!";
                if (StringUtils.isNotEmpty(action)) {
                    msg += "\nError: Invalid 'action=' value.";
                } else {
                    msg += "\nError: You need to specify 'action=' parameter.";
                }
            }

            long elapsedTimeMillis = System.currentTimeMillis() - timeStart;

            out.println(msg);
            out.println("Done in: " + (elapsedTimeMillis / (1000F)) + " secs./" + (elapsedTimeMillis / (60 * 1000F))
                    + " mins.");
        } else {
            out.println("Invalid paramter!");
        }

        out.println("\nEND");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // do nothing...
    }

    /* LOAD */
    private String loadByStore(String rule, Store store) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.loadDemoteRules(store);
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.loadElevateRules(store);
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.loadExcludeRules(store);
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.loadFacetSortRules(store);
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.loadRedirectRules(store);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.loadRelevancyRules(store);
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.loadSpellRules(store);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.loadByStoreId(store.getStoreId());
            } else if (rule.equals("typeahead")) {
                status = "" + typeaheadRuleMigratorService.loadByStoreId(store.getStoreId());
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error loading " + rule.toUpperCase() + " rules for store[" + store.getStoreId() + "].";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rules successfully loaded: store[" + store.getStoreId() + "].";
        } else {
            msg = "Unable to load " + rule.toUpperCase() + " rules for store[" + store.getStoreId() + "].";
        }

        return msg;
    }

    private String loadByStoreKeyword(String rule, StoreKeyword storeKeyword) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.loadDemoteRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.loadElevateRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.loadExcludeRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.loadSpellRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "unimplemented";
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error loading " + rule.toUpperCase() + " rules for store[" + storeKeyword.getStoreId()
                    + "] keyword[" + storeKeyword.getKeywordId() + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rules successfully loaded: store[" + storeKeyword.getStoreId() + "] keyword["
                    + storeKeyword.getKeywordId() + "].";
        } else {
            msg = "Unable to load " + rule.toUpperCase() + " rules for store[" + storeKeyword.getStoreId()
                    + "] keyword[" + storeKeyword.getKeywordId() + "].";
        }

        return msg;
    }

    private String loadByName(String rule, Store store, String name, String type) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("facetsort")) {
                RuleType ruleType = null;
                if (type.equalsIgnoreCase("keyword")) {
                    ruleType = RuleType.KEYWORD;
                } else if (type.equalsIgnoreCase("template")) {
                    ruleType = RuleType.TEMPLATE;
                } else {
                    status = "error";
                }
                if (ruleType != null) {
                    status = "" + solrService.loadFacetSortRuleByName(store, name, ruleType);
                }
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.loadRedirectRuleByName(store, name);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.loadRelevancyRuleByName(store, name);
            } else if (rule.equals("banner")) {
                status = "" + bannerRuleItemMigratorService.loadByRuleName(store.getStoreId(), name);
            } else if (rule.equals("typeahead")) {
                status = "" + typeaheadRuleMigratorService.loadByRuleName(store.getStoreId(), name);
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error loading " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] name[" + name
                    + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully loaded: store[" + store.getStoreId() + "] name[" + name
                    + "].";
        } else {
            msg = "Unable to load " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] name[" + name
                    + "].";
            ;
        }

        return msg;
    }

    private String loadById(String rule, Store store, String id) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.loadFacetSortRuleById(store, id);
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.loadRedirectRuleById(store, id);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.loadRelevancyRuleById(store, id);
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.loadSpellRuleById(store, id);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.loadByRuleId(store.getStoreId(), id);
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.loadByRuleId(store.getStoreId(), id);
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error loading " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] id[" + id + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully loaded: store[" + store.getStoreId() + "] id[" + id + "].";
        } else {
            msg = "Unable to load " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] id[" + id
                    + "].";
        }

        return msg;
    }

    /* RESET */
    private String resetByStore(String rule, Store store) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.resetDemoteRules(store);
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.resetElevateRules(store);
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.resetExcludeRules(store);
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.resetFacetSortRules(store);
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.resetRedirectRules(store);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.resetRelevancyRules(store);
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.resetSpellRules(store);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.resetByStoreId(store.getStoreId());
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.resetByStoreId(store.getStoreId());
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error resetting " + rule.toUpperCase() + " rules for store[" + store.getStoreId().toUpperCase()
                    + "].";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rules successfully reset: store[" + store.getStoreId().toUpperCase() + "].";
        } else {
            msg = "Unable to reset " + rule.toUpperCase() + " rule for store[" + store.getStoreId().toUpperCase()
                    + "].";
        }

        return msg;
    }

    private String resetByStoreKeyword(String rule, StoreKeyword storeKeyword) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.resetDemoteRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.resetElevateRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.resetExcludeRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.resetSpellRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "unimplemented";
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error resetting " + rule.toUpperCase() + " rules for store[" + storeKeyword.getStoreId()
                    + "] keyword[" + storeKeyword.getKeywordId() + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rules successfully reset: store[" + storeKeyword.getStoreId() + "] keyword["
                    + storeKeyword.getKeywordId() + "].";
        } else {
            msg = "Unable to reset " + rule.toUpperCase() + " rules for store[" + storeKeyword.getStoreId()
                    + "] keyword[" + storeKeyword.getKeywordId() + "].";
        }

        return msg;
    }

    private String resetByName(String rule, Store store, String name, String type) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("facetsort")) {
                RuleType ruleType = null;
                if (type.equalsIgnoreCase("keyword")) {
                    ruleType = RuleType.KEYWORD;
                } else if (type.equalsIgnoreCase("template")) {
                    ruleType = RuleType.TEMPLATE;
                } else {
                    status = "error";
                }
                if (ruleType != null) {
                    status = "" + solrService.resetFacetSortRuleByName(store, name, ruleType);
                }
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.resetRedirectRuleByName(store, name);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.resetRelevancyRuleByName(store, name);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.resetByRuleName(store.getStoreId(), name);
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.resetByRuleName(store.getStoreId(), name);
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error resetting " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] name[" + name
                    + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully reset: store[" + store.getStoreId() + "] name[" + name
                    + "].";
        } else {
            msg = "Unable to reset " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] name[" + name
                    + "].";
        }

        return msg;
    }

    private String resetById(String rule, Store store, String id) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "";
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.resetFacetSortRuleById(store, id);
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.resetRedirectRuleById(store, id);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.resetRelevancyRuleById(store, id);
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.resetSpellRuleById(store, id);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.resetByRuleId(store.getStoreId(), id);
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.resetByRuleId(store.getStoreId(), id);
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error resetting " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] id[" + id
                    + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully reset: store[" + store.getStoreId() + "] id[" + id + "].";
        } else {
            msg = "Unable to reset " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] id[" + id
                    + "].";
        }

        return msg;
    }

    /* DELETE */
    private String deleteByStore(String rule, Store store) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.deleteDemoteRules(store);
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.deleteElevateRules(store);
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.deleteExcludeRules(store);
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.deleteFacetSortRules(store);
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.deleteRedirectRules(store);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.deleteRelevancyRules(store);
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.deleteSpellRules(store);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.deleteByStoreId(store.getStoreId());
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.deleteByStoreId(store.getStoreId());
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error deleting " + rule.toUpperCase() + " rule for store[" + store.getStoreId().toUpperCase() + "].";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully deleted: store[" + store.getStoreId().toUpperCase() + "].";
        } else {
            msg = "Unable to delete " + rule.toUpperCase() + " rule: store[" + store.getStoreId().toUpperCase() + "].";
        }

        return msg;
    }

    private String deleteByStoreKeyword(String rule, StoreKeyword storeKeyword) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.deleteDemoteRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.deleteElevateRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.deleteExcludeRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.deleteSpellRules(storeKeyword);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "unimplemented";
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error deleting " + rule.toUpperCase() + " rules for store[" + storeKeyword.getStoreId()
                    + "] keyword[" + storeKeyword.getKeywordId() + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rules successfully deleted: store[" + storeKeyword.getStoreId() + "] keyword["
                    + storeKeyword.getKeywordId() + "].";
        } else {
            msg = "Unable to delete " + rule.toUpperCase() + " rules for store[" + storeKeyword.getStoreId()
                    + "] keyword[" + storeKeyword.getKeywordId() + "].";
        }

        return msg;
    }

    private String deleteById(String rule, Store store, String id) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.deleteFacetSortRuleById(store, id);
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.deleteRedirectRuleById(store, id);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.deleteRelevancyRuleById(store, id);
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.deleteSpellRuleById(store, id);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.deleteByRuleId(store.getStoreId(), id);
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.deleteByRuleId(store.getStoreId(), id);
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error deleting " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] id[" + id
                    + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully deleted: store[" + store.getStoreId() + "] id[" + id + "].";
        } else {
            msg = "Unable to delete " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] id[" + id
                    + "].";
        }

        return msg;
    }

    private String deleteByName(String rule, Store store, String name, String type) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "unimplemented";
            } else if (rule.equalsIgnoreCase("facetsort")) {
                RuleType ruleType = null;
                if (type.equalsIgnoreCase("keyword")) {
                    ruleType = RuleType.KEYWORD;
                } else if (type.equalsIgnoreCase("template")) {
                    ruleType = RuleType.TEMPLATE;
                } else {
                    status = "error";
                }
                if (ruleType != null) {
                    status = "" + solrService.deleteFacetSortRuleByName(store, name, ruleType);
                }
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.deleteRedirectRuleByName(store, name);
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.deleteRelevancyRuleByName(store, name);
            } else if (rule.equalsIgnoreCase("banner")) {
                status = "" + bannerRuleItemMigratorService.deleteByRuleName(store.getStoreId(), name);
            } else if (rule.equalsIgnoreCase("typeahead")) {
                status = "" + typeaheadRuleMigratorService.deleteByRuleName(store.getStoreId(), name);
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error deleting " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] name[" + name
                    + "].";
        } else if (status.equals("unimplemented")) {
            msg = "Unimplemented method for " + rule.toUpperCase() + " rule.";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rule successfully deleted: store[" + store.getStoreId() + "] name[" + name
                    + "].";
        } else {
            msg = "Unable to delete " + rule.toUpperCase() + " rule for store[" + store.getStoreId() + "] name[" + name
                    + "].";
        }

        return msg;
    }

    /* COMMIT */
    private String commit(String rule, Store store) {
        String msg = "";
        String status = "";

        try {
            if (rule.equalsIgnoreCase("demote")) {
                status = "" + solrService.commitDemoteRule();
            } else if (rule.equalsIgnoreCase("elevate")) {
                status = "" + solrService.commitElevateRule();
            } else if (rule.equalsIgnoreCase("exclude")) {
                status = "" + solrService.commitExcludeRule();
            } else if (rule.equalsIgnoreCase("facetsort")) {
                status = "" + solrService.commitFacetSortRule();
            } else if (rule.equalsIgnoreCase("redirect")) {
                status = "" + solrService.commitRedirectRule();
            } else if (rule.equalsIgnoreCase("relevancy")) {
                status = "" + solrService.commitRelevancyRule();
            } else if (rule.equalsIgnoreCase("spell")) {
                status = "" + solrService.commitSpellRule();
            } else if (rule.equalsIgnoreCase("banner")) {
                // status = "" + solrService.commitBannerRuleItem();
                // TODO expose commit method.
            } else if (rule.equalsIgnoreCase("typeahead")) {
                // status = "" + solrService.commitTypeaheadRule();
                // TODO expose commit method.
            }
        } catch (Exception e) {
            status = "error";
        }

        if (status.equals("error")) {
            msg = "Error commiting " + rule.toUpperCase() + " rules for store[" + store.getStoreId() + "].";
        } else if (status.equals("true")) {
            msg = rule.toUpperCase() + " rules successfully commited: store[" + store.getStoreId() + "].";
        } else {
            msg = "Unable to commit " + rule.toUpperCase() + " rules for store[" + store.getStoreId() + "].";
        }

        return msg;
    }

}