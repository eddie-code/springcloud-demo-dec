[TOC]

# 目录

## Feign

### 2-4 将Feign应用到服务消费者中

- 创建项目，引入依赖
- 借助动态代理接口实现远程调用
- 比较Eureka, Ribbon和Feign的调用方式
- 请求 [sayHi](localhost:40001/sayHi)
  - DemoController
  - FeignService (请求 eureka-client | com.example.springcloud.controller.DemoController#sayHi 方法)
  
### 2-9 改造项目结构-1

- 构建公共接口层（注意不要多引入依赖）
  - feign-client-intf
- 改造服务提供者
- 创建基于公共接口层的服务消费者
  - feign-client
  -  [GET sayHi](localhost:40002/sayHi)
  -  [POST sayHi](localhost:40002/sayHi)
     - { "name":"eddie", "port":"10000" } 