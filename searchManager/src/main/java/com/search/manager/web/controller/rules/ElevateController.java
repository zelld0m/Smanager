package com.search.manager.web.controller.rules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.ElevateProduct;
import com.search.manager.model.RecordSet;
import com.search.manager.report.model.ElevateReportBean;
import com.search.manager.report.model.ElevateReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.xml.ElevateRuleXml;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.ElevateService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.xml.file.RuleXmlReportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/elevate")
@Scope(value = "prototype")
public class ElevateController {

    private static final Logger logger =
            LoggerFactory.getLogger(ElevateController.class);
    private static final String RULE_TYPE = RuleEntity.ELEVATE.toString();
    @Autowired
    private ElevateService elevateService;
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private RuleVersionService ruleVersionService;
    @Autowired
    private RuleXmlReportUtil ruleXmlReportUtil;
    
    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "rules/elevate";
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

        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String filter = request.getParameter("filter");
        String page = request.getParameter("page");
        String filename = request.getParameter("filename");
        String itemsPerPage = request.getParameter("itemperpage");
        long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));

        Date headerDate = new Date(clientTimezone);


        logger.debug(String.format("Received request to download report as an XLS: %s %s %s %s %s %s", keyword, type, filter, page, itemsPerPage, filename));

        if (StringUtils.isBlank(filename)) {
            filename = "elevate";
        }

        int nPage = 0;
        int nItemsPerPage = 0;
        if (!"all".equalsIgnoreCase(page)) {
            try {
                nPage = Integer.parseInt(page);
            } catch (Exception e) {
                logger.error("Error parsing page: " + page, e);
            }
            try {
                nItemsPerPage = Integer.parseInt(itemsPerPage);
            } catch (Exception e) {
            }
        }
        RecordSet<ElevateProduct> elevateProducts = elevateService.getProducts(filter, keyword, nPage, nItemsPerPage);

        List<ElevateReportBean> list = new ArrayList<ElevateReportBean>();
        for (ElevateProduct p : elevateProducts.getList()) {
            list.add(new ElevateReportBean(p));
        }

        String subTitle = "List of %%Filter%%Elevated Items for [" + keyword + "]";
        if ("active".equalsIgnoreCase(filter)) {
            subTitle = StringUtils.replace(subTitle, "%%Filter%%", "Active ");
        } else if ("expired".equalsIgnoreCase(filter)) {
            subTitle = StringUtils.replace(subTitle, "%%Filter%%", "Expired ");
        } else {
            subTitle = StringUtils.replace(subTitle, "%%Filter%%", "");
        }

        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<ElevateReportBean> reportModel = new ElevateReportModel(reportHeader, list);

        // Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
            downloadService.downloadXLS(response, reportModel, null);
        }
    }

    @RequestMapping(value = "/{store}/version/xls", method = RequestMethod.GET)
    // TODO: change to POST, retrieve filter type
    public void getVersionXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String filter = request.getParameter("filter");
        String filename = request.getParameter("filename");
        long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
        Date headerDate = new Date(clientTimezone);

        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

        String subTitle = "List of %%Filter%%Elevated Items for [" + keyword + "]";
        if ("active".equalsIgnoreCase(filter)) {
            subTitle = StringUtils.replace(subTitle, "%%Filter%%", "Active ");
        } else if ("expired".equalsIgnoreCase(filter)) {
            subTitle = StringUtils.replace(subTitle, "%%Filter%%", "Expired ");
        } else {
            subTitle = StringUtils.replace(subTitle, "%%Filter%%", "");
        }

        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);

        ReportModel<ElevateReportBean> reportModel = new ElevateReportModel(reportHeader, new ArrayList<ElevateReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();

        List<RuleXml> rules = ruleVersionService.getRuleVersions(RULE_TYPE, keyword);
        if (rules != null) {
            for (RuleXml xml : rules) {
                if (xml != null) {
                    SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(xml, RuleEntity.ELEVATE);
                    subModels.add(new ElevateReportModel(reportHeader, subReportHeader, ruleXmlReportUtil.getElevateProducts((ElevateRuleXml) xml)));
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