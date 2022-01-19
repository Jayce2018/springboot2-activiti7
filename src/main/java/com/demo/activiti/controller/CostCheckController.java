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
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * activiti7 并行网关流程模拟【成本核算】
 * 模拟场景：并行流程、单节点多人候选人处理
 *
 * @author sunjie
 * @date 2022/1/12 10:18
 **/
@Slf4j
@RestController
@RequestMapping("/cost/check")
public class CostCheckController {
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
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().key(resourceName);
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

        log.info("流程定义信息-> {}", processDefinitionList);
        List<ProcessInfo> processInfos = TypeConvertUtil.listConvertor(processDefinitionList, ProcessInfo.class);
        processInfos = processInfos.stream().sorted(Comparator.comparing(ProcessInfo::getVersion).reversed()).collect(Collectors.toList());
        return processInfos;
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
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().deploymentId(form.getDeployId());
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
        log.info("流程定义信息：{}", processDefinition);

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), form.getVariables());

        log.info("流程实例ID：{}", processInstance.getId());
        return processInstance.getId();
    }

    /**
     * 查询当前实例下的正在处理的任务
     *
     * @param form 流程请求对象
     * @return List<TaskInfo> 当前待处理的任务集合
     */
    @PostMapping(value = "/task/info")
    public List<TaskInfo> taskInfo(@RequestBody ActivitiForm form) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(form.getProcessInstanceId())
                .taskCandidateOrAssigned(form.getUserId())
                .list();

        log.info("任务集合：{}", tasks);
        return TypeConvertUtil.listConvertor(tasks, TaskInfo.class);
    }

    /**
     * 指定候选人
     * 说明：责任人存在时，候选人会失效。建议逻辑使用候选人操作，方便动态设置
     *
     * @param form 流程请求对象
     * @return String 任务主键
     */
    @PostMapping(value = "/task/add/candidate")
    public String taskAddCandidate(@RequestBody ActivitiForm form) {
        taskService.addCandidateUser(form.getTaskId(), form.getUserId());
        //申领，无需此操作，完成时校验即可。会导致多人候选变成单一责任人，增加后续多余操作过程
        //taskService.claim(------);
        log.info("任务：{}", form.getTaskId());
        return form.getTaskId();
    }

    /**
     * 当前任务完成
     *
     * @param form 流程请求对象
     * @return String 任务ID
     */
    @PostMapping(value = "/task/complete")
    public String taskComplete(@RequestBody ActivitiForm form) {
        //form.getUserId() 实际业务中用当前登录用户即可
        Task task = taskService.createTaskQuery().taskId(form.getTaskId()).singleResult();
        if (null != task) {
            String assignee = task.getAssignee();
            if (StringUtils.isNotEmpty(assignee)) {
                if (StringUtils.isEmpty(form.getUserId())) {
                    throw new RuntimeException("存在责任定义，请指定责任人查询");
                }
                if (!assignee.equals(form.getUserId())) {
                    throw new RuntimeException("当前用户无权限操作");
                }
            } else {
                //再查候选人，候选中不存在则提示报错
                List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(form.getTaskId());
                List<String> userIdList = identityLinksForTask.stream().map(IdentityLink::getUserId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(userIdList)) {
                    if (!userIdList.contains(form.getUserId())) {
                        throw new RuntimeException("当前候选人无权限操作");
                    }
                }
                //根据需求，可追加候选组校验
            }
        } else {
            throw new RuntimeException("任务不存在");
        }
        //添加备注,记录完成人
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "complete:" + form.getUserId());
        taskService.complete(form.getTaskId(), form.getVariables());

        log.info("当前任务完成，id：{}", form.getTaskId());
        return form.getTaskId();
    }

    /**
     * 任务活动历史
     *
     * @param processInstanceId 流程实例ID
     * @return List<HistoricActivityInfo> 历史信息
     */
    @GetMapping(value = "/history/info")
    public List<HistoricActivityInfo> historyInfo(@RequestParam(value = "processInstanceId") String processInstanceId) {
        List<HistoricActivityInstance> hisList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();

        log.info("当前任务完成，id：{}", hisList);
        return TypeConvertUtil.listConvertor(hisList, HistoricActivityInfo.class);
    }

}
