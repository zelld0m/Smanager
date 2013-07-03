package com.search.manager.schema.model.qf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.BoostFactor;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.Schema;

@DataTransferObject(converter = BeanConverter.class)
public class QueryFieldsModel implements QueryFieldModelComponent {

	private static final long serialVersionUID = 1L;
	
	private List<QueryField> queryFields = new ArrayList<QueryField>();
	
	public List<QueryField> getQueryFields() {
		return new ArrayList<QueryField>(queryFields);
	}
	
	public void addQueryField(QueryField queryField) {
		queryFields.add(queryField);
	}
	
	public void removeQueryField(QueryField queryField) {
		queryFields.remove(queryField);
	}
	
	public static QueryFieldsModel toModel(Schema schema, String queryFields, boolean validate) throws SchemaException {
		QueryFieldsModel model = new QueryFieldsModel();
		if (StringUtils.isEmpty(queryFields)) {
			return model;
		}
		
//		if (Pattern.compile("(?:\\w+\\^\\d+(?:\\.\\d+)?)(?:\\s+\\w+\\^\\d+(?:\\.\\d+)?)*").matcher(queryFields).matches()) {
			Pattern p = Pattern.compile("(\\w+)\\^(\\S*)\\s*(.*)");
			while (!StringUtils.isEmpty(queryFields)) {
				Matcher matcher = p.matcher(queryFields);
				if (matcher.matches()) {
					String fieldName = matcher.group(1);
					Field field = schema.getField(fieldName);
					if (field == null) {
						throw new SchemaException("Unknown field: " + fieldName);
					}
					model.addQueryField(new QueryField(field, new BoostFactor(matcher.group(2))));
					queryFields = StringUtils.trim(matcher.group(3));
				}
				else {
					throw new SchemaException("Unrecognized pattern: " + queryFields);
				}
			}
//		}
//		else {
//			throw new SchemaException("Invalid format: " + queryFields);
//		}
		if (validate) {
			model.validate();
		}
		return model;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (queryFields.size() > 0) {
			for (QueryField queryField: queryFields) {
				builder.append(queryField).append(" ");
			}
			builder.deleteCharAt(builder.length() - 1);
			
		}
		return builder.toString();
	}

	@Override
	public boolean validate() throws SchemaException {
		List<String> list = new ArrayList<String>();
		for (QueryField queryField: queryFields) {
			queryField.validate();
			String fieldName = queryField.getField().getName();
			if (list.contains(fieldName)) {
				throw new SchemaException("Multiple declaration of field: " + fieldName);
			}
			else {
				list.add(fieldName);
			}
		}
		return true;
	}
	
}
