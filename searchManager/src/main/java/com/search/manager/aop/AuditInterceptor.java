package com.search.manager.aop;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.search.manager.dao.sp.AuditTrailDAO;
import com.search.manager.dao.sp.DAOUtils;
import com.search.manager.enums.ReplaceKeywordMessageType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.model.AuditTrail;
import com.search.manager.model.BannerRule;
import com.search.manager.model.BannerRuleItem;
import com.search.manager.model.DemoteResult;
import com.search.manager.model.ElevateResult;
import com.search.manager.model.ExcludeResult;
import com.search.manager.model.FacetGroup;
import com.search.manager.model.FacetGroupItem;
import com.search.manager.model.FacetSort;
import com.search.manager.model.ImagePath;
import com.search.manager.model.Keyword;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRuleCondition;
import com.search.manager.model.Relevancy;
import com.search.manager.model.RelevancyField;
import com.search.manager.model.RelevancyKeyword;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SpellRule;
import com.search.manager.model.StoreKeyword;
import com.search.manager.model.User;
import com.search.manager.model.constants.AuditTrailConstants;
import com.search.manager.model.constants.AuditTrailConstants.Operation;
import com.search.manager.service.UtilityService;

@Aspect
@Component("auditInterceptor")
public class AuditInterceptor {

    private static final Logger logger = Logger.getLogger(AuditInterceptor.class);
    @Autowired
    private AuditTrailDAO auditTrailDAO;

    @Before(value = "com.search.manager.aop.SystemArchitecture.inServiceLayer()"
            + "&& target(bean) "
            + "&& @annotation(com.search.manager.aop.Audit)"
            + "&& @annotation(auditable)",
            argNames = "bean,auditable")
    public void performAudit(JoinPoint jp, Object bean, Audit auditable) {
        logger.info(String.format("Audit Level: %s", auditable.auditLevel()));
        logger.info(String.format("Audit Message: %s", auditable.message()));
        logger.info(String.format("Bean Called: %s", bean.getClass().getName()));
        logger.info(String.format("Method Called: %s", jp.getSignature().getName()));
    }

