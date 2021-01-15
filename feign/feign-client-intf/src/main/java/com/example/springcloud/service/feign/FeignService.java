package com.example.springcloud.service.feign;

import com.example.springcloud.model.Friend;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service
 * @ClassName FeignService
 * @blog blog.eddilee.cn
 * @description SpringCloud专用接口
 * @date created in 2021-01-14 16:59
 * @modified by
 */
@FeignClient("feign-client")
public interface FeignService {

	/**
	 * 请求 eureka-client/sayHi
	 * 
	 * @return str
	 */
	@GetMapping("/sayHi")
	String sayHi();

	/**
	 * post 请求
	 * 
	 * @param friend
	 * @return friend
	 */
	@PostMapping("/sayHi")
	Friend sayHiPost(@RequestBody Friend friend);

	/**
	 * 传入超时秒数
	 * 
	 * @param timeout 超时
	 * @return str
	 */
	@GetMapping("/retry")
	String retry(@RequestParam(name = "timeout") int timeout);
}
