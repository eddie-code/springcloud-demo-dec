package com.example.springcloud;

import com.example.springcloud.service.MyService;
import feign.Feign;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName HystrixFallbackApplicationTest
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-18 14:30
 * @modified by
 */
@DisplayName("测试用例")
class HystrixFallbackApplicationTest {

    /**
     * Java单元测试之JUnit 5快速上手
     * https://www.cnblogs.com/one12138/p/11536492.html
     */

    /**
     * hystrix/hystrix-fallback/src/main/resources/application.yml
     * 方式二：如果传入参数比较复杂，可以通过main方法拿到Key
     */
    @Test
    void testFirstTest() throws NoSuchMethodException {
        System.out.println("获取配置 hystrix 针对方法超时所用到key: " + Feign.configKey(MyService.class,
                MyService.class.getMethod("retry", int.class)));
    }

}