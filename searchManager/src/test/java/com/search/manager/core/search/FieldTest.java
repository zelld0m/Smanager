package com.search.manager.core.search;

import com.search.manager.core.search.Field.FieldOperator;

public class FieldTest {

	public static void main (String[] args) {
		String columnName = "price";
		
		// PROPERTY
		Field field = new Field(columnName);
		System.out.println("sql = " + field);
		
		// AVG
		field = new Field(columnName, FieldOperator.AVG);
		System.out.println("sql = " + field);
		
		field = new Field(columnName, FieldOperator.AVG, "ave_price");
		System.out.println("sql = " + field);
		
		// COUNT
		field = new Field(columnName, FieldOperator.COUNT);
		System.out.println("sql = " + field);
		
		field = new Field(columnName, FieldOperator.COUNT, "count_price");
		System.out.println("sql = " + field);
		
		// COUNT_DISTINCT
		field = new Field(columnName, FieldOperator.COUNT_DISTINCT);
		System.out.println("sql = " + field);
		
		field = new Field(columnName, FieldOperator.COUNT_DISTINCT, "count_distinct_price");
		System.out.println("sql = " + field);
		
		// MAX
		field = new Field(columnName, FieldOperator.MAX);
		System.out.println("sql = " + field);
		
		field = new Field(columnName, FieldOperator.MAX, "max_price");
		System.out.println("sql = " + field);
		
		// MIN
		field = new Field(columnName, FieldOperator.MIN);
		System.out.println("sql = " + field);
		
		field = new Field(columnName, FieldOperator.MIN, "min_price");
		System.out.println("sql = " + field);
		
		// SUM
		field = new Field(columnName, FieldOperator.SUM);
		System.out.println("sql = " + field);
		
		field = new Field(columnName, FieldOperator.SUM, "total_price");
		System.out.println("sql = " + field);
		
		// CUSTOM
		field = new Field("my_" + columnName, FieldOperator.CUSTOM);
		System.out.println("sql = " + field);
		
		field = new Field("my_"+ columnName, FieldOperator.CUSTOM, "my_custom_price");
		System.out.println("sql = " + field);
	}
}
