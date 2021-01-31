[TOC]

# 前言

## 2-3 第二代网关组件Gateway介绍

### 网二代的标签

- Spring官方推荐
- Netty 性能好

### Gateway做什么

- Gateway
    - 路由寻址
    - 负载均衡
    - 限流
    - 鉴权
    
### Gateway VS Zuul

#### 性能

 -| Gateway | Zuul 1.x | Zuul 2.x|
---|---|---|---
靠谱性 | 官方支持 | 曾经靠谱过 | 专业放鸽子
性能 | Netty | 同步阻塞, 性能慢 | Netty
RPS | >32000 | 20000左右 | 25000左右
Spring Cloud | 已整合 | 已整合 | 暂无整合计划

#### 综合对比

 -| Gateway | Zuul 1.x | Zuul 2.x|
---|---|---|---
长连接 | 支持 | 长不支持 | 支持
编程体验 | 比较难 | 比较简单 | 比较难
调式&链路追踪 | 比较难 | 无压力 | 比较难