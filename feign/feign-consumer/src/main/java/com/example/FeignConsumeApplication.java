package com.example;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example
 * @ClassName FeignConsumeApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-14 11:12
 * @modified by
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FeignConsumeApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(FeignConsumeApplication.class)
				.web(WebApplicationType.SERVLET)
				.run(args);
	}

}
