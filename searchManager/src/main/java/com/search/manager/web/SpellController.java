package com.search.manager.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.SpellRule;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SpellReportBean;
import com.search.manager.report.model.SpellReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.DBRuleVersion;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleTransferService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.service.SpellRuleService;
import com.search.manager.xml.file.RuleXmlReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/spell")
@Scope(value = "prototype")
public class SpellController {

    private static final Logger logger =
            LoggerFactory.getLogger(SpellController.class);
    @Autowired
    private RuleVersionService ruleVersionService;
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private SpellRuleService spellRuleService;
    @Autowired
    private RuleTransferService ruleTransferService;
    @Autowired
    private DaoService daoService;
    @Autowired
    private RuleXmlReportUtil ruleXmlReportUtil;
    @Autowired
    private RuleVersionUtil ruleVersionUtil;
    
    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store) {
        model.addAttribute("store", store);
        return "lexicon/spell";
    }

    @RequestMapping(value = "/{store}/xls")
    public void getCurrentVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename,
            @RequestParam("clientTimezone") Long clientTimezone, @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download current rule as an XLS: %s", filename));

        Date headerDate = new Date(clientTimezone);
        String subTitle = "Did You Mean Rules";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<SpellReportBean> reportModel = new SpellReportModel(reportHeader, new ArrayList<SpellReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

        try {
            List<SpellRule> spellRules = daoService.getSpellRules(store, null);
            int maxSuggest = daoService.getMaxSuggest(store);

            if (spellRules != null) {
                DBRuleVersion version = new DBRuleVersion();

                version.setStore(store);
                version.getProps().put("maxSuggest", String.valueOf(maxSuggest));

                SubReportHeader subReportHeader = ruleXmlReportUtil
                        .getVersionSubReportHeader(version, RuleEntity.SPELL);
                subModels.add(new SpellReportModel(reportHeader, subReportHeader, Lists.transform(spellRules,
                        SpellReportBean.transformer)));
            }
            download(response, reportModel, subModels, type);
        } catch (DaoException e) {
            logger.error("Error occurred on getCurrentVersion.", e);
        }
    }

    @RequestMapping(value = "/{store}/export/xls")
    public void getExportVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename,
            @RequestParam("clientTimezone") Long clientTimezone, @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download current rule as an XLS: %s", filename));

        Date headerDate = new Date(clientTimezone);
        String subTitle = "Did You Mean Rules";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<SpellReportBean> reportModel = new SpellReportModel(reportHeader, new ArrayList<SpellReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        @SuppressWarnings("unchecked")
        RuleVersionListXml<DBRuleVersion> ruleVersions = ruleVersionUtil.getPublishedList(store, RuleEntity.SPELL,
                "spell_rule");

        try {
            if (ruleVersions != null) {
                int latestIdx = ruleVersions.getVersions().size() - 1;
                DBRuleVersion latest = ruleVersions.getVersions().get(latestIdx);
                List<SpellRule> spellRule = daoService.getSpellRuleVersion(store, 0);

                if (latest != null && spellRule != null && spellRule.size() > 0) {
                    SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(latest,
                            RuleEntity.SPELL);
                    subModels.add(new SpellReportModel(reportHeader, subReportHeader, Lists.transform(spellRule,
                            SpellReportBean.transformer)));
                }
            }
            download(response, reportModel, subModels, type);
        } catch (DaoException e) {
            logger.error("Error occurred on getExportVersion.", e);
        }
    }

    @RequestMapping(value = "/{store}/import/xls")
    public void getImportVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename,
            @RequestParam("clientTimezone") Long clientTimezone, @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download current rule as an XLS: %s", filename));

        Date headerDate = new Date(clientTimezone);
        String subTitle = "Did You Mean Rules for Import";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<SpellReportBean> reportModel = new SpellReportModel(reportHeader, new ArrayList<SpellReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        RuleXml xml = ruleTransferService
                .getRuleToImport(RuleEntity.getValue(RuleEntity.SPELL.getCode()), "spell_rule");

        try {
            if (xml != null) {
                SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(xml, RuleEntity.SPELL);
                subModels.add(new SpellReportModel(reportHeader, subReportHeader, Lists.transform(
                        daoService.getSpellRuleVersion(xml.getStore(), 0), SpellReportBean.transformer)));
            }
            download(response, reportModel, subModels, type);
        } catch (DaoException e) {
            logger.error("Error occurred on getImportVersion.", e);
        }
    }

    @RequestMapping(value = "/{store}/version/xls")
    public void getRuleVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename, @RequestParam("id") String ruleId,
            @RequestParam("clientTimezone") Long clientTimezone, @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

        Date headerDate = new Date(clientTimezone);
        String subTitle = "Did You Mean Rules";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<SpellReportBean> reportModel = new SpellReportModel(reportHeader, new ArrayList<SpellReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        List<RuleXml> rules = ruleVersionService.getRuleVersions("Did You Mean", ruleId);

        try {
            if (rules != null) {
                for (RuleXml xml : rules) {
                	if (!xml.isDeleted()) {
                        DBRuleVersion version = (DBRuleVersion) xml;
                        List<SpellRule> spellRules = daoService.getSpellRuleVersion(store, (int) version.getVersion());
                        if (spellRules != null) {
                            SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(version,
                                    RuleEntity.SPELL);
                            subModels.add(new SpellReportModel(reportHeader, subReportHeader, Lists.transform(spellRules,
                                    SpellReportBean.transformer)));
                        }
                	}
                }
            }

            download(response, reportModel, subModels, type);
        } catch (DaoException e) {
            logger.error("Error occurred on getRuleVersions.", e);
        }
    }

    @RequestMapping(value = "/{store}/version/xls/{versionNo}")
    public void getRuleVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable("versionNo") long versionNo, @PathVariable String store,
            @RequestParam("filename") String filename, @RequestParam("id") String ruleId,
            @RequestParam("clientTimezone") Long clientTimezone, @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

        Date headerDate = new Date(clientTimezone);
        String subTitle = "Did You Mean Rules";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<SpellReportBean> reportModel = new SpellReportModel(reportHeader, new ArrayList<SpellReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        List<RuleXml> rules = ruleVersionService.getRuleVersions("Did You Mean", ruleId);

        try {
            if (rules != null) {
                for (RuleXml xml : rules) {
                    DBRuleVersion version = (DBRuleVersion) xml;

                    if (version != null && xml.getVersion() == versionNo) {
                        SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(version,
                                RuleEntity.SPELL);
                        subModels.add(new SpellReportModel(reportHeader, subReportHeader, Lists.transform(
                                daoService.getSpellRuleVersion(store, (int) versionNo), SpellReportBean.transformer)));
                    }
                }
            }

            download(response, reportModel, subModels, type);
        } catch (DaoException e) {
            logger.error("Error occurred on getRuleVersion.", e);
        }
    }

    private void download(HttpServletResponse response, ReportModel<? extends ReportBean<?>> mainModel,
            ArrayList<ReportModel<? extends ReportBean<?>>> subModels, String fileType) {
        // Delegate to downloadService. Make sure to pass an instance of
        // HttpServletResponse
        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(fileType)) {
            try {
                downloadService.downloadMultiSheetXLS(response, mainModel, subModels);
            } catch (ClassNotFoundException e) {
                logger.error("Error encountered while trying to download.", e);
                e.printStackTrace();
            }
        }
    }
}
