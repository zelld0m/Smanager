package com.search.manager.core.service.sp;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.search.Filter;
import com.search.manager.core.search.Search;
import com.search.manager.core.search.Filter.MatchType;
import com.search.manager.core.search.SearchResult;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.service.UtilityService;

@Service("typeaheadRuleServiceSp")
public class TypeaheadRuleServiceSpImpl extends GenericServiceSpImpl<TypeaheadRule> implements TypeaheadRuleService{

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
	public TypeaheadRuleServiceSpImpl(@Qualifier("typeaheadRuleDaoSp") TypeaheadRuleDao dao) {
		super(dao);
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
                return dao.add(typeaheadRule);
            } catch (CoreDaoException e) {
                throw new CoreServiceException(e);
            }
        }

        return null;
    }

}
