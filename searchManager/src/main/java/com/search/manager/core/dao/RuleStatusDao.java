package com.search.manager.core.dao;

import java.util.List;

import com.search.manager.core.exception.CoreDaoException;
import com.search.manager.core.model.RuleStatus;

public interface RuleStatusDao extends GenericDao<RuleStatus> {

	List<String> getCleanList(List<String> ruleRefIds, Integer ruleTypeId,
			String pStatus, String aStatus) throws CoreDaoException;

	// Add RuleStatusDao specific method here...

}
