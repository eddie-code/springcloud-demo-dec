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


## 2-11 自定义过滤器实现接口计时功能

### 自定义过滤器

- 创建 TimerFilter 实现计时功能
- 添加 TimerFilter 到路由

#### TimerFilter

```java
@Slf4j
@Component
public class TimerFilter implements GatewayFilter, Ordered {
//public class TimerFilter implements GlobalFilter, Ordered {  // GlobalFilter 全局过滤器

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        StopWatch watch = new StopWatch();
        watch.start(exchange.getRequest().getURI().getRawPath());

//        exchange.getAttributes().put("requestTimeBegain",System.currentTimeMillis());
        exchange.getRequest().getHeaders();
        return chain.filter(exchange).then(
            Mono.fromRunnable(()->{
                watch.stop();
                log.info(watch.prettyPrint());
            })  // 异步编程
        );
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

> 全局 GlobalFilter 不需要在 @Autowired TimerFilter


## 2-14 基于jwt实现用户鉴权-1

### 实现JWT鉴权

- 创建 auth-service (登录、鉴权等服务)
- 添加 JwtService类 实现token创建和验证
- 网关层集成 auth-service (添加 AuthFilter 到网关层)

```java
package com.example.springcloud

@Data
@Slf4j
@Component("authFilter")
@ConfigurationProperties("ignore.jwt")
public class AuthFilter implements GatewayFilter, Ordered {

    private static final String AUTH = "Authorization";

    private static final String USERNAME = "jwt-user-name";

    private String[] skipAuthUrls;

    @Autowired
    private AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Auth start");

        String url = exchange.getRequest().getURI().getPath();
        System.out.println(url);

