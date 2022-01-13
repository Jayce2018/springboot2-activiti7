package com.demo.activiti.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * HistoricActivity对象快照
 *
 * @author sunjie
 * @date 2022/1/13 09:58
 **/
@Data
public class HistoricActivityInfo {
    private String id;

    private String activityId;

    private String activityName;

    private String activityType;

    private String processDefinitionId;

    private String processInstanceId;

    private String executionId;

    private String taskId;

    private String calledProcessInstanceId;

    private String assignee;

    private Date startTime;

    private Date endTime;

    private Long durationInMillis;

    private String deleteReason;

    private String tenantId;
}
