package com.example.springcloud.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.model
 * @ClassName Product
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 10:06
 * @modified by
 */
@Data
@Builder
public class Product {

	/**
	 * 商品id
	 */
	private Long productId;

	/**
	 * 商品描述
	 */
	private String description;

	/**
	 * 库存
	 */
	private Long stock;

}
