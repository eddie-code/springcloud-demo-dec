package com.example.springcloud.service;

import com.example.springcloud.Product;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service
 * @ClassName IdubboService
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-03-23 16:07
 * @modified by
 */
public interface IDubboService {

	/**
	 * 发布商品
	 * 
	 * @param product
	 * @return
	 */
	Product publish(Product product);

}