    @AfterReturning(value = "com.search.manager.aop.SystemArchitecture.inDaoLayer()"
            + "&& target(bean) "
            + "&& @annotation(com.search.manager.aop.Audit)"
            + "&& @annotation(auditable)",
            returning = "returnValue",
            argNames = "bean,auditable,returnValue")
    public void performDaoAudit(JoinPoint jp, Object bean, Audit auditable, Object returnValue) {
        if (logger.isTraceEnabled()) {
            logger.trace("****************************************************");
            logger.trace(String.format("Audit Level: %s", auditable.auditLevel()));
            logger.trace(String.format("Audit Message: %s", auditable.message()));
            logger.trace(String.format("Bean Called: %s", bean.getClass().getName()));
            logger.trace(String.format("Method Called: %s", jp.getSignature().getName()));
            logger.trace(String.format("Return value: %s", returnValue));
            for (Object obj : jp.getArgs()) {
                if (obj != null) {
                    logger.trace(obj.getClass() + " : " + obj);
                } else {
                    logger.trace(null);
                }
            }
            logger.trace("****************************************************");
        }

        // return value is 1 if operation is successful
        // but sometimes id of added record is returned. in this case unsuccessful add will return null;
        if (returnValue instanceof String && StringUtils.isEmpty((String) returnValue) || returnValue instanceof Integer && (Integer) returnValue <= 0) {
            return;
        }

        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setEntity(auditable.entity().toString());
        auditTrail.setOperation(auditable.operation().toString());
        auditTrail.setCreatedDate(new DateTime());
        auditTrail.setUsername(UtilityService.getUsername());

        switch (auditable.entity()) {
            case banner:
                if (ArrayUtils.contains(AuditTrailConstants.entityOperationMap.get(AuditTrailConstants.Entity.banner), auditable.operation())) {
                    logBanner(jp, auditable, auditTrail);
                }
                break;
            case elevate:
                logElevate(jp, auditable, auditTrail);
                break;
            case exclude:
                logExclude(jp, auditable, auditTrail);
                break;
            case demote:
                logDemote(jp, auditable, auditTrail);
                break;
            case facetSort:
                if (ArrayUtils.contains(AuditTrailConstants.facetSortOperations, auditable.operation())) {
                    logFacetSort(jp, auditable, auditTrail);
                } else if (ArrayUtils.contains(AuditTrailConstants.facetSortGroupOperations, auditable.operation())) {
                    logFacetGroup(jp, auditable, auditTrail);
                } else if (ArrayUtils.contains(AuditTrailConstants.facetSortGroupItemOperations, auditable.operation())) {
                    logFacetGroupItem(jp, auditable, auditTrail);
                }

                break;
            case queryCleaning:
                if (ArrayUtils.contains(AuditTrailConstants.queryCleaningOperations, auditable.operation())) {
                    logQueryCleaning(jp, auditable, auditTrail);
                } else if (ArrayUtils.contains(AuditTrailConstants.queryCleaningConditionOperations, auditable.operation())) {
                    logQueryCleaningCondition(jp, auditable, auditTrail);
                } else if (ArrayUtils.contains(AuditTrailConstants.queryCleaningKeywordOperations, auditable.operation())) {
                    logQueryCleaningKeyword(jp, auditable, auditTrail);
                }
                break;
            case relevancy:
                if (ArrayUtils.contains(AuditTrailConstants.relevancyOperations, auditable.operation())) {
                    logRelevancy(jp, auditable, auditTrail);
                } else if (ArrayUtils.contains(AuditTrailConstants.relevancyFieldOperations, auditable.operation())) {
                    logRelevancyField(jp, auditable, auditTrail);
                } else if (ArrayUtils.contains(AuditTrailConstants.relevancyKeywordOperations, auditable.operation())) {
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
                //logRuleStatus(jp, auditable, auditTrail);
                break;
            case security:
                logSecurity(jp, auditable, auditTrail);
                break;
            case spell:
                logDidYouMean(jp, auditable, auditTrail);
        }
    }

    private void logElevate(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {

        ElevateResult e = null;
        StoreKeyword sk = null;

        if (auditable.operation().equals(Operation.clear)) {
            sk = (StoreKeyword) jp.getArgs()[0];
            auditTrail.setStoreId(sk.getStoreId());
            auditTrail.setKeyword(sk.getKeywordId());
        } else {
            e = (ElevateResult) jp.getArgs()[0];
            auditTrail.setStoreId(e.getStoreKeyword().getStoreId());
            auditTrail.setKeyword(e.getStoreKeyword().getKeywordId());
            auditTrail.setReferenceId(e.getMemberId());
        }

        StringBuilder message = null;

        switch (auditable.operation()) {
            case add:
                message = new StringBuilder("Adding ID[%1$s]");

                if (e.getLocation() != null) {
                    message.append(" to position [%4$s]");
                }

                if (e.getExpiryDate() != null) {
                    message.append(" expiring on [%2$s]");
                }

                if (StringUtils.isNotBlank(e.getComment())) {
                    message.append(" Comment [%3$s]");
                }
                break;
            case update:
                message = new StringBuilder("Elevating ID[%1$s] to position[%4$s]");

                if (e.isForceAdd() != null) {
                    message.append(BooleanUtils.isTrue(e.getForceAdd()) ? "; set to force add" : "; remove force add");
                }

                break;
            case delete:
                message = new StringBuilder("Removing elevated entry ID[%1$s]");
                break;
            case appendComment:
                message = new StringBuilder("Appending comment [%3$s] for elevated entry ID[%1$s]");
                break;
            case updateComment:
                message = new StringBuilder("Setting comment [%3$s] for elevated entry ID[%1$s]");
                break;
            case updateExpiryDate:
                message = new StringBuilder();
                if (e.getExpiryDate() != null) {
                    message.append("Changing expiry date to [%2$s] for elevated entry ID[%1$s]");
                } else {
                    message.append("Removing expiry date for elevated entry ID[%1$s]");
                }
                break;
            case clear:
                message = new StringBuilder("Removing all elevated entries");
                break;
            default:
                message = new StringBuilder();
                return;
        }

        if (auditable.operation().equals(Operation.clear)) {
            auditTrail.setDetails(String.format(message.toString()));
        } else {
            if (e.getCondition() != null) {
                message.append(" Condition[%5$s]");
            }

            auditTrail.setDetails(String.format(message.toString(),
                    auditTrail.getReferenceId(),
                    ObjectUtils.toString(JodaDateTimeUtil.formatFromStorePatternWithZone(e.getExpiryDate(), JodaPatternType.DATE)), e.getComment(),
                    e.getLocation() == null || e.getLocation() == 0 ? 1 : e.getLocation(),
                    e.getCondition() != null ? e.getCondition().getReadableString() : ""));
        }

        logAuditTrail(auditTrail);
    }

    private void logExclude(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {

        ExcludeResult e = null;
        StoreKeyword sk = null;

        if (auditable.operation().equals(Operation.clear)) {
            sk = (StoreKeyword) jp.getArgs()[0];
            auditTrail.setStoreId(sk.getStoreId());
            auditTrail.setKeyword(sk.getKeywordId());
        } else {
            e = (ExcludeResult) jp.getArgs()[0];
            auditTrail.setStoreId(e.getStoreKeyword().getStoreId());
            auditTrail.setKeyword(e.getStoreKeyword().getKeywordId());
            auditTrail.setReferenceId(e.getMemberId());
        }

        StringBuilder message = null;

        switch (auditable.operation()) {
            case add:
                message = new StringBuilder("Adding ID[%1$s]");
                if (e.getExpiryDate() != null) {
                    message.append(" expiring on [%2$s]");
                }

                if (StringUtils.isNotBlank(e.getComment())) {
                    message.append(" Comment [%3$s]");
                }
                break;
            case delete:
                message = new StringBuilder("Removing excluded entry ID[%1$s]");
                break;
            case appendComment:
                message = new StringBuilder("Appending comment [%3$s] for excluded entry ID[%1$s]");
                break;
            case updateComment:
                message = new StringBuilder("Setting comment [%3$s] for excluded entry ID[%1$s]");
                break;
            case updateExpiryDate:
                message = new StringBuilder();
                if (e.getExpiryDate() != null) {
                    message.append("Changing expiry date to [%2$s] for excluded entry ID[%1$s]");
                } else {
                    message.append("Removing expiry date for excluded entry ID[%1$s]");
                }
                break;
            case clear:
                message = new StringBuilder("Removing all excluded entries");
                break;
            default:
                message = new StringBuilder();
                return;
        }

        if (auditable.operation().equals(Operation.clear)) {
            auditTrail.setDetails(String.format(message.toString()));
        } else {
            if (e.getCondition() != null) {
                message.append(" Condition[%4$s]");
            }
            auditTrail.setDetails(String.format(message.toString(),
                    auditTrail.getReferenceId(), ObjectUtils.toString(JodaDateTimeUtil.formatFromStorePatternWithZone(e.getExpiryDate(), JodaPatternType.DATE)), e.getComment(), e.getCondition() != null ? e.getCondition().getReadableString() : ""));
        }

        logAuditTrail(auditTrail);
    }

    private void logDemote(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {

        DemoteResult e = null;
        StoreKeyword sk = null;

        if (auditable.operation().equals(Operation.clear)) {
            sk = (StoreKeyword) jp.getArgs()[0];
            auditTrail.setStoreId(sk.getStoreId());
            auditTrail.setKeyword(sk.getKeywordId());
        } else {
            e = (DemoteResult) jp.getArgs()[0];
            auditTrail.setStoreId(e.getStoreKeyword().getStoreId());
            auditTrail.setKeyword(e.getStoreKeyword().getKeywordId());
            auditTrail.setReferenceId(e.getMemberId());
        }

        StringBuilder message = null;

        switch (auditable.operation()) {
            case add:
                message = new StringBuilder("Adding ID[%1$s]");

                if (e.getLocation() != null) {
                    message.append(" to position [%4$s]");
                }

                if (e.getExpiryDate() != null) {
                    message.append(" expiring on [%2$s]");
                }

                if (StringUtils.isNotBlank(e.getComment())) {
                    message.append(" Comment [%3$s]");
                }
                break;
            case update:
                message = new StringBuilder("Demoting ID[%1$s] to position[%4$s]");
                break;
            case delete:
                message = new StringBuilder("Removing demoted entry ID[%1$s]");
                break;
            case appendComment:
                message = new StringBuilder("Appending comment [%3$s] for demoted entry ID[%1$s]");
                break;
            case updateComment:
                message = new StringBuilder("Setting comment [%3$s] for demoted entry ID[%1$s]");
                break;
            case updateExpiryDate:
                message = new StringBuilder();
                if (e.getExpiryDate() != null) {
                    message.append("Changing expiry date to [%2$s] for demoted entry ID[%1$s]");
                } else {
                    message.append("Removing expiry date for demoted entry ID[%1$s]");
                }
                break;
            case clear:
                message = new StringBuilder("Removing all demoted entries");
                break;
            default:
                message = new StringBuilder();
                return;
        }

        if (auditable.operation().equals(Operation.clear)) {
            auditTrail.setDetails(String.format(message.toString()));
        } else {
            if (e.getCondition() != null) {
                message.append(" Condition[%5$s]");
            }
            auditTrail.setDetails(String.format(message.toString(),
                    auditTrail.getReferenceId(), ObjectUtils.toString(JodaDateTimeUtil.formatFromStorePatternWithZone(e.getExpiryDate(), JodaPatternType.DATE)), e.getComment(), e.getLocation() == null || e.getLocation() == 0 ? 1 : e.getLocation(), e.getCondition() != null ? e.getCondition().getReadableString() : ""));
        }

        logAuditTrail(auditTrail);
    }

    private void logBanner(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {

        StringBuilder message = new StringBuilder();
        Operation operation = auditable.operation();

        if (ArrayUtils.contains(AuditTrailConstants.bannerOperations, auditable.operation())) {
            BannerRule rule = (BannerRule) jp.getArgs()[0];
            auditTrail.setReferenceId(rule.getRuleId());
            auditTrail.setStoreId(rule.getStoreId());
            auditTrail.setKeyword(rule.getRuleName());
            // Operation is either Add or Delete only
            message.append(operation == Operation.add ? "Adding " : "Removing ").append("Banner Rule with ID = [%1$s]");
            if (StringUtils.isNotBlank(rule.getRuleName())) {
                message.append(" and Name = [%2$s]");
            }
            auditTrail.setDetails(String.format(message.toString(), rule.getRuleId(), rule.getRuleName()));
        } else if (ArrayUtils.contains(AuditTrailConstants.bannerItemOperations, operation)) {
            BannerRuleItem ruleItem = (BannerRuleItem) jp.getArgs()[0];
            BannerRule rule = (BannerRule) ruleItem.getRule();
            auditTrail.setStoreId(rule.getStoreId());
            auditTrail.setReferenceId(rule.getRuleId());
            auditTrail.setKeyword(rule.getRuleName());

            switch (operation) {
                case addBanner:
                    message.append("Adding ");
                    break;
                case updateBanner:
                    message.append("Updating ");
                    break;
                case deleteBanner:
                    message.append("Removing ");
                    break;
            }
            message.append("Banner with ID = [%1$s]");
            if (StringUtils.isNotBlank(rule.getRuleName())) {
                message.append(" for Rule Name = [%12$s]");
            }
            message.append(Operation.deleteBanner.equals(operation) ? ": " : ": Setting ");

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
            if (ruleItem.getImagePath() != null && StringUtils.isNotBlank(ruleItem.getImagePath().getId())) {
                message.append("Image Path Id = [%6$s] and ");
            }
            if (ruleItem.getImagePath() != null && StringUtils.isNotBlank(ruleItem.getImagePath().getPath())) {
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

            if (StringUtils.equals(message.substring(message.length() - 5), " and ")) {
                message.replace(message.length() - 5, message.length() - 1, "");
            }

            auditTrail.setDetails(String.format(message.toString(), ruleItem.getMemberId(), ruleItem.getPriority(),
                    ObjectUtils.toString(JodaDateTimeUtil.formatFromStorePatternWithZone(ruleItem.getStartDate(), JodaPatternType.DATE)),
                    ObjectUtils.toString(JodaDateTimeUtil.formatFromStorePatternWithZone(ruleItem.getEndDate(), JodaPatternType.DATE)),
                    ruleItem.getDisabled(),
                    ruleItem.getImagePath() != null ? ruleItem.getImagePath().getId() : "",
                    ruleItem.getImagePath() != null ? ruleItem.getImagePath().getPath() : "",
                    ruleItem.getImageAlt(), ruleItem.getLinkPath(), ruleItem.getOpenNewWindow(),
                    ruleItem.getDescription(), rule.getRuleName()));
        } else if (ArrayUtils.contains(AuditTrailConstants.imagePathOperations, operation)) {
            ImagePath imagePath = (ImagePath) jp.getArgs()[0];
            auditTrail.setStoreId(imagePath.getStoreId());
            auditTrail.setReferenceId(imagePath.getId());

            // Operation is either Add or Update only
            message.append(operation == Operation.add ? "Adding " : "Updating ").append("Image Path  with ID = [%1$s]");
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

            if (StringUtils.equals(message.substring(message.length() - 5), " and ")) {
                message.replace(message.length() - 5, message.length() - 1, "");
            }

            auditTrail.setDetails(String.format(message.toString(), imagePath.getId(),
                    imagePath.getPath(), imagePath.getPathType(), imagePath.getAlias()));
        }
        logAuditTrail(auditTrail);
    }

    @SuppressWarnings("unchecked")
    private void logDidYouMean(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        List<SpellRule> added = null;
        List<SpellRule> updated = null;
        List<SpellRule> deleted = null;
        Integer maxSuggest = null;
        String username = UtilityService.getUsername();
        StringBuilder message = new StringBuilder();

        switch (auditable.operation()) {
            case add:
                added = (List<SpellRule>) jp.getArgs()[0];
                break;
            case update:
                updated = (List<SpellRule>) jp.getArgs()[0];
                deleted = (List<SpellRule>) jp.getArgs()[1];
                break;
            case updateSetting:
                maxSuggest = (Integer) jp.getArgs()[1];
                break;
        }

        if (added != null) {
            for (SpellRule rule : added) {
                AuditTrail at = createTrail(auditable, username);
                at.setReferenceId(rule.getRuleId());
                at.setStoreId(rule.getStoreId());

                message.delete(0, message.length());
                message.append("Adding");

                if (StringUtils.isNotBlank(rule.getRuleId())) {
                    message.append(" ID [%1$s]");
                }

                if (rule.getSearchTerms() != null) {
                    message.append(" Search Terms [%2$s]");
                }

                if (rule.getSuggestions() != null) {
                    message.append(" Suggestions [%3$s]");
                }

                if (StringUtils.isNotBlank(rule.getComment())) {
                    message.append(" Comment [%4$s]");
                }

                at.setDetails(String.format(message.toString(),
                        at.getReferenceId(),
                        rule.getSearchTerms() != null ? StringUtils.join(rule.getSearchTerms(), "|") : "",
                        rule.getSuggestions() != null ? StringUtils.join(rule.getSuggestions(), "|") : "",
                        rule.getComment()));

                logAuditTrail(at);
            }
        }

        if (updated != null) {
            for (SpellRule rule : updated) {
                AuditTrail at = createTrail(auditable, username);
                at.setReferenceId(rule.getRuleId());
                at.setStoreId(rule.getStoreId());

                message.delete(0, message.length());
                message.append("Updating ID [%1$s]");

                if (rule.getSearchTerms() != null) {
                    message.append(" Search Terms [%2$s]");
                }

                if (rule.getSuggestions() != null) {
                    message.append(" Suggestions [%3$s]");
                }

                if (StringUtils.isNotBlank(rule.getComment())) {
                    message.append(" Comment [%4$s]");
                }

                at.setDetails(String.format(message.toString(),
                        at.getReferenceId(),
                        rule.getSearchTerms() != null ? StringUtils.join(rule.getSearchTerms(), "|") : "",
                        rule.getSuggestions() != null ? StringUtils.join(rule.getSuggestions(), "|") : "",
                        rule.getComment()));

                logAuditTrail(at);
            }
        }

        if (deleted != null) {
            for (SpellRule rule : deleted) {
                AuditTrail at = createTrail(auditable, username);
                at.setReferenceId(rule.getRuleId());
                at.setStoreId(rule.getStoreId());

                message.delete(0, message.length());
                message.append("Removing ID [%1$s]");

                if (rule.getSearchTerms() != null) {
                    message.append(" Search Terms [%2$s]");
                }

                if (rule.getSuggestions() != null) {
                    message.append(" Suggestions [%3$s]");
                }

                if (StringUtils.isNotBlank(rule.getComment())) {
                    message.append(" Comment [%4$s]");
                }

                at.setDetails(String.format(message.toString(),
                        at.getReferenceId(),
                        rule.getSearchTerms() != null ? StringUtils.join(rule.getSearchTerms(), "|") : "",
                        rule.getSuggestions() != null ? StringUtils.join(rule.getSuggestions(), "|") : "",
                        rule.getComment()));

                logAuditTrail(at);
            }
        }

        if (maxSuggest != null) {
            AuditTrail at = createTrail(auditable, username);

            at.setReferenceId("spell_rule");
            at.setStoreId((String) jp.getArgs()[0]);

            at.setDetails("Maximum suggestion updated to " + maxSuggest);
            logAuditTrail(at);
        }
    }

    private AuditTrail createTrail(Audit auditable, String username) {
        AuditTrail at = new AuditTrail();
        at.setEntity(auditable.entity().toString());
        at.setOperation(auditable.operation().toString());
        at.setCreatedDate(new DateTime());
        at.setUsername(username);

        return at;
    }

    private void logFacetSort(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {

        FacetSort e = null;
        e = (FacetSort) jp.getArgs()[0];
        auditTrail.setReferenceId(e.getRuleId());
        auditTrail.setStoreId(e.getStoreId());

        StringBuilder message = null;

        switch (auditable.operation()) {
            case add:
                message = new StringBuilder("Adding ID[%1$s]");
                if (StringUtils.isNotBlank(e.getRuleName())) {
                    message.append(" Rule Name [%2$s]");
                }

                if (e.getRuleType() != null) {
                    message.append(" Rule Type [%3$s]");
                }

                if (e.getSortType() != null) {
                    message.append(" Sort Order [%4$s]");
                }
                break;
            case update:
                message = new StringBuilder("Updating ID[%1$s]");
                if (StringUtils.isNotBlank(e.getRuleName())) {
                    message.append(" Rule Name [%2$s]");
                }

                if (e.getRuleType() != null) {
                    message.append(" Rule Type [%3$s]");
                }

                if (e.getSortType() != null) {
                    message.append(" Sort Order [%4$s]");
                }
                break;
            case delete:
                message = new StringBuilder("Removing ID[%1$s]");
                break;
            default:
                message = new StringBuilder();
                return;
        }

        auditTrail.setDetails(
                String.format(message.toString(),
                auditTrail.getReferenceId(), e.getRuleName(),
                e.getRuleType() != null ? e.getRuleType().getDisplayText() : "",
                e.getSortType() != null ? e.getSortType().getDisplayText() : ""));

        logAuditTrail(auditTrail);
    }

    private void logFacetGroup(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        FacetGroup e = null;
        e = (FacetGroup) jp.getArgs()[0];
        auditTrail.setReferenceId(e.getRuleId());
        auditTrail.setStoreId(e.getStoreId());

        StringBuilder message = null;

        switch (auditable.operation()) {
            case updateGroup:
                message = new StringBuilder("Updating ID[%1$s]");
                if (StringUtils.isNotBlank(e.getId())) {
                    message.append(" Facet ID [%2$s]");
                }

                message.append(" Sort Order [%3$s]");

                break;
            default:
                message = new StringBuilder();
                return;
        }

        auditTrail.setDetails(
                String.format(message.toString(),
                auditTrail.getReferenceId(),
                e.getId(),
                e.getSortType() != null ? e.getSortType().getDisplayText() : "Using Rule's General Sort Order"));

        logAuditTrail(auditTrail);
    }

    private void logFacetGroupItem(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        @SuppressWarnings("unchecked")
        List<FacetGroupItem> e = (ArrayList<FacetGroupItem>) jp.getArgs()[0];

        String facetGroupName = "";
        String facetSortOrder = "";
        String highlightedFacets = "";
        List<String> selectedItemList = new ArrayList<String>();

        if (CollectionUtils.isNotEmpty(e)) {
            auditTrail.setReferenceId(e.get(0).getRuleId());
            auditTrail.setStoreId(e.get(0).getStoreId());
            facetGroupName = e.get(0).getFacetGroupName();
            facetSortOrder = e.get(0).getFacetGroupSortOrder();

            for (FacetGroupItem item : e) {
                selectedItemList.add(item.getName());
            }

            highlightedFacets = StringUtils.join(selectedItemList.toArray(), " | ");
        }

        StringBuilder message = null;

        switch (auditable.operation()) {
            case updateGroupItem:
                message = new StringBuilder("Updating ID[%1$s]");
                if (StringUtils.isNotBlank(facetGroupName)) {
                    message.append(" Facet Name [%2$s]");
                }

                message.append(" Highlighted Facets [%4$s]");

                break;
            default:
                message = new StringBuilder();
                return;
        }

        auditTrail.setDetails(
                String.format(message.toString(),
                auditTrail.getReferenceId(),
                facetGroupName,
                facetSortOrder,
                (StringUtils.isNotBlank(highlightedFacets) ? highlightedFacets : "No Highlighted Facet")));

        logAuditTrail(auditTrail);
    }

    private void logKeyword(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        Keyword k = (Keyword) jp.getArgs()[0];
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
        StoreKeyword sk = (StoreKeyword) jp.getArgs()[0];
        auditTrail.setStoreId(sk.getStoreId());
        auditTrail.setKeyword(sk.getKeywordId());
        auditTrail.setReferenceId(sk.getKeywordId());
        switch (auditable.operation()) {
            case add:
                auditTrail.setDetails(String.format("Adding Keyword[%1$s] to Store[%2$s].",
                        auditTrail.getReferenceId(), sk.getStoreId()));
                break;
            default:
                return;
        }
        logAuditTrail(auditTrail);
    }

    private void logQueryCleaning(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        RedirectRule rule = (RedirectRule) jp.getArgs()[0];
        String searchTerm = rule.getSearchTerm();
        String condition = rule.getCondition();
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
                StringBuffer log = new StringBuffer();
                log.append(String.format("Updated Rule ID[%1$s] : ", refId));
                if (rule.getRuleName() != null) {
                    log.append(String.format("name = [%1$s];", rule.getRuleName()));
                }
                if (rule.getDescription() != null) {
                    log.append(String.format("description = [%1$s];", rule.getDescription()));
                }
                if (rule.getRedirectType() != null) {
                    log.append(String.format("redirect type = [%1$s];", rule.getRedirectType()));
                }
                if (rule.getIncludeKeyword() != null) {
                    log.append(String.format("include keyword = [%1$s];", rule.getIncludeKeyword()));
                }
                if (rule.getChangeKeyword() != null) {
                    log.append(String.format("change keyword = [%1$s];", rule.getChangeKeyword()));
                }
                if (rule.getReplaceKeywordMessageType() != null) {
                    ReplaceKeywordMessageType messageType = rule.getReplaceKeywordMessageType();

                    if (messageType != null) {
                        log.append(String.format("replace keyword message type = [%1$s];", messageType.getDisplayText()));

                        if (messageType == ReplaceKeywordMessageType.CUSTOM && StringUtils.isNotBlank(rule.getReplaceKeywordMessageCustomText())) { //with custom message
                            log.append(String.format("replace keyword custom message = [%1$s];", rule.getReplaceKeywordMessageCustomText()));
                        }
                    }
                }
                auditTrail.setDetails(log.toString());
                break;
            case delete:
                auditTrail.setDetails(String.format("Removed Rule ID[%1$s].", refId));
                break;
            default:
                return;
        }
        logAuditTrail(auditTrail);
    }

    private void logQueryCleaningCondition(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        RedirectRuleCondition rule = (RedirectRuleCondition) jp.getArgs()[0];
        auditTrail.setStoreId(rule.getStoreId());
        String refId = String.valueOf(rule.getRuleId());
        auditTrail.setReferenceId(refId);
        // TODO: check if working
        switch (auditable.operation()) {
            case addCondition:
                auditTrail.setDetails(String.format("Added Condition[%1$d] with value '%3$s' for Rule ID[%2$s].", rule.getSequenceNumber(), refId, rule.getReadableString()));
                break;
            case updateCondition:
                auditTrail.setDetails(String.format("Update Condition[%1$d] with value '%3$s' for Rule ID[%2$s].", rule.getSequenceNumber(), refId, rule.getReadableString()));
                break;
            case removeCondition:
                auditTrail.setDetails(String.format("Removed Condition[%1$d] from Rule ID[%2$s].", rule.getSequenceNumber(), refId));
                break;
            default:
                return;
        }
        logAuditTrail(auditTrail);
    }

    private void logQueryCleaningKeyword(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        RedirectRule rule = (RedirectRule) jp.getArgs()[0];
        String searchTerm = rule.getSearchTerm();
        auditTrail.setStoreId(rule.getStoreId());
        String refId = String.valueOf(rule.getRuleId());
        auditTrail.setKeyword(searchTerm);
        auditTrail.setReferenceId(refId);
        switch (auditable.operation()) {
            case mapKeyword:
                auditTrail.setDetails(String.format("Added Search Term[%1$s] for Rule ID[%2$s].", searchTerm, refId));
                break;
            case unmapKeyword:
                auditTrail.setDetails(String.format("Removed Search Term[%1$s] from Rule ID[%2$s].", searchTerm, refId));
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
        Relevancy relevancy = (Relevancy) jp.getArgs()[0];
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
                        DAOUtils.getStoreId(relevancy.getStore()), ObjectUtils.toString(relevancy.getStartDate()), ObjectUtils.toString(relevancy.getEndDate())));
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
                        ObjectUtils.toString(relevancy.getStartDate()), ObjectUtils.toString(relevancy.getEndDate())));
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
        RelevancyField relevancyField = (RelevancyField) jp.getArgs()[0];
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
        RelevancyKeyword relevancyKeyword = (RelevancyKeyword) jp.getArgs()[0];
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
        RuleStatus ruleStatus = (RuleStatus) jp.getArgs()[0];
        auditTrail.setStoreId(ruleStatus.getStoreId());
        auditTrail.setReferenceId(ruleStatus.getRuleRefId());
        //TODO get keywords for query cleaning/ranking rule?
        if (RuleEntity.ELEVATE.getCode() == ruleStatus.getRuleTypeId() || RuleEntity.EXCLUDE.getCode() == ruleStatus.getRuleTypeId()) {
            auditTrail.setKeyword(ruleStatus.getRuleRefId());
        }
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
                    // TODO: only log approval/publishing events; logging of import/export events are handled elsewhere 
                    if (StringUtils.isNotBlank(ruleStatus.getApprovalStatus()) || StringUtils.isNotBlank(ruleStatus.getPublishedStatus())) {
                        auditTrail.setDetails(String.format("Updated reference id = [%1$s], rule type = [%2$s], approval status = [%3$s], published status = [%4$s].",
                                auditTrail.getReferenceId(), RuleEntity.getValue(ruleStatus.getRuleTypeId()), ruleStatus.getApprovalStatus(), ruleStatus.getPublishedStatus()));
                    } else {
                        return;
                    }
                }
                break;
            default:
                return;
        }
        logAuditTrail(auditTrail);
    }

    private void logSecurity(JoinPoint jp, Audit auditable, AuditTrail auditTrail) {
        if (auditTrail.getUsername() == null) {
            return;
        }
        User user = (User) jp.getArgs()[0];
        auditTrail.setStoreId(user.getStoreId());
        auditTrail.setReferenceId(user.getUsername());
        auditTrail.setCreatedBy(user.getCreatedBy() == null ? user.getLastModifiedBy() : user.getCreatedBy());
        switch (auditable.operation()) {
            case add:
                auditTrail.setDetails(String.format("Created username = [%1$s], group = [%2$s], locked = [%3$s], expired = [%4$s].", user.getUsername(), user.getGroupId(),
                        !user.isAccountNonExpired(), !user.isAccountNonExpired()));
                break;
            case update:
                auditTrail.setDetails(String.format("Updated username = [%1$s], locked = [%2$s], expired = [%3$s].", user.getUsername(),
                        !user.isAccountNonExpired(), !user.isAccountNonExpired()));
                break;
            case delete:
                auditTrail.setDetails(String.format("Deleted username = [%1$s].", user.getUsername()));
                break;
            case resetPassword:
                auditTrail.setDetails(String.format("Password has been reset for username = [%1$s].", user.getUsername()));
                break;
            default:
                return;
        }
        logAuditTrail(auditTrail);
    }
}
