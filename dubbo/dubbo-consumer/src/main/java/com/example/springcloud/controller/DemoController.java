package com.example.springcloud.controller;

import com.example.springcloud.Product;
import com.example.springcloud.service.IDubboService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-03-24 11:42
 * @modified by
 */
@RestController
public class DemoController {

    /**
     * 负载均衡策略: 轮询调度
     */
    @DubboReference(loadbalance = "roundrobin")
    private IDubboService dubboService;

	@PostMapping("/publish")
	public Product publish(@RequestParam String name) {
        return dubboService.publish(Product.builder()
                .name(name)
                .build()
        );
	}

}
