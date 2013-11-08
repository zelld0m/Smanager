package com.search.manager.core.search;

import junit.framework.Assert;

import org.junit.Test;

import com.search.manager.core.search.Field.FieldOperator;

public class FieldTest {

	@Test
	public void sqlFormat() {
		String columnName = "price";

		// PROPERTY
		Field field = new Field(columnName);
		Assert.assertEquals("`price`", field.toString());

		// AVG
		field = new Field(columnName, FieldOperator.AVG);
		Assert.assertEquals("AVG(`price`)", field.toString());

		field = new Field(columnName, FieldOperator.AVG, "ave_price");
		Assert.assertEquals("AVG(`price`) as `ave_price`", field.toString());

		// COUNT
		field = new Field(columnName, FieldOperator.COUNT);
		Assert.assertEquals("COUNT(`price`)", field.toString());

		field = new Field(columnName, FieldOperator.COUNT, "count_price");
		Assert.assertEquals("COUNT(`price`) as `count_price`", field.toString());

		// COUNT_DISTINCT
		field = new Field(columnName, FieldOperator.COUNT_DISTINCT);
		Assert.assertEquals("COUNT_DISTINCT(`price`)", field.toString());

		field = new Field(columnName, FieldOperator.COUNT_DISTINCT,
				"count_distinct_price");
		Assert.assertEquals(
				"COUNT_DISTINCT(`price`) as `count_distinct_price`",
				field.toString());

		// MAX
		field = new Field(columnName, FieldOperator.MAX);
		Assert.assertEquals("MAX(`price`)", field.toString());

		field = new Field(columnName, FieldOperator.MAX, "max_price");
		Assert.assertEquals("MAX(`price`) as `max_price`", field.toString());

		// MIN
		field = new Field(columnName, FieldOperator.MIN);
		Assert.assertEquals("MIN(`price`)", field.toString());

		field = new Field(columnName, FieldOperator.MIN, "min_price");
		Assert.assertEquals("MIN(`price`) as `min_price`", field.toString());

		// SUM
		field = new Field(columnName, FieldOperator.SUM);
		Assert.assertEquals("SUM(`price`)", field.toString());

		field = new Field(columnName, FieldOperator.SUM, "total_price");
		Assert.assertEquals("SUM(`price`) as `total_price`", field.toString());

		// CUSTOM
		field = new Field("my_" + columnName, FieldOperator.CUSTOM);
		// TODO Assert.assertEquals("CUSTOM: `my_price`", field.toString());

		field = new Field("my_" + columnName, FieldOperator.CUSTOM,
				"my_custom_price");
		// TODO Assert.assertEquals("CUSTOM: `my_price` as `my_custom_price`",
		// field.toString());
	}
}
