package com.search.manager.utility;

import com.google.common.base.Function;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.ImagePath;
import com.search.manager.model.ImagePathType;
import com.search.manager.report.model.xml.BannerItemXml;

public class Transformers {

    public static final Function<BannerItemXml, BannerRuleItem> bannerItemXmlToRule = new Function<BannerItemXml, BannerRuleItem>() {
        public BannerRuleItem apply(BannerItemXml xml) {
            BannerRuleItem rule = null;

            if (xml != null) {
                rule = new BannerRuleItem();

                // common
                rule.setCreatedBy(xml.getCreatedBy());
                rule.setCreatedDate(xml.getCreatedDate());
                rule.setLastModifiedBy(xml.getLastModifiedBy());
                rule.setLastModifiedDate(xml.getLastModifiedDate());
                // other
                rule.setPriority(xml.getPriority());
                rule.setImagePath(new ImagePath(null, xml.getImagePathId(), xml.getImagePath(), ImagePathType.get(xml
                        .getImagePathType()), xml.getImageAlias()));
                rule.setLinkPath(xml.getLinkPath());
                rule.setImageAlt(xml.getImageAlt());
                rule.setDescription(xml.getDescription());
                rule.setDisabled(xml.getDisabled());
                rule.setMemberId(xml.getMemberId());
                rule.setStartDate(xml.getStartDate());
                rule.setEndDate(xml.getEndDate());
                rule.setOpenNewWindow(xml.getOpenNewWindow());
            }

            return rule;
        }
    };
}
