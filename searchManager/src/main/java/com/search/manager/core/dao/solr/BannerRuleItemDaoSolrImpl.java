package com.search.manager.core.dao.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.search.manager.core.dao.BannerRuleItemDao;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;

@Repository("bannerRuleItemDaoSolr")
public class BannerRuleItemDaoSolrImpl extends
		GenericDaoSolrImpl<BannerRuleItem> implements BannerRuleItemDao {

	@Override
	protected Search generateQuery(BannerRuleItem model) {
		if (model != null) {
			Search search = new Search(BannerRuleItem.class);
			List<Filter> filters = new ArrayList<Filter>();
			BannerRule bannerRule = model.getRule();
			if (bannerRule != null) {
				if (StringUtils.isNotBlank(bannerRule.getStoreId())) {
					filters.add(new Filter("store", bannerRule.getStoreId()));
				}
				if (StringUtils.isNotBlank(bannerRule.getRuleId())) {
					filters.add(new Filter("ruleId", bannerRule.getRuleId()));
				}
				if (StringUtils.isNotBlank(bannerRule.getRuleName())) {
					filters.add(new Filter("ruleName", bannerRule.getRuleName()));
				}
			}
			ImagePath imagePath = model.getImagePath();
			if (imagePath != null) {
				if (StringUtils.isNotBlank(imagePath.getId())) {
					filters.add(new Filter("imagePathId", imagePath.getId()));
				}
				if (StringUtils.isNotBlank(imagePath.getPath())) {
					filters.add(new Filter("path", imagePath.getPath()));
				}
				if (StringUtils.isNotBlank(imagePath.getSize())) {
					filters.add(new Filter("size", imagePath.getSize()));
				}
				if (StringUtils.isNotBlank(imagePath.getPathType().toString())) {
					filters.add(new Filter("pathType", imagePath.getPathType()));
				}
			}

			if (StringUtils.isNotBlank(model.getMemberId())) {
				filters.add(new Filter("memberId", model.getMemberId()));
			}
			if (model.getPriority() != null) {
				filters.add(new Filter("priority", model.getPriority()));
			}
			if (model.getStartDate() != null) {
				filters.add(new Filter("startDate", model.getStartDate()));
			}
			if (model.getEndDate() != null) {
				filters.add(new Filter("endDate", model.getEndDate()));
			}
			if (StringUtils.isNotBlank(model.getImageAlt())) {
				filters.add(new Filter("imageAlt", model.getImageAlt()));
			}
			if (StringUtils.isNotBlank(model.getLinkPath())) {
				filters.add(new Filter("linkPath", model.getLinkPath()));
			}
			if (model.getOpenNewWindow() != null) {
				filters.add(new Filter("openNewWindow", model
						.getOpenNewWindow()));
			}
			if (StringUtils.isNotBlank(model.getDescription())) {
				filters.add(new Filter("description", model.getDescription()));
			}
			if (model.getDisabled() != null) {
				filters.add(new Filter("disabled", model.getDisabled()));
			}

			search.addFilters(filters);

			return search;
		}
		return null;
	}

}
