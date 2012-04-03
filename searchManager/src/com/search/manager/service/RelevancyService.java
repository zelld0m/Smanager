package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.SearchCriteria;
import com.search.manager.model.SearchCriteria.ExactMatch;
import com.search.manager.model.SearchCriteria.MatchType;
import com.search.manager.model.Store;
import com.search.manager.model.StoreKeyword;
import com.search.manager.schema.SchemaException;
import com.search.manager.schema.SolrSchemaUtility;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.Schema;
import com.search.manager.schema.model.bf.BoostFunctionModel;
import com.search.manager.schema.model.bq.BoostQueryModel;
import com.search.manager.schema.model.mm.MinimumToMatchModel;
import com.search.manager.schema.model.qf.QueryField;
import com.search.manager.schema.model.qf.QueryFieldsModel;
import com.search.manager.utility.DateAndTimeUtils;
import com.search.ws.SearchHelper;

@RemoteProxy(
		name = "RelevancyServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "relevancyService")
)
public class RelevancyService {
	private static final Logger logger = Logger.getLogger(RelevancyService.class);

	@Autowired private DaoService daoService;

	@RemoteMethod
	public Relevancy getById(String relevancyId){
		try {
			Relevancy relevancy = new Relevancy();
			relevancy.setRelevancyId(relevancyId);
			relevancy.setStore(new Store(UtilityService.getStoreName()));
			return daoService.getRelevancyDetails(relevancy);
		} catch (DaoException e) {
			logger.error("Failed during getAllByName()",e);
		}
		return null;
	}

	@RemoteMethod
	public int addOrUpdateRelevancyField(String relevancyId, String fieldName, String fieldValue) throws Exception{
		try {
			logger.info(String.format("%s %s %s", relevancyId, fieldName, fieldValue));
			Relevancy relevancy = new Relevancy();
			relevancy.setRelevancyId(relevancyId);
			relevancy.setStore(new Store(UtilityService.getStoreName()));
			relevancy.setLastModifiedBy(UtilityService.getUsername());

			RelevancyField relevancyField = new RelevancyField();

			//bq post-processing
			if (StringUtils.equalsIgnoreCase("bq", fieldName)){
				try {
					Schema schema = SolrSchemaUtility.getSchema();
					BoostQueryModel boostQueryModel = BoostQueryModel.toModel(schema, fieldValue, true);
					fieldValue = boostQueryModel.toString();
				} catch (SchemaException e) {
					throw e;
				}
			}
			
			//bf post-processing
			if (StringUtils.equalsIgnoreCase("bf", fieldName)){
				try {
					Schema schema = SolrSchemaUtility.getSchema();
					BoostFunctionModel.toModel(schema, fieldValue, true);
				} catch (SchemaException e) {
					throw e;
				}
			}

			relevancyField.setFieldName(fieldName);
			relevancyField.setFieldValue(fieldValue);
			relevancyField.setRelevancy(relevancy);

			return daoService.addOrUpdateRelevancyField(relevancyField);
		} catch (DaoException e) {
			logger.error("Failed during addOrUpdateRelevancyField()",e);
		}
		
		return 0;
	}

