package com.search.manager.report.model;

import java.util.List;

public class SpellReportModel extends ReportModel<SpellReportBean> {

    public SpellReportModel(ReportHeader reportHeader, List<SpellReportBean> records) {
        super(reportHeader, SpellReportBean.class, records);
    }

    public SpellReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<SpellReportBean> records) {
        super(reportHeader, subReportHeader, SpellReportBean.class, records);
    }
}
