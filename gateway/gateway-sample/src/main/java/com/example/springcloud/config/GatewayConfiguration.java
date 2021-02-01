package com.example.springcloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import java.time.ZonedDateTime;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.config
 * @ClassName GatewayConfiguration
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-31 17:26
 * @modified by
 */
@Configuration
public class GatewayConfiguration {

	@Bean
	@Order
	public RouteLocator getRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/java/**")
					.and().method(HttpMethod.GET)
					.and().header("name")            // header 要带 name
					.filters(f -> f.stripPrefix(1)
	//						.rewritePath("/java/(?<segment>.*)", "/${segment}")  // 重写跳转规则
							.addResponseHeader("java-param","gateway-config") // 添加到返回的 header 头
                )
                .uri("lb://FEIGN-CLIENT")
//				.uri("http://www.baidu.com")
        		)
				.route(r -> r.path("/seckill/**")
						.and().after(ZonedDateTime.now().plusMinutes(1)) // 系统加载后, 推迟一分钟在生效
//						.and().before()
//						.and().between()
						.filters(f -> f.stripPrefix(1))
						.uri("lb://FEIGN-CLIENT")
				)
				.build();
	}

}
