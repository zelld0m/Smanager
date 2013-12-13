package com.search.manager.workflow.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RuleStatus;
import com.search.manager.workflow.service.RuleStatusService;

@Repository(value="ruleStatusService")
public class RuleStatusServiceImpl implements RuleStatusService{

	@Autowired
	private DaoService daoService;
	
	private static final Logger logger =
            LoggerFactory.getLogger(RuleStatusServiceImpl.class);
	
	public RuleStatus getRuleStatus(String storeId, String ruleType, String ruleRefId) {
        RuleStatus result = null;

        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setRuleRefId(ruleRefId);
            ruleStatus.setStoreId(storeId);

            result = daoService.getRuleStatus(ruleStatus);
        } catch (DaoException e) {
            logger.error("Failed during getRuleStatus()", e);
        }
        return result == null ? new RuleStatus() : result;
    }
	
	public RuleStatus createRuleStatus(String storeId, String userName) {
        RuleStatus ruleStatus = new RuleStatus();
        ruleStatus.setCreatedBy(userName);
        ruleStatus.setLastModifiedBy(userName);
        ruleStatus.setStoreId(storeId);
        return ruleStatus;
    }
}
