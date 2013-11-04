package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.List;

import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.search.processor.SolrSearchProcessor;
import com.search.manager.core.search.processor.SqlSearchProcessor;
import com.search.manager.solr.util.SolrServerFactory;

public class SearchProcessorTest {
	
	private static SearchProcessor sqlSearchProcessor;
	private static SearchProcessor solrSearchProcessor;
	
	public static SolrServerFactory solrServerFactory;
	
	public static void main(String[] args) {
		solrSearchProcessor = new SolrSearchProcessor(solrServerFactory);
		sqlSearchProcessor = new SqlSearchProcessor("TODO");
		
		try {
			basicQuery();
			basicFieldQuery();
			basicFilter();
			basicSort();
			
			// test query
			testQuery1();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void basicQuery() throws Exception {
		Search search = new Search(BannerRuleItem.class);
		String query = "";

		query = sqlSearchProcessor.generateStrQuery(search);
		System.out.println("sql: " + query);

		query = solrSearchProcessor.generateStrQuery(search);
		System.out.println("solr: " + query);
	}

	// Test Select Clause(fl)
	public static void basicFieldQuery() throws Exception {
		Search search = new Search(BannerRuleItem.class);
		String query = "";

		// Fields
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("id"));
		fields.add(new Field("store"));
		fields.add(new Field("ruleId"));
		fields.add(new Field("ruleName"));
		fields.add(new Field("memberId"));
		fields.add(new Field("priority"));
		fields.add(new Field("startDate"));
		fields.add(new Field("endDate"));
		fields.add(new Field("imageAlt"));
		fields.add(new Field("linkPath"));
		fields.add(new Field("openNewWindow"));
		search.addFields(fields);

		query = sqlSearchProcessor.generateStrQuery(search);
		System.out.println("\nsql: " + query);

		query = solrSearchProcessor.generateStrQuery(search);
		System.out.println("solr: " + query);
	}

	public static void basicFilter() throws Exception {
		Search search = new Search(BannerRuleItem.class);
		String query = "";

		// Filters
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.like("store", "macmall"));
		filters.add(Filter.equal("disabled", false));
		filters.add(Filter.greaterOrEqual("priority", 1));
		search.addFilters(filters);

		query = sqlSearchProcessor.generateStrQuery(search);
		System.out.println("\nsql: " + query);

		query = solrSearchProcessor.generateStrQuery(search);
		System.out.println("solr: " + query);
	}

	public static void basicSort() throws Exception {
		Search search = new Search(BannerRuleItem.class);
		String query = "";
		
		// Sorts
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("store"));
		sorts.add(new Sort("priority", true));
		search.addSorts(sorts);

		query = sqlSearchProcessor.generateStrQuery(search);
		System.out.println("\nsql: " + query);

		query = solrSearchProcessor.generateStrQuery(search);
		System.out.println("solr: " + query);
	}

	public static void testQuery1() throws Exception {
		Search search = new Search(BannerRuleItem.class);
		String query = "";
		
		// Fields
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("store"));
		fields.add(new Field("ruleName"));
		search.addFields(fields);
		
		// Filters
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.equal("store", "macmall"));
		filters.add(Filter.equal("priority", 1));
		search.addFilters(filters);
		
		// Sorts
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("priority", true));
		search.addSorts(sorts);
		
		query = sqlSearchProcessor.generateStrQuery(search);
		System.out.println("\nsql: " + query);

		query = solrSearchProcessor.generateStrQuery(search);
		System.out.println("solr: " + query);
	}
	
}
