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
import org.springframework.web.bind.annotation.RequestMethod;

import com.search.manager.model.AuditTrail;
import com.search.manager.model.RecordSet;
import com.search.manager.report.model.AuditTrailReportBean;
import com.search.manager.report.model.AuditTrailReportModel;
import com.search.manager.report.model.ReportBean;
import com.search.manager.report.model.ReportHeader;
import com.search.manager.report.model.ReportModel;
import com.search.manager.report.model.SubReportHeader;
import com.search.manager.service.AuditService;
import com.search.manager.service.DownloadService;

@Controller
@RequestMapping("/audit")
@Scope(value = "prototype")
public class AuditController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditController.class);
    @Autowired
    private DownloadService downloadService;
    @Autowired
    private AuditService auditService;

    @RequestMapping(value = "/{store}")
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) {
        model.addAttribute("store", store);

        return "audit/audit";
    }

    @RequestMapping(value = "/{store}/xls", method = RequestMethod.GET)
    public void getXLS(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String store) throws ClassNotFoundException {

        String type = request.getParameter("type");
        String userName = request.getParameter("username");
        String operation = request.getParameter("operation");
        String entity = request.getParameter("entity");
        String keyword = request.getParameter("keyword");
        String referenceId = request.getParameter("referenceId");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String totalSize = request.getParameter("totalSize");
        String filename = request.getParameter("filename");
//        long clientTimezone = Long.parseLong(request.getParameter("clientTimezone"));
//        Date headerDate = new Date(clientTimezone);
        DateTime headerDate = new DateTime();
        
        logger.debug(String.format("Received request to download report as an XLS: %s %s %s %s %s %s %s %s %s", userName, operation, entity, keyword, referenceId, startDate, endDate, totalSize, filename));

        int nTotalSize = 0;
        try {
            nTotalSize = Integer.parseInt(totalSize);
        } catch (Exception e) {
            nTotalSize = 1000;
        }
        
//      RecordSet<AuditTrail> auditTrails = auditService.getAuditTrail(userName, operation, entity, keyword, referenceId, startDate, endDate, 1, nTotalSize);
        /*
        List<AuditTrailReportBean> list = new ArrayList<AuditTrailReportBean>();
        for (AuditTrail auditTrail : auditTrails.getList()) {
            list.add(new AuditTrailReportBean(auditTrail));
        }

        String subTitle = "Audit Trail";

        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        ReportModel<AuditTrailReportBean> reportModel = new AuditTrailReportModel(reportHeader, list);
        */
        
        String subTitle = "Audit Trail";
        ReportHeader reportHeader = new ReportHeader("Search GUI (%%StoreName%%)", subTitle, filename, headerDate);
        RecordSet<AuditTrail> auditTrails = auditService.getAuditTrail(userName, operation, entity, keyword, referenceId, startDate, endDate, 1, nTotalSize);
        ReportModel<AuditTrailReportBean> reportModel = new AuditTrailReportModel(reportHeader, new ArrayList<AuditTrailReportBean>());
        ArrayList<ReportModel<? extends ReportBean<?>>> subModels = new ArrayList<ReportModel<? extends ReportBean<?>>>();
        List<AuditTrailReportBean> records = new ArrayList<AuditTrailReportBean>();
        int maxRow = 65500;
        int rowCount = 0;
        int page = 1;
        
        for (int i=0; i < auditTrails.getList().size(); i++) {
            records.add(new AuditTrailReportBean(auditTrails.getList().get(i)));
            rowCount++;
            if (rowCount > maxRow || i == auditTrails.getList().size() - 1) {
                SubReportHeader subReportHeader = new SubReportHeader();
                subReportHeader.setFileName("Page " + page);
                subModels.add(new AuditTrailReportModel(reportHeader, subReportHeader, records));
                records = new ArrayList<AuditTrailReportBean>();
                rowCount = 0;
                page++;
            }
        }
        
        // Delegate to downloadService. Make sure to pass an instance of HttpServletResponse
        if (DownloadService.downloadType.EXCEL.toString().equalsIgnoreCase(type)) {
            // downloadService.downloadXLS(response, reportModel, null);
            downloadService.downloadMultiSheets(response, reportModel, subModels);
        }
    }
}
