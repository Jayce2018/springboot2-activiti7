package com.demo.activiti.task;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * 模拟系统任务
 *
 * @author sunjie
 * @date 2022/1/14 09:36
 **/
@Component
public class OrderExistTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("status", "true");
    }
}
