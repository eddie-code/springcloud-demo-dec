package com.example.springcloud.service.impl;

import com.example.springcloud.model.Friend;
import com.example.springcloud.service.MyService;
import com.example.springcloud.service.feign.FeignService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service.impl
 * @ClassName RequestCacheService
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-18 15:59
 * @modified by
 */
@Slf4j
@Service
public class RequestCacheService {

     // 使用这个引入会启动报错的， 使用 MyService 就可以
//    @Autowired
//    private FeignService service;

    @Autowired
    private MyService service;

    @CacheResult
    @HystrixCommand(commandKey = "cacheKey")
    public Friend requestCache(@CacheKey String name) {
        log.info("request cache = [{}]",name);
        Friend friend = new Friend();
        friend.setName(name);
        friend = service.sayHiPost(friend);
        log.info("after request cache = [{}]",name);
        return friend;
    }

}
