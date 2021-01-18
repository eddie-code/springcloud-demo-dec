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