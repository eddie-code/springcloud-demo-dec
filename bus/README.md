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
    
## 1-8 配置中心改造为总线架构-2

#### 请求测试

- 启动服务
    - EurekaServerApplication :20000/
    - ConfigBusServerEurekaApplication :60002/
    - ConfigBusClientApplication (61001) :61001/
    - ConfigBusClientApplication (61002) :61002/
- GET 
    - http://localhost:61001/refresh/words
    - http://localhost:61002/refresh/words
- 修改Github上的配置值 [springcloud-demo-dec/config-repo/config-consumer-prod.yml](https://github.com/eddie-code/springcloud-demo-dec/blob/develop/config-repo/config-consumer-prod.yml)
    - words: 'God bless u'  修改成  words: 'God bless u，eddie~'
- POST http://localhost:60002/actuator/bus-refresh  (Postman请求后的状态码是 204 = 成功)

> 还有一种方式： POST http://localhost:61001/actuator/bus-refresh 或者 61002 端口, 也是会刷新配置成功. <br> 这就是意味着无论你发送刷新请求到配置中心, 又或者是下面的节点, 也是可以的.