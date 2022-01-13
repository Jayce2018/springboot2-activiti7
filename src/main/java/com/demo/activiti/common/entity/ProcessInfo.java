package com.demo.activiti.common.entity;

import lombok.Data;

/**
 * ProcessDefinition对象快照
 *
 * @author sunjie
 * @date 2022/1/13 10:01
 **/
@Data
public class ProcessInfo {
    private String id;

    private String category;

    private String name;

    private String key;

    private String description;

    private Integer version;

    private String resourceName;

    private String deploymentId;

    private String diagramResourceName;

    private Boolean hasStartFormKey;

    private Boolean hasGraphicalNotation;

    private Boolean isSuspended;

    private String tenantId;

    private String engineVersion;
}
