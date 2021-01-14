package com.example.springcloud.controller;

import com.example.springcloud.service.feign.FeignService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.example.springcloud.model.Friend;

import lombok.extern.slf4j.Slf4j;

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
        return "This is " + port;
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
}
