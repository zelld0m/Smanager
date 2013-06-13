package com.search.manager.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.Keyword;
import com.search.manager.model.RecordSet;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.RuleStatus;
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
import com.search.ws.SearchHelper;

@Service(value = "relevancyService")
@RemoteProxy(
		name = "RelevancyServiceJS",
		creator = SpringCreator.class,
		creatorParams = @Param(name = "beanName", value = "relevancyService")
)
public class RelevancyService extends RuleService{
	private static final Logger logger = Logger.getLogger(RelevancyService.class);

	@Autowired private DaoService daoService;

	@Override
	public RuleEntity getRuleEntity() {
		return RuleEntity.RANKING_RULE;
	}

	@RemoteMethod
	public Relevancy getRule(String ruleId){
		try {
			Relevancy rule = new Relevancy(ruleId);
			rule.setStore(new Store(UtilityService.getStoreId()));
			rule = daoService.getRelevancyDetails(rule);
			// TODO: probably create a new method. one for Approval page. Another for Simulator and Top Keywords
			List<RelevancyKeyword> relKWList = daoService.getRelevancyKeywords(rule).getList();
			rule.setRelKeyword(relKWList);
			return rule;
		} catch (DaoException e) {
			logger.error("Failed during getRule()",e);
		}
		return null;
	}

	@RemoteMethod
	public List<String> checkForRuleNameDuplicates(String[] ruleIds, String[] ruleNames) throws DaoException {
		List<String> duplicateRuleNames = new ArrayList<String>();
		for (int i = 0; i < ruleIds.length; i++) {
			String ruleName = ruleNames[i];
			if (checkForRuleNameDuplicate(ruleIds[i], ruleName)) {
				duplicateRuleNames.add(ruleName);				
			}
		}
		return duplicateRuleNames;
	}

