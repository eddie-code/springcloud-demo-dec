package com.example.springcloud.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.config
 * @ClassName RedisLimiterConfiguration
 * @blog blog.eddilee.cn
 * @description 限流配置类
 * @date created in 2021-02-04 11:55
 * @modified by
 */
@Configuration
public class RedisLimiterConfiguration {

    /**
     * 按照Path限流
     */
    @Bean("pathKeyResolver")
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getPath()
                        .toString()
        );
    }

    /**
     * 实现针对用户的限流
     */
    @Bean("userKeyResolver")
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(
                        exchange.getRequest()
                                .getQueryParams()
                                .getFirst("user")));
    }

    /**
     * 针对来源IP的限流
     */
    @Bean("ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                Objects.requireNonNull(
                        exchange.getRequest()
                                .getHeaders()
                                .getFirst("X-Forwarded-For"))
        );
    }

	/**
	 * ID: KEY 限流的业务标识
     * 我们这里根据用户请求IP地址进行限流
	 */
    @Bean("remoteAddressKeyResolver")
    @Primary //优先选择
    public KeyResolver remoteAddressKeyResolver(){
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    @Bean("redisLimiterUser")
    @Primary //优先选择
    public RedisRateLimiter redisRateLimiterUser(){
        //这里可以自己创建一个限流脚本,也可以使用默认的令牌桶
        //defaultReplenishRate:限流桶速率,每秒10个
        //defaultBurstCapacity:桶的容量
        return new RedisRateLimiter(1,2);
    }

    @Bean("redisLimiterProduct")
    public RedisRateLimiter redisRateLimiterProduct(){
        //这里可以自己创建一个限流脚本,也可以使用默认的令牌桶
        //defaultReplenishRate:限流桶速率,每秒10个
        //defaultBurstCapacity:桶的容量
        return new RedisRateLimiter(1,2);
    }
}
