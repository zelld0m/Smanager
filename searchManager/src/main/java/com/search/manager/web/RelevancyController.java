package com.search.manager.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.dao.file.RuleVersionUtil;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Keyword;
import com.search.manager.model.Relevancy;
import com.search.manager.report.model.KeywordReportBean;
import com.search.manager.report.model.KeywordReportModel;
import com.search.manager.report.model.RelevancyFieldReportBean;
import com.search.manager.report.model.RelevancyFieldReportModel;
import com.search.manager.report.model.RelevancyReportBean;
import com.search.manager.report.model.RelevancyReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.RankingRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RelevancyService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.service.UtilityService;
import com.search.manager.xml.file.RuleXmlReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/relevancy")
@Scope(value = "prototype")
public class RelevancyController {

    private static final Logger logger =
            LoggerFactory.getLogger(RelevancyController.class);
    private static final String RULE_TYPE = RuleEntity.RANKING_RULE.toString();
    @Autowired
    private RelevancyService relevancyService;
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private RuleVersionService ruleVersionService;

    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {

        Map<String, String> longFields = new LinkedHashMap<String, String>();
        longFields.put("qf", "Query Fields");
        longFields.put("bf", "Boost Function");
        longFields.put("pf", "Phrase Field");
        longFields.put("bq", "Boost Query");

        Map<String, String> shortFields = new LinkedHashMap<String, String>();
        shortFields.put("mm", "Min To Match");
        shortFields.put("qs", "Query Slop");
        shortFields.put("tie", "Tie Breaker");
        shortFields.put("ps", "Phrase Slop");
        shortFields.put("q.alt", "Q Alt");

        model.addAttribute("store", store);
        model.addAttribute("longFields", longFields);
        model.addAttribute("shortFields", shortFields);

        return "rules/relevancy";
    }

    /**
     * Downloads the report as an Excel format.
     * <p>
     * Make sure this method doesn't return any model. Otherwise, you'll get an
     * "IllegalStateException: getOutputStream() has already been called for this
     * response"
     */
    @RequestMapping(value = "/{store}/xls", method = RequestMethod.GET)
    // TODO: change to POST, retrieve filter type
    public void getXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {

        String relevancyId = request.getParameter("id");
        String filename = request.getParameter("filename");
        String type = request.getParameter("type");
        long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));

        Date headerDate = new Date(clientTimezone);

        logger.debug(String.format("Received request to download report as an XLS: %s %s", relevancyId, filename));

        if (StringUtils.isBlank(filename)) {
            filename = "ranking rule";
        }

        Relevancy relevancy = relevancyService.getRule(relevancyId);
        List<KeywordReportBean> keywords = new ArrayList<KeywordReportBean>();
        for (Keyword keyword : relevancyService.getAllKeywordInRule(relevancyId, null, 0, 0).getList()) {
            keywords.add(new KeywordReportBean(keyword));
        }

        List<RelevancyFieldReportBean> relevancyFields = new ArrayList<RelevancyFieldReportBean>();
        for (String key : relevancy.getParameters().keySet()) {
            String value = relevancy.getParameters().get(key);
            if (value != null) {
                relevancyFields.add(new RelevancyFieldReportBean(new BasicNameValuePair(key, value)));
            }
        }

        List<RelevancyReportBean> list = new ArrayList<RelevancyReportBean>();
        list.add(new RelevancyReportBean(relevancy));

        String subTitle = "Relevancy Rule [" + relevancy.getRelevancyName() + "]";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<RelevancyReportBean> reportModel = new RelevancyReportModel(reportHeader, list);

        List<ReportModel<? extends ReportBean<?>>> subReports = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        subReports.add(new KeywordReportModel(null, keywords));
        subReports.add(new RelevancyFieldReportModel(null, relevancyFields));

        // Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
            downloadService.downloadXLS(response, reportModel, subReports);
        }
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{store}/version/xls", method = RequestMethod.GET)
    // TODO: change to POST, retrieve filter type
    public void getVersionXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
        String ruleId = request.getParameter("id");
        String type = request.getParameter("type");
        String filename = request.getParameter("filename");
        long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
        Date headerDate = new Date(clientTimezone);

        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

        RuleVersionListXml listXml = RuleVersionUtil.getRuleVersionList(UtilityService.getStoreId(), RuleEntity.RANKING_RULE, ruleId);
        String subTitle = String.format("Ranking Rule [%s]", listXml != null ? listXml.getRuleName() : "");

        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);

        ReportModel<RelevancyReportBean> reportModel = new RelevancyReportModel(reportHeader, new ArrayList<RelevancyReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

        List<RuleXml> rules = ruleVersionService.getRuleVersions(RULE_TYPE, ruleId);
        if (rules != null) {
            for (RuleXml rule : rules) {
                RankingRuleXml xml = (RankingRuleXml) rule;
                if (xml != null) {
                    SubReportHeader subReportHeader = RuleXmlReportUtil.getVersionSubReportHeader(xml, RuleEntity.RANKING_RULE);
                    subModels.addAll(RuleXmlReportUtil.getRelevancySubReports(xml, reportHeader, subReportHeader));
                }
            }
        }
        download(response, reportModel, subModels, type);
    }

    private void download(HttpServletResponse response, ReportModel<? extends ReportBean<?>> mainModel, ArrayList<ReportModel<? extends ReportBean<?>>> subModels, String fileType) {
        // Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(fileType)) {
            try {
                downloadService.downloadXLS(response, mainModel, subModels);
            } catch (ClassNotFoundException e) {
                logger.error("Error encountered while trying to download.", e);
                e.printStackTrace();
            }
        }
    }
}
