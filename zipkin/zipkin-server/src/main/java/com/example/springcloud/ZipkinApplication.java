package com.example.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import zipkin.server.internal.EnableZipkinServer;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName ZipkinApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-22 15:45
 * @modified by
 */
@EnableZipkinServer
@SpringBootApplication
public class ZipkinApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ZipkinApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
