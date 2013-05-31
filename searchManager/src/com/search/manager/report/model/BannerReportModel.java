package com.search.manager.report.model;

import java.util.List;

public class BannerReportModel extends ReportModel<BannerReportBean> {

    public BannerReportModel(ReportHeader reportHeader, List<BannerReportBean> records) {
        super(reportHeader, BannerReportBean.class, records);
    }

    public BannerReportModel(ReportHeader reportHeader, SubReportHeader subReportHeader, List<BannerReportBean> records) {
        super(reportHeader, subReportHeader, BannerReportBean.class, records);
    }
}
