package com.demo.activiti.controller;

import com.demo.activiti.common.entity.ActivitiForm;
import com.demo.activiti.common.entity.HistoricActivityInfo;
import com.demo.activiti.common.entity.ProcessInfo;
import com.demo.activiti.common.entity.TaskInfo;
import com.demo.activiti.common.util.TypeConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * activiti7 流程模拟。常规逻辑[transfer]
 * session 1 : 常规逻辑
 *
 * @author sunjie
 * @date 2022/1/12 10:18
 **/
@Slf4j
@RestController
@RequestMapping("/activiti")
public class ActivitiController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    /**
     * 流程部署
     *
     * @param resourceName 资源名
     * @return java.lang.String 部署id
     */
    @GetMapping(value = "/deployment")
    public String deployment(@RequestParam(value = "resourceName") String resourceName) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name("转换流程");
        Deployment deploy = deploymentBuilder
                .addClasspathResource("processes/" + resourceName + ".bpmn20.xml")
                .deploy();

        log.info("部署流程成功，id={}", deploy.getId());
        return deploy.getId();
    }

    /**
     * 已部署的流程
     *
     * @param definitionKey 流程配置定义key
     * @return List<ProcessInfo> 数据
     */
    @GetMapping(value = "/process/definition")
    public List<ProcessInfo> processDef(@RequestParam(value = "definitionKey") String definitionKey) {
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(definitionKey)
                .list();

        log.info("流程信息-> {}", processDefinitionList);
        return TypeConvertUtil.listConvertor(processDefinitionList, ProcessInfo.class);
    }

    /**
     * 启动流程
     *
     * @param form 流程请求对象
     * @return String 流程实例ID
     */
    @PostMapping(value = "/process/start")
    public String processStart(@RequestBody ActivitiForm form) {
        //查出流程id并返回
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .deploymentId(form.getDeployId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        log.info("流程定义信息：{}", processDefinition);

        final ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());

        log.info("流程实例ID：{}", processInstance.getId());
        return processInstance.getId();
    }

    /**
     * 查询当前实例下的正在处理的任务
     *
     * @param processInstance 流程实例ID
     * @return List<TaskInfo> 当前待处理的任务集合
     */
    @GetMapping(value = "/task/info")
    public List<TaskInfo> taskInfo(@RequestParam(value = "processInstance") String processInstance) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance)
                .list();

        log.info("任务集合：{}", tasks);
        return TypeConvertUtil.listConvertor(tasks, TaskInfo.class);
    }

    /**
     * 当前任务完成
     * @param taskId 任务ID
     * @return String 任务ID
     */
    @GetMapping(value = "/task/complete")
    public String taskComplete(@RequestParam(value = "taskId") String taskId) {
        taskService.complete(taskId);

        log.info("当前任务完成，id：{}", taskId);
        return taskId;
    }

    /**
     * 任务活动历史
     *
     * @param processInstance 流程实例ID
     * @return List<HistoricActivityInfo> 历史信息
     */
    @GetMapping(value = "/history/info")
    public List<HistoricActivityInfo> historyInfo(@RequestParam(value = "processInstance") String processInstance) {
        List<HistoricActivityInstance> hisList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstance)
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();

        log.info("当前任务完成，id：{}", hisList);
        List<HistoricActivityInfo> historicActivityInfos = TypeConvertUtil.listConvertor(hisList, HistoricActivityInfo.class);
        historicActivityInfos = historicActivityInfos.stream()
                .sorted(Comparator.comparing(HistoricActivityInfo::getStartTime))
                .collect(Collectors.toList());
        return historicActivityInfos;
    }

}
