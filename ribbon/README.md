[TOC]

# 目录

## Ribbon

### 1-5 【Demo】给消费者添加负载均衡功能

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

### 1-8 配置负载均衡策略

1、 全局配置
```java
@Bean
public IRule defaultLBStrategy() {
    return new RandomRule();
}
```

2、 指定服务配置（优先级低）

```yaml
eureka-client: # 服务名称
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule # 乱序
```

3、 注解方式（优先级高）

```java
@Configuration
@RibbonClient(name = "eureka-client", configuration = com.netflix.loadbalancer.RandomRule.class)
public class RibbonConfiguration {
}
```

> 优先级高低，和Spring加载顺序有关 <br>
 注解方式 高于 指定服务配置  <br>
 全局配置 高于 指定服务配置