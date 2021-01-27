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

## 1-6 搭建配置中心Config-Server

### 搭建配置中心

- 创建config-server项目引入依赖
- 添加参数和启动类
- 通过GET请求拉取文件（URL的几种Pattern）

#### 创建config-server项目引入依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

#### 启动类

```java
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ConfigServerApplication.class)
				.web(WebApplicationType.SERVLET)
				.run(args);
	}
}
```

#### 添加参数
application.yml
```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/eddie-code/springcloud-demo-dec.git
          search-paths: config-repo  # 如果一个仓库存放多个定义了不同名称的文件夹，再到里面的配置文件*.yml (多个情况下逗号分隔，也支持通配符*号)
          force-pull: true  # 强制拉取资源文件
          default-label: develop # 指定存放配置的分支名称
#          username: eddie-code
#          password: ******

server:
  port: 60000

```

#### 通过GET请求拉取文件（URL的几种Pattern）

```xml
Github之前创建的文件：
springcloud-demo-dec/config-repo/config-consumer-dev.yml

使用PostMan请求：
GET localhost:60000/config-consumer/dev
GET localhost:60000/config-consumer/dev/develop

控制台会打印：
    Adding property source: file:/C:/Users/ADMINI~1/AppData/Local/Temp/config-repo-7782002141354066613/config-repo/config-consumer-dev.yml

意思是已经把配置文件存放在本地
```

不同的结尾格式返回不同的参数格式：
```xml
GET localhost:60000/config-consumer-dev.yml

info:
  profile: dev
name: Eddie
words: God bless me

----

GET localhost:60000/config-consumer-dev.properties

info.profile: dev
name: Eddie
words: God bless me

---

GET localhost:60000/config-consumer-dev.json

{
    "info": {
        "profile": "dev"
    },
    "name": "Eddie",
    "words": "God bless me"
}

---

指定分支名称
GET localhost:60000/develop/config-consumer-dev.json

```

> 如若提示： "message": "No such label: master" 请添加对应的分支名称, 可在config-server配置文件或者请求时候添加