package com.demo.activiti.common.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 流程请求对象
 *
 * @author sunjie
 * @date 2022/1/14 16:47
 **/
@Data
public class ActivitiForm {
    @ApiModelProperty("部署id")
    private String deployId;

    @ApiModelProperty("流程定义key")
    private String processKey;

    @ApiModelProperty("变量")
    private Map<String, Object> variables;

    @ApiModelProperty("任务主键")
    private String taskId;

    @ApiModelProperty("流程实例主键")
    private String processInstanceId;

    @ApiModelProperty("用户")
    private String userId;

    @ApiModelProperty("节点名称")
    private String jumpNodeName;
}
