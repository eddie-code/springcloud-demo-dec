package com.example.springcloud.service.notfeign;

import org.springframework.web.bind.annotation.RequestBody;

import com.example.springcloud.model.Friend;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service
 * @ClassName FeignService
 * @blog blog.eddilee.cn
 * @description 提供给不使用SpringCloud的
 * @date created in 2021-01-14 16:59
 * @modified by
 */
public interface NotFeignService {

	/**
	 * 请求 eureka-client/sayHi
	 * 
	 * @return str
	 */
	String sayHi();

	/**
     * post 请求
	 * 
	 * @param friend
     * @return friend
	 */
	Friend sayHiPost(@RequestBody Friend friend);

}
