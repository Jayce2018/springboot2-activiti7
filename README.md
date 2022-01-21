# boot-activiti

#### 介绍
spring boot 2.* 与activiti7.* 整合案例

#### 说明
M5版本 ACT_RE_DEPLOYMENT表缺字段
`VERSION_` int DEFAULT NULL,
`PROJECT_RELEASE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL

#### 流程
springboot+activiti7流程开发小demo
开发流程：
1.创建流程文件(.bpmn)文件，并生成.png
2.运行ActivityApplication会进行流程的部署
3.开始流程
4.完成相关任务

#### 注意：
idea为19及其以下，安装插件actiBPM，流程文件后缀为.bpmn
idea为19以上，可安装activiti-bpmn-visualizer，流程文件后缀为.bpmn20.xml

> 方便演示，都在controller实现，具体场景需合理应用

慎用 ProcessRuntime、TaskRuntime!!!
