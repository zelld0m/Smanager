package com.search.manager.schema.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.analysis.TokenFilterFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.schema.CopyField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.RelevancyConfig;
import com.search.manager.schema.analyzer.model.AnalyzerComponent;
import com.search.manager.schema.analyzer.model.AnalyzerComponent.AnalyzerComponentType;

@DataTransferObject(converter = BeanConverter.class)
public class Schema implements Serializable {
	
	private static final Logger logger = Logger.getLogger(Schema.class);
	
	private static final long serialVersionUID = 1L;
	
	private Map<String,Field> fieldMap = new LinkedHashMap<String, Field>();
	
	public List<Field> getFields() {
		return new ArrayList<Field>(fieldMap.values());
	}
	
	public Field getField(String fieldName) {
		return fieldMap.get(fieldName);
	}
	
	public List<Field> getNumericFields() {
		List<Field> fields = new ArrayList<Field>();
		for (Field field: fieldMap.values()) {
			if (field.getGenericType() == GenericType.NUMERIC) {
				fields.add(field);
			}
		}
		return fields;
	}
	
	public List<Field> getIndexedFields() {
		List<Field> fields = new ArrayList<Field>();
		for (Field field: fieldMap.values()) {
			if (field.isIndexed()) {
				fields.add(field);
			}
		}
		return fields;
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> getIndexedFields(String keyword, List<Field> excludeFields) {
		List<Field> fields = new ArrayList<Field>();
		
		List<String> excludeFieldNames = (List<String>) CollectionUtils.collect(excludeFields, new Transformer() {  
		    @Override  
			public Object transform(Object o) {  
		          return (String)((Field) o).getName();  
		    }  
		});  
		
		for (Field field: fieldMap.values()) {
			if (field.isIndexed() && !excludeFieldNames.contains(field.getName()) && StringUtils.containsIgnoreCase(field.getName(), keyword)) {
				fields.add(field);
			}
		}
		return fields;
	}
	
	public Schema(IndexSchema indexSchema) {
	
		// Field list
		List<String> ignoredFields = RelevancyConfig.getInstance().getIgnoredFields();
		Map<String,FieldType> fieldTypeMap = new HashMap<String, FieldType>();
		// iterate over the declared fields in the schema
		for (SchemaField field: indexSchema.getFields().values()) {
			if (!field.indexed() || ignoredFields.contains(field.getName())) {
				// ignore non-indexed fields
				continue;
			}

			// fieldTypes + tokenizers
			String fieldTypeName = field.getType().getTypeName();
			if (fieldTypeMap.get(fieldTypeName) == null) {
				List<AnalyzerComponent> analyzerChain = new ArrayList<AnalyzerComponent>();
				// if not a token chain, then it's likely to be a DefaultAnalyzer with only 1 verbatim token produced
				if (field.getType().getQueryAnalyzer() instanceof TokenizerChain) {
					TokenFilterFactory[] tokenFactories = ((TokenizerChain)(field.getType().getQueryAnalyzer())).getTokenFilterFactories();
					int size = tokenFactories.length;
					if (size > 0) {
						String name = tokenFactories[0].getClass().getSimpleName();
						AnalyzerComponent component = RelevancyConfig.getInstance().getTokenizer(name);
						analyzerChain.add(component == null ? new AnalyzerComponent(name, null, null, AnalyzerComponentType.Tokenizer): component);
						
						for (int i = 1; i < size; i++) {
							component = RelevancyConfig.getInstance().getFilter(name);
							analyzerChain.add(component == null ? new AnalyzerComponent(name, null, null, AnalyzerComponentType.Filter): component);
						}
					}
				}
				String type = field.getType().getClass().getSimpleName();
				GenericType genericType = GenericType.STRING;
				if (StringUtils.containsIgnoreCase(type, "DateField")) {
					genericType = GenericType.DATE;
				}
				else if (StringUtils.containsIgnoreCase(type, "IntField") || StringUtils.containsIgnoreCase(type, "ShortField") ||
						StringUtils.containsIgnoreCase(type, "LongField") || StringUtils.containsIgnoreCase(type, "FloatField") ||
						StringUtils.containsIgnoreCase(type, "DoubleField")) {
					genericType = GenericType.NUMERIC;
				}
				logger.debug("field type: " + field.getType().getTypeName() + "; type: " + type + "; generic type: " + genericType);
				fieldTypeMap.put(fieldTypeName, new FieldType(fieldTypeName, genericType, analyzerChain));
			}
			fieldMap.put(field.getName(), new Field(field.getName(), fieldTypeMap.get(fieldTypeName), field.indexed(), field.stored()));
		}
		
		for (Field field: fieldMap.values()) {
			List<CopyField> copyFields = indexSchema.getCopyFieldsList(field.getName());
			if (copyFields != null) {
				for (CopyField copyfield: copyFields) {
					SchemaField destSchemaField = copyfield.getDestination();
					if (destSchemaField.indexed() && !ignoredFields.contains(destSchemaField.getName())) {
						Field destField = fieldMap.get(destSchemaField.getName());
						field.addRelatedField(destField);
						destField.addRelatedField(field);
					}
				}
			}
		}
		
		// add GenereicUser_Keyword
		fieldMap.put("GenericUser_Keywords", new Field("GenericUser_Keywords", fieldTypeMap.get("text"), true, true));
		
		// sort the entries
		List<String> keys = new ArrayList<String>(fieldMap.keySet());
		Collections.sort(keys, new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareToIgnoreCase(arg1);
			}
			
		});
		for (String key: keys) {
			fieldMap.put(key, fieldMap.remove(key));
		}
		
		if (logger.isDebugEnabled()) {
			for (String field: fieldMap.keySet()) {
				StringBuilder fieldInfo = new StringBuilder();
				fieldInfo.append("field name: " + field);// + " " + field.getType().getQueryAnalyzer());
				if (!fieldMap.get(field).getRelatedFields().isEmpty()) {
					fieldInfo.append("\trelated fields: ");
					for (Field copyField: fieldMap.get(field).getRelatedFields()) {
						fieldInfo.append(" " + copyField.getName());
					}
				}
				logger.debug(fieldInfo);
			}
			logger.debug("total fields: " + fieldMap.size());
		}
	}
	
	
	
}
