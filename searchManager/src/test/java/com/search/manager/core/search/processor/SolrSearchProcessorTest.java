package com.search.manager.core.search.processor;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchProcessor;
import com.search.manager.core.search.SearchResult;

public class SolrSearchProcessorTest extends BaseIntegrationTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SolrSearchProcessorTest.class);

	@Autowired
	@Qualifier("solrSearchProcessor")
	private SearchProcessor searchProcessor;

	@Test
	public void SearchProcessorWiringTest() {
		assertNotNull(searchProcessor);
	}

	@Test
	public void searchTest() {
		Search search = new Search(BannerRuleItem.class);

		try {
			@SuppressWarnings("unchecked")
			SearchResult<BannerRuleItem> searchResult = (SearchResult<BannerRuleItem>) searchProcessor
					.processSearch(search);

			if (searchResult != null) {
				logger.info("size = " + searchResult.getTotalCount());

				List<BannerRuleItem> bannerRuleItems = searchResult.getResult();

				for (BannerRuleItem bannerRuleItem : bannerRuleItems) {
					logger.info(bannerRuleItem.toJson());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
