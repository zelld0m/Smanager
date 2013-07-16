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
				rule.setImagePath(new ImagePath(null, xml.getImagePathId(), xml
						.getImagePath(), xml.getImageSize(), ImagePathType
						.get(xml.getImagePathType()), xml.getImageAlias()));
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

	public static final Function<BannerRuleItem, BannerItemXml> bannerItemRuleToXml = new Function<BannerRuleItem, BannerItemXml>() {
		public BannerItemXml apply(BannerRuleItem rule) {
			BannerItemXml xml = null;

			if (rule != null) {
				xml = new BannerItemXml();
				xml.setMemberId(rule.getMemberId());
				xml.setStartDate(rule.getStartDate());
				xml.setEndDate(rule.getEndDate());
				xml.setImageAlt(rule.getImageAlt());
				xml.setLinkPath(rule.getLinkPath());
				xml.setOpenNewWindow(rule.getOpenNewWindow());
				xml.setDescription(rule.getDescription());
				xml.setDisabled(rule.getDisabled());
				xml.setPriority(rule.getPriority());

				if (rule.getImagePath() != null) {
					xml.setImagePathId(rule.getImagePath().getId());
					xml.setImageAlias(rule.getImagePath().getAlias());
					xml.setImagePath(rule.getImagePath().getPath());
					xml.setImagePathType(rule.getImagePath().getPathType() != null ? rule
							.getImagePath().getPathType().getDisplayText()
							: "");
				}

				xml.setCreatedBy(rule.getCreatedBy());
				xml.setCreatedDate(rule.getCreatedDate());
				xml.setLastModifiedBy(rule.getLastModifiedBy());
				xml.setLastModifiedDate(rule.getLastModifiedDate());
			}

			return xml;
		}
	};
}
