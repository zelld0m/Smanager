package com.search.manager.report.model;

import java.util.List;

public class AuditTrailReportModel extends ReportModel<AuditTrailReportBean> {

    public AuditTrailReportModel(ReportHeader reportHeader, List<AuditTrailReportBean> records) {
        super(reportHeader, AuditTrailReportBean.class, records);
    }

    public AuditTrailReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader,
            List<AuditTrailReportBean> records) {
        super(reportHeader, subReportHeader, AuditTrailReportBean.class, records);
    }

}
