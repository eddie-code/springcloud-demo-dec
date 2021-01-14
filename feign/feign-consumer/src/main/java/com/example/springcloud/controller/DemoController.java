package com.example.springcloud.controller;

import com.example.springcloud.service.FeignService;
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
 * @date created in 2021-01-14 17:03
 * @modified by
 */
@RestController
public class DemoController {

	@Autowired
	private FeignService feignService;

	@GetMapping("/sayHi")
	public String sayHi() {
		return feignService.sayHi();
	}

}
