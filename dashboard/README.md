[TOC]

# 前言

## 1-23 Turbine集成大盘监控-2 

- 创建 hystrix-dashboard 项目, 引入依赖
- 启动大盘监控
- 解读监控页面内容, 玩断路器 

### 启动

1. EurekaServerApplication :20000/
1. FeignConsumeApplication :40001/
1. HystrixDashboardApplication :53000/
1. HystrixFallbackApplication :50000/
1. TurbineApplication :52000/

### 浏览器

1. http://localhost:53000/hystrix/ 打开 dashboard 页面
1. 中间地址栏输入 http://localhost:52000/turbine.stream
1. PostMan请求 http://localhost:50000/fallback 再返回页面看仪表盘数据

> 每次请求都会出现相关接口的信息, 波浪线代表请求数量幅度, 中间绿点or红点代表健康度

Success | Short-Circuited | Bad Request | Timeout | Rejected | Failure | Error %
---|---|---|---|---|---|---
成功|短路|错误请求|超时|拒绝|失败|错误%