	@RemoteMethod
	public RecordSet<Relevancy> getAllByName(String name, int page, int itemsPerPage){
		try {
			logger.info(String.format("%s %d %d", name, page, itemsPerPage));
			Relevancy relevancy = new Relevancy();
			relevancy.setStore(new Store(UtilityService.getStoreName()));
			relevancy.setRelevancyName(name);
			SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy, null, null, page, itemsPerPage);
			return daoService.searchRelevancy(criteria, MatchType.LIKE_NAME);
		} catch (DaoException e) {
			logger.error("Failed during getAllByName()",e);
		}
		return null;
	}

	@RemoteMethod
	public RecordSet<Relevancy> getAll(){
		return getAllByName("", 0, 0);
	}

	@RemoteMethod
	public String addRelevancy(String name, String description , String startDate, String endDate){
		try {
			logger.info(String.format("%s %s %s %s", name, description, startDate, endDate));
			String store = UtilityService.getStoreName();
			Relevancy relevancy = new Relevancy();
			relevancy.setStore(new Store(store));
			relevancy.setRelevancyName(name);
			relevancy.setDescription(description);
			relevancy.setStartDate(StringUtils.isBlank(startDate) ? null : DateAndTimeUtils.toSQLDate(store, startDate));
			relevancy.setEndDate(StringUtils.isBlank(endDate) ? null : DateAndTimeUtils.toSQLDate(store, endDate));
			relevancy.setCreatedBy(UtilityService.getUsername());
			return StringUtils.trimToEmpty(daoService.addRelevancyAndGetId(relevancy));
		} catch (DaoException e) {
			logger.error("Failed during addRelevancy()",e);
		}
		return StringUtils.EMPTY;
	}
	
	@RemoteMethod
	public String addRelevancyByCloning(String relevancyId, String name, String startDate, String endDate, String description) throws Exception{
		String clonedId = StringUtils.EMPTY;
		Relevancy clonedRelevancy = new Relevancy();
		
		try {
		String store = UtilityService.getStoreName();
		Relevancy relevancy = new Relevancy();
		relevancy.setStore(new Store(store));
		relevancy.setRelevancyName(name);
		relevancy.setDescription(description);
		relevancy.setStartDate(StringUtils.isBlank(startDate) ? null : DateAndTimeUtils.toSQLDate(store, startDate));
		relevancy.setEndDate(StringUtils.isBlank(endDate) ? null : DateAndTimeUtils.toSQLDate(store, endDate));
		relevancy.setCreatedBy(UtilityService.getUsername());
		
		clonedId = StringUtils.trimToEmpty(daoService.addRelevancyAndGetId(relevancy));
		
		Relevancy hostRelevancy = getById(relevancyId);
		clonedRelevancy = getById(clonedId);
		
		Map<String, String> fields = hostRelevancy.getParameters();
		
		for (String key: fields.keySet()){
			try {
				addOrUpdateRelevancyField(clonedId, key, fields.get(key));
			} catch (Exception e) {
				daoService.deleteRelevancy(clonedRelevancy);
				throw e;
			}
		}
		
		} catch (DaoException e) {
			logger.error("Failed during addRelevancy()",e);
		}
		
		return clonedId;
	}

	@RemoteMethod
	public int updateRelevancy(String id, String name, String description , String startDate, String endDate){
		try {
			logger.info(String.format("%s %s %s %s %s", id, name, description, startDate, endDate));
			String store = UtilityService.getStoreName();
			Relevancy relevancy = new Relevancy();
			relevancy.setStore(new Store(store));
			relevancy.setRelevancyId(id);
			relevancy.setRelevancyName(name);
			relevancy.setDescription(description);
			relevancy.setStartDate(DateAndTimeUtils.toSQLDate(store, startDate));
			relevancy.setEndDate(DateAndTimeUtils.toSQLDate(store, endDate));
			relevancy.setLastModifiedBy(UtilityService.getUsername());
			return daoService.updateRelevancy(relevancy);
		} catch (DaoException e) {
			logger.error("Failed during addRelevancy()",e);
		}
		return 0;
	}

	@RemoteMethod
	public int deleteRelevancy(String relevancyId){
		try {
			Relevancy relevancy = new Relevancy();
			relevancy.setRelevancyId(relevancyId);
			relevancy.setStore(new Store(UtilityService.getStoreName()));
			return daoService.deleteRelevancy(relevancy);
		} catch (DaoException e) {
			logger.error("Failed during getAllByName()",e);
		}
		return 0;
	}

	@RemoteMethod
	public BoostQueryModel getValuesByString(String bq) {
		logger.info(String.format("%s", bq));
		Schema schema = SolrSchemaUtility.getSchema();
		BoostQueryModel boostQueryModel = new BoostQueryModel();
		
		try {
			boostQueryModel = BoostQueryModel.toModel(schema, bq, true);
		} catch (SchemaException e) {
			e.printStackTrace();
		}

		return boostQueryModel;
	}

	@RemoteMethod
	public RecordSet<String> getValuesByField(String keyword, int page, int itemsPerPage, String facetField, String[] excludeList) {
		logger.info(String.format("%s %d %d %s %s", keyword, page, itemsPerPage, facetField, Arrays.toString(excludeList)));

		String server = UtilityService.getServerName();
		String store = UtilityService.getStoreLabel();

		List<String> facetValues = SearchHelper.getFacetValues(server, store, facetField);

		if (ArrayUtils.isNotEmpty(excludeList)){	
			facetValues.remove(" ");
			facetValues.remove(StringUtils.EMPTY);
			facetValues.removeAll(Arrays.asList(excludeList));
		}


		List<String> searchList = new ArrayList<String>();

		for(String value: facetValues){
			if (StringUtils.isNotBlank(keyword) && StringUtils.containsIgnoreCase(value, keyword))
				searchList.add(value);
		}

		int fromIndex = (page-1)*itemsPerPage;
		int toIndex = (page*itemsPerPage)-1;

		if (StringUtils.isNotEmpty(keyword)){
			int maxIndex = searchList.size()- 1;
			return new RecordSet<String>(searchList.subList(fromIndex, toIndex>maxIndex ? maxIndex+1 : toIndex+1), searchList.size());
		}else{
			int maxIndex = facetValues.size()- 1;
			return new RecordSet<String>(facetValues.subList(fromIndex, toIndex>maxIndex ? maxIndex+1 : toIndex+1), facetValues.size());
		}
	}

	@RemoteMethod
	public RecordSet<Keyword> getKeywordInRule(String relevancyId, String keyword, int page, int itemsPerPage) {
		logger.info(String.format("%s %d %d", relevancyId, page, itemsPerPage));
		try{
			RelevancyKeyword rk = new RelevancyKeyword();
			Relevancy r = new Relevancy();
			r.setRelevancyId(relevancyId);
			//r.setStore(new Store(null)); //TODO: SP Fix
			rk.setRelevancy(r);
			rk.setKeyword(new Keyword(""));
			SearchCriteria<RelevancyKeyword> criteria = new SearchCriteria<RelevancyKeyword>(rk, null, null, 0, 0);

			RecordSet<RelevancyKeyword> rs = daoService.searchRelevancyKeywords(criteria, MatchType.MATCH_ID, ExactMatch.SIMILAR);
			List<RelevancyKeyword> list = new LinkedList<RelevancyKeyword>();
			list = rs.getList();
			List<RelevancyKeyword> matchedList = new LinkedList<RelevancyKeyword>();

			Iterator<RelevancyKeyword> iterator = list.iterator();
			while (iterator.hasNext()) {
				RelevancyKeyword relKey = (RelevancyKeyword) iterator.next();
				String key = relKey.getKeyword()==null? StringUtils.EMPTY : relKey.getKeyword().getKeyword();
				if (StringUtils.isNotBlank(keyword) && StringUtils.containsIgnoreCase(key, keyword)){
					matchedList.add(relKey);
				}
			}

			int maxIndex = 0;
			int fromIndex = (page-1)*itemsPerPage;
			int toIndex = (page*itemsPerPage)-1;

			
			RecordSet<RelevancyKeyword> relkeyRS = null;
			
			if (StringUtils.isNotBlank(keyword)){
				maxIndex = matchedList.size()- 1;
				relkeyRS = new RecordSet<RelevancyKeyword>(matchedList.subList(fromIndex, toIndex>maxIndex ? maxIndex+1 : toIndex+1), matchedList.size());
			}else{
				maxIndex = list.size()- 1;
				relkeyRS = new RecordSet<RelevancyKeyword>(list.subList(fromIndex, toIndex>maxIndex ? maxIndex+1 : toIndex+1), list.size());
			}
			
			List<Keyword> keywordList = new ArrayList<Keyword>();
			
			for(RelevancyKeyword relKey : relkeyRS.getList()){
				keywordList.add(relKey.getKeyword());
			}
			
			return new RecordSet<Keyword>(keywordList, relkeyRS.getTotalSize());
			
		} catch (DaoException e) {
			logger.error("Failed during getKeyword()",e);
		}
		return null;
	}

	@RemoteMethod
	public int addKeywordToRule(String relevancyId, String keywordId) {
		logger.info(String.format("%s %s", relevancyId, keywordId));
		try{
			Relevancy relevancy = new Relevancy(relevancyId);
			daoService.addKeyword(UtilityService.getStoreName(), keywordId);
			Keyword keyword =  new Keyword(keywordId);
			daoService.addRelevancyKeyword(new RelevancyKeyword(keyword, relevancy));
		} catch (DaoException e) {
			logger.error("Failed during addKeywordToRule()",e);
		}
		return 0;
	}

	@RemoteMethod
	public int deleteKeywordInRule(String relevancyId, String keywordId) {
		logger.info(String.format("%s %s", relevancyId, keywordId));
		try{
			Relevancy relevancy = new Relevancy(relevancyId);
			Keyword keyword =  new Keyword(keywordId);
			daoService.deleteRelevancyKeyword(new RelevancyKeyword(keyword, relevancy));
		} catch (DaoException e) {
			logger.error("Failed during deleteKeywordInRule()",e);
		}
		return 0;
	}

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
	
	@RemoteMethod
	public int getRelevancyCount(String keyword){
		try {
			StoreKeyword storeKeyword = new StoreKeyword(new Store(UtilityService.getStoreName()), new Keyword(keyword));
			return daoService.getRelevancyKeywordCount(storeKeyword);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public DaoService getDaoService() {
		return daoService;
	} 

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}
}