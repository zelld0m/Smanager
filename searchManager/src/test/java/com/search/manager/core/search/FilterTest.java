package com.search.manager.core.search;

import java.util.ArrayList;
import java.util.List;

public class FilterTest {

	public static void main(String[] args) {
		List<String> testIds = new ArrayList<String>();
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter1 = Filter.equal("id", "123");
		Filter filter2 = Filter.greaterThan("location", 2);
		Filter filter3 =Filter.equal("forceAdd", false);
		Filter filter4 =Filter.like("store", "pcmall");
		Filter filter5 = Filter.isNotNull("keyword");
		Filter filter6 = Filter.isNotNull("entity");
		Filter filter7 = Filter.isNotNull("value");
		Filter filter8 = Filter.isNotNull("memberId");
							
		filters.add(filter1);
		filters.add(filter2);
		filters.add(filter3);
		filters.add(filter4);
		filters.add(filter5);
		filters.add(filter6);
		filters.add(filter7);
		filters.add(filter8);
		
		// EQUAL
		// Default operator
		Filter filter = new Filter("rule_name", "test123");
		System.out.println("EQUAL : " + filter);
		
		// NOT_EQUAL
		System.out.println("NOT_EQUAL : " + Filter.notEqual("rule_id", "123"));
		
		testIds.add("123");
		testIds.add("345");
		testIds.add("678");
		testIds.add("910");
		System.out.println("NOT_EQUAL : " + Filter.notEqual("rule_id", testIds));
		
		// LESS_THAN
		System.out.println("LESS_THAN : " + Filter.lessThan("location", 3));
		
		// GREATER_THAN
		System.out.println("GREATER_THAN : " + Filter.greaterThan("location", 0));
		
		// LESS_OR_EQUAL
		System.out.println("LESS_OR_EQUAL : " + Filter.lessOrEqual("location", 3));
		
		// GREATER_OR_EQUAL
		System.out.println("GREATER_OR_EQUAL : " + Filter.greaterOrEqual("location", 0));
		
		// LIKE
		System.out.println("LIKE : " + Filter.like("rule_name", "test%"));
		
		// IN
		System.out.println("IN : " + Filter.in("rule_id", testIds));
		
		// NOT_IN
		System.out.println("NOT_IN : " + Filter.notIn("rule_id", testIds));
		
		// NULL
		System.out.println("NULL : " + Filter.isNull("memberId"));
		
		// NOT_NULL
		System.out.println("NOT_NULL : " + Filter.isNotNull("memberId"));
		
		// EMPTY
		System.out.println("EMPTY : " + Filter.isEmpty("keyword"));
		
		// NOT_EMPTY
		System.out.println("NOT_EMPTY : " + Filter.isNotEmpty("keyword"));
		
		// AND
		System.out.println("AND : " + Filter.and(filter1));
		System.out.println("AND : " + Filter.and(filter1, filter2));
		System.out.println("AND : " + Filter.and(filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8));
		
		// OR
		System.out.println("OR : " + Filter.or(filter1));
		System.out.println("OR : " + Filter.or(filter1, filter2));
		System.out.println("OR : " + Filter.or(filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8));
		
		// And/Or
		Filter filterAnd = Filter.and(filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8);
		Filter filterOr = Filter.or(filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8);
		System.out.println("AND OR : " + Filter.or(filterAnd, filterOr));
		
		// NOT
		System.out.println("NOT : " + Filter.not(filter1));
		System.out.println("NOT : " + Filter.not(filterAnd));
		
		// SOME
		System.out.println("SOME : " + Filter.some("id", filter1));
		
		// ALL
		System.out.println("ALL : " + Filter.all("id", filter1));
		
		// NONE
		System.out.println("NONE : " + Filter.none("id", filter1));
		
		// CUSTOM
		// TODO
	}
}
