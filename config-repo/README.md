[TOC]

# 前言

## 1-1 本章概述

- 配置中心介绍, 在微服务中的应用
- Config核心功能和应用
- 直连式架构模式
  - GitHub准备
  - 搭建config-server应用
  - 应用方直连配置中心
- 参数动态刷新机制 + Github
- 思考-高可用性能分析
  - 单节点崩溃了，高可用改造方向
  - 借助Eureka实现高可用配置中心架构
  
## 1-2 配置中心在微服务中的应用

### 常规的配置定义

```xml
配置项定义
    -> 程序Hardcode
    -> 配置文件
        -> application.yml
        -> bootstrap.yml
    -> 环境变量
        -> 操作系统层面
        -> 启动参数, 如 verbose:gc
    -> 数据库存储
    -> 高可用
    -> 版本控制
        -> 修改记录
        -> 版本控制, 权限控制
    -> 业务需求
        -> 动态推送变更
        -> 内容加密
```

### 传统配置管理的缺点

格式不统一
- json
- properties
- yml

没有版本控制
- 想改就改

基于静态配置
- 修改, 发布, 流程繁琐

发布零散
- 没有统一管理

### 配置项的静态内容

静态配置
- 环境配置：数据库连接串, Eureka注册中心, Kafka连接, 应用名称
- 安全配置：连接密码, 公钥私钥, HTTP连接Cert

### 配置项的动态内容

动态配置
- 功能控制：功能开关, 人工熔断开关, 蓝绿发布(金丝雀测试), 数据源切换
- 业务规则：当日外汇利率, 动态文案, 规则引擎参数
- 应用参数： 网关黑白名单, 缓存过期时间, 日志MDC设置


## 1-5 准备工作 - Git Repo中的配置文件命名规则 

### 创建GitHub配置仓库

- 创建[Github](https://github.com/eddie-code/springcloud-demo-dec/tree/develop)仓库
- 文件命名规则（文件名可不能随便起）
- 添加配置文件和属性

### 文件命名规则

- Application & Profile
  - {application}-{profile}.yml
  - {application}-{profile}.properties
  
- Label - 代码分支的名称
