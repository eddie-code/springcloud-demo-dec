[TOC]

# 目录

## 一、搭建注册中心

- 创建Demo顶层pom和子项目 eureka-server
- 添加Eureka依赖
- 设置启动类
- Start走起

### 解读注册中心UI页面

- 页面样子是怎样的?
- 都是英文怎么搞啊?
- 我注册的实例服务在哪呢?
- 我自己跑哪里?

#### IP Address

http://localhost:20000

#### System Status

1. Environment、Data center 都是默认
1. Current time 当前系统时间
1. Uptime 自从注册中心启动到现在 (小时)
1. Lease expiration enabled 服务自保
1. Renews threshold 每分钟最小续约数
1. Renews (last min) 过去一分钟续约数量

#### DS Replicas (显示注册中心集群)

Instances currently registered with Eureka

1. Application	应用名称
1. AMIs
1. Availability
1. Zones
1. Status	是否可用

#### General Info

1. total-avail-memory  可用内存
1. environment  
1. num-of-cpus  CPU核数
1. current-memory-usage  使用内存率
1. server-uptime  
1. registered-replicas  集群配置
1. unavailable-replicas  集群配置
1. available-replicas  集群配置

#### Last 1000 since startup

1. Last 1000 cancelled leases  最近1000个已取消租约
1. Last 1000 newly registered leases  最近1000个新注册的租约