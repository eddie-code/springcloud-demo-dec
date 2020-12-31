package com.example.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName EurekaServerApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2020-12-31 10:35
 * @modified by
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerPeerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerPeerApplication.class, args);
    }

}
