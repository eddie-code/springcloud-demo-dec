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
