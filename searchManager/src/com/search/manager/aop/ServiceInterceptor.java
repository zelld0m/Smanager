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
		
		if (!(returnValue instanceof Integer && (Integer)returnValue > 0)) {
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
		String[] searchTerms = rule.getSearchTerm().split(RedirectUtility.DBL_ESC_PIPE_DELIM);
		String condition = rule.getCondition().replace(RedirectUtility.DBL_PIPE_DELIM, RedirectUtility.OR); 
		auditTrail.setStoreId(rule.getStoreId());
		String refId = String.valueOf(rule.getRuleId());
		for (String searchTerm : searchTerms) {
			auditTrail.setKeyword(searchTerm);
			auditTrail.setReferenceId(refId);
			switch (auditable.operation()) {
				case add:
						auditTrail.setDetails(String.format("Added Rule ID[%1$s] : search term = [%2$s], condition = [%3$s].", refId, searchTerm, condition));
					break;
				case update:
						auditTrail.setDetails(String.format("Updated Rule ID[%1$s] : search term = [%2$s], condition = [%3$s].", refId, searchTerm, condition));
					break;
				case delete:
						auditTrail.setDetails(String.format("Removed Rule ID[%1$s] : search term = [%2$s], condition = [%3$s].", refId, searchTerm, condition));
					break;
				default:
					return;
			}
			logAuditTrail(auditTrail);
		}
	}

	private void logAuditTrail(AuditTrail auditTrail) {
		auditTrailDAO.addAuditTrail(auditTrail);
	}

	public void setAuditTrailDAO(AuditTrailDAO auditTrailDAO) {
		this.auditTrailDAO = auditTrailDAO;
	}
	
}