	@RemoteMethod
	public boolean checkForRuleNameDuplicate(String ruleId, String ruleName) throws DaoException {
		Relevancy relevancy = new Relevancy();
		relevancy.setStore(new Store(UtilityService.getStoreId()));
		relevancy.setRelevancyName(ruleName);
		SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy, null, null, 0, 0);
		RecordSet<Relevancy> set = daoService.searchRelevancy(criteria, MatchType.MATCH_NAME);
		if (set.getTotalSize() > 0) {
			for (Relevancy r: set.getList()) {
				if (StringUtils.equals(StringUtils.trim(ruleName), StringUtils.trim(r.getRelevancyName()))) {
					if (StringUtils.isBlank(ruleId) || !StringUtils.equals(ruleId, r.getRelevancyId())){
						return true;						
					}
				}
			}
		}
		return false;
	}

	@RemoteMethod
	public int addRuleFieldValue(String relevancyId, String fieldName, String fieldValue) throws Exception{
		try {
			logger.info(String.format("%s %s %s", relevancyId, fieldName, fieldValue));
			Relevancy relevancy = new Relevancy();
			relevancy.setRelevancyId(relevancyId);
			relevancy.setStore(new Store(UtilityService.getStoreId()));
			relevancy.setLastModifiedBy(UtilityService.getUsername());

			RelevancyField relevancyField = new RelevancyField();

			//bq post-processing
			if (StringUtils.equalsIgnoreCase("bq", fieldName)){
				try {
					Schema schema = SolrSchemaUtility.getSchema(UtilityService.getServerName(), UtilityService.getStoreId());
					BoostQueryModel boostQueryModel = BoostQueryModel.toModel(schema, fieldValue, true);
					fieldValue = boostQueryModel.toString();
				} catch (SchemaException e) {
					logger.error("Failed during addRuleFieldValue()",e);
					return 0;
				}
			}

			//bf post-processing
			if (StringUtils.equalsIgnoreCase("bf", fieldName)){
				try {
					Schema schema = SolrSchemaUtility.getSchema(UtilityService.getServerName(), UtilityService.getStoreId());
					BoostFunctionModel.toModel(schema, fieldValue, true);
				}catch (SchemaException e) {
					logger.error("Failed during addOrUpdateRelevancyField()",e);
					return 0;
				}catch (Exception e) {
					logger.error("Failed during addOrUpdateRelevancyField()",e);
					return 0;
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
	public RecordSet<Relevancy> getAllRule(String name, int page, int itemsPerPage){
		try {
			logger.info(String.format("%s %d %d", name, page, itemsPerPage));
			Relevancy relevancy = new Relevancy();
			relevancy.setStore(new Store(UtilityService.getStoreId()));
			relevancy.setRelevancyName(name);
			SearchCriteria<Relevancy> criteria = new SearchCriteria<Relevancy>(relevancy, null, null, page, itemsPerPage);

			return daoService.searchRelevancy(criteria, MatchType.LIKE_NAME);
		} catch (DaoException e) {
			logger.error("Failed during getAllByName()",e);
		}
		return null;
	}

	@RemoteMethod
	public Relevancy cloneRule(String ruleId, String name, String startDate, String endDate, String description) throws Exception{
		String clonedId = StringUtils.EMPTY;
		Relevancy clonedRelevancy = null;
		String userName = UtilityService.getUsername();
		if(ruleId.equalsIgnoreCase(""))
			ruleId=UtilityService.getStoreId()+"_default";
		try {
			String storeId = UtilityService.getStoreId();
			Relevancy relevancy = new Relevancy();
			relevancy.setStore(new Store(storeId));
			relevancy.setRelevancyName(name);
			relevancy.setDescription(description);
			relevancy.setStartDateTime(StringUtils.isBlank(startDate) ? null :  JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, startDate, JodaPatternType.DATE));
			relevancy.setEndDateTime(StringUtils.isBlank(endDate) ? null : JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, endDate, JodaPatternType.DATE));
			relevancy.setCreatedBy(userName);
			clonedId = StringUtils.trimToEmpty(daoService.addRelevancyAndGetId(relevancy));
			Relevancy hostRelevancy = getRule(ruleId);
			Map<String, String> fields = hostRelevancy.getParameters();
			for (String key: fields.keySet()){
				try {
					addRuleFieldValue(clonedId, key, fields.get(key));
				} catch (Exception e) {
					daoService.deleteRelevancy(clonedRelevancy);
					logger.error("Failed during cloneRule()",e);
				}
			}

			try {
				daoService.addRuleStatus(new RuleStatus(RuleEntity.RANKING_RULE, storeId, clonedId, name, 
						userName, userName, RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED));
			} catch (DaoException de) {
				logger.error("Failed to create rule status for ranking rule: " + name);
			}

			clonedRelevancy = getRule(clonedId);
		} catch (DaoException e) {
			logger.error("Failed during addRelevancy()",e);
		}
		return clonedRelevancy;
	}

	@RemoteMethod
	public int updateRule(String id, String name, String description , String startDate, String endDate){
		try {
			logger.info(String.format("%s %s %s %s %s", id, name, description, startDate, endDate));
			String storeId = UtilityService.getStoreId();
			Relevancy rule = new Relevancy();
			rule.setStore(new Store(storeId));
			rule.setRuleId(id);
			rule.setRuleName(name);
			rule.setDescription(description);
			rule.setStartDateTime(JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, startDate, JodaPatternType.DATE));
			rule.setEndDateTime(JodaDateTimeUtil.toDateTimeFromStorePattern(storeId, endDate, JodaPatternType.DATE));
			rule.setLastModifiedBy(UtilityService.getUsername());
			return daoService.updateRelevancy(rule);
		} catch (DaoException e) {
			logger.error("Failed during addRelevancy()",e);
		}
		return 0;
	}

	@RemoteMethod
	public int deleteRule(String ruleId){
		try {
			try {
				daoService.createRuleVersion(UtilityService.getStoreId(), RuleEntity.RANKING_RULE, ruleId, UtilityService.getUsername(), "Deleted Rule", "Deleted Rule");
			} catch (Exception e) {
				logger.error("Error creating backup. " + e.getMessage());
			}
			String username = UtilityService.getUsername();
			Relevancy rule = new Relevancy();
			rule.setRuleId(ruleId);
			String storeName = UtilityService.getStoreId();
			rule.setStore(new Store(storeName));
			rule.setLastModifiedBy(username);
			int status = daoService.deleteRelevancy(rule);
			if (status > 0) {
				RuleStatus ruleStatus = new RuleStatus();
				ruleStatus.setRuleTypeId(RuleEntity.RANKING_RULE.getCode());
				ruleStatus.setRuleRefId(rule.getRuleId());
				ruleStatus.setStoreId(storeName);
				daoService.updateRuleStatusDeletedInfo(ruleStatus, username);
			}
			return status;
		} catch (DaoException e) {
			logger.error("Failed during getAllByName()",e);
		}
		return 0;
	}

	@RemoteMethod
	public BoostQueryModel getValuesByString(String bq) {
		logger.info(String.format("%s", bq));
		Schema schema = SolrSchemaUtility.getSchema(UtilityService.getServerName(), UtilityService.getStoreId());
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
		String store = UtilityService.getStoreId();

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
	public RecordSet<Keyword> getAllKeywordInRule(String ruleId) {
		return getAllKeywordInRule(ruleId, "", 0, 0);
	}

	@RemoteMethod
	public RecordSet<Keyword> getAllKeywordInRule(String ruleId, String keyword, int page, int itemsPerPage) {
		logger.info(String.format("%s %d %d", ruleId, page, itemsPerPage));
		try{
			RelevancyKeyword rk = new RelevancyKeyword();
			Relevancy r = new Relevancy();
			r.setRelevancyId(ruleId);
			r.setStore(new Store(UtilityService.getStoreId()));
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

			if(page==0 && itemsPerPage==0){
				fromIndex = 0;
				toIndex = rs.getTotalSize()-1;
			}

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
		int result = -1;
		try{
			Relevancy relevancy = new Relevancy(relevancyId);
			relevancy.setStore(new Store(UtilityService.getStoreId()));
			daoService.addKeyword(new StoreKeyword(UtilityService.getStoreId(), keywordId));
			Keyword keyword =  new Keyword(keywordId);
			return daoService.addRelevancyKeyword(new RelevancyKeyword(keyword, relevancy));
		} catch (DaoException e) {
			logger.error("Failed during addKeywordToRule()",e);
		}
		return result;
	}

	@RemoteMethod
	public int deleteKeywordInRule(String relevancyId, String keywordId) {
		int result = -1;
		try{
			Relevancy relevancy = new Relevancy(relevancyId);
			relevancy.setStore(new Store(UtilityService.getStoreId()));
			Keyword keyword =  new Keyword(keywordId);
			return daoService.deleteRelevancyKeyword(new RelevancyKeyword(keyword, relevancy));
		} catch (DaoException e) {
			logger.error("Failed during deleteKeywordInRule()",e);
		}
		return result;
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
			Schema schema = SolrSchemaUtility.getSchema(UtilityService.getServerName(), UtilityService.getStoreId());
			QueryFieldsModel qFieldModel = QueryFieldsModel.toModel(schema, fieldValue, true);
			if (qFieldModel!=null) qFieldList = qFieldModel.getQueryFields();
		} catch (SchemaException e) {
			e.printStackTrace();
		}

		return new RecordSet<QueryField>(qFieldList,qFieldList.size());
	}

	@RemoteMethod
	public RecordSet<Field> getIndexedFields(int page, int itemsPerPage, String filter, String[] excludedFields) {
		Schema schema = SolrSchemaUtility.getSchema(UtilityService.getServerName(), UtilityService.getStoreId());

		List<Field> excludeFieldList = new ArrayList<Field>();

		for (String string: excludedFields) {
			Field field = schema.getField(string);
			excludeFieldList.add(field);
			// do not remove related fields
			//			List<Field> relatedFields = field.getRelatedFields();
			//			if (CollectionUtils.isNotEmpty(relatedFields)) excludeFieldList.addAll(relatedFields);
		}

		List<Field> fields = new LinkedList<Field>(schema.getIndexedFields(filter, excludeFieldList));
		int maxIndex = fields.size()- 1;
		int fromIndex = (page-1)*itemsPerPage;
		int toIndex = (page*itemsPerPage)-1;
		return new RecordSet<Field>(fields.subList(fromIndex, toIndex>maxIndex ? maxIndex+1 : toIndex+1), fields.size());
	}

	@RemoteMethod
	public int getTotalRuleUsedByKeyword(String keyword){
		try {
			StoreKeyword storeKeyword = new StoreKeyword(new Store(UtilityService.getStoreId()), new Keyword(keyword));
			return daoService.getRelevancyKeywordCount(storeKeyword);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@RemoteMethod
	public RecordSet<RelevancyKeyword> getAllRuleUsedByKeyword(String keyword){
		try {
			Relevancy relevancy = new Relevancy("", "");
			relevancy.setStore(new Store(UtilityService.getStoreId()));
			return daoService.searchRelevancyKeywords(new SearchCriteria<RelevancyKeyword>(
					new RelevancyKeyword(new Keyword(keyword), relevancy), null, null, 0, 0),
					MatchType.LIKE_NAME, ExactMatch.MATCH);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RemoteMethod
	public int updateRulePriority(String ruleId, String keyword, int priority){
		try {
			Relevancy relevancy = new Relevancy(ruleId);
			relevancy.setStore(new Store(UtilityService.getStoreId()));
			RelevancyKeyword tmpRelKey = new RelevancyKeyword(new Keyword(keyword), relevancy);
			RelevancyKeyword rk = daoService.getRelevancyKeyword(tmpRelKey);
			rk.setRelevancy(relevancy);
			rk.setPriority(priority);
			return daoService.updateRelevancyKeyword(rk);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@RemoteMethod
	public int getTotalKeywordInRule(String ruleId) {
		try {
			Relevancy rule = new Relevancy();
			rule.setRuleId(ruleId);
			rule.setStore(new Store(UtilityService.getStoreId()));
			return daoService.getRelevancyKeywordCount(rule);
		} catch (DaoException e) {
			logger.error("Failed during getRedirectKeywordCount()", e);
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
