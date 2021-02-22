package com.example.springcloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class SleuthTrace2Application {

    @Bean
    @LoadBalanced
    public RestTemplate lb() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = "/traceB")
    public String traceB() {
        log.info("---------Trace.B");
        return "traceB";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(SleuthTrace2Application.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
