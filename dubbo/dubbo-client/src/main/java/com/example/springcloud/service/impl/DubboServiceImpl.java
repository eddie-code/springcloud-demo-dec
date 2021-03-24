package com.example.springcloud.service.impl;

import com.example.springcloud.Product;
import com.example.springcloud.service.IDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService; // 2.7.9 版本 更新的 https://gitee.com/nilera/dubbo/commit/f798140392a32b562fd159fd1762efa628d3304f
//import org.springframework.stereotype.Service;  // 2.7.3 版本还可以使用

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.service.impl
 * @ClassName DubboServiceImpl
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-03-24 10:06
 * @modified by
 */
@Slf4j
@DubboService
public class DubboServiceImpl implements IDubboService {

    /**
     * 发布商品
     *
     * @param product
     * @return
     */
    @Override
    public Product publish(Product product) {
        log.info("Publish Product：[{}]",product.getName());
        return product;
    }

}
