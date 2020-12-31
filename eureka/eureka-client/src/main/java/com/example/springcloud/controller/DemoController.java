package com.example.springcloud.controller;

import com.example.springcloud.model.Friend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
public class DemoController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/sayHi")
    public String sayHi() {
        return "This is " + port;
    }

    @PostMapping("/sayHi")
    public Friend sayHiPost(@RequestBody Friend friend) {
        log.info("You are {}", friend.getName());
        friend.setPort(port);
        return friend;
    }

}
