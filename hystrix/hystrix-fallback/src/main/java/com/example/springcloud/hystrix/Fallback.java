package com.example.springcloud.hystrix;

import com.example.springcloud.model.Friend;
import com.example.springcloud.service.MyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.hystrix
 * @ClassName Fallback
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-18 10:35
 * @modified by
 */
@Slf4j
@Component
public class Fallback implements MyService {
//public class Fallback {   // java.net.Inet6AddressImpl.getHostByAddr(Native Method)

	/**
	 * 访问此接口，必定抱出异常，用于测试
	 *
	 * @return str
	 */
	@Override
	public String error() {
		String msg = "Fallback: I'm not a black sheep any more";
		log.info(msg);
		return msg;
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
		String msg = "u are late !";
		return msg;
	}

	/**
	 * 请求 eureka-client/sayHi
	 *
	 * @return str
	 */
	@Override
	public String sayHi() {
		return null;
	}

	/**
	 * post 请求
	 *
	 * @param friend
	 * @return friend
	 */
	@Override
	public Friend sayHiPost(Friend friend) {
		return null;
	}

}
