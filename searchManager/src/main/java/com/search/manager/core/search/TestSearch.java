package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.search.manager.model.ExcludeResult;

public class TestSearch {
	
	public static void main(String[] args) throws Exception {
		Search search = new Search(ExcludeResult.class);
		search.setDisjunction(true);
		
		List<Field> fields = new ArrayList<Field>();
		//fields.add(new Field("location", Field.OP_SUM, "total_location"));
//		fields.add(new Field("location", "total_location"));
		fields.add(new Field("forceAdd"));
		fields.add(new Field("store"));
		fields.add(new Field("keyword"));
		
		List<String> storeFilters = new ArrayList<String>();
		storeFilters.add("pcmall");
		storeFilters.add("macmall");
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter("location", "1", Filter.OP_GREATER_THAN));
		filters.add(new Filter("forceAdd", false));
		filters.add(new Filter("store", storeFilters, Filter.OP_IN));
		filters.add(new Filter("keyword1", "test101"));
		
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("store", true));
		sorts.add(new Sort("location", true));
		
		search.addFields(fields);
		search.setFilters(filters);
		search.setSorts(sorts);
		
		SolrServer sorlServer = new HttpSolrServer("");
		SolrSearchProcessor solrSearchProcessor = new SolrSearchProcessor(sorlServer);
		SqlSearchProcessor sqlSearchProcessor = new SqlSearchProcessor(search);
		System.out.println(solrSearchProcessor.generateStrQuery(search));
		System.out.println(sqlSearchProcessor.generateStrQuery(search));
	}

}
