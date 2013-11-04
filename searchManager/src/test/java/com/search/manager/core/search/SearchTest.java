package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.List;

import com.search.manager.core.model.BannerRuleItem;

public class SearchTest {

	public static void main(String[] args) {
		Search search = new Search(BannerRuleItem.class);
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("store"));
		fields.add(new Field("ruleId"));
		fields.add(new Field("ruleName"));
		fields.add(new Field("memberId"));
		fields.add(new Field("priority"));
		
		search.addFields(fields);
		
		// Paging
		search.setMaxResult(15);
		search.setPage(3);
		
		// Sort
		search.addSort(new Sort("priority"));
		
		System.out.println(search);
	}
	
}
