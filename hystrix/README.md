[TOC]

# 目录

## Hystrix

### 1-7 Feign+Hystrix实现Fallback降级 

#### Fallback降级
- 创建 hystrix-consumer项目引入依赖
- 实现Fallback降级逻辑
- Fallback降级还有什么花式玩法?

#### 测试流程与结果
- 启动服务
  - EurekaServerApplication :20000/
  - FeignClientApplication (40002) :40002/
  - HystrixFallbackApplication :50000/
- PostMan
  - http://localhost:50000/fallback
  - 返回 Fallback: I'm not a black sheep any more
- 分析
  - MyService 继承了 FeignService (存在必定会异常的接口)
  - 通过请求 GET fallback.error() 出现异常
  - 就降级到 @FeignClient 指定的 fallback = Fallback.class 里面 error() 打印 "Fallback: I'm not a black sheep any more"
    - Fallback 需要 implements MyService, 不然启动会报错
    
### 1-8 Hystrix实现Timeout降级

#### 针对方法设置超时时间

com.example.springcloud.HystrixFallbackApplicationTest
```java
@Test
void testFirstTest() throws NoSuchMethodException {
    System.out.println("获取配置 hystrix 针对方法超时所用到key: " + Feign.configKey(MyService.class,
            MyService.class.getMethod("retry", int.class)));
}
```

方式二：如果传入参数比较复杂，可以通过main方法拿到Key
```yaml
hystrix:
  command:
    MyService#retry(int):  # main返回的值
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000 # 该属性用来配置HystrixCommand执行的超时时间，单位为毫秒
```

测试不同超时秒数，看是否降级
```xml
localhost:50000/timeout?timeout=${0,1,2,3}
```

### 1-9 Hystrix实现Request Cache减压 

#### Request Cache减压

- 降级? No <br> 其实 Request Cache只是一种减压手段
- 使用@CacheResult缓存返回值
- 什么我的Request Cache不生效

#### 要点：
1. lombok.@Cleanup 注解, 代替 try...finally... 
1. HystrixRequestContext context = HystrixRequestContext.initializeContext(); 上下文, 配合 @CacheKey 使用
1. @HystrixCommand(commandKey = "cacheKey") 配合 application.yml  (方式三)
1. RequestCacheService 引用 FeignService 还是 MyService 呢?
   1. RequestCacheService 如果直接引用 FeignService 会启动报错的, 需要在 @FeignClient 加入 primary = false 属性 (相关调用的Feign接口都需要添加)  *不推荐, 改动多*
   1. RequestCacheService 直接引入当前项目接口 hystrix-fallback#com.example.springcloud.service.MyService  *推荐, 改动少*

#### PostMan测试

```xml
GET localhost:50000/cache?name=Eddie

after request cache = [Eddie]
request cache = [Eddie!]
after request cache = [Eddie!]
```

> request cache 只会缓存传入的匹配的参数

### 1-10 多级降级方案 

#### 一错再错-多级降级

- 模拟二级降级场景
- 错无止境 - 有什么解决方案?

##### 如何实现？ 通过 @HystrixCommand 注解实现

```java
@Override
@HystrixCommand(fallbackMethod = "fallback2")
public String error() {
    String msg = "Fallback: I'm not a black sheep any more";
    log.info(msg);
    throw new RuntimeException("first fallback");
}

@HystrixCommand(fallbackMethod = "fallback3")
public String fallback2() {
    String msg = "Fallback: again";
    log.info(msg);
    throw new RuntimeException("fallback again");
}

public String fallback3() {
    String msg = "Fallback: again and again";
    log.info(msg);
    return "success";
}
```

##### 日志输出

```java
2021-01-20 09:17:34.817  INFO 4940 --- [trix-Fallback-1] c.example.springcloud.hystrix.Fallback   : Fallback: I'm not a black sheep any more
2021-01-20 09:17:34.824  INFO 4940 --- [trix-Fallback-2] c.example.springcloud.hystrix.Fallback   : Fallback: again
2021-01-20 09:17:34.825  INFO 4940 --- [trix-Fallback-2] c.example.springcloud.hystrix.Fallback   : Fallback: again and again
```

#### Hystrix注解方式配置超时时间

