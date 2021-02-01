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

## 2-5 Gateway急速落地-创建默认路由规则

### Gateway急速落地

- 创建 gateway-sample 项目, 引入依赖
- 连接 Eureka 自动创建路由
- 通过 Actuator 实现动态路由功能

#### 请求测试

- 启动服务
    - EurekaServerApplication :20000/
    - FeignClientApplication (40002) :40002/
    - FeignClientApplication (40004) :40004/
    - GatewayApplication :65000/
- 查看 Eureka 上面服务是否正常注册
- 查看自动创建的路由
    - http://localhost:65000/actuator/gateway/routes <br> {uri	"lb://FEIGN-CLIENT"}  <br> 包含路由名称和负载均衡功能
- 通过网关请求服务
    - http://localhost:65000/feign-client/sayHi | 因为网关自带负载均衡, 所以会来回切换端口


请求新增路由 <br> 
POST http://localhost:65000/actuator/gateway/routes/dynamic 
```json
{
    "predicates": [
        {
            "name": "Path",
            "args": {
                "_genkey_0": "/dynamic/**"
            }
        }
    ],
    "filters": [
        {
            "name": "StripPrefix",
            "args": {
                "_genkey_0": "1"
            }
        }
    ],
    "uri": "lb://FEIGN-CLIENT",
    "order": 0
}
```

#### 查看添加的路由规则 <br>
http://localhost:65000/actuator/gateway/routes
```json
[
  {
    "route_id": "CompositeDiscoveryClient_GATEWAY-SAMPLE",
    "route_definition": {
      "id": "CompositeDiscoveryClient_GATEWAY-SAMPLE",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/gateway-sample/**"
          }
        }
      ],
      "filters": [
        {
          "name": "RewritePath",
          "args": {
            "regexp": "/gateway-sample/(?<remaining>.*)",
            "replacement": "/${remaining}"
          }
        }
      ],
      "uri": "lb://GATEWAY-SAMPLE",
      "order": 0
    },
    "order": 0
  },
  {
    "route_id": "CompositeDiscoveryClient_FEIGN-CLIENT",
    "route_definition": {
      "id": "CompositeDiscoveryClient_FEIGN-CLIENT",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "pattern": "/feign-client/**"
          }
        }
      ],
      "filters": [
        {
          "name": "RewritePath",
          "args": {
            "regexp": "/feign-client/(?<remaining>.*)",
            "replacement": "/${remaining}"
          }
        }
      ],
      "uri": "lb://FEIGN-CLIENT",
      "order": 0
    },
    "order": 0
  },
  {
    "route_id": "dynamic",
    "route_definition": {
      "id": "dynamic",
      "predicates": [
        {
          "name": "Path",
          "args": {
            "_genkey_0": "/dynamic/**"
          }
        }
      ],
      "filters": [
        {
          "name": "StripPrefix",
          "args": {
            "_genkey_0": "1"
          }
        }
      ],
      "uri": "lb://FEIGN-CLIENT",
      "order": 0
    },
    "order": 0
  }
]
```

#### 替换原来路由名称 <br>

变更前：http://localhost:65000/feign-client/sayHi  <br>
变更后：http://localhost:65000/dynamic/sayHi

#### 动态删除路由

DELETE http://localhost:65000/actuator/gateway/routes/dynamic <br> 
根据后面 dynamic 来指定需要删除的路由名称


## 2-8 利用Path断言实现URL映射

### Path断言

- 使用Path断言转发请求 (yaml配置 + java配置)
- 配合使用Method断言

#### yaml配置

```yaml
spring:
  application:
    name: gateway-sample
  cloud:
    gateway:
      routes:  # 断言配置
        - id: feignclient
          uri: lb://FEIGN-CLIENT
          predicates:
            - Path=/yml/**
          filters:
            - StripPrefix=1 # //localhost:9000/yml/sayHi 会切割前面的  //localhost:9000/sayHi 
```

#### java配置

```java
@Configuration
public class GatewayConfiguration {

	@Bean
	@Order
	public RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes().route(r -> r.path("/java/**")
                .and().method(HttpMethod.GET)
                .and().header("name")            // header 要带 name
                .filters(f -> f.stripPrefix(1)
//						.rewritePath("/java/(?<segment>.*)", "/${segment}")  // 重写跳转规则
                        .addResponseHeader("java-param","gateway-config") // 添加到返回的 header 头
                )
                .uri("lb://FEIGN-CLIENT")
//				.uri("http://www.baidu.com")
        ).build();
	}
}
```

## 2-9 利用After断言实现简易的定时秒杀场景

### After断言实现简易秒杀

- 创建模拟下单接口
- 通过After断言设置生效时间

#### 创建网关样例控制层

```java
@Slf4j
@RestController
@RequestMapping("gateway")
public class GatewayController {

	private static final Map<Long, Product> items = new ConcurrentHashMap<>();

	@ResponseBody
	@RequestMapping(value = "details", method = RequestMethod.GET)
	public Product get(@RequestParam("pid") Long pid) {
		if (!items.containsKey(pid)) {
			Product product = Product.builder()
                    .productId(pid)
                    .description("iphone 12 pro")
                    .stock(100L)
                    .build();
			items.putIfAbsent(pid, product);
		}
		return items.get(pid);
	}

    @RequestMapping(value = "placeOrder", method = RequestMethod.POST)
	public String buy(@RequestParam("pid") Long pid) {
		Product product = items.get(pid);

		if (product == null) {
			return "产品没有找到!";
		} else if (product.getStock() <= 0L) {
			return "售罄";
		}

		synchronized (product) {
			if (product.getStock() <= 0L) {
				return "售罄";
			}
			product.setStock(product.getStock() - 1);
		}
		return "下单";
	}

}
```

com.example.springcloud.config.GatewayConfiguration <br> 追加route 
```java
.route(r -> r.path("/seckill/**")
        .and().after(ZonedDateTime.now().plusMinutes(1)) // 系统加载后, 推迟一分钟在生效
//						.and().before()
//						.and().between()
        .filters(f -> f.stripPrefix(1))
        .uri("lb://FEIGN-CLIENT")
)
```

#### 第一次测试是否成功

- Feign直接调用
    - 查库存 GET localhost:40002/gateway/details?pid=10086
    - 下订单 POST localhost:40002/gateway/placeOrder?pid=10086
- Gateway调用
    - GET localhost:65000/seckill/gateway/details?pid=10086
    - POST localhost:65000/seckill/gateway/placeOrder?pid=10086

