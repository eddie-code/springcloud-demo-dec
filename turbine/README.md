[TOC]

# 前言

## Turbine 聚合信息

- 创建 hystrix-turbine 子模块, 引入依赖
- 添加 Turbine 配置, 指定监控服务名称
- hystrix-fallback 项目开发 actuator 服务

### 启动服务

1. EurekaServerApplication :20000/
1. FeignClientApplication (40002) :40002/
1. HystrixFallbackApplication :50000/

#### 请求测试
```properties
# 1. 访问接口
http://localhost:50000/actuator/hystrix.stream

# 2. PostMan请求接口, 多请求这些接口几次
http://localhost:50000/fallback
localhost:50000/timeout?timeout=1

# 3. 返回页面查看
会打印很多相关的信息
```

### 启动 Turbine

#### 请求测试
```properties
http://localhost:52000/turbine.stream

也会打印差不多的信息
```