package com.search.manager.schema.model.bq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.search.manager.schema.RelevancyConfig;
import com.search.manager.schema.SchemaException;
import com.search.manager.schema.SolrSchemaUtility;
import com.search.manager.schema.model.BoostFactor;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.Schema;
import com.search.manager.schema.model.VerifiableModel;
import com.search.ws.ConfigManager;

public class BoostQueryModel implements VerifiableModel {

	private static final Logger logger = Logger.getLogger(BoostQueryModel.class);
	
	private static final long serialVersionUID = 1L;

	private List<BoostQuery> expression = new ArrayList<BoostQuery>();
	
	private static int countInstances(String string, String searchString) {
		int num = 0;
		if (StringUtils.isNotEmpty(string)) {
			int stringLength = string.length();
			int searchStringLength = searchString.length();
			int i = 0;
			while (i < stringLength) {
				i = string.indexOf(searchString, i);
				if (i >= 0) {
					num++;
					i += searchStringLength;
				}
				else {
					break;
				}
			}
		}
		return num;
	}
	
	private static Expression getExpression(String subExpression, List<Expression> expressions) {
		Pattern p = Pattern.compile("%(.*)%");
		Matcher m2 = p.matcher(subExpression);
		if (m2.matches()) {
			return expressions.get(Integer.parseInt(m2.group(1)) - 1);
		}
		return null;
	}

	private static boolean containsNonEscapedOrNonQuotedSpaces(String string) {
		boolean spaceFound = false;
		int start = string.indexOf('"');
		int end = 0;
		if (start >= 0) {
			while (start >= 0) {
				if (start != 0) {
					spaceFound = Pattern.matches(".*[^\\\\] .*", string.substring(end, start));
				}
				end = string.indexOf('"', start+1);
				start = string.indexOf('"', ++end);
				if (start < 0) {
					spaceFound = Pattern.matches(".*[^\\\\] .*", string.substring(end));
				}
			}
		}
		else {
			spaceFound = Pattern.matches(".*[^\\\\] .*", string);
		}
		return spaceFound;
	}
	
