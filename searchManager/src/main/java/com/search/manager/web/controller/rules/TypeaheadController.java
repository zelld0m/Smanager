package com.search.manager.web.controller.rules;

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

import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.report.model.TypeaheadReportBean;
import com.search.manager.report.model.TypeaheadReportModel;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.report.model.xml.TypeaheadRuleXml;
import com.search.manager.service.DownloadService;
import com.search.manager.service.RuleVersionService;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.Transformers;
import com.search.manager.xml.file.RuleXmlReportUtil;

@Controller
@RequestMapping("/typeahead")
@Scope(value = "prototype")
public class TypeaheadController {

	private static final Logger logger =
            LoggerFactory.getLogger(TypeaheadController.class);
	
	@Autowired
    private RuleVersionService ruleVersionService;
	@Autowired
    private DownloadService downloadService;
	@Autowired
    private RuleXmlReportUtil ruleXmlReportUtil;
	@Autowired
    private UtilityService utilityService;
	
	@RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store) {
        model.addAttribute("store", store);

        return "rules/typeahead";
    }

	@RequestMapping(value = "/{store}/version/xls")
    public void getRuleVersion(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable String store, @RequestParam("filename") String filename, @RequestParam("id") String ruleId,
            @RequestParam String keyword, @RequestParam("clientTimezone") Long clientTimezone,
            @RequestParam("type") String type) {
        logger.debug(String.format("Received request to download version report as an XLS: %s", filename));

        DateTime headerDate = new DateTime();
        String subTitle = "Typeahead Rule [" + keyword + "]";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<TypeaheadReportBean> reportModel = new TypeaheadReportModel(reportHeader,
                new ArrayList<TypeaheadReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        List<RuleXml> xmls = ruleVersionService.getRuleVersions("Typeahead", ruleId);

        for (RuleXml xml : xmls) {
            TypeaheadRuleXml ruleXml = (TypeaheadRuleXml) xml;
            List<TypeaheadReportBean> reportBeans = new ArrayList<TypeaheadReportBean>();
            
            reportBeans.add(new TypeaheadReportBean(new TypeaheadRule(ruleXml)));
            SubReportHeader subReportHeader = ruleXmlReportUtil.getVersionSubReportHeader(utilityService.getStoreId(), xml, RuleEntity.TYPEAHEAD);
            subModels.add(new TypeaheadReportModel(reportHeader, subReportHeader, reportBeans));
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
