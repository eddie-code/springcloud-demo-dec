package com.example.springcloud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.entity
 * @ClassName Account
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 16:14
 * @modified by
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String username;

    private String token;

    private String refreshToken;

}
