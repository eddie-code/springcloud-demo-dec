package com.example.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName HystrixFallbackApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-18 10:23
 * @modified by
 */
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker //断路器
@SpringBootApplication
public class HystrixFallbackApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(HystrixFallbackApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
	}

}
