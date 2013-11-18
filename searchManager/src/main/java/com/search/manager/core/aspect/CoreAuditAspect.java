package com.search.manager.core.aspect;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.manager.core.annotation.Auditable;
import com.search.manager.core.annotation.AuditableMethod;
import com.search.manager.core.exception.CoreServiceException;
import com.search.manager.core.model.AuditTrail;
import com.search.manager.core.model.BannerRule;
import com.search.manager.core.model.BannerRuleItem;
import com.search.manager.core.model.ImagePath;
import com.search.manager.core.service.AuditTrailService;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.constants.AuditTrailConstants.Operation;
import com.search.manager.service.UtilityService;

@Aspect
@Component("coreAuditAspect")
public class CoreAuditAspect {

	private final static Logger logger = LoggerFactory
			.getLogger(CoreAuditAspect.class);

	@Autowired
	@Qualifier("auditTrailServiceSp")
	private AuditTrailService auditTrailService;

	@Before(value = "@annotation(auditableMethod)")
	public void performAudit(JoinPoint joinPoint,
			AuditableMethod auditableMethod) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Auditable auditable = joinPoint.getTarget().getClass()
				.getAnnotation(Auditable.class);
		if (auditable != null) {
			logger.info("BEGIN AUDIT: " + className + "." + methodName + "()");
			logger.info("AUDIT: " + auditable.entity());
			logger.info("OPERATION: " + auditableMethod.operation());
		}
	}

	@AfterReturning(value = "@annotation(auditableMethod)", returning = "result")
	public void performDaoAudit(JoinPoint joinPoint, Object result,
			AuditableMethod auditableMethod) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Auditable auditable = joinPoint.getTarget().getClass()
				.getAnnotation(Auditable.class);

		if (auditable != null) {
			AuditTrail auditTrail = new AuditTrail();
			auditTrail.setEntity(auditable.entity().toString());
			auditTrail.setOperation(auditableMethod.operation().toString());
			auditTrail.setCreatedDate(new DateTime());
			auditTrail.setUsername(UtilityService.getUsername());

			switch (auditable.entity()) {
			case bannerRule:
				logBannerRule(joinPoint, auditableMethod, auditTrail);
				break;
			case bannerRuleItem:
				logBannerRuleItem(joinPoint, auditableMethod, auditTrail);
				break;
			case imagePath:
				logImagePath(joinPoint, auditableMethod, auditTrail);
				break;
			}
			logger.info("END AUDIT: " + className + "." + methodName + "()");
		}
	}

	private void logAuditTrail(AuditTrail auditTrail) {
		try {
			auditTrailService.add(auditTrail);
		} catch (CoreServiceException e) {
			logger.error(e.getMessage());
		}
	}

	private void logBannerRule(JoinPoint joinPoint,
			AuditableMethod auditableMethod, AuditTrail auditTrail) {
		StringBuilder message = new StringBuilder();
		Operation operation = auditableMethod.operation();

		BannerRule rule = (BannerRule) joinPoint.getArgs()[0];
		auditTrail.setReferenceId(rule.getRuleId());
		auditTrail.setStoreId(rule.getStoreId());
		auditTrail.setKeyword(rule.getRuleName());
		// Operation is either Add or Delete only
		message.append(operation == Operation.add ? "Adding " : "Removing ")
				.append("Banner Rule with ID = [%1$s]");
		if (StringUtils.isNotBlank(rule.getRuleName())) {
			message.append(" and Name = [%2$s]");
		}
		auditTrail.setDetails(String.format(message.toString(),
				rule.getRuleId(), rule.getRuleName()));

		logAuditTrail(auditTrail);
	}

	private void logBannerRuleItem(JoinPoint joinPoint,
			AuditableMethod auditableMethod, AuditTrail auditTrail) {
		StringBuilder message = new StringBuilder();
		Operation operation = auditableMethod.operation();

		BannerRuleItem ruleItem = (BannerRuleItem) joinPoint.getArgs()[0];
		BannerRule rule = ruleItem.getRule();
		auditTrail.setStoreId(rule.getStoreId());
		auditTrail.setReferenceId(rule.getRuleId());
		auditTrail.setKeyword(rule.getRuleName());

		switch (operation) {
		case add:
			message.append("Adding ");
			break;
		case update:
			message.append("Updating ");
			break;
		case delete:
			message.append("Removing ");
			break;
		}
		message.append("Banner with ID = [%1$s]");
		if (StringUtils.isNotBlank(rule.getRuleName())) {
			message.append(" for Rule Name = [%12$s]");
		}
		message.append(Operation.deleteBanner.equals(operation) ? ": "
				: ": Setting ");

		if (ruleItem.getPriority() != null && ruleItem.getPriority() > 0) {
			message.append("Priority = [%2$s] and ");
		}
		if (ruleItem.getStartDate() != null) {
			message.append("Start Date = [%3$s] and ");
		}
		if (ruleItem.getEndDate() != null) {
			message.append("End Date = [%4$s] and ");
		}
		if (ruleItem.getDisabled() != null) {
			message.append("Disabled status = [%5$s] and ");
		}
		if (StringUtils.isNotBlank(ruleItem.getDescription())) {
			message.append("Description = [%11$s] and ");
		}
		if (ruleItem.getImagePath() != null
				&& StringUtils.isNotBlank(ruleItem.getImagePath().getId())) {
			message.append("Image Path Id = [%6$s] and ");
		}
		if (ruleItem.getImagePath() != null
				&& StringUtils.isNotBlank(ruleItem.getImagePath().getPath())) {
			message.append("Image Path = [%7$s] and ");
		}
		if (StringUtils.isNotBlank(ruleItem.getImageAlt())) {
			message.append("Image Alt = [%8$s] and ");
		}
		if (StringUtils.isNotBlank(ruleItem.getLinkPath())) {
			message.append("Link Path = [%9$s] and ");
		}
		if (ruleItem.getOpenNewWindow() != null) {
			message.append("Open in New Window = [%10$s] and ");
		}

		if (StringUtils
				.equals(message.substring(message.length() - 5), " and ")) {
			message.replace(message.length() - 5, message.length() - 1, "");
		}

		auditTrail.setDetails(String.format(message.toString(), ruleItem
				.getMemberId(), ruleItem.getPriority(), ObjectUtils
				.toString(JodaDateTimeUtil.formatFromStorePatternWithZone(
						ruleItem.getStartDate(), JodaPatternType.DATE)),
				ObjectUtils.toString(JodaDateTimeUtil
						.formatFromStorePatternWithZone(ruleItem.getEndDate(),
								JodaPatternType.DATE)), ruleItem.getDisabled(),
				ruleItem.getImagePath() != null ? ruleItem.getImagePath()
						.getId() : "",
				ruleItem.getImagePath() != null ? ruleItem.getImagePath()
						.getPath() : "", ruleItem.getImageAlt(), ruleItem
						.getLinkPath(), ruleItem.getOpenNewWindow(), ruleItem
						.getDescription(), rule.getRuleName()));

		logAuditTrail(auditTrail);
	}

	private void logImagePath(JoinPoint joinPoint,
			AuditableMethod auditableMethod, AuditTrail auditTrail) {
		StringBuilder message = new StringBuilder();
		Operation operation = auditableMethod.operation();

		ImagePath imagePath = (ImagePath) joinPoint.getArgs()[0];
		auditTrail.setStoreId(imagePath.getStoreId());
		auditTrail.setReferenceId(imagePath.getId());

		// Operation is either Add or Update only
		message.append(operation == Operation.add ? "Adding " : "Updating ")
				.append("Image Path  with ID = [%1$s]");
		if (StringUtils.isNotBlank(imagePath.getPath())) {
			message.append(" and Image Path = [%2$s]");
		}
		message.append(": Setting ");
		if (imagePath.getPathType() != null) {
			message.append("Path Type = [%3$s] and ");
		}
		if (StringUtils.isNotBlank(imagePath.getAlias())) {
			message.append("Alias = [%4$s] and ");
		}

		if (StringUtils
				.equals(message.substring(message.length() - 5), " and ")) {
			message.replace(message.length() - 5, message.length() - 1, "");
		}

		auditTrail.setDetails(String.format(message.toString(),
				imagePath.getId(), imagePath.getPath(),
				imagePath.getPathType(), imagePath.getAlias()));

		logAuditTrail(auditTrail);
	}
}
