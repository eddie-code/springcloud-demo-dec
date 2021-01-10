package com.example.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-10 22:47
 * @modified by
 */
@RestController
public class DemoController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/sayHi")
    public String sayHi() {
        return restTemplate.getForObject("http://eureka-client/sayHi", String.class);
    }

}
