package com.search.manager.core.service;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.RuleStatus;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleStatusEntity;

public interface RuleStatusService extends GenericService<RuleStatus> {

	List<String> getCleanList(List<String> ruleRefIds, Integer ruleTypeId,
			String pStatus, String aStatus) throws CoreServiceException;

	Map<String, Boolean> updateRuleStatus(RuleStatusEntity status,
			List<RuleStatus> ruleStatusList, String requestBy,
			DateTime requestDateTime) throws CoreServiceException;

	RuleStatus updateRuleStatusExportInfo(RuleStatus ruleStatus,
			String exportBy, ExportType exportType, DateTime exportDateTime)
			throws CoreServiceException;

	RuleStatus updateRuleStatusPublishInfo(RuleStatus ruleStatus,
			RuleStatusEntity requestedPublishStatus, String requestBy,
			DateTime requestDateTime) throws CoreServiceException;

	RuleStatus updateRuleStatusApprovalInfo(RuleStatus ruleStatus,
			RuleStatusEntity requestedApprovalStatus, String requestBy,
			DateTime requestDateTime) throws CoreServiceException;

	boolean updateRuleStatusDeletedInfo(RuleStatus ruleStatus, String deletedBy)
			throws CoreServiceException;

	RuleStatus getRuleStatus(String ruleType, String ruleRefId)
			throws CoreServiceException;

	// Add RuleStatusService specific method here...

}
