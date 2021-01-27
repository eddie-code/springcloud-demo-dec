package com.example.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName ConfigServerApplication
 * @blog blog.eddilee.cn
 * @description
 *
 * 		GET http://localhost:60000/{label}/{application}-{profile}.{json.yml,properties}
 * 		GET http://localhost:60000/{application}/{profile}/{label}
 *
 * @date created in 2021-01-27 14:19
 * @modified by
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ConfigServerApplication.class)
				.web(WebApplicationType.SERVLET)
				.run(args);
	}
}
