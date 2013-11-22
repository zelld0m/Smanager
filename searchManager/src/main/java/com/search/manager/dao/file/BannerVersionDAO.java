package com.search.manager.dao.file;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.BannerRuleItemService;
import com.search.manager.core.service.BannerRuleService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.report.model.xml.BannerRuleXml;
import com.search.manager.report.model.xml.RuleVersionListXml;
import com.search.manager.utility.Transformers;

@Component("bannerVersionDAO")
public class BannerVersionDAO extends AbstractRuleVersionDAO<BannerRuleXml> {

	private static final Logger logger = LoggerFactory
			.getLogger(BannerVersionDAO.class);

	@Autowired
	@Qualifier("bannerRuleServiceSp")
	private BannerRuleService bannerRuleService;
	@Autowired
	@Qualifier("bannerRuleItemServiceSp")
	private BannerRuleItemService bannerRuleItemService;

	@Override
	protected RuleEntity getRuleEntity() {
		return RuleEntity.BANNER;
	}

	@Override
	protected boolean addLatestVersion(
			RuleVersionListXml<?> ruleVersionListXml, String store,
			String ruleId, String username, String name, String notes,
			boolean isVersion) {
		if (ruleVersionListXml != null) {
			@SuppressWarnings("unchecked")
			List<BannerRuleXml> ruleXmlList = ((RuleVersionListXml<BannerRuleXml>) ruleVersionListXml)
					.getVersions();
			long version = ruleVersionListXml.getNextVersion();

			try {
				// Get all items
				BannerRule bannerRule = bannerRuleService.searchById(store,
						ruleId);
				BannerRuleItem bannerRuleItem = new BannerRuleItem();
				bannerRuleItem.setRule(bannerRule);
				SearchResult<BannerRuleItem> searchResult = bannerRuleItemService
						.search(bannerRuleItem);

				List<BannerRuleItem> bannerRuleItems = null;

				if (searchResult.getTotalCount() > 0) {
					bannerRuleItems = searchResult.getResult();
				}

				ruleXmlList.add(new BannerRuleXml(store, ruleId, bannerRule
						.getRuleName(), name, notes, username, Lists.transform(
						bannerRuleItems, Transformers.bannerItemRuleToXml),
						version));
				ruleVersionListXml.setRuleId(ruleId);
				ruleVersionListXml.setRuleName(bannerRule.getRuleName());

				return true;
			} catch (CoreServiceException e) {
				logger.error("Error occurred in addLatestVersion.", e);
			}
		}
		return false;
	}

}
