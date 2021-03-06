package com.search.manager.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.enums.ImportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;

@DataTransferObject(converter = BeanConverter.class)
public class ImportRuleTask extends ModelBean {

    private static final long serialVersionUID = 8380046988342666844L;

    private String taskId;
    private RuleEntity ruleEntity;
    private String sourceStoreId;
    private String sourceRuleId;
    private String sourceRuleName;
    private String targetStoreId;
    private String targetRuleId;
    private String targetRuleName;
    private ImportType importType;
    private TaskExecutionResult taskExecutionResult;

    public ImportRuleTask() {

    }

    public ImportRuleTask(String taskId, RuleEntity ruleEntity, String sourceStoreId, String sourceRuleId,
            String sourceRuleName, String targetStoreId, String targetRuleId, String targetRuleName,
            ImportType importType, TaskExecutionResult taskExecutionResult) {
        this.taskId = taskId;
        this.ruleEntity = ruleEntity;
        this.sourceStoreId = sourceStoreId;
        this.sourceRuleId = sourceRuleId;
        this.sourceRuleName = sourceRuleName;
        this.targetStoreId = targetStoreId;
        this.targetRuleId = targetRuleId;
        this.targetRuleName = targetRuleName;
        this.importType = importType;
        this.taskExecutionResult = taskExecutionResult;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public RuleEntity getRuleEntity() {
        return ruleEntity;
    }

    public void setRuleEntity(RuleEntity ruleEntity) {
        this.ruleEntity = ruleEntity;
    }

    public String getSourceStoreId() {
        return sourceStoreId;
    }

    public void setSourceStoreId(String sourceStoreId) {
        this.sourceStoreId = sourceStoreId;
    }

    public String getSourceRuleId() {
        return sourceRuleId;
    }

    public void setSourceRuleId(String sourceRuleId) {
        this.sourceRuleId = sourceRuleId;
    }

    public String getSourceRuleName() {
        return sourceRuleName;
    }

    public void setSourceRuleName(String sourceRuleName) {
        this.sourceRuleName = sourceRuleName;
    }

    public String getTargetStoreId() {
        return targetStoreId;
    }

    public void setTargetStoreId(String targetStoreId) {
        this.targetStoreId = targetStoreId;
    }

    public String getTargetRuleId() {
        return targetRuleId;
    }

    public void setTargetRuleId(String targetRuleId) {
        this.targetRuleId = targetRuleId;
    }

    public String getTargetRuleName() {
        return targetRuleName;
    }

    public void setTargetRuleName(String targetRuleName) {
        this.targetRuleName = targetRuleName;
    }

    public ImportType getImportType() {
        return importType;
    }

    public void setImportType(ImportType importType) {
        this.importType = importType;
    }

    public TaskExecutionResult getTaskExecutionResult() {
        return taskExecutionResult;
    }

    public void setTaskExecutionResult(TaskExecutionResult taskExecutionResult) {
        this.taskExecutionResult = taskExecutionResult;
    }

    public List<Map<String, Object>> getTaskMessages() {
        List<Map<String, Object>> listOfMap = new ArrayList<Map<String, Object>>();
        Map<String, Object> mp = new HashMap<String, Object>();

        if (getTaskExecutionResult().getTaskStatus().equals(TaskStatus.QUEUED)) {
            mp = new HashMap<String, Object>();
            mp.put("message",
                    "Awaiting action for " + JodaDateTimeUtil.getDateDiffMessage(this.getCreatedDate(), new DateTime()));
            mp.put("dateLabel1", "Created Date:");
            mp.put("displayDate1", this.getCreatedDate());
            listOfMap.add(mp);
        }
        if (getTaskExecutionResult().getTaskStatus().equals(TaskStatus.IN_PROCESS)) {
            mp = new HashMap<String, Object>();
            mp.put("message",
                    "Awaited completion for "
                            + JodaDateTimeUtil.getDateDiffMessage(this.getCreatedDate(), this.getTaskExecutionResult()
                                    .getTaskStartDateTime()));
            mp.put("dateLabel1", "Created Date:");
            mp.put("displayDate1", this.getCreatedDate());
            listOfMap.add(mp);
        }
        if (!getTaskExecutionResult().getTaskStatus().equals(TaskStatus.QUEUED)) {
            mp = new HashMap<String, Object>();
            mp.put("message",
                    "Started "
                            + JodaDateTimeUtil.getDateDiffMessage(this.getTaskExecutionResult().getTaskStartDateTime(),
                                    new DateTime()));
            mp.put("dateLabel1", "Start Date:");
            mp.put("displayDate1", this.getTaskExecutionResult().getTaskStartDateTime());
            listOfMap.add(mp);
        }
        if (!getTaskExecutionResult().getTaskStatus().equals(TaskStatus.QUEUED)
                && !getTaskExecutionResult().getTaskStatus().equals(TaskStatus.IN_PROCESS)) {
            mp = new HashMap<String, Object>();
            mp.put("message",
                    "Ended after"
                            + JodaDateTimeUtil.getDateDiffMessage(this.getTaskExecutionResult().getTaskStartDateTime(),
                                    this.getTaskExecutionResult().getTaskEndDateTime()));
            mp.put("dateLabel1", "Start Date:");
            mp.put("displayDate1", this.getTaskExecutionResult().getTaskStartDateTime());
            mp.put("dateLabel2", "End Date:");
            mp.put("displayDate2", this.getTaskExecutionResult().getTaskEndDateTime());
            listOfMap.add(mp);
        }

        return listOfMap;
    }
}