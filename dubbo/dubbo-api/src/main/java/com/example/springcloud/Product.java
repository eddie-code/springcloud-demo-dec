package com.example.springcloud;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud
 * @ClassName Product
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-03-23 16:03
 * @modified by
 */
@Data
public class Product implements Serializable {

    /**
     * dubbo的实体需要添加序列化或反序列化, 不然会报错的
     */
    private static final long serialVersionUID = -96420682L;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

}
