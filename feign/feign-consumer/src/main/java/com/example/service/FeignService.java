package com.example.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.service
 * @ClassName FeignService
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-14 16:59
 * @modified by
 */
@FeignClient("eureka-client")
public interface FeignService {

	/**
	 * 请求 eureka-client/sayHi
	 * 
	 * @return str
	 */
	@GetMapping("/sayHi")
	String sayHi();

}
