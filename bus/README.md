[TOC]

# 前言

## 1-7 总线式架构的配置中心-1

### 配置中心总线式改造

- 创建 config-bus-server 和 config-bus-consumer
- 启动 RabbitMQ 修改配置属性
- 使用 Actuator 服务推送 Bus 变更信息

#### Rabbitmq的部署

[blog.eddilee.cn](https://blog.eddilee.cn/archives/docker%E9%83%A8%E7%BD%B2rabbitmq%E9%9B%86%E7%BE%A4)

#### 请求测试

- 启动服务
    - EurekaServerApplication :20000/
    - ConfigBusServerEurekaApplication :60002/
- GET http://localhost:60002/develop/config-consumer-prod.json
- GET http://localhost:60002/actuator
    - 全局查询："bus-refresh" 是否存在