		if (null != skipAuthUrls && Arrays.asList(skipAuthUrls).contains(url)) {
			log.info("ignore jwt auth");
			return chain.filter(exchange);
		}

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders header = request.getHeaders();
        String token = header.getFirst(AUTH);
        String username = header.getFirst(USERNAME);

        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isBlank(token)) {
            log.error("token not found");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        AuthResponse resp = authService.verify(token, username);

        assert resp != null;
        if (resp.getCode() != 1L) {
            log.error("invalid token");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        // TODO 将用户信息存放在请求header中传递给下游业务
        ServerHttpRequest.Builder mutate = request.mutate();
        assert username != null;
        mutate.header("jwt-user-name", username);
        ServerHttpRequest buildReuqest = mutate.build();

        //todo 如果响应中需要放数据，也可以放在response的header中
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("jwt-username",username);
        return chain.filter(exchange.mutate()
                .request(buildReuqest)
                .response(response)
                .build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

## 2-15 基于jwt实现用户鉴权-2

### 请求测试

基于 auth-service 服务请求

```xml
- 生成Token
    - POST http://localhost:65100/login
    - Body -> x-www-form-unlencoded -> username=me / password=123456
- 校验Token
    - GET http://localhost:65100/verify?username=${生成token时候的用户名}&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJlZGRpZSIsImV4cCI6MTYxMjE5MDY3MSwiaWF0IjoxNjEyMTkwNjExLCJ1c2VybmFtZSI6Im1lIn0.oDWI17FUGhNKZpQOhFHR2UqG2XgecfpySVAxMiUz6oc
- 刷新token
    - POST http://localhost:65100/refresh
    - Body -> x-www-form-unlencoded -> refreshToken=${生成token时候的refreshToken}
```

基于 gateway-sample 服务请求
```xml
- 拦截请求
    - http://localhost:65000/java/sayHi
    - Headers
        - name:eddie
      Authorization:eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJlZGRpZSIsImV4cCI6MTYxMjM0Mjc3NCwiaWF0IjoxNjEyMzQyNzE0LCJ1c2VybmFtZSI6Im1lIn0.RKtuUTcKBMBEzJM0GFZYbaSBuUc7VbiHrAIa8uqRqfI
      jwt-user-name:me
- 放行请求
    - http://localhost:65000/java/sayHi2
    - Headers
        - name:eddie
```

> TIPS： 必需添加 Headers:name=${value}, 不然会报404. 除非 route 不指定添加头部信息 

#### 修复 AuthFilter 包路径抛出空指针问题

- 错误提示： 空指针
- 引发错误： AuthFilter 存放在 com.example.springcloud.filter 下
- 修复问题： 使用 restTemplate 请求方式

## 2.16 通过Gateway层对Service层各类异常做统一处理

现有的网关层异常格式：

```json
{
    "timestamp": "2021-02-04T03:04:58.501+0000",
    "path": "/java/sayHi2",
    "status": 404,
    "error": "Not Found",
    "message": null
}
```

通过 装饰器编程模式+代理模式，给Gateway加一层特效，改变ResponseBody中的数据结构，顺带也体验一下如何将编程模式运用到实际需求中

### 代理模式 - BodyHackerFunction接口

```java
package com.example.springcloud.tools;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface BodyHackerFunction extends BiFunction<ServerHttpResponse, Publisher<? extends DataBuffer>, Mono<Void>> {
}
```

这里引入代理模式是为了将装饰器和具体业务代理逻辑拆分开来，在装饰器中只需要依赖一个代理接口，而不需要和具体的代理逻辑绑定起来

### 装饰器模式 - BodyHackerDecrator

接下来我们定义一个装饰器类，这个装饰器继承自ServerHttpResponseDecorator类，我们这里就用装饰器模式给Response Body的构造过程加上一层特效

```java
package com.example.springcloud.tools;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

public class BodyHackerHttpResponseDecorator extends ServerHttpResponseDecorator {

    /**
     * 负责具体写入Body内容的代理类
     */
    private BodyHackerFunction delegate = null;

    public BodyHackerHttpResponseDecorator(BodyHackerFunction bodyHandler, ServerHttpResponse delegate) {
        super(delegate);
        this.delegate = bodyHandler;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return delegate.apply(getDelegate(), body);
    }
}
```

这个装饰器的构造方法接收一个BodyHancker代理类，其中的关键方法writeWith就是用来向Response Body中写入内容的。这里我们覆盖了该方法，使用代理类来托管方法的执行，而在整个装饰器类中看不到一点业务逻辑，这就是我们常说的单一职责。

### 创建Filter

```java
package com.example.springcloud.filter;

import com.example.springcloud.tools.BodyHackerFunction;
import com.example.springcloud.tools.BodyHackerHttpResponseDecorator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ErrorFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        // TODO 这里定义写入Body的逻辑
        BodyHackerFunction delegate = (resp, body) -> Flux.from(body)
                .flatMap(orgBody -> {
                    // 原始的response body
                    byte[] orgContent = new byte[orgBody.readableByteCount()];
                    orgBody.read(orgContent);

                    String content = new String(orgContent);
                    log.info("original content {}", content);

                    // 如果500错误，则替换
                    if (resp.getStatusCode().value() == 500) {
                        content = String.format("{\"status\":%d,\"path\":\"%s\"}",
                                resp.getStatusCode().value(),
                                request.getPath().value());
                    }

                    // 告知客户端Body的长度，如果不设置的话客户端会一直处于等待状态不结束
                    HttpHeaders headers = resp.getHeaders();
                    headers.setContentLength(content.length());
                    return resp.writeWith(Flux.just(content)
                            .map(bx -> resp.bufferFactory().wrap(bx.getBytes())));
                }).then();

        // 将装饰器当做Response返回
        BodyHackerHttpResponseDecorator responseDecorator = new BodyHackerHttpResponseDecorator(delegate, exchange.getResponse());

        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    @Override
    public int getOrder() {
        // WRITE_RESPONSE_FILTER的执行顺序是-1，我们的Hacker在它之前执行
        return -2;
    }
}
```

在这个Filter中，我们定义了一个装饰器类BodyHackerHttpResponseDecorator，同时声明了一个匿名内部类(代码TODO部分)，实现了BodyHackerFunction代理类的Body替换逻辑，并且将这个代理类传入了装饰器。这个装饰器将直接参与构造Response Body。

我们还覆盖了getOrder方法，是为了确保我们的filter在默认的Response构造器之前执行

我们对500的HTTP Status做了特殊定制，使用我们自己的JSON内容替换了原始内容，同学们可以根据需要向JSON中加入其它参数。对于其他非500 Status的Response来说，我们还是返回初始的Body。

我们在feign-client的GatewayController中定一个500的错误方法进行测试

```java
@GetMapping("/valid")
public String valid(){
    int i = 1/0;
    return "Error Test Success";
}
```
ErrorFilter的注入方式同之前的过滤器一样

请求测试
```xml
http://localhost:65000/java/valid

name:eddie
Authorization:eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJlZGRpZSIsImV4cCI6MTYxMjQwODkwNCwiaWF0IjoxNjEyNDA4ODQ0LCJ1c2VybmFtZSI6Im1lIn0.Y5xbAeuQ0QQd2iNbxD_3UptTeDCkdRKL5TC1qmh405g
jwt-user-name:me
```

1/0 状态码=500, 返回 { "status": 500, "path": "/java/valid" }

<br>

[The complete sample project for this tutorial can be downloaded code.](https://github.com/eddie-code/springcloud-demo-dec/tree/develop/gateway)