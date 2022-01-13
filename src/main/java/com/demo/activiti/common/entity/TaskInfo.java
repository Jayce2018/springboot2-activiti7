package com.demo.activiti.common.entity;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * task对象快照
 *
 * @author sunjie
 * @date 2022/1/13 09:55
 **/
@Data
public class TaskInfo {

    private String id;

    private String name;

    private String description;

    private Integer priority;

    private String owner;

    private String assignee;

    private String processInstanceId;

    private String executionId;

    private String processDefinitionId;

    private Date createTime;

    private String taskDefinitionKey;

    private Date dueDate;

    private String category;

    private String parentTaskId;

    private String tenantId;

    private String formKey;

    private Map<String, Object> taskLocalVariables;

    private Map<String, Object> processVariables;

    private Date claimTime;
}
