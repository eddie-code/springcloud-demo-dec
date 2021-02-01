package com.example.springcloud.controller;

import com.example.springcloud.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName GatewayController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 10:04
 * @modified by
 */
@Slf4j
@RestController
@RequestMapping("gateway")
public class GatewayController {

	private static final Map<Long, Product> items = new ConcurrentHashMap<>();

	@ResponseBody
	@RequestMapping(value = "details", method = RequestMethod.GET)
	public Product get(@RequestParam("pid") Long pid) {
		if (!items.containsKey(pid)) {
			Product product = Product.builder()
                    .productId(pid)
                    .description("iphone 12 pro")
                    .stock(100L)
                    .build();
			items.putIfAbsent(pid, product);
		}
		return items.get(pid);
	}

    @RequestMapping(value = "placeOrder", method = RequestMethod.POST)
	public String buy(@RequestParam("pid") Long pid) {
		Product product = items.get(pid);

		if (product == null) {
			return "产品没有找到!";
		} else if (product.getStock() <= 0L) {
			return "售罄";
		}

		synchronized (product) {
			if (product.getStock() <= 0L) {
				return "售罄";
			}
			product.setStock(product.getStock() - 1);
		}
		return "下单";
	}

}
