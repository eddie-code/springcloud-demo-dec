package com.example.springcloud.entity;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.entity
 * @ClassName ErrorCode
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 16:16
 * @modified by
 */
public class AuthResponseCode {

	public static final Long SUCCESS = 1L;

	public static final Long INCORRECT_PWD = 1000L;

	public static final Long USER_NOT_FOUND = 1001L;

	public static final Long INVALID_TOKEN = 1002L;

}
