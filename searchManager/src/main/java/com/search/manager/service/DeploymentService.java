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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.search.manager.dao.DaoException;
import com.search.manager.dao.DaoService;
import com.search.manager.dao.sp.DAOConstants;
import com.search.manager.enums.ExportType;
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
import com.search.ws.client.SearchGuiClientService;
import com.search.ws.client.SearchGuiClientServiceImpl;

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
    private UtilityService utilityService;
    @Autowired
    private RuleXmlUtil ruleXmlUtil;

    @RemoteMethod
    public RecordSet<RuleStatus> getApprovalList(String ruleType, Boolean includeApprovedFlag) {
        RecordSet<RuleStatus> rSet = null;
        int ruleTypeId = RuleEntity.getId(ruleType);
        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(ruleTypeId);
            ruleStatus.setStoreId(utilityService.getStoreId());
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
    public List<String> approveRule(String storeId, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
        // TODO: add transaction dependency handshake
        List<String> result = approveRule(storeId, ruleType, Arrays.asList(ruleRefIdList), comment);
        daoService.addRuleStatusComment(RuleStatusEntity.APPROVED, storeId, utilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, result));


        return result;
    }

    private List<String> approveRule(String storeId, String ruleType, List<String> ruleRefIdList, String comment) {
        List<String> result = new ArrayList<String>();
        try {
			List<RuleStatus> ruleStatusList = generateApprovalList(storeId, ruleRefIdList, RuleEntity.getId(ruleType), RuleStatusEntity.APPROVED.toString());
            getSuccessList(result, daoService.updateRuleStatus(RuleStatusEntity.APPROVED, ruleStatusList, utilityService.getUsername(), DateTime.now()));

            try {
                if (result != null && result.size() > 0 && mailService.isApprovalNotificationEnable(storeId)) {
					List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                    mailService.sendNotification(storeId, RuleStatusEntity.APPROVED, ruleType, utilityService.getUsername(), ruleStatusInfoList, comment);
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
    public List<String> unapproveRule(String storeId, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) {
        // TODO: add transaction dependency handshake
        List<String> result = unapproveRule(storeId, ruleType, Arrays.asList(ruleRefIdList), comment);
        daoService.addRuleStatusComment(RuleStatusEntity.REJECTED, storeId, utilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, result));
		return result;
    }

    public List<String> unapproveRule(String storeId, String ruleType, List<String> ruleRefIdList, String comment) {
        List<String> result = new ArrayList<String>();
        try {
            List<RuleStatus> ruleStatusList = generateApprovalList(storeId, ruleRefIdList, RuleEntity.getId(ruleType), RuleStatusEntity.REJECTED.toString());
            getSuccessList(result, daoService.updateRuleStatus(RuleStatusEntity.REJECTED, ruleStatusList, utilityService.getUsername(), DateTime.now()));

            try {
                if (result != null && result.size() > 0 && mailService.isApprovalNotificationEnable(storeId)) {
                    List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                    mailService.sendNotification(storeId, RuleStatusEntity.REJECTED, ruleType, utilityService.getUsername(), ruleStatusInfoList, comment);
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
            ruleStatus.setStoreId(utilityService.getStoreId());
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

    //TODO: Transfer to WorkflowServiceImpl
    public RecordSet<DeploymentModel> publishRuleNoLock(String store, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
        String username = utilityService.getUsername();
        boolean isAutoExport = BooleanUtils.toBoolean(utilityService.getStoreSetting(store, DAOConstants.SETTINGS_AUTO_EXPORT));
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
            ruleMap = publishRule(store, ruleType, approvedRuleList, comment);
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
                        RuleXml ruleXml = ruleXmlUtil.getLatestVersion(daoService.getPublishedRuleVersions(store, ruleType, ruleId));
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
    //TODO: Transfer to WorkflowServiceImpl
    @RemoteMethod
    public RecordSet<DeploymentModel> publishRule(String storeId, String storeName, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
        boolean obtainedLock = false;
        String userName = utilityService.getUsername();

        try {
            obtainedLock = utilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
            return publishRuleNoLock(storeId, ruleType, ruleRefIdList, comment, ruleStatusIdList);

        } finally {
            if (obtainedLock) {
                utilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
            }
        }
    }
    //TODO: Transfer to WorkflowServiceImpl
    @SuppressWarnings("unchecked")
    private Map<String, Boolean> publishRule(String storeId, String ruleType, List<String> ruleRefIdList, String comment) {
        try {

            // insert custom handling for linguistics rules here
            if (RuleEntity.SPELL.equals(RuleEntity.find(ruleType))) {
                // Spell rule publishing has two phases.
                // 1. Create published version on the database.
                // 2. Create xml file to be transferred to WS to be picked up and transferred to solr.
                // When any of the two steps fail, deployment should be considered a failure.
                // Published version in DB should rollback. File should not be existing.
                daoService.publishSpellRules(storeId);

            }
            
            List<RuleStatus> ruleStatusList = getPublishingListFromMap(storeId, publishWSMap(storeId, ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString());
            Map<String, Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.PUBLISHED, ruleStatusList, utilityService.getUsername(), DateTime.now());

            List<String> result = new ArrayList<String>();
            getSuccessList(result, ruleMap);

            if (ruleMap != null && ruleMap.size() > 0) {
                try {
                    if (mailService.isPushToProdNotificationEnable(storeId)) {
                        List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                        mailService.sendNotification(storeId, RuleStatusEntity.PUBLISHED, ruleType, utilityService.getUsername(), ruleStatusInfoList, comment);
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
    public RecordSet<DeploymentModel> unpublishRule(String storeId, String ruleType, String[] ruleRefIdList, String comment, String[] ruleStatusIdList) throws PublishLockException {
        boolean obtainedLock = false;
        String userName = utilityService.getUsername();
        String storeName = utilityService.getStoreName();
        try {
            obtainedLock = utilityService.obtainPublishLock(RuleEntity.find(ruleType), userName, storeName);
            //clean list, only approved rules should be published
            List<String> cleanList = null;
            List<String> publishedRuleIds = new ArrayList<String>();
            List<DeploymentModel> deployList = new ArrayList<DeploymentModel>();
            try {
                cleanList = daoService.getCleanList(Arrays.asList(ruleRefIdList), RuleEntity.getId(ruleType), RuleStatusEntity.PUBLISHED.toString(), null);
            } catch (DaoException e) {
                logger.error("Failed during getCleanList()", e);
            }
            Map<String, Boolean> ruleMap = unpublishRule(storeId, ruleType, cleanList, comment);

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
            daoService.addRuleStatusComment(RuleStatusEntity.UNPUBLISHED, utilityService.getStoreId(), utilityService.getUsername(), comment, getRuleStatusIdList(ruleRefIdList, ruleStatusIdList, publishedRuleIds));
            return new RecordSet<DeploymentModel>(deployList, deployList.size());
        } finally {
            if (obtainedLock) {
                utilityService.releasePublishLock(RuleEntity.find(ruleType), userName, storeName);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Boolean> unpublishRule(String storeId, String ruleType, List<String> ruleRefIdList, String comment) {
        try {
            List<RuleStatus> ruleStatusList = getPublishingListFromMap(storeId, unpublishWSMap(ruleRefIdList, RuleEntity.find(ruleType)), RuleEntity.getId(ruleType), RuleStatusEntity.UNPUBLISHED.toString());
            Map<String, Boolean> ruleMap = daoService.updateRuleStatus(RuleStatusEntity.UNPUBLISHED, ruleStatusList, utilityService.getUsername(), DateTime.now());

            List<String> result = new ArrayList<String>();
            getSuccessList(result, ruleMap);

            if (ruleMap != null && ruleMap.size() > 0) {
                try {
                    if (mailService.isPushToProdNotificationEnable(storeId)) {
                        List<RuleStatus> ruleStatusInfoList = getRuleStatusInfo(result, ruleStatusList);
                        mailService.sendNotification(storeId, RuleStatusEntity.UNPUBLISHED, ruleType, utilityService.getUsername(), ruleStatusInfoList, comment);
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

    @RemoteMethod //TODO: move to RuleStatusServiceImpl
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

    @RemoteMethod
    public RecordSet<RuleStatus> getAllRuleStatus(String ruleType) {
        try {
            RuleStatus ruleStatus = new RuleStatus();
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setStoreId(utilityService.getStoreId());
            return daoService.getRuleStatus(new SearchCriteria<RuleStatus>(ruleStatus));
        } catch (DaoException e) {
            logger.error("Failed during getAllRuleStatus()", e);
        }
        return null;
    }

    @RemoteMethod
    // Used by Submit For Approval and Delete Rule
    public RuleStatus processRuleStatus(String storeId, String ruleType, String ruleRefId, String description, Boolean isDelete) {
        int result = -1;
        try {
            String username = utilityService.getUsername();
            RuleStatus ruleStatus = createRuleStatus(storeId);
            ruleStatus.setRuleTypeId(RuleEntity.getId(ruleType));
            ruleStatus.setRuleRefId(ruleRefId);
            ruleStatus.setDescription(description);
            ruleStatus.setLastModifiedBy(username);
            ruleStatus.setStoreId(storeId);
            result = isDelete ? daoService.updateRuleStatusDeletedInfo(ruleStatus, username)
                    : daoService.updateRuleStatusApprovalInfo(ruleStatus, RuleStatusEntity.PENDING, username, DateTime.now());

            if (result > 0) {
                RuleStatus ruleStatusInfo = getRuleStatus(storeId, ruleType, ruleRefId);
                try {
                    if (!isDelete && mailService.isPendingNotificationEnable(storeId)) {
                        List<RuleStatus> ruleStatusInfoList = new ArrayList<RuleStatus>();
                        ruleStatusInfoList.add(ruleStatusInfo);
                        mailService.sendNotification(storeId, RuleStatusEntity.PENDING, ruleType, utilityService.getUsername(), ruleStatusInfoList, "");
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

    private List<RuleStatus> generateApprovalList(String storeId, List<String> ruleRefIdList, Integer ruleTypeId, String status) {
        List<RuleStatus> ruleStatusList = new ArrayList<RuleStatus>();
        for (String ruleRefId : ruleRefIdList) {
            RuleStatus ruleStatus = createRuleStatus(storeId);
            ruleStatus.setRuleTypeId(ruleTypeId);
            ruleStatus.setRuleRefId(ruleRefId);
            ruleStatus.setApprovalStatus(status);
            ruleStatusList.add(ruleStatus);
        }
        return ruleStatusList;
    }
    //TODO: Transfer to WorkflowServiceImpl
    private List<RuleStatus> getPublishingListFromMap(String storeId, Map<String, Boolean> ruleRefIdMap, Integer ruleTypeId, String status) {
        List<RuleStatus> rsList = new ArrayList<RuleStatus>();
        for (Map.Entry<String, Boolean> e : ruleRefIdMap.entrySet()) {
            if (e.getValue()) {
                RuleStatus ruleStatus = createRuleStatus(storeId);
                ruleStatus.setRuleTypeId(ruleTypeId);
                ruleStatus.setRuleRefId(e.getKey());
                ruleStatus.setPublishedStatus(status);
                rsList.add(ruleStatus);
            }
        }
        return rsList;
    }
    //TODO: transfer to RuleStatusServiceImpl
    private RuleStatus createRuleStatus(String storeId) {
        String userName = utilityService.getUsername();
        RuleStatus ruleStatus = new RuleStatus();
        ruleStatus.setCreatedBy(userName);
        ruleStatus.setLastModifiedBy(userName);
        ruleStatus.setStoreId(storeId);
        return ruleStatus;
    }
    //TODO: Transfer to WorkflowServiceImpl
    private Map<String, Boolean> publishWSMap(String storeId, List<String> ruleList, RuleEntity ruleType) {
        SearchGuiClientService service = new SearchGuiClientServiceImpl();
        return service.deployRulesMap(storeId, ruleList, ruleType);
    }

    private Map<String, Boolean> unpublishWSMap(List<String> ruleList, RuleEntity ruleType) {
        SearchGuiClientService service = new SearchGuiClientServiceImpl();
        return service.unDeployRulesMap(utilityService.getStoreId(), ruleList, ruleType);
    }
    //TODO: Transfer to WorkflowServiceImpl
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
