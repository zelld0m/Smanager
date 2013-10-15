package com.search.manager.core.search;

public class TestField {

	public static void main (String[] args) {
		Field field = new Field("salary");
		System.out.println("sql  = " + field);
		System.out.println("solr = " + field.toStringSolr());
		
		field = new Field("salary", "total");
		System.out.println("sql  = " + field);
		System.out.println("solr = " + field.toStringSolr());
		
		field = new Field("salary", Field.OP_SUM);
		System.out.println("sql  = " + field);
		System.out.println("solr = " + field.toStringSolr());
		
		field = new Field("salary", Field.OP_SUM, "total");
		System.out.println("sql  = " + field);
		System.out.println("solr = " + field.toStringSolr());
	}
}
