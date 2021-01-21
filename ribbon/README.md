[TOC]

# 前言

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

### 1-9 【源码品读】负载均衡策略解析-1

- 负载均衡策略源码探秘
  - 熟悉七种负载均衡策略
  - 复习自旋锁的使用方式 (Syn)
  - 什么是防御性编程？ 防谁？
  
### 1-${16,17} 【造轮子】自定义IRule-${1,2}

- 造轮子-自定义IRule
  - 自定义基于一致性哈希负载均衡策略
  - 指定服务应用自定义策略
 - 通过PostMan测试
   - localhost:31000/sayHi?test=32123  得到 This is 30002  
   - localhost:31000/sayHi?test=456456 得到 This is 30001

> test=xxx 其实可以对应你的服务器id,或者某些服务应用标识