package com.example.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-28 21:52
 * @modified by
 */
@RestController
public class DemoController {

	@Value("${name}")
	private String name;

	@Value("${myWords}")
	private String words;

	@GetMapping("/name")
	public String getName() {
		return name;
	}

	@GetMapping("/words")
	public String getWords() {
		return words;
	}

}