```java
@GetMapping("/timeout2")
@HystrixCommand(fallbackMethod = "timeoutFallback", commandProperties = {
        // 配置的3秒超时, 实际1S就超时降级
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000") })
public String timeout2(Integer timeout) {
    return myService.retry(timeout);
}

/**
 * fallbackMethod = "timeoutFallback" 所指定的方法，入参必需一致
 * 
 * @param timeout 秒
 * @return str
 */
public String timeoutFallback(Integer timeout) {
    return "sussess";
}
```

##### 测试

```java
# "hystrix.command.default.execution.isolation..thread.timeoutInMilliseconds=2000" 所以返回的 "u are late !"
localhost:50000/timeout2?timeout=3  

# 修改 ... timeoutInMilliseconds=5000
localhost:50000/timeout2?timeout=2
localhost:50000/timeout2?timeout=5

都是返回 sussess

```

> 注解方式需要注意一点："hystrix.command.default.execution.isolation..thread.timeoutInMilliseconds=2000" 配置文件的超时时间有冲突的, 在第一次 timeout2.myService.retry(timeout) 调用时候 配置文件就会生效, 会选择优先最短的超时时间为准. 所以配置文件需要设置比注解的超时时间高, 才会生效 "...timeoutInMilliseconds=5000"

### 1-14 Feign集成Hystrix熔断器 

#### 集成Hystrix熔断器

- 熔断器参数配置
- 验证断路器开关状态切换

#### 熔断器相关配置

```yaml
# 熔断器配置 - 非主要的参数
hystrix:
  command:
    default:
      circuitBreaker:
        enabled: true # 不重要配置 - 全局开关, 默认就是 true
        forceOpen: false # 强制开启熔断开关 - 即使没有熔断也会走入熔断逻辑， 不要轻易开启
        forceClosed: false # 强制关闭熔断开关 - 不要轻易开启

---
# 熔断器配置 - 主要的参数
# {requestVolumeThreshold,errorThresholdPercentage,timeInMilliseconds} 综合三个参数,
# 在20秒的时间窗口内, 当你的请求书达到预置=5的时候，我开始对你进行熔断统计的开关判断, 如果你在这个时间窗口内请求数的失败机率达到 50% 熔断开启
hystrix:
  command:
    default:
      circuitBreaker:
        requestVolumeThreshold: 5 # 熔断的前提条件 (请求的数量), 在一定的时间窗口内, 请求达到5个以后才开始进行熔断判断
        sleepWindowInMilliseconds: 15000 # 单位毫秒, 当熔断开启以后, 经过多少秒再进入半开状态  (查看日志，可以分别出是否已经开启，最后请求时间和下一条相隔1分半钟)
        errorThresholdPercentage: 50 # 超过 50% 的失败请求, 则进入熔断开关开启
      # 配置时间窗口
      metrics:
        rollingStats:
          timeInMilliseconds: 20000 # 单位为毫秒
```

#### PostMan测试

```properties
# 快速请求十次改接口
localhost:50000/timeout?timeout=1

# 日志打印
2021-01-20 14:52:46.010  INFO 18940 --- [io-40002-exec-8] c.e.s.controller.DemoController          : retry=[40002]
2021-01-20 14:52:47.729  INFO 18940 --- [io-40002-exec-5] c.e.s.controller.DemoController          : retry=[40002]
2021-01-20 14:52:50.192  INFO 18940 --- [io-40002-exec-4] c.e.s.controller.DemoController          : retry=[40002]
2021-01-20 14:52:51.698  INFO 18940 --- [io-40002-exec-7] c.e.s.controller.DemoController          : retry=[40002]
2021-01-20 14:52:52.989  INFO 18940 --- [io-40002-exec-9] c.e.s.controller.DemoController          : retry=[40002]
2021-01-20 14:53:09.684  INFO 18940 --- [io-40002-exec-1] c.e.s.controller.DemoController          : retry=[40002]

# 分析日志
请求十次, 请求达到5个以后才开始进行熔断判断, 然后就会停止. 就会开启半开状态 sleepWindowInMilliseconds= 一分半钟.
再第五条记录后, 时间相隔 2021-01-20 14:53:09.684 与 2021-01-20 14:55:33.649 看出已经开启

```