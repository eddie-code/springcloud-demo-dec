package com.example.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.filter
 * @ClassName TimerFilter
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 13:55
 * @modified by
 */
@Slf4j
@Component
public class TimerFilter implements GatewayFilter, Ordered { //Ordered是指定执行顺序的接口
// public class TimerFilter implements GlobalFilter, Ordered { // GlobalFilter 全局过滤器

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 这里就是执行完过滤进行调用的地方
		StopWatch timer = new StopWatch();
		// 开始计时
		timer.start(exchange.getRequest().getURI().getRawPath());
		// 我们还可以对调用链进行加工,手工放入请求参数
		exchange.getAttributes().put("requestTimeBegin", System.currentTimeMillis());
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			// 这里就是执行完过滤进行调用的地方
			timer.stop();
			log.info(timer.prettyPrint());
		}));
	}

	@Override
	public int getOrder() {
		return 0;
	}
}