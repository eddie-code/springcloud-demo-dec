package com.example.springcloud.service;

import com.example.springcloud.hystrix.Fallback;
import com.example.springcloud.service.feign.FeignService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service
 * @ClassName MyService
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-18 10:28
 * @modified by
 */
@FeignClient(value = "feign-client", fallback = Fallback.class)
public interface MyService extends FeignService {
}
