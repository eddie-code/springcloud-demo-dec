[TOC]

# 目录

## Ribbon

### 1-5 【Demo】给消费者添加负载均衡功能

<details>
<summary>点击查看</summary>
<br>

- 创建 ribbon-consumer
- 添加依赖, 调用eureka-client
- 启动多个eureka-client （服务提供者）
- 将负载均衡策略应用到全局或指定服务
- 测试负载均衡
  - 启动 EurekaServerApplication :20000/ 
  - 启动 EurekaClientApplication :30000/
  - 启动 EurekaClientApplication (30002) :30002/
  - 启动 RibbonConsumerApplication :31000/
  - PostMan 请求 localhost:31000/sayHi  查看返回值是否变换端口

</details>
<br>
