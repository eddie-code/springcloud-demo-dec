package com.example.springcloud.config;

import com.example.springcloud.rules.MyRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.config
 * @ClassName RibbonConfiguration
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-01-11 15:33
 * @modified by
 */
@Configuration
//@RibbonClient(name = "eureka-client", configuration = com.netflix.loadbalancer.RandomRule.class)
@RibbonClient(name = "eureka-client", configuration = MyRule.class)
public class RibbonConfiguration {

    /**
     * 1、默认是顺序请求
     * 2. RandomRule() 打乱顺序的
     */

//	@Bean
//	public IRule defaultLBStrategy() {
//		return new RandomRule(); //这里配置策略，和配置文件对应
//	}

}
