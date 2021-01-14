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