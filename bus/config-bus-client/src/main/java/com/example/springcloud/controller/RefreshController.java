package com.example.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName RefreshController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-28 21:52
 * @modified by
 */
@RefreshScope
@RestController
@RequestMapping("/refresh")
public class RefreshController {

	@Value("${words}")
	private String words;

	@Value("${food}")
	private String food;


	@GetMapping("/words")
	public String getWords() {
		return words;
	}

	@GetMapping("/dinner")
	public String dinner() {
		return "May I have one " + food;
	}

}
