package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

@XmlRootElement(name = "item")
@DataTransferObject(converter = BeanConverter.class)
public class BannerItemXml extends RuleItemXml {

    private static final long serialVersionUID = 1L;

    private int priority;
    private DateTime startDate;
    private DateTime endDate;
    private String imageAlt;
    private String linkPath;
    private String openNewWindow;
    private String description;
    private Boolean disabled;
    private String imagePathId;
    private String imageAlias;
    private String imagePathType;

    public BannerItemXml() {
        super();
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }

    public String getOpenNewWindow() {
        return openNewWindow;
    }

    public void setOpenNewWindow(String openNewWindow) {
        this.openNewWindow = openNewWindow;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getImagePathId() {
        return imagePathId;
    }

    public void setImagePathId(String imagePathId) {
        this.imagePathId = imagePathId;
    }

    public String getImageAlias() {
        return imageAlias;
    }

    public void setImageAlias(String imageAlias) {
        this.imageAlias = imageAlias;
    }

    public String getImagePathType() {
        return imagePathType;
    }

    public void setImagePathType(String imagePathType) {
        this.imagePathType = imagePathType;
    }

    public String getImagePath2() {
        return super.getImagePath();
    }

    public void setImagePath2(String imagePath) {
        super.setImagePath(imagePath);
    }
}