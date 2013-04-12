package com.search.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.search.manager.cache.dao.DaoCacheService;
import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SpellReportBean;
import com.search.manager.report.model.SpellReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.report.model.xml.SpellRules;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.service.UtilityService;
import com.search.manager.xml.file.RuleXmlReportUtil;

@Controller
@RequestMapping("/spell")
@Scope(value = "prototype")
public class SpellController {

    private static final Logger logger = Logger.getLogger(SpellController.class);

    @Autowired
    DaoCacheService daoCacheService;
    @Autowired
    RuleVersionService ruleVersionService;
    @Autowired
    DownloadService downloadService;

    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store) {
        try {
            daoCacheService.setUserCurrentPage(UtilityService.getUsername(), "Spell");
        } catch (Exception e) {
            logger.error("Failed to access local cache ", e);
        }
        model.addAttribute("store", store);
        return "lexicon/spell";
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

        if (rules != null) {
            for (RuleXml xml : rules) {
                if (xml != null) {
                    SubReportHeader subReportHeader = RuleXmlReportUtil
                            .getVersionSubReportHeader(xml, RuleEntity.SPELL);
                    subModels.add(new SpellReportModel(reportHeader, subReportHeader, Lists.transform(
                            ((SpellRules) xml).getSpellRule(), SpellReportBean.transformer)));
                }
            }
        }

        download(response, reportModel, subModels, type);
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
