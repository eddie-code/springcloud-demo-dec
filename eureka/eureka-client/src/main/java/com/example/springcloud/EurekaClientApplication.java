package com.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName EurekaClientApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2020-12-31 13:57
 * @modified by
 */
@EnableDiscoveryClient
@SpringBootApplication
public class EurekaClientApplication {

    /**
     * @EnableDiscoveryClient与@EnableEurekaClient区别
     * spring cloud中discovery service有许多种实现（eureka、consul、zookeeper等等）
     *
     * @EnableDiscoveryClient基于spring-cloud-commons
     * @EnableEurekaClient基于spring-cloud-netflix 其实用更简单的话来说，
     * 就是如果选用的注册中心是eureka，那么就推荐@EnableEurekaClient
     * 如果是其他的注册中心，那么推荐使用@EnableDiscoveryClient。
     */

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

}
