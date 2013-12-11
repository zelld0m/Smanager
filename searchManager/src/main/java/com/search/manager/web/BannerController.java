package com.search.manager.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.BannerReportBean;
import com.search.manager.report.model.BannerReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.xml.BannerItemXml;
import com.search.manager.report.model.xml.BannerRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.BannerService;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.utility.Transformers;
import com.search.manager.xml.file.RuleXmlReportUtil;

@Controller
@RequestMapping("/banner")
@Scope(value = "prototype")
public class BannerController {

    private static final Logger logger =
            LoggerFactory.getLogger(BannerController.class);
    @SuppressWarnings("unused")
	@Autowired
    private BannerService bannerService;
    @Autowired
    private RuleVersionService ruleVersionService;
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private RuleXmlReportUtil ruleXmlReportUtil;
    
    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store) {
        model.addAttribute("store", store);

        return "ads/banner";
    }

    @RequestMapping(value = "/{store}/xls")
    public void getCurrentRule(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename, @RequestParam("id") String ruleId,
            @RequestParam String keyword, @RequestParam("clientTimezone") Long clientTimezone,
            @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

//        Date headerDate = new Date(clientTimezone);
        DateTime headerDate = new DateTime();
        String subTitle = "Banners for keyword [" + keyword + "]";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        BannerRuleXml xml = (BannerRuleXml) ruleVersionService.getCurrentRuleXml("Banner", ruleId);
        List<BannerReportBean> reportBeans = new ArrayList<BannerReportBean>();

        if (xml.getItemXml() != null) {
            for (BannerItemXml itemXml : xml.getItemXml()) {
               reportBeans.add(new BannerReportBean(Transformers.bannerItemXmlToRule.apply(itemXml)));
            }
        }

        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
            try {
                downloadService.downloadXLS(response, new BannerReportModel(reportHeader, reportBeans), null);
            } catch (ClassNotFoundException e) {
                logger.error("Error encountered while trying to download.", e);
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/{store}/version/xls")
    public void getRuleVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename, @RequestParam("id") String ruleId,
            @RequestParam String keyword, @RequestParam("clientTimezone") Long clientTimezone,
            @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

//        Date headerDate = new Date(clientTimezone);
        DateTime headerDate = new DateTime();
        String subTitle = "Banners for keyword [" + keyword + "]";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<BannerReportBean> reportModel = new BannerReportModel(reportHeader,
                new ArrayList<BannerReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        List<RuleXml> xmls = ruleVersionService.getRuleVersions("Banner", ruleId);

        for (RuleXml xml : xmls) {
            BannerRuleXml ruleXml = (BannerRuleXml) xml;
            List<BannerItemXml> itemXmls = ruleXml.getItemXml();
            List<BannerReportBean> reportBeans = new ArrayList<BannerReportBean>();

            if (itemXmls != null) {
                for (BannerItemXml itemXml : itemXmls) {
                    reportBeans.add(new BannerReportBean(Transformers.bannerItemXmlToRule.apply(itemXml)));
                }
            }

            subModels.add(new BannerReportModel(reportHeader, ruleXmlReportUtil.getVersionSubReportHeader(ruleXml,
                    RuleEntity.BANNER), reportBeans));
        }

        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
            try {
                downloadService.downloadXLS(response, reportModel, subModels);
            } catch (ClassNotFoundException e) {
                logger.error("Error encountered while trying to download.", e);
                e.printStackTrace();
            }
        }
    }
}
