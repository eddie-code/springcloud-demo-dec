package com.example.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName TurbineApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-21 15:54
 * @modified by
 */
@EnableHystrix
@EnableTurbine
//@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableAutoConfiguration
public class TurbineApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(TurbineApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
	}

}
