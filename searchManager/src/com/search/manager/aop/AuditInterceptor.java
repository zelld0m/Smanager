package com.search.manager.aop;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.manager.dao.sp.AuditTrailDAO;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.service.UtilityService;

@Aspect
@Component("auditInterceptor")
public class AuditInterceptor {
	
	private static final Logger logger = Logger.getLogger(AuditInterceptor.class);
	@Autowired private AuditTrailDAO auditTrailDAO;
	
	@Before(value="com.search.manager.aop.SystemArchitecture.inServiceLayer()" +
			"&& target(bean) " +
			"&& @annotation(com.search.manager.aop.Audit)" +
			"&& @annotation(auditable)",
			argNames="bean,auditable")
	public void performAudit(JoinPoint jp, Object bean, Audit auditable) {
		logger.info(String.format("Audit Level: %s",auditable.auditLevel()));
		logger.info(String.format("Audit Message: %s",auditable.message()));
		logger.info(String.format("Bean Called: %s", bean.getClass().getName()));
		logger.info(String.format("Method Called: %s", jp.getSignature().getName()));

	}
	
	@AfterReturning(value="com.search.manager.aop.SystemArchitecture.inDaoLayer()" +
			"&& target(bean) " +
			"&& @annotation(com.search.manager.aop.Audit)" +
			"&& @annotation(auditable)",
			returning = "returnValue", 
			argNames="bean,auditable,returnValue")
	public void performDaoAudit(JoinPoint jp, Object bean, Audit auditable, Object returnValue) {
		if (logger.isTraceEnabled()) {
			logger.trace("****************************************************");
			logger.trace(String.format("Audit Level: %s",auditable.auditLevel()));
			logger.trace(String.format("Audit Message: %s",auditable.message()));
			logger.trace(String.format("Bean Called: %s", bean.getClass().getName()));
			logger.trace(String.format("Method Called: %s", jp.getSignature().getName()));
			logger.trace(String.format("Return value: %s", returnValue));
			for (Object obj: jp.getArgs()) {
				if (obj != null)
					logger.trace(obj.getClass() + " : " + obj);
				else
					logger.trace(null);
			}
			logger.trace("****************************************************");			
		}
		
		// return value is 1 if operation is successful
		// but sometimes id of added record is returned. in this case unsuccessful add will return null;
		if (returnValue instanceof String && StringUtils.isEmpty((String)returnValue) || returnValue instanceof Integer && (Integer)returnValue <= 0) {
			return;
		}
		
		AuditTrail auditTrail = new AuditTrail();
		auditTrail.setEntity(auditable.entity().toString());
		auditTrail.setOperation(auditable.operation().toString());
		auditTrail.setDate(new Date());
		auditTrail.setUsername(UtilityService.getUsername());

		switch(auditable.entity()) {
			case banner:
				break;
			case campaign:
				break;
			case elevate:
				logElevate(jp, auditable, auditTrail);
				break;
			case exclude:
				logExclude(jp, auditable, auditTrail);
				break;
			case queryCleaning:
				logQueryCleaning(jp, auditable, auditTrail);
				break;
			case relevancy:
				if (ArrayUtils.contains(AuditTrailConstants.relevancyOperations, auditable.operation())) {
					logRelevancy(jp, auditable, auditTrail);					
				}
				else if (ArrayUtils.contains(AuditTrailConstants.relevancyFieldOperations, auditable.operation())) {
					logRelevancyField(jp, auditable, auditTrail);
				}
				else if (ArrayUtils.contains(AuditTrailConstants.relevancyKeywordOperations, auditable.operation())) {
					logRelevancyKeyword(jp, auditable, auditTrail);					
				}
				break;
			case keyword:
				logKeyword(jp, auditable, auditTrail);
				break;
			case storeKeyword:
				logStoreKeyword(jp, auditable, auditTrail);
				break;
			case ruleStatus:
				logRuleStatus(jp, auditable, auditTrail);
				break;
		}
	}
	
