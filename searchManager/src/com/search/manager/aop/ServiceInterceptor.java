package com.search.manager.aop;

import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.search.manager.dao.sp.AuditTrailDAO;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.StoreKeyword;
import com.search.manager.service.UtilityService;
import com.search.manager.utility.RedirectUtility;

@Aspect
public class ServiceInterceptor {
	
	private static final Logger logger = Logger.getLogger(ServiceInterceptor.class);
	private AuditTrailDAO auditTrailDAO;
	
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
			argNames="bean,auditable")
	public void performDaoAudit(JoinPoint jp, Object bean, Audit auditable) {
		// TODO: need to check if return value is greater than o before logging
		logger.info("****************************************************");
		logger.info(String.format("Audit Level: %s",auditable.auditLevel()));
		logger.info(String.format("Audit Message: %s",auditable.message()));
		logger.info(String.format("Bean Called: %s", bean.getClass().getName()));
		logger.info(String.format("Method Called: %s", jp.getSignature().getName()));
		for (Object obj: jp.getArgs()) {
			if (obj != null)
			logger.info(obj.getClass() + " : " + obj);
			else
		logger.info(null);
		}
		logger.info("****************************************************");
		
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
			case keyword:
				// TODO: update DAO signature
				//logKeyword(jp, auditable, auditTrail);
				break;
			case storeKeyword:
				// TODO: update DAO signature
				//logStoreKeyword(jp, auditable, auditTrail);
				break;
		}
	}
	
	private String getStringValue(Object obj) {
		return obj == null ? null : String.valueOf(obj);
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
			case updateComment:
				auditTrail.setDetails(String.format("Appending comment [%2$s] for elevated entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case appendComment:
				auditTrail.setDetails(String.format("Setting comment [%2$s] for elevated entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case updateExpiryDate:
				auditTrail.setDetails(String.format("Changing expiry date to [%2$tF] for elevated entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getExpiryDate()));
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
			case updateComment:
				auditTrail.setDetails(String.format("Appending comment [%2$s] for excluded entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case appendComment:
				auditTrail.setDetails(String.format("Setting comment [%2$s] for excluded entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getComment()));
				break;
			case updateExpiryDate:
				auditTrail.setDetails(String.format("Changing expiry date to [%2$tF] for excluded entry EDP[%1$s]",
						auditTrail.getReferenceId(), e.getExpiryDate()));
				break;
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
						auditTrail.getReferenceId(),k.getKeywordId()));
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
		auditTrail.setStoreId(rule.getStoreId());
		auditTrail.setKeyword(rule.getSearchTerm().replace(RedirectUtility.DBL_PIPE_DELIM,","));
		auditTrail.setReferenceId(rule.getRuleName());
		switch (auditable.operation()) {
			case add:
				auditTrail.setDetails(String.format("Added Rule[%1$s] for search terms[%2$s], apply condition [%3$s].",
						auditTrail.getReferenceId(), rule.getSearchTerm().replace(RedirectUtility.DBL_PIPE_DELIM,","),rule.getCondition().replace(RedirectUtility.DBL_PIPE_DELIM, RedirectUtility.OR)));
				break;
			case update:
				auditTrail.setDetails(String.format("Updated Rule[%1$s] for search terms[%2$s], apply condition [%3$s]. ",
						auditTrail.getReferenceId(), auditTrail.getKeyword(),rule.getCondition().replace(RedirectUtility.DBL_PIPE_DELIM, RedirectUtility.OR)));
				break;
			case delete:
				auditTrail.setDetails(String.format("Removed Rule[%1$s]", auditTrail.getReferenceId()));
				break;
			default:
				return;
		}
		logAuditTrail(auditTrail);
	}

	private void saveQCAudit(RedirectRule rule, AuditTrail auditTrail) {
		String[] searchTerms = rule.getSearchTerm().split(RedirectUtility.DBL_ESC_PIPE_DELIM);
		for (String string : searchTerms) {
			
		}
	}
	
	private void logAuditTrail(AuditTrail auditTrail) {
		auditTrailDAO.addAuditTrail(auditTrail);
	}

	public void setAuditTrailDAO(AuditTrailDAO auditTrailDAO) {
		this.auditTrailDAO = auditTrailDAO;
	}
	
	
}
