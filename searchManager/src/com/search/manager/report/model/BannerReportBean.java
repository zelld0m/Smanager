package com.search.manager.report.model;

import org.apache.commons.lang.StringUtils;

import com.search.manager.model.BannerRuleItem;
import com.search.manager.report.annotation.ReportField;

public class BannerReportBean extends ReportBean<BannerRuleItem> {

    public BannerReportBean(BannerRuleItem model) {
        super(model);
    }

    @ReportField(label = "Priority", size = 15, sortOrder = 1)
    public String getElevation() {
        return String.valueOf(model.getPriority());
    }

    @ReportField(label = "Alias", size = 15, sortOrder = 2)
    public String getAlias() {
        return model.getImagePath() != null ? StringUtils.defaultString(model.getImagePath().getAlias()) : "";
    }

    @ReportField(label = "Image Path", size = 20, sortOrder = 3)
    public String getImagePath() {
        return model.getImagePath() != null ? StringUtils.defaultString(model.getImagePath().getPath()) : "";
    }

    @ReportField(label = "Link Path", size = 20, sortOrder = 4)
    public String getLinkPath() {
        return StringUtils.defaultString(model.getLinkPath());
    }

    @ReportField(label = "Image Alt", size = 20, sortOrder = 5)
    public String getImageAlt() {
        return StringUtils.defaultString(model.getImageAlt());
    }

    @ReportField(label = "Description", size = 20, sortOrder = 6)
    public String getDescription() {
        return StringUtils.defaultString(model.getDescription());
    }

    //@ReportField(label = "Open In New Window", size = 15, sortOrder = 7)
    public String getOpenInNewWindow() {
        return StringUtils.defaultString(model.getOpenNewWindow());
    }

    @ReportField(label = "Disabled", size = 10, sortOrder = 7)
    public String getDisabled() {
        return String.valueOf(model.getDisabled() != null ? model.getDisabled() ? "Yes" : "No" : "No");
    }

    @ReportField(label = "Start Date", size = 15, sortOrder = 8)
    public String getStartDate() {
        return model.getStartDate() != null ? model.getStartDate().toString("MM/dd/yyyy") : "";
    }

    @ReportField(label = "End Date", size = 15, sortOrder = 9)
    public String getEndDate() {
        return model.getEndDate() != null ? model.getEndDate().toString("MM/dd/yyyy") : "";
    }
}