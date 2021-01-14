package com.example.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName Application
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-14 21:54
 * @modified by
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FeignClientAdvancedApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(FeignClientAdvancedApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
