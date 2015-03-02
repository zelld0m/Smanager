package com.search.manager.core.service.sp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.enums.KeywordAttributeType;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.KeywordAttribute;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.Filter.MatchType;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.KeywordAttributeService;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.service.UtilityService;

@Service("typeaheadRuleServiceSp")
public class TypeaheadRuleServiceSpImpl extends GenericServiceSpImpl<TypeaheadRule> implements TypeaheadRuleService{

	private static final Logger logger =
			LoggerFactory.getLogger(TypeaheadRuleServiceSpImpl.class);

	private KeywordAttributeService keywordAttributeService;
	@Autowired
	@Qualifier("ruleStatusServiceSp")
	private RuleStatusService ruleStatusService;
	@Autowired
	@Qualifier("utilityService")
	private UtilityService utilityService;

	public TypeaheadRuleServiceSpImpl() {
		super();
	}

	@Autowired
	public TypeaheadRuleServiceSpImpl(@Qualifier("typeaheadRuleDaoSp") TypeaheadRuleDao dao,
			@Qualifier("keywordAttributeServiceSp") KeywordAttributeService keywordAttributeService) {
		super(dao);
		this.keywordAttributeService = keywordAttributeService;
	}

	public void setRuleStatusService(RuleStatusService ruleStatusService) {
		this.ruleStatusService = ruleStatusService;
	}

	public void setUtilityService(UtilityService utilityService) {
		this.utilityService = utilityService;
	}

	@Override
	public TypeaheadRule add(TypeaheadRule model) throws CoreServiceException {

		TypeaheadRule rule = null;

		try {

			DateTime createdDate = new DateTime();
			if (StringUtils.isBlank(model.getCreatedBy())) {
				model.setCreatedBy(utilityService.getUsername());
			}
			if (model.getCreatedDate() == null) {
				model.setCreatedDate(createdDate);
			}
			
			if(model.getPriority() == null) {
				model.setPriority(1);
			}
			
			if(model.getDisabled() == null) {
				model.setDisabled(false);
			}

			rule = dao.add(model);

			if(rule != null) {
				RuleStatus ruleStatus = new RuleStatus(RuleEntity.TYPEAHEAD, model.getStoreId(),
						model.getRuleId(), model.getRuleName(), utilityService.getUsername(),
						utilityService.getUsername(), RuleStatusEntity.ADD, RuleStatusEntity.UNPUBLISHED);
				ruleStatus.setCreatedBy(utilityService.getUsername());
				ruleStatus.setCreatedDate(createdDate);
				ruleStatus.setRuleTypeId(RuleEntity.TYPEAHEAD.getCode());
				ruleStatusService.add(ruleStatus);
			}

		} catch (CoreDaoException e) {
			e.printStackTrace();
		}

		return rule;
	}

	@Override
	public TypeaheadRule searchById(String storeId, String id)
			throws CoreServiceException {

		if (StringUtils.isBlank(storeId) || StringUtils.isBlank(id)) {
			return null;
		}

		Search search = new Search(TypeaheadRule.class);
		search.addFilter(new Filter(DAOConstants.PARAM_STORE_ID, storeId));
		search.addFilter(new Filter(DAOConstants.PARAM_RULE_ID, id));
		search.addFilter(new Filter(DAOConstants.PARAM_MATCH_TYPE, MatchType.MATCH_ID.getIntValue()));
		search.setPageNumber(1);
		search.setMaxRowCount(1);

		SearchResult<TypeaheadRule> searchResult = search(search);

		if (searchResult.getTotalCount() > 0) {
			return (TypeaheadRule) CollectionUtils.get(searchResult.getResult(), 0);
		}

		return null;
	}

