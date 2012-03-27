package com.search.manager.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;

import com.search.manager.model.RecordSet;
import com.search.manager.schema.SchemaException;
import com.search.manager.schema.SolrSchemaUtility;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.Schema;
import com.search.manager.schema.model.mm.MinimumToMatchModel;
import com.search.manager.schema.model.qf.QueryField;
import com.search.manager.schema.model.qf.QueryFieldsModel;

@RemoteProxy(
		name = "SchemaServiceJS",
	    creator = SpringCreator.class,
	    creatorParams = @Param(name = "beanName", value = "schemaService")
	)
public class SchemaService {
	private static final Logger logger = Logger.getLogger(SchemaService.class);

	@RemoteMethod
	public MinimumToMatchModel getMinShouldMatch(String fieldValue) {
		MinimumToMatchModel minToMatchModel = null;
		logger.info(String.format("%s", fieldValue));
		try {
			minToMatchModel = MinimumToMatchModel.toModel(fieldValue, true);
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		
		return minToMatchModel;
	}
	
	@RemoteMethod
	public RecordSet<QueryField> getQueryFields(String fieldValue) {
		List<QueryField> qFieldList = new ArrayList<QueryField>();
		logger.info(String.format("%s", fieldValue));
		try {
			Schema schema = SolrSchemaUtility.getSchema();
			QueryFieldsModel qFieldModel = QueryFieldsModel.toModel(schema, fieldValue, true);
			if (qFieldModel!=null) qFieldList = qFieldModel.getQueryFields();
		} catch (SchemaException e) {
			e.printStackTrace();
		}
		
		return new RecordSet<QueryField>(qFieldList,qFieldList.size());
	}
	
	@RemoteMethod
	public RecordSet<Field> getIndexedFields(int page, int itemsPerPage, String keyword, String[] excludedFields) {
		Schema schema = SolrSchemaUtility.getSchema();
		
		List<Field> excludeFieldList = new ArrayList<Field>();
		
		for (String string: excludedFields) {
			Field field = schema.getField(string);
			List<Field> relatedFields = field.getRelatedFields();
			excludeFieldList.add(field);
			if (CollectionUtils.isNotEmpty(relatedFields)) excludeFieldList.addAll(relatedFields);
		}
		
		List<Field> fields = new LinkedList<Field>(schema.getIndexedFields(keyword, excludeFieldList));
		int maxIndex = fields.size()- 1;
		int fromIndex = (page-1)*itemsPerPage;
	    int toIndex = (page*itemsPerPage)-1;
		return new RecordSet<Field>(fields.subList(fromIndex, toIndex>maxIndex ? maxIndex+1 : toIndex+1), fields.size());
	}
}
