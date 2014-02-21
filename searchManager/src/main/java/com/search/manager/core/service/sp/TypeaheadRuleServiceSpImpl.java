package com.search.manager.core.service.sp;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.core.dao.TypeaheadRuleDao;
import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.core.model.TypeaheadRule;
import com.search.manager.core.service.RuleStatusService;
import com.search.manager.core.service.TypeaheadRuleService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.service.UtilityService;

@Service("typeaheadRuleServiceSp")
public class TypeaheadRuleServiceSpImpl extends GenericServiceSpImpl<TypeaheadRule> implements TypeaheadRuleService{

	@Autowired
	private RuleStatusService ruleStatusService;
	@Autowired
	private TypeaheadRuleDao dao;
	@Autowired
	private UtilityService utilityService;
	
	@Autowired
	public TypeaheadRuleServiceSpImpl(TypeaheadRuleDao dao) {
		super(dao);
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
            	 RuleStatus ruleStatus = new RuleStatus(RuleEntity.TYPEAHEAD, utilityService.getStoreId(),
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
		// TODO Auto-generated method stub
		return null;
	}

}
