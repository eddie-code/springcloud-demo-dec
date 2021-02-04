package com.example.springcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.entity
 * @ClassName Account
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 16:14
 * @modified by
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

	private String username;

	private String token;

	/**
	 * 当token接近失效的时候可以用refreshToken生成一个新的token
	 */
	private String refreshToken;

}