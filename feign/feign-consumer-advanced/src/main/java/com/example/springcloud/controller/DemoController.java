package com.example.springcloud.controller;

import com.example.springcloud.model.Friend;
import com.example.springcloud.service.feign.FeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @date created in 2021-01-14 21:55
 * @modified by
 */
@Slf4j
@RestController
public class DemoController {

    @Autowired
    private FeignService feignService;


    @GetMapping("/sayHi")
    public String sayHi() {
        return feignService.sayHi();
    }

    @PostMapping("/sayHi")
    public Friend sayHiPost() {
        Friend friend = new Friend();
        friend.setName("test");
        return feignService.sayHiPost(friend);
    }

    @GetMapping("/retry")
    public String retry(Integer timeout) {
        return feignService.retry(timeout);
    }


}
