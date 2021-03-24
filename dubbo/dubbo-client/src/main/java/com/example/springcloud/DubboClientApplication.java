package com.example.springcloud;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName DubboClientApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-03-24 9:55
 * @modified by
 */
@EnableDubbo // import org.apache.dubbo.xxxx
@SpringBootApplication
public class DubboClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboClientApplication.class);
    }

}
