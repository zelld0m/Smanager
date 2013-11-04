package com.search.manager.core.search.processor;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.search.manager.core.BaseIntegrationTest;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.SearchProcessor;
import com.search.manager.core.search.SearchResult;

public class SolrSearchProcessorTest extends BaseIntegrationTest {
	
	@Autowired
	@Qualifier("solrSearchProcessor")
	private SearchProcessor solrSearchProcessor;
	
	@Test
	public void SearchProcessorWiringTest() {
		assertNotNull(solrSearchProcessor);
	} 
	
	@Test
	public void searchTest() {
		Search search = new Search(BannerRuleItem.class);
		
		try {
			@SuppressWarnings("unchecked")
			SearchResult<BannerRuleItem> searchResult = (SearchResult<BannerRuleItem>) solrSearchProcessor.processSearch(search);
			
			if(searchResult != null) {
				System.out.println("size = " + searchResult.getTotalCount());
				
				List<BannerRuleItem> bannerRuleItems = searchResult.getResult();
				
				for(BannerRuleItem bannerRuleItem : bannerRuleItems) {
					System.out.println(bannerRuleItem.toJson());
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	
}
