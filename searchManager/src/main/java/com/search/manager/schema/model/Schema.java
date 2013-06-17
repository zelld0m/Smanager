package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Schema implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, FieldType> fieldTypes = new LinkedHashMap<String, FieldType>();
	private Map<String, Field> fields = new LinkedHashMap<String, Field>();
	
	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}

	public List<Field> getFields() {
		return new ArrayList<Field>(fields.values());
	}
	
	public Field getField(String fieldName) {
		return fields.get(fieldName);
	}
	
	public List<Field> getNumericFields() {
		List<Field> fields2 = new ArrayList<Field>();
		for (Field field: fields.values()) {
			if (field.getGenericType() == GenericType.NUMERIC) {
				fields2.add(field);
			}
		}
		return fields2;
	}
	
	public List<Field> getIndexedFields() {
		List<Field> fields2 = new ArrayList<Field>();
		for (Field field: fields.values()) {
			if (field.isIndexed()) {
				fields2.add(field);
			}
		}
		return fields2;
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> getIndexedFields(String keyword, List<Field> excludeFields) {
		List<Field> fields2 = new ArrayList<Field>();
		
		List<String> excludeFieldNames = (List<String>) CollectionUtils.collect(excludeFields, new Transformer() {  
		    @Override  
			public Object transform(Object o) {  
		          return (String)((Field) o).getName();  
		    }  
		});  
		
		for (Field field: fields.values()) {
			if (field.isIndexed() && !excludeFieldNames.contains(field.getName()) && StringUtils.containsIgnoreCase(field.getName(), keyword)) {
				fields2.add(field);
			}
		}
		return fields2;
	}
	
	public void setFieldTypes(Map<String, FieldType> fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	public Map<String, FieldType> getFieldTypes() {
		return fieldTypes;
	}
	
}