	private void logElevate(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		ElevateResult e = (ElevateResult)jp.getArgs()[0];
		auditTrail.setStoreId(e.getStoreKeyword().getStoreId());
		auditTrail.setKeyword(e.getStoreKeyword().getKeywordId());
		auditTrail.setReferenceId(e.getEdp());
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Adding EDP[%1$s] to position[%2$s] expiring on[%3$tF]. Comment[%4$s]",
						auditTrail.getReferenceId(), e.getLocation(),e.getExpiryDate(), e.getComment()));
				break;
			case update:
				auditTrail.setDetails(String.format("Elevated EDP[%1$s] to position[%2$s]",
						auditTrail.getReferenceId(), e.getLocation()));
				break;
			case delete:
				auditTrail.setDetails(String.format("Removed elevated entry EDP[%1$s]",
						auditTrail.getReferenceId()));
				break;
			case appendComment:
				auditTrail.setDetails(String.format("Appending comment [%2$s] for elevated entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case updateComment:
				auditTrail.setDetails(String.format("Setting comment [%2$s] for elevated entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case updateExpiryDate:
				auditTrail.setDetails(String.format("Changing expiry date to [%2$tF] for elevated entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getExpiryDate()));
				break;
			case clear:
				auditTrail.setDetails(String.format("Removed all elevated entries"));				
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}

	private void logExclude(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		ExcludeResult e = (ExcludeResult)jp.getArgs()[0];
		auditTrail.setStoreId(e.getStoreKeyword().getStoreId());
		auditTrail.setKeyword(e.getStoreKeyword().getKeywordId());
		auditTrail.setReferenceId(e.getEdp());
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Adding EDP[%1$s]. Comment[%2$s]",
						auditTrail.getReferenceId(),e.getExpiryDate(), e.getComment()));
				break;
			case delete:
				auditTrail.setDetails(String.format("Removed excluded entry EDP[%1$s]",
						auditTrail.getReferenceId()));
				break;
			case appendComment:
				auditTrail.setDetails(String.format("Appending comment [%2$s] for excluded entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case updateComment:
				auditTrail.setDetails(String.format("Setting comment [%2$s] for excluded entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case updateExpiryDate:
				auditTrail.setDetails(String.format("Changing expiry date to [%2$tF] for excluded entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getExpiryDate()));
				break;
			case clear:
				auditTrail.setDetails(String.format("Removed all elevated entries"));				
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}
	
	private void logKeyword(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		Keyword k = (Keyword)jp.getArgs()[0];
		auditTrail.setKeyword(k.getKeywordId());
		auditTrail.setReferenceId(k.getKeywordId());
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Adding Keyword[%1$s].",
						auditTrail.getReferenceId()));
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}
	
	private void logStoreKeyword(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		StoreKeyword sk = (StoreKeyword)jp.getArgs()[0];
		auditTrail.setStoreId(sk.getStoreId());
		auditTrail.setKeyword(sk.getKeywordId());
		auditTrail.setReferenceId(sk.getKeywordId());
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Adding Keyword[%1$s] to Store[%2$s].",
						auditTrail.getReferenceId(), sk.getKeywordId(), sk.getStoreId()));
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}

	private void logQueryCleaning(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		RedirectRule rule = (RedirectRule)jp.getArgs()[0];
		String searchTerm = rule.getSearchTerm();
		String condition = rule.getRedirectFilter(); 
		auditTrail.setStoreId(rule.getStoreId());
		String refId = String.valueOf(rule.getRuleId());
		auditTrail.setKeyword(searchTerm);
		auditTrail.setReferenceId(refId);
		// TODO: check if working
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Added Rule ID[%1$s] : name = [%2$s], search term = [%3$s], condition = [%4$s].", 
						refId, rule.getRuleName(), searchTerm, condition));
				break;
			case update:
				auditTrail.setDetails(String.format("Updated Rule ID[%1$s] : name = [%2$s].", 
						refId, rule.getRuleName()));
				break;
			case delete:
				auditTrail.setDetails(String.format("Removed Rule ID[%1$s].", refId));
				break;
			case mapKeyword:
				auditTrail.setDetails(String.format("Added Search Term[%1$s] for Rule ID[%2$s].", searchTerm, refId));
				break;
			case unmapKeyword:
				auditTrail.setDetails(String.format("Removed Search Term[%1$s] from Rule ID[%2$s].", searchTerm, refId));
				break;
			case addCondition:
				auditTrail.setDetails(String.format("Added Condition[%1$s] for Rule ID[%2$s].", condition, refId));
				break;
			case removeCondition:
				auditTrail.setDetails(String.format("Removed Condition[%1$s] from Rule ID[%2$s].", condition, refId));
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}

	private void logAuditTrail(AuditTrail auditTrail) {
		auditTrailDAO.addAuditTrail(auditTrail);
	}

	public void setAuditTrailDAO(AuditTrailDAO auditTrailDAO) {
		this.auditTrailDAO = auditTrailDAO;
	}
	
	private void logRelevancy(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		Relevancy relevancy = (Relevancy)jp.getArgs()[0];
		auditTrail.setStoreId(DAOUtils.getStoreId(relevancy.getStore()));
		auditTrail.setReferenceId(relevancy.getRelevancyId());
		StringBuilder message = null;
		switch (auditable.operation()) {
			case add:
				message = new StringBuilder("Adding relevancy[%1$s] with name[%2$s] and description[%3$s] to store[%4$s]");
				if (relevancy.getStartDate() != null || relevancy.getStartDate() != null) {
					message.append(" with schedule");
					if (relevancy.getStartDate() != null) {
						message.append(" from [%5$s]");						
					}
					if (relevancy.getEndDate() != null) {
						message.append(" to [%6$s]");
					}
				}
				auditTrail.setDetails(String.format(message.toString(),
						relevancy.getRelevancyId(), StringUtils.trimToEmpty(relevancy.getRelevancyName()), StringUtils.trimToEmpty(relevancy.getDescription()), 
						DAOUtils.getStoreId(relevancy.getStore()), relevancy.getStartDate(), relevancy.getEndDate()));
				break;
			case update:
				message = new StringBuilder("Updating relevancy[%1$s] with name[%2$s] and description[%3$s] ");
				if (relevancy.getStartDate() != null || relevancy.getStartDate() != null) {
					message.append(" with schedule");
					if (relevancy.getStartDate() != null) {
						message.append(" from [%4$s]");						
					}
					if (relevancy.getEndDate() != null) {
						message.append(" to [%5$s]");
					}					
				}
				auditTrail.setDetails(String.format(message.toString(),
						relevancy.getRelevancyId(), StringUtils.trimToEmpty(relevancy.getRelevancyName()), StringUtils.trimToEmpty(relevancy.getDescription()), 
						relevancy.getStartDate(), relevancy.getEndDate()));
				break;
			case delete:
				message = new StringBuilder("Deleting relevancy[%1$s]");
				auditTrail.setDetails(String.format(message.toString(), relevancy.getRelevancyId()));
				break;
			case updateComment:
				auditTrail.setDetails(String.format("Setting comment [%2$s] for relevancy[%1$s]",
						auditTrail.getReferenceId(), relevancy.getComment()));
				break;
			case appendComment:
				auditTrail.setDetails(String.format("Appending comment [%2$s] for relevancy[%1$s]",
						auditTrail.getReferenceId(), relevancy.getComment()));
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}
	
	private void logRelevancyField(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		RelevancyField relevancyField = (RelevancyField)jp.getArgs()[0];
		auditTrail.setStoreId(DAOUtils.getStoreId(relevancyField.getRelevancy().getStore()));
		auditTrail.setReferenceId(relevancyField.getRelevancy().getRelevancyId());
		switch (auditable.operation()) {
			case addRelevancyField:
				auditTrail.setDetails(String.format("Adding new relevancy field[%2$s] for relevancy[%1$s] with value[%3$s]",
						auditTrail.getReferenceId(), relevancyField.getFieldName(), relevancyField.getFieldValue()));				
				break;
			case updateRelevancyField:
				auditTrail.setDetails(String.format("Updating relevancy field[%2$s] for relevancy[%1$s] with value[%3$s]",
						auditTrail.getReferenceId(), relevancyField.getFieldName(), relevancyField.getFieldValue()));				
				break;
			case saveRelevancyField:
				auditTrail.setDetails(String.format("Saving relevancy field[%2$s] for relevancy[%1$s] with value[%3$s]",
						auditTrail.getReferenceId(), relevancyField.getFieldName(), relevancyField.getFieldValue()));				
				break;
			case deleteRelevancyField:
				auditTrail.setDetails(String.format("Deleting relevancy field[%2$s] for relevancy[%1$s]",
						auditTrail.getReferenceId(), relevancyField.getFieldName()));				
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}

	private void logRelevancyKeyword(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		RelevancyKeyword relevancyKeyword = (RelevancyKeyword)jp.getArgs()[0];
		auditTrail.setStoreId(DAOUtils.getStoreId(relevancyKeyword.getRelevancy().getStore()));
		auditTrail.setKeyword(DAOUtils.getKeywordId(relevancyKeyword.getKeyword()));
		auditTrail.setReferenceId(relevancyKeyword.getRelevancy().getRelevancyId());
		switch (auditable.operation()) {
			case mapKeyword:
				auditTrail.setDetails(String.format("Mapping keyword[%2$s] to relevancy[%1$s] with priority[%3$s]",
						auditTrail.getReferenceId(), auditTrail.getKeyword(), relevancyKeyword.getPriority()));				
				break;
			case unmapKeyword:
				auditTrail.setDetails(String.format("Unmapping keyword[%2$s] from relevancy[%1$s]",
						auditTrail.getReferenceId(), auditTrail.getKeyword()));				
				break;
			case updateKeywordMapping:
				auditTrail.setDetails(String.format("Setting priority[%3$s] of relevancy[%2$s] for keyword[%1$s]",
						auditTrail.getReferenceId(), auditTrail.getKeyword(), relevancyKeyword.getPriority()));				
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}
	
	private void logRuleStatus(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
		RuleStatus ruleStatus = (RuleStatus)jp.getArgs()[0];
		auditTrail.setStoreId(UtilityService.getStoreName());
		auditTrail.setReferenceId(ruleStatus.getRuleRefId());
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Added reference id = [%1$s], rule type = [%2$s], approval status = [%3$s], published status = [%4$s].", 
						auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId()), ruleStatus.getApprovalStatus(), ruleStatus.getPublishedStatus()));
				break;
			case update:
				if (RuleStatusEntity.DELETE.toString().equals(ruleStatus.getUpdateStatus())) {
					auditTrail.setDetails(String.format("Deleted reference id = [%1$s], rule type = [%2$s], approval status = [%3$s], published status = [%4$s].", 
							auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId()), ruleStatus.getApprovalStatus(), ruleStatus.getPublishedStatus()));
				} else {
					auditTrail.setDetails(String.format("Updated reference id = [%1$s], rule type = [%2$s], approval status = [%3$s], published status = [%4$s].", 
							auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId()), ruleStatus.getApprovalStatus(), ruleStatus.getPublishedStatus()));
				}
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}


}