	@Override
	public TypeaheadRule transfer(TypeaheadRule typeaheadRule) throws CoreServiceException {

		// Validate required fields for transfer method.
		if (StringUtils.isNotBlank(typeaheadRule.getStoreId()) && StringUtils.isNotBlank(typeaheadRule.getRuleName())
				&& StringUtils.isNotBlank(typeaheadRule.getCreatedBy())) {
			try {
				List<KeywordAttribute> sectionList = typeaheadRule.getSectionList();
				
				TypeaheadRule result = dao.add(typeaheadRule);
				
				if(sectionList != null) {
					deleteSections(result);
					result.setSectionList(sectionList);
					addSections(result);
				}
				
				return result;
			} catch (CoreDaoException e) {
				throw new CoreServiceException(e);
			}
		}

		return null;
	}

	// The typeaheadRule should contain the sections and sectionItems that needs to be inserted.
	@Override
	public Boolean addSections(TypeaheadRule typeaheadRule) {

		List<KeywordAttribute> sectionList = typeaheadRule.getSectionList();

		if(sectionList != null) {
			for(KeywordAttribute parentSection : sectionList) {
				parentSection.setKeywordId(typeaheadRule.getRuleId());
				parentSection.setCreatedBy(utilityService.getUsername());

				try {
					KeywordAttribute result = keywordAttributeService.add(parentSection);

					if(result == null) {
						throw new CoreServiceException("Unable to add section: '" + parentSection.getInputValue() + "' of " +parentSection.getKeywordAttributeType()+ "type.");
					}

					List<KeywordAttribute> sectionItemList = parentSection.getKeywordAttributeItems();

					if(sectionItemList != null) {
						for(KeywordAttribute sectionItem : sectionItemList) {
							sectionItem.setKeywordId(typeaheadRule.getRuleId());
							sectionItem.setParentAttributeId(result.getKeywordAttributeId());
							sectionItem.setCreatedBy(parentSection.getCreatedBy());
							KeywordAttribute resultItem = keywordAttributeService.add(sectionItem);

							if(resultItem == null) {
								throw new CoreServiceException("Unable to add section item: '" + sectionItem.getInputValue() + "' of " +sectionItem.getKeywordAttributeType()+ " type.");
							}
						}
					}

				} catch (CoreServiceException e) {
					logger.error("An error occurred while adding sections of rule '"+typeaheadRule.getRuleName()+"'.", e);
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public Boolean deleteSections(TypeaheadRule typeaheadRule) {

		try {

			List<KeywordAttribute> sectionList = getParentSectionList(typeaheadRule);

			if(sectionList != null) {

				for(KeywordAttribute parentSection : sectionList) {
					KeywordAttribute keywordAttributeItem = new KeywordAttribute();
					keywordAttributeItem.setParentAttributeId(parentSection.getKeywordAttributeId());
					SearchResult<KeywordAttribute> searchResultItems = keywordAttributeService.search(keywordAttributeItem); 
					List<KeywordAttribute> sectionItems = searchResultItems.getTotalCount() > 0 ? searchResultItems.getList() : null;

					if(sectionItems != null) {
						for(KeywordAttribute sectionItem : sectionItems) {
							if(!keywordAttributeService.delete(sectionItem)) {
								throw new CoreServiceException("Unable to delete item: '" + sectionItem.getInputValue() + "' of " +sectionItem.getKeywordAttributeType()+ " type.");
							}
						}
					}

					if(!keywordAttributeService.delete(parentSection)) {
						throw new CoreServiceException("Unable to delete section: '" + parentSection.getInputValue() + "' of " +parentSection.getKeywordAttributeType()+ " type.");
					}
				}

			}
		} catch (CoreServiceException e) {
			logger.error("Failed to delete existing keyword attributes of rule '"+typeaheadRule.getRuleName()+"'.", e);
			return false;
		}
		return true;
	}

	private List<KeywordAttribute> getParentSectionList(TypeaheadRule rule) throws CoreServiceException {

		List<KeywordAttribute> sectionList = new ArrayList<KeywordAttribute>();
		SearchResult<KeywordAttribute> searchResult;

		KeywordAttribute parentQuery = new KeywordAttribute();
		parentQuery.setKeywordId(rule.getRuleId());

		List<KeywordAttributeType> parentList = new ArrayList<KeywordAttributeType>();
		parentList.addAll(Arrays.asList(KeywordAttributeType.PARENT_TYPES));
		parentList.add(KeywordAttributeType.OVERRIDE_PRIORITY);
		
		for(KeywordAttributeType type : parentList) {
			parentQuery.setInputParamEnumId(type.getCode());
			searchResult = keywordAttributeService.search(parentQuery);
			if(searchResult.getTotalCount() > 0) {
				sectionList.addAll(searchResult.getList());
			}
		}

		return sectionList;
	}

	@Override
	public void initializeTypeaheadSections(TypeaheadRule rule) {

		try {

			KeywordAttribute attribute = new KeywordAttribute();
			attribute.setKeywordId(rule.getRuleId());
			
			SearchResult<KeywordAttribute> searchResult = keywordAttributeService.search(attribute);
			
			// Arrange data
			if(searchResult.getTotalCount() > 0) {
			
				List<KeywordAttribute> sectionList = searchResult.getList();
				Map<String, List<KeywordAttribute>> parentChildMap = new HashMap<String, List<KeywordAttribute>>();
				List<KeywordAttribute> parentList = new ArrayList<KeywordAttribute>();
				
				for(KeywordAttribute section : sectionList) {
					
					String parentAttributeId = section.getParentAttributeId();
					// If parent exist, place in the parent child map.
					if(parentAttributeId != null) {
						if(parentChildMap.get(parentAttributeId) == null) {
							parentChildMap.put(parentAttributeId, new ArrayList<KeywordAttribute>());
						}
						parentChildMap.get(parentAttributeId).add(section);
						
					} 
					// If parent does not exist, place in the parent list.
					else {
						parentList.add(section);
					}
				}
				
				// Set children list to parent
				for(KeywordAttribute parent : parentList) {
					parent.setKeywordAttributeItems(parentChildMap.get(parent.getKeywordAttributeId()));
				}
				
				rule.setSectionList(parentList);
			} else {
				rule.setSectionList(new ArrayList<KeywordAttribute>());
			}
			
		} catch (CoreServiceException e) {
			logger.error("An error occured while retirieving the sections of '"+rule.getRuleName()+"'.", e);
		} 

	}
	
	@Override
	public Boolean updatePrioritySection(TypeaheadRule rule, String lastModifiedBy, DateTime lastModifiedDate, Boolean disabled) {
		KeywordAttribute attribute = new KeywordAttribute();
		attribute.setKeywordId(rule.getRuleId());
		attribute.setInputParamEnumId(KeywordAttributeType.OVERRIDE_PRIORITY.getCode());		
		SearchResult<KeywordAttribute> searchResult;
		try {
			searchResult = keywordAttributeService.search(attribute);
			Integer priority = rule.getPriority();
			if(searchResult.getTotalCount() > 0) {
				attribute = searchResult.getList().get(0);
				attribute.setInputParamEnumId(KeywordAttributeType.OVERRIDE_PRIORITY.getCode());
				if(priority != null) {
					attribute.setInputValue(priority.toString());
				}
				attribute.setLastModifiedBy(lastModifiedBy);
				attribute.setLastModifiedDate(lastModifiedDate);
				attribute.setDisabled(disabled);
				
				attribute = keywordAttributeService.update(attribute);
			} else {
				attribute.setPriority(0);
				attribute.setInputValue(priority != null ? priority.toString() : "0");
				attribute.setCreatedBy(lastModifiedBy);
				attribute.setCreatedDate(lastModifiedDate);
				attribute.setDisabled(disabled);
				
				attribute = keywordAttributeService.add(attribute);
			}
			
			return attribute != null;
		} catch (CoreServiceException e) {
			logger.error("An error occured while overriding the priority of the rule '"+rule.getRuleName()+"'.", e);
		}
		
		return false;
	}

}
