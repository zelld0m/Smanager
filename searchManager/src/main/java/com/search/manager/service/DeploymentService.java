package com.search.manager.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.ExportType;
import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.exception.PublishLockException;
import com.search.manager.mail.WorkflowNotificationMailService;
import com.search.manager.model.Comment;
import com.search.manager.model.DeploymentModel;
import com.search.manager.model.RecordSet;
import com.search.manager.model.RuleStatus;
import com.search.manager.model.SearchCriteria;
import com.search.manager.report.model.xml.RuleXml;
import com.search.manager.xml.file.RuleXmlUtil;
import com.search.ws.ConfigManager;
import com.search.ws.ConfigManager.PropertyFileType;
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service(value = "deploymentService")
@RemoteProxy(
        name = "DeploymentServiceJS",
        creator = SpringCreator.class,
        creatorParams =
        @Param(name = "beanName", value = "deploymentService"))
public class DeploymentService {

    private static final Logger logger =
            LoggerFactory.getLogger(DeploymentService.class);
    @Autowired
    private DaoService daoService;
    @Autowired
    private WorkflowNotificationMailService mailService;
    @Autowired
    private RuleTransferService ruleTransferService;
    @Autowired
    private ConfigManager configManager;

    @RemoteMethod
    public RecordSet<RuleStatus> getApprovalList(String ruleType, Boolean includeApprovedFlag) {
        RecordSet<RuleStatus> rSet = null;
        int ruleTypeId = RuleEntity.getId(ruleType);
        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(ruleTypeId);
            ruleStatus.setStoreId(UtilityService.getStoreId());
            if (includeApprovedFlag) {
                ruleStatus.setApprovalStatus(RuleStatusEntity.getString(RuleStatusEntity.PENDING, RuleStatusEntity.APPROVED));
            } else {
                ruleStatus.setApprovalStatus(RuleStatusEntity.PENDING.toString());
            }
            SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(ruleStatus, null, null, null, null);
            rSet = daoService.getRuleStatus(searchCriteria);
        } catch (DaoException e) {
            logger.error("Failed during getApprovalList()", e);
        }
        return rSet;
    }

    private String[] getRuleStatusIdList(String[] ruleRefIdList, String[] ruleStatusIdList, List<String> ruleRefIdsToMatch) {
        List<String> list = new ArrayList<String>();
        int i = 0;
        for (String ruleRefId : ruleRefIdsToMatch) {
            i = ArrayUtils.indexOf(ruleRefIdList, ruleRefId);
            if (i >= 0) {
                list.add(ruleStatusIdList[i]);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    @RemoteMethod
    public List<String> approveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
        // TODO: add transaction dependency handshake
        List<String> result = approveRule(ruleType, Arrays.asList(ruleRefIdList), comment);
        daoService.addRuleStatusComment(RuleStatusEntity.APPROVED, UtilityService.getStoreId(), UtilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, result));

        return result;
    }

    private List<String> approveRule(String ruleType, List<String> ruleRefIdList, String comment) {
        List<String> result = new ArrayList<String>();
        try {
            List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), RuleStatusEntity.APPROVED.toString());
            getSuccessList(result, daoService.updateRuleStatus(RuleStatusEntity.APPROVED, ruleStatusList, UtilityService.getUsername(), DateTime.now()));

            try {
                if (result != null && result.size() > 0 && "1".equals(ConfigManager.getInstance().getProperty(PropertyFileType.MAIL,UtilityService.getStoreId(), "approvalNotification"))) {
                    List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                    mailService.sendNotification(RuleStatusEntity.APPROVED, ruleType, UtilityService.getUsername(), ruleStatusInfoList, comment);
                }
            } catch (Exception e) {
                logger.error("Failed during sending approval notification. approveRule()", e);
            }
        } catch (DaoException e) {
            logger.error("Failed during approveRule()", e);
        }
        return result;
    }

    private void getSuccessList(List<String> result, Map<String, Boolean> map) {
        for (Map.Entry<String, Boolean> e : map.entrySet()) {
            if (e.getValue()) {
                result.add(e.getKey());
            }
        }
    }

    @RemoteMethod
    public List<String> unapproveRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
        // TODO: add transaction dependency handshake
        List<String> result = unapproveRule(ruleType, Arrays.asList(ruleRefIdList), comment);
        daoService.addRuleStatusComment(RuleStatusEntity.REJECTED, UtilityService.getStoreId(), UtilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, result));
        return result;
    }

    public List<String> unapproveRule(String ruleType, List<String> ruleRefIdList, String comment) {
        List<String> result = new ArrayList<String>();
        try {
            List<RuleStatus> ruleStatusList = generateApprovalList(ruleRefIdList, RuleEntity.getId(ruleType), RuleStatusEntity.REJECTED.toString());
            getSuccessList(result, daoService.updateRuleStatus(RuleStatusEntity.REJECTED, ruleStatusList, UtilityService.getUsername(), DateTime.now()));

            try {
                if (result != null && result.size() > 0 && "1".equals(ConfigManager.getInstance().getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(), "approvalNotification"))) {
                    List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                    mailService.sendNotification(RuleStatusEntity.REJECTED, ruleType, UtilityService.getUsername(), ruleStatusInfoList, comment);
                }
            } catch (Exception e) {
                logger.error("Failed during sending approval notification. unapproveRule()", e);
            }
        } catch (DaoException e) {
            logger.error("Failed during unapproveRule()", e);
        }
        return result;
    }

    @RemoteMethod
    public RecordSet<RuleStatus> getDeployedRules(String ruleType, String filterBy) {
        RecordSet<RuleStatus> rSet = null;
        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setStoreId(UtilityService.getStoreId());
            SearchCriteria<RuleStatus> searchCriteria = new SearchCriteria<RuleStatus>(ruleStatus, null, null, null, null);
            if (StringUtils.isBlank(filterBy)) {
                ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
                ruleStatus.setPublishedStatus(RuleStatusEntity.UNPUBLISHED.toString());
                RecordSet<RuleStatus> approvedRset = daoService.getRuleStatus(searchCriteria);
                ruleStatus.setApprovalStatus(null);
                ruleStatus.setPublishedStatus(RuleStatusEntity.PUBLISHED.toString());
                RecordSet<RuleStatus> publishedRset = daoService.getRuleStatus(searchCriteria);
                rSet = combineRecordSet(approvedRset, publishedRset);
            } else if (filterBy.equalsIgnoreCase(RuleStatusEntity.APPROVED.toString())) {
                ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
                ruleStatus.setUpdateStatus("ADD,UPDATE");
                rSet = daoService.getRuleStatus(searchCriteria);
            } else if (filterBy.equalsIgnoreCase(RuleStatusEntity.PUBLISHED.toString())) {
                ruleStatus.setPublishedStatus(RuleStatusEntity.PUBLISHED.toString());
                ruleStatus.setUpdateStatus("ADD,UPDATE");
                rSet = daoService.getRuleStatus(searchCriteria);
            } else if ("DELETE".equalsIgnoreCase(filterBy)) {
                ruleStatus.setApprovalStatus(RuleStatusEntity.APPROVED.toString());
                ruleStatus.setUpdateStatus("DELETE");
                rSet = daoService.getRuleStatus(searchCriteria);
            }
        } catch (DaoException e) {
            logger.error("Failed during getDeployedRules()", e);
        }
        return rSet;
    }

    private RecordSet<RuleStatus> combineRecordSet(RecordSet<RuleStatus> approvedRset, RecordSet<RuleStatus> publishedRset) {
        List<RuleStatus> list = new ArrayList<RuleStatus>();
        list.addAll(approvedRset.getList());
        list.addAll(publishedRset.getList());
        return new RecordSet<RuleStatus>(list, approvedRset.getTotalSize() + publishedRset.getTotalSize());
    }

    public RecordSet<DeploymentModel> publishRuleNoLock(String store, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
        String username = UtilityService.getUsername();
        boolean isAutoExport = BooleanUtils.toBoolean(UtilityService.getStoreSetting(DAOConstants.SETTINGS_AUTO_EXPORT));
        List<String> approvedRuleList = null;
        List<DeploymentModel> publishingResultList = new ArrayList<DeploymentModel>();

        try {
            if (ArrayUtils.isEmpty(ruleRefIdList)) {
                logger.error("No rule id specified");
            } else if (ArrayUtils.getLength(ruleRefIdList) != ArrayUtils.getLength(ruleStatusIdList)) {
                logger.error(String.format("Inconsistent rule id & rule status id count, RuleID: %s, RuleStatusID: %s", StringUtils.join(ruleRefIdList), StringUtils.join(ruleStatusIdList)));
            } else {
            	approvedRuleList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), null, RuleStatusEntity.APPROVED.toString());
            }
        } catch (DaoException e) {
            logger.error("Failed during retrieval of approved rules list", e);
        }

        Map<String, Boolean> ruleMap = new HashMap<String, Boolean>();

        //publish qualified rule, only approved rule
        if (CollectionUtils.isEmpty(approvedRuleList)) {
            logger.error("No approved rules retrieved for publishing");
        } else {
            // Actual publishing of rules to production
            ruleMap = publishRule(ruleType, approvedRuleList, comment);
        }

        if (MapUtils.isEmpty(ruleMap)) {
            logger.error(String.format("No rules were published from the list of rule id: %s", StringUtils.join(ruleRefIdList, ',')));
        }

        DeploymentModel deploymentModel = null;
        RuleEntity ruleEntity = null;
        List<String> publishedRuleStatusIdList = new ArrayList<String>();
        String ruleId = "";

        //Populate deployment model for all rules queued for publishing
        // The following code generates xml files for published rules required for export.
        // Note that at this point, rules have already been published.
        for (int i = 0; i < Array.getLength(ruleRefIdList); i++) {
            ruleId = ruleRefIdList[i];
            deploymentModel = new DeploymentModel(ruleId, 0);

            if (MapUtils.isNotEmpty(ruleMap) && ruleMap.containsKey(ruleId) && BooleanUtils.isTrue(ruleMap.get(ruleId))) {
                ruleEntity = RuleEntity.find(ruleType);
                deploymentModel.setPublished(1);
                publishedRuleStatusIdList.add(ruleStatusIdList[i]);
                String name = null;

                if (RuleEntity.SPELL.equals(ruleEntity)) {
                    name = "Did You Mean Rules";
                }
                
                if (daoService.createPublishedVersion(store, ruleEntity, ruleId, username, name, comment)) {
                    daoService.addRuleStatusComment(RuleStatusEntity.PUBLISHED, store, username, comment, publishedRuleStatusIdList.toArray(new String[0]));
                    logger.info(String.format("Published Rule XML created: %s %s", ruleEntity, ruleId));
                    if (isAutoExport) {
                        RuleXml ruleXml = RuleXmlUtil.getLatestVersion(daoService.getPublishedRuleVersions(store, ruleType, ruleId));
                        if (ruleXml != null) {
                            try {
                                daoService.exportRule(store, ruleEntity, ruleId, ruleXml, ExportType.AUTOMATIC, username, "Automatic Export on Publish");
                            } catch (DaoException e) {
                                // TODO: make more detailed
                                logger.error("Error occurred while exporting rule: ", e);
                            }
                        }
                    }
                } else {
                    logger.error(String.format("Failed to create published rule xml: %s %s", ruleEntity, ruleId));
                }
            }

            publishingResultList.add(deploymentModel);
        }
                
        return new RecordSet<DeploymentModel>(publishingResultList, publishingResultList.size());
    }

    @RemoteMethod
    public RecordSet<DeploymentModel> publishRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
        boolean obtainedLock = false;
        String userName = UtilityService.getUsername();
        String storeName = UtilityService.getStoreName();
        String storeId = UtilityService.getStoreId();
        try {
            obtainedLock = UtilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
            return publishRuleNoLock(storeId, ruleType, ruleRefIdList, comment, ruleStatusIdList);
        } finally {
            if (obtainedLock) {
                UtilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Boolean> publishRule(String ruleType, List<String> ruleRefIdList, String comment) {
        try {

            // insert custom handling for linguistics rules here
            if (RuleEntity.SPELL.equals(RuleEntity.find(ruleType))) {
                // Spell rule publishing has two phases.
                // 1. Create published version on the database.
                // 2. Create xml file to be transferred to WS to be picked up and transferred to solr.
                // When any of the two steps fail, deployment should be considered a failure.
                // Published version in DB should rollback. File should not be existing.
                daoService.publishSpellRules(UtilityService.getStoreId());
            }

            List<RuleStatus> ruleStatusList = getPublishingListFromMap(publishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString());
            Map<String, Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.PUBLISHED, ruleStatusList, UtilityService.getUsername(), DateTime.now());

            List<String> result = new ArrayList<String>();
            getSuccessList(result, ruleMap);

            if (ruleMap != null && ruleMap.size() > 0) {
                try {
                    if ("1".equals(ConfigManager.getInstance().getProperty(PropertyFileType.MAIL,UtilityService.getStoreId(), "pushToProdNotification"))) {
                        List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                        mailService.sendNotification(RuleStatusEntity.PUBLISHED, ruleType, UtilityService.getUsername(), ruleStatusInfoList, comment);
                    }
                } catch (Exception e) {
                    logger.error("Failed during sending pushToProd notification. publishRule()", e);
                }
                return ruleMap;
            }

        } catch (Exception e) {
            logger.error("Failed during publishRule()", e);
        }

        return Collections.EMPTY_MAP;
    }

    @RemoteMethod
    public RecordSet<DeploymentModel> unpublishRule(String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
        boolean obtainedLock = false;
        String userName = UtilityService.getUsername();
        String storeName = UtilityService.getStoreName();
        try {
            obtainedLock = UtilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
            //clean list, only approved rules should be published
            List<String> cleanList = null;
            List<String> publishedRuleIds = new ArrayList<String>();
            List<DeploymentModel> deployList = new ArrayList<DeploymentModel>();
            try {
                cleanList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString(), null);
            } catch (DaoException e) {
                logger.error("Failed during getCleanList()", e);
            }
            Map<String, Boolean> ruleMap = unpublishRule(ruleType, cleanList, comment);

            for (String ruleId : ruleRefIdList) {
                DeploymentModel deploy = new DeploymentModel();
                deploy.setRuleId(ruleId);
                deploy.setPublished(0);

                if (ruleMap != null && ruleMap.size() > 0) {
                    if (ruleMap.containsKey(ruleId)) {
                        if (ruleMap.get(ruleId)) {
                            deploy.setPublished(1);
                            publishedRuleIds.add(ruleId);
                        }
                    }
                }
                deployList.add(deploy);
            }
            daoService.addRuleStatusComment(RuleStatusEntity.UNPUBLISHED, UtilityService.getStoreId(), UtilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, publishedRuleIds));
            return new RecordSet<DeploymentModel>(deployList, deployList.size());
        } finally {
            if (obtainedLock) {
                UtilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Boolean> unpublishRule(String ruleType, List<String> ruleRefIdList, String comment) {
        try {
            List<RuleStatus> ruleStatusList = getPublishingListFromMap(unpublishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.UNPUBLISHED.toString());
            Map<String, Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.UNPUBLISHED, ruleStatusList, UtilityService.getUsername(), DateTime.now());

            List<String> result = new ArrayList<String>();
            getSuccessList(result, ruleMap);

            if (ruleMap != null && ruleMap.size() > 0) {
                try {
                    if ("1".equals(ConfigManager.getInstance().getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(), "pushToProdNotification"))) {
                        List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                        mailService.sendNotification(RuleStatusEntity.UNPUBLISHED, ruleType, UtilityService.getUsername(), ruleStatusInfoList, comment);
                    }
                } catch (Exception e) {
                    logger.error("Failed during sending pushToProd notification. unpublishRule()", e);
                }
                return ruleMap;
            }

        } catch (Exception e) {
            logger.error("Failed during unpublishRule()", e);
        }

        return Collections.EMPTY_MAP;
    }

    @RemoteMethod
    public RuleStatus getRuleStatus(String ruleType, String ruleRefId) {
        RuleStatus result = null;

        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setRuleRefId(ruleRefId);
            ruleStatus.setStoreId(UtilityService.getStoreId());
            result = daoService.getRuleStatus(ruleStatus);
        } catch (DaoException e) {
            logger.error("Failed during getRuleStatus()", e);
        }
        return result == null ? new RuleStatus() : result;
    }

    @RemoteMethod
    public RecordSet<RuleStatus> getAllRuleStatus(String ruleType) {
        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setStoreId(UtilityService.getStoreId());
            return daoService.getRuleStatus(new SearchCriteria<RuleStatus>(ruleStatus));
        } catch (DaoException e) {
            logger.error("Failed during getAllRuleStatus()", e);
        }
        return null;
    }

    @RemoteMethod
    // Used by Submit For Approval and Delete Rule
    public RuleStatus processRuleStatus(String ruleType, String ruleRefId, String description, Boolean isDelete) {
        int result = -1;
        try {
            String username = UtilityService.getUsername();
            RuleStatus ruleStatus = createRuleStatus();
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setRuleRefId(ruleRefId);
            ruleStatus.setDescription(description);
            ruleStatus.setLastModifiedBy(username);
            ruleStatus.setStoreId(UtilityService.getStoreId());
            result = isDelete ? daoService.updateRuleStatusDeletedInfo(ruleStatus, username)
                    : daoService.updateRuleStatusApprovalInfo(ruleStatus, RuleStatusEntity.PENDING, username, DateTime.now());

            if (result > 0) {
                RuleStatus ruleStatusInfo = getRuleStatus(ruleType, ruleRefId);
                try {
                    if (!isDelete && "1".equals(ConfigManager.getInstance().getProperty(PropertyFileType.MAIL, UtilityService.getStoreId(), "pendingNotification"))) {
                        List<RuleStatus> ruleStatusInfoList = new ArrayList<RuleStatus>();
                        ruleStatusInfoList.add(ruleStatusInfo);
                        mailService.sendNotification(RuleStatusEntity.PENDING, ruleType, UtilityService.getUsername(), ruleStatusInfoList, "");
                    }
                } catch (Exception e) {
                    logger.error("Failed during sending 'Submitted For Approval' notification. processRuleStatus()", e);
                }

                return ruleStatusInfo;
            }
        } catch (DaoException e) {
            logger.error("Failed during processRuleStatus()", e);
        } catch (Exception e) {
            logger.error("Failed during processRuleStatus()", e);
        }
        return null;
    }

    @RemoteMethod
    public int recallRule(String ruleType, List<String> ruleRefIdList) {
        return 0;
        //return unpublishRule(ruleType, ruleRefIdList);
    }

    @RemoteMethod
    public RecordSet<Comment> getComment(String ruleStatusId, int page, int itemsPerPage) {
        RecordSet<Comment> rSet = null;
        try {
            Comment comment = new Comment();
            comment.setReferenceId(ruleStatusId);
            comment.setRuleTypeId(RuleEntity.RULE_STATUS.getCode());
            rSet = daoService.getComment(new SearchCriteria<Comment>(comment, null, null, page, itemsPerPage));
        } catch (DaoException e) {
            logger.error("Failed during getComment()", e);
        }
        return rSet;
    }

    @RemoteMethod
    public int removeComment(Integer commentId) {
        int result = -1;
        try {
            daoService.removeComment(commentId);
        } catch (DaoException e) {
            logger.error("Failed during removeComment()", e);
        }
        return result;
    }

    private List<RuleStatus> generateApprovalList(List<String> ruleRefIdList, Integer ruleTypeId, String status) {
        List<RuleStatus> ruleStatusList = new ArrayList<RuleStatus>();
        for (String ruleRefId : ruleRefIdList) {
            RuleStatus ruleStatus = createRuleStatus();
            ruleStatus.setRuleTypeId(ruleTypeId);
            ruleStatus.setRuleRefId(ruleRefId);
            ruleStatus.setApprovalStatus(status);
            ruleStatusList.add(ruleStatus);
        }
        return ruleStatusList;
    }

    private List<RuleStatus> getPublishingListFromMap(Map<String, Boolean> ruleRefIdMap, Integer ruleTypeId, String status) {
        List<RuleStatus> rsList = new ArrayList<RuleStatus>();
        for (Map.Entry<String, Boolean> e : ruleRefIdMap.entrySet()) {
            if (e.getValue()) {
                RuleStatus ruleStatus = createRuleStatus();
                ruleStatus.setRuleTypeId(ruleTypeId);
                ruleStatus.setRuleRefId(e.getKey());
                ruleStatus.setPublishedStatus(status);
                rsList.add(ruleStatus);
            }
        }
        return rsList;
    }

    private RuleStatus createRuleStatus() {
        String userName = UtilityService.getUsername();
        RuleStatus ruleStatus = new RuleStatus();
        ruleStatus.setCreatedBy(userName);
        ruleStatus.setLastModifiedBy(userName);
        ruleStatus.setStoreId(UtilityService.getStoreId());
        return ruleStatus;
    }

    private Map<String, Boolean> publishWSMap(List<String> ruleList, RuleEntity ruleType) {
        SearchGuiClientService service = new SearchGuiClientServiceImpl();
        return service.deployRulesMap(UtilityService.getStoreId(), ruleList, ruleType);
    }

    private Map<String, Boolean> unpublishWSMap(List<String> ruleList, RuleEntity ruleType) {
        SearchGuiClientService service = new SearchGuiClientServiceImpl();
        return service.unDeployRulesMap(UtilityService.getStoreId(), ruleList, ruleType);
    }

    private List<RuleStatus> getRuleStatusInfo(List<String> results, List<RuleStatus> ruleStatusList) {
        List<RuleStatus> ruleStatusInfoList = new ArrayList<RuleStatus>();
        for (RuleStatus ruleStatus : ruleStatusList) {
            try {
                if (results.contains(ruleStatus.getRuleRefId())) {
                    ruleStatus = daoService.getRuleStatus(ruleStatus);
                    ruleStatusInfoList.add(ruleStatus);
                }
            } catch (DaoException e) {
                logger.error("Error getting rule status info.", e);
            }
        }

        return ruleStatusInfoList;
    }
}
