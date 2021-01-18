package com.example.springcloud.controller;

import com.example.springcloud.service.MyService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-18 10:39
 * @modified by
 */
@RestController
public class DemoController {

	@Autowired
	private MyService myService;

	@GetMapping("/fallback")
	public String fallback() {
		return myService.error();
	}

	@GetMapping("/timeout")
	public String timeout(Integer timeout) {
		return myService.retry(timeout);
	}

}