	@SuppressWarnings("unchecked")
	private static Object generateSubExpression(String subExpression, LinkedList<Expression> expressions) throws SchemaException {
		Object value = getExpression(subExpression, expressions);
		if (value == null) {
			value = generateExpression(subExpression, expressions);
		}
		if (value == null) {
			if (containsNonEscapedOrNonQuotedSpaces(subExpression)) {
				throw new SchemaException("Value contains non-escaped/non-quoted spaces: " + subExpression);
			}
			// TODO: check that there are no spaces here
			value = subExpression;
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private static Expression generateExpression(String string, LinkedList<Expression> expressions) throws SchemaException {
		Expression expression = null;
		for (Operator operator: RelevancyConfig.getInstance().getAllFilterOperators()) {
			String regexp = operator.getRegExp();
			if (regexp != null) {
				Matcher m = Pattern.compile(operator.getRegExp()).matcher(string);
				if (m.matches()) {
					logger.debug("matched operator: " + operator.getName());
					
					Object lvalue = (m.groupCount() > 0) ? m.group(1) : null;
					if (lvalue != null) {
						lvalue = generateSubExpression((String)lvalue, expressions);
					}
					
					Object rvalue = (m.groupCount() > 1) ? m.group(2) : null;
					if (rvalue != null) {
						rvalue = generateSubExpression((String)rvalue, expressions);
					}
					
					expression = new Expression(operator, lvalue, rvalue);
					break;
				}
			}
		}
		return expression;
	}
	
	@SuppressWarnings("unchecked")
	private static Object generateSubFieldExpression(Schema schema, String subExpression,
			LinkedList<Expression> expressions, boolean validate) throws SchemaException {
		Object value = null;
		Pattern p = Pattern.compile("%(.*)%");
		Matcher m2 = p.matcher(subExpression);
		if (m2.matches()) {
			value = expressions.get(Integer.parseInt(m2.group(1)) - 1);
		}
		else {
			Expression expression = generateFieldExpression(schema, subExpression, expressions, validate);
			if (expression != null) {
				value = expression;
			}
			else  {
				int pos = getFieldPosition(subExpression);
				if (pos > 0) {
					String fieldName = subExpression.substring(0, pos);
					if (StringUtils.contains(fieldName, " ")) {
						throw new SchemaException("Invalid field: " + fieldName);
					}
					Field field = schema.getField(fieldName);
					if (field == null) {
						if (validate) {
							throw new SchemaException("Unknown field: " + fieldName);
						}
						else {
							field = new Field(fieldName, null, false, false);
						}
					}
					String filter = subExpression.substring(pos + 1);
					Expression filterExpression = null;
					Object fieldValue = filter;
					m2 = p.matcher(filter);
					if (m2.matches()) {
						filterExpression = expressions.get(Integer.parseInt(m2.group(1)) - 1);
						logger.debug("filter for field[" + field + "] is " + m2.group(1) + ": " + filterExpression);
					}
					else {
						if (containsNonEscapedOrNonQuotedSpaces(filter)) {
							throw new SchemaException("Value contains non-escaped/non-quoted spaces: " + filter);
						}
						filterExpression = new Expression(RelevancyConfig.getInstance().getLogicalOperator("group"), fieldValue);
						logger.debug("filter for field[" + field + "] is " + filterExpression);
					}
					value = new SubQuery(field, filterExpression);
				}
			}
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private static Expression generateFieldExpression(Schema schema, String string, LinkedList<Expression> expressions, boolean validate) throws SchemaException {
		Expression expression = null;
		for (Operator operator: RelevancyConfig.getInstance().getAllLogicalOperators()) {
			String regexp = operator.getRegExp();
			if (regexp != null) {
				Matcher m = Pattern.compile(operator.getRegExp()).matcher(string);
				logger.debug("trying to macth operator: " + operator.getName() + " with " + string);
				if (m.matches()) {
					logger.debug("matched operator: " + operator.getName());
					
					Object lvalue = (m.groupCount() > 0) ? m.group(1) : null;
					String strValue = (String)lvalue;
					if (lvalue != null) {
						logger.debug("lvalue is " + strValue);
						lvalue = generateSubFieldExpression(schema, strValue, expressions, validate);
						if (lvalue == null) {
							if (containsNonEscapedOrNonQuotedSpaces(strValue)) {
								throw new SchemaException("Value contains non-escaped/non-quoted spaces: " + strValue);
							}
							lvalue = strValue;
						}
					}
					
					Object rvalue = (m.groupCount() > 1) ? m.group(2) : null;
					strValue = (String)rvalue;
					if (rvalue != null) {
						logger.debug("rvalue is  " + strValue);
						rvalue = generateSubFieldExpression(schema, strValue, expressions, validate);
						if (rvalue == null) {
							if (containsNonEscapedOrNonQuotedSpaces(strValue)) {
								throw new SchemaException("Value contains non-escaped/non-quoted spaces: " + strValue);
							}
							rvalue = strValue;
						}
					}
					expression = new Expression(operator, lvalue, rvalue);
					break;
				}
			}
		}
		return expression;
	}
	
	private static int getFieldPosition(String string) {
		int pos = string.indexOf(":");
		while (pos > 0 && string.charAt(pos -1) == '*') {
			pos = string.indexOf(":", pos + 1);
		}
		return pos;
	}
	
	@SuppressWarnings("unchecked")
	public static BoostQueryModel toModel(Schema schema, String bq, boolean validate) throws SchemaException {
		BoostQueryModel model = new BoostQueryModel();
		
		while (StringUtils.isNotEmpty(bq)) {
			
			// Split by SubQuery
			// TODO: add escape for special characters. might not be necessaary though
			int boostPosition = bq.indexOf("^");
			if (boostPosition < 0) {
				throw new SchemaException("Missing boost factor.");
			}
			int end = bq.indexOf(" ", boostPosition + 1);
			if (end < 0) {
				end = bq.length();
			}
			
			// subQuery
			String strSubQuery = StringUtils.trim(bq.substring(0, boostPosition));
			// boost factor
			String boostFactor = bq.substring(boostPosition +  1, end);
			BoostQuery boostQuery = new BoostQuery();
			boostQuery.setBoost(new BoostFactor(boostFactor));
			
			// check matching parentheses
			if (countInstances(strSubQuery, "(") != countInstances(strSubQuery, ")")) {
				throw new SchemaException("Mismatched parenthesis: " + strSubQuery);
			}
			
			// generate the model
			logger.debug("current string: " + strSubQuery);
			LinkedList<Expression> expressionList = new LinkedList<Expression>();
			while (StringUtils.isNotBlank(strSubQuery)) {
				// get innermost matching "(" and ")" and substitute with a holder
				int openPos = strSubQuery.indexOf('(');
				int closePos = strSubQuery.indexOf(')');
				int matchStart = openPos;
				if (openPos >= 0) {
					while (openPos < closePos) {
						openPos = strSubQuery.indexOf('(', openPos + 1);
						if (openPos >= 0 && openPos < closePos) {
							matchStart = openPos;
						}
						else {
							break;
						}
					}
					String expression = strSubQuery.substring(matchStart, closePos + 1);
					logger.debug("***********************Evaluating: " + expression);
					Expression ex = null;
					int pos = getFieldPosition(expression);
					if (pos > 0) {
						ex = generateFieldExpression(schema, expression, expressionList, validate);
					}
					else {
						ex = generateExpression(expression, expressionList);
					}
					logger.debug("Adding expression: " + ex);
					expressionList.add(ex);
					strSubQuery = strSubQuery.replace(strSubQuery.substring(matchStart, closePos + 1), "%" + String.valueOf(expressionList.size()) + "%");
				}
				else {
					int pos = getFieldPosition(strSubQuery);
					if (pos > 0) {
						String fieldName = strSubQuery.substring(0, pos);
						if (StringUtils.contains(fieldName, " ")) {
							throw new SchemaException("Invalid field: " + fieldName);
						}
						Field field = schema.getField(fieldName);
						if (field == null) {
							if (validate) {
								throw new SchemaException("Unknown field: " + fieldName);
							}
							else {
								field = new Field(fieldName, null, false, false);
							}
						}

						SubQuery subQuery = null;
						String fieldValue = strSubQuery.substring(pos + 1);
						if (fieldValue.contains(" ")) {
							boolean match = false;
							if (!match) {
								Pattern p = Pattern.compile(".*(%(.*)%).*");
								while (true) {
									Matcher m2 = p.matcher(fieldValue);
									if (m2.matches()) {
										for (int i=0; i <= m2.groupCount(); i++) {
											logger.debug(i + ": " + m2.group(i));
										}
										fieldValue = fieldValue.replaceFirst(m2.group(1), expressionList.get(Integer.parseInt(m2.group(2)) - 1).toString());
									}
									else {
										break;
									}
								}
								throw new SchemaException("Invalid filter: " + fieldValue);
							}
							
						}
						Expression ex = getExpression(fieldValue, expressionList);
						// TODO: what happens if we remove this
						if (ex == null) {
							ex = new Expression(RelevancyConfig.getInstance().getLogicalOperator("group"), fieldValue);
						}
						subQuery = new SubQuery(field, ex);
						boostQuery.setExpression(new Expression(RelevancyConfig.getInstance().getLogicalOperator("group"), subQuery));
						logger.debug("Adding boostQuery: " + boostQuery);
						model.expression.add(boostQuery);
						break;
					}
					else {
						if (Pattern.matches("%.*%", strSubQuery)) {
							boostQuery.setExpression(expressionList.getLast());
							logger.debug("Adding boostQuery: " + boostQuery);
							model.expression.add(boostQuery);
							break;
						}
						else {
							expressionList.add(generateFieldExpression(schema, strSubQuery, expressionList, validate));
							strSubQuery = "%" + String.valueOf(expressionList.size()) + "%";
						}
					}
				}
			}
			
			// next
			bq = bq.substring(end++);
			if (StringUtils.isNotEmpty(bq)) {
				logger.debug("Next bq to process: " + bq);
			}
		}
		
		if (validate) {
			model.validate();
		}
		return model;
	}
	
	
	
	@Override
	public boolean validate() throws SchemaException {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public String toString() {
		return (expression == null) ? "" : expression.toString();
	}
	
	//TODO: Workaround methods for simple UI
	public boolean getIsManufacturerOnly(){
		if (CollectionUtils.isEmpty(expression)) return false;
		
		for (BoostQuery boostQuery:expression){
			try {
				Expression<SubQuery, SubQuery> expression = boostQuery.getExpression();
				SubQuery sq = expression.getLValue();
				if (!StringUtils.equalsIgnoreCase(sq.getField().getName(), "Manufacturer")) return false;
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean getIsCategoryOnly(){
		if (CollectionUtils.isEmpty(expression)) return false;
		
		for (BoostQuery boostQuery:expression){
			try {
				Expression<SubQuery, SubQuery> expression = boostQuery.getExpression();
				SubQuery sq = expression.getLValue();
				if (!StringUtils.equalsIgnoreCase(sq.getField().getName(), "Category")) return false;
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<String> getSelectedFacetValues(){
		Set<String> selectedFacetValues = new HashSet<String>();
		
		for (BoostQuery boostQuery:expression){
			try {
				Expression<SubQuery, SubQuery> expression = boostQuery.getExpression();
				SubQuery sq = expression.getLValue();
				String value = (String) sq.getExpression().getLValue();
				if (StringUtils.isNotBlank(value))
					selectedFacetValues.add(value);
			} catch (Exception e) {
				return new ArrayList<String>(new HashSet<String>());
			}
		}
		
		return new ArrayList<String>(selectedFacetValues);
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		Pattern p = Pattern.compile("(.*?) AND (.*?)");
		Matcher m = p.matcher("A AND B");
		if (m.matches()) {
			for (int i = 0; i <= m.groupCount(); i++) {
				logger.debug(m.group(i));
			}
		}
		
//		if (Pattern.matches(".*[^\\\\] .*", "[FROM A TO B]")) {
//			logger.debug("contains non-escaped space");
//		}

//		if (true) {
//			return;
//		}
		
    	ConfigManager.getInstance("solr.xml");
    	RelevancyConfig conf = RelevancyConfig.getInstance("relevancy.xml");
		
		for (Operator e: conf.getAllLogicalOperators()) {
			logger.debug(e.getName() + ": " + e.getText() + " : " + e.getRegExp());
		}
		for (Operator e: conf.getAllFilterOperators()) {
			logger.debug(e.getName() + ": " + e.getText() + " : " + e.getRegExp());
		}
		
		Expression a = new Expression(conf.getLogicalOperator("or"), "hello", "world");
		Expression b = new Expression(conf.getLogicalOperator("and"), "good morning", a);
		Expression c = new Expression(conf.getFilterOperator("contains"), "hello test");
		
		logger.debug(a);
		logger.debug(b);
		logger.debug(c);
		
		try {
			a.validate();
			b.validate();
			c.validate();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		
		
		Schema schema = SolrSchemaUtility.getSchema();
		String[] bqs =  {
//			"Manufacturer:Apple^10",
//			"(Manufacturer:Apple)^10",
			"(Manufacturer:Apple)^10 Manufacturer:Belkin^20"
//			"Manufacturer:(Apple AND Lenovo)^10",
//			"(Manufacturer:(Apple AND Lenovo))^10",
//			"Manufacturer:(*:* AND NOT Lenovo)^10",
//			"Manufacturer:(*Lenovo*)^10",
//			"Manufacturer:([* TO *])^10",
//			"Manufacturer:(*:* AND NOT [* TO *])^10",
//			"Manufacturer:([A TO B])^10",
//			"Manufacturer:({A TO B})^10",
//			"Manufacturer:(A~10)^10",
//			"Manufacturer:*A*^10",  // *A* treated as search term
//			"Manufacturer:A~10^10", // A~10 treated as search term
//			"Manufacturer:(*A*)^10",
//			"Manufacturer:(*A* AND B)^10",
//			"Manufacturer:(\"A B\"~10)^10",
//
//			//ok
//			"Manufacturer:IBM^10 Manufacturer:Apple^10",
//			"Manufacturer:Belkin^10 Manufacturer:(IBM AND Lenovo)^10 (Manufacturer:(Apple AND AppleCare))^10",
//			"(Manufacturer:(Apple AND AppleCare AND \"Belkin Systems\") AND Manufacturer:(IBM AND Lenovo) AND Manufacturer:HP)^10",
//			"*:* AND NOT (Category:Computers)^10",
//			"((Manufacturer:(Apple AND AppleCare) AND Manufacturer:Lenovo))^10 *:* AND NOT (Category:Computers)^10",
		};
		
		String[] errorbqs =  {
//				"Manufacturer:Apple AND Lenovo^10", // error: no grouping defined
//				"Manufacturer:Apple OR Lenovo^10", // error: no grouping defined
//				"Manufacturer:*:* AND NOT Lenovo^10", // error: no grouping defined
//				"Manufacturer:[* TO *]^10", // error: no grouping defined
//				"Manufacturer:[A TO B]^10", // error: no grouping defined
//				"Manufacturer:([FROM A TO B])^10", // error: value cannot be multiple words unless space is escaped by backslash
//				"Manufacturer:([A TO B B])^10", // error: value cannot be multiple words unless space is escaped by backslash
//				"Manufacturer:{A TO B}^10", // error: no grouping defined
//				"Manufacturer:\"A B\"~10^10", // error: no grouping defined
//
//				"(Manufacturer:Apple)", // error: no boost factor
//				"((Manufacturer:Apple)^10", // error: mismatched parentheses
//				"(Manufacturer:Apple)^10 (Manufacturer:Belkin))^20", // error: mismatched parentheses
//				"(Manufacturer:Apple)^10 AND Manufacturer:Belkin^20", // error: cannot have AND between boost queries
//				"Manufacturer:IBM Manufacturer:Apple^10", // error: no boost factor declared
//				"Manufacturer:IBM (Manufacturer:Apple)^10", // error: no boost factor declared
//				"Manufacturer:IBM (Manufacturer:Apple)^10 Manufacturer:Belkin^20", // error: no boost factor declared
//				"(Manufacturer:Apple Belkin)^10", // error: no field declared or value is not single
//				"((Manufacturer:(Apple AND AppleCare) AND Manufacturer:Lenovo))^10 AND *:* AND NOT (Category:Computers)^10" // TODO error: cannot have AND between boost queries
			};
		
		for (String bq: bqs) {
			try {
				BoostQueryModel model = BoostQueryModel.toModel(schema, bq, true);
				logger.info(model.toString() + " is valid.");
				logger.error(model.getSelectedFacetValues());
			} catch (Exception e) {
				logger.error(bq + " is invalid: " + e.getMessage(), e);
			}
		}
		
		for (String bq: errorbqs) {
			try {
				BoostQueryModel model = BoostQueryModel.toModel(schema, bq, true);
				logger.error(model.toString() + " is valid.");
			} catch (Exception e) {
				logger.info(bq + " is invalid: " + e.getMessage());
			}
		}

	}
	
}
