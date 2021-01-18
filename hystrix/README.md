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