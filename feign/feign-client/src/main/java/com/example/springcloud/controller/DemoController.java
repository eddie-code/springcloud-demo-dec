package com.example.springcloud.controller;

import com.example.springcloud.service.feign.FeignService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springcloud.model.Friend;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2020-12-31 13:59
 * @modified by
 */
@Slf4j
@RestController
public class DemoController implements FeignService {

	@Value("${server.port}")
	private String port;

	/**
	 * 请求 eureka-client/sayHi
	 *
	 * @return str
	 */
	@Override
	public String sayHi() {
		System.out.println("sayHi " + LocalDate.now());
		return "This is " + port;
	}

	@Override
	public String sayHi2() {
		System.out.println("sayHi2 " + LocalDate.now());
		return "Two This is " + port;
	}

	@Override
	public String valid(){
		System.out.println("valid " + LocalDate.now());
		int i = 1/0;
		return "Error Test Success" + port;
	}

	/**
	 * post 请求
	 *
	 * @param friend
	 * @return friend
	 */
	@Override
	public Friend sayHiPost(Friend friend) {
		log.info("You are {}", friend.getName());
		friend.setPort(port);
		return friend;
	}

	/**
	 * 传入超时秒数
	 *
	 * @param timeout
	 *            超时
	 * @return str
	 */
	@Override
	public String retry(int timeout) {
		while (timeout-- >= 0) {
			// 超时时间 大于等于 0 就进入线程睡眠
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info("retry=[{}]", port);
		return port;
	}

	/**
	 * 访问此接口，必定抱出异常，用于测试
	 *
	 * @return str
	 */
	@Override
	public String error() {
		throw new RuntimeException("black sheep");
	}
}
