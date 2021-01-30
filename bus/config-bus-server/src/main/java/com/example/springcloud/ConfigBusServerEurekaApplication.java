package com.example.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName ConfigServerEurekaApplication
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-27 14:19
 * @modified by
 */
@EnableConfigServer
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigBusServerEurekaApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ConfigBusServerEurekaApplication.class)
				.web(WebApplicationType.SERVLET)
				.run(args);
	}

}
