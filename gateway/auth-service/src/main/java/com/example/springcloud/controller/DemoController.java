package com.example.springcloud.controller;

import com.example.springcloud.service.JwtService;
import com.example.springcloud.entity.Account;
import com.example.springcloud.entity.AuthResponse;
import com.example.springcloud.entity.AuthResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author eddie.lee
 * @ProjectName springcloud-demo-dec
 * @Package com.example.springcloud.controller
 * @ClassName DemoController
 * @blog blog.eddilee.cn
 * @description
 * @date created in 2021-02-01 21:56
 * @modified by
 */
@Slf4j
@RestController
public class DemoController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成token
     * @param username str
     * @param password str
     * @return AuthResponse
     */
    @PostMapping("/login")
    @ResponseBody
    public AuthResponse login(@RequestParam String username,
                              @RequestParam String password) {

        Account account = Account.builder()
                .username(username)
                .build();

        // TODO 验证username + password, 正常会需要加盐, 查询数据库是否存在该用户之类的
        // 1-生成token
        String token = jwtService.token(account);
        account.setToken(token);
        // 这里保存拿到新token的key
        account.setRefreshToken(UUID.randomUUID().toString());

        // 保存token, 用于刷新token, 单机节点可以声明一个Map
        redisTemplate.opsForValue().set(account.getRefreshToken(), account);

        // 2-返回token
        return AuthResponse.builder()
                .account(account)
                .code(AuthResponseCode.SUCCESS)
                .build();
    }

    /**
     * 刷新token
     * @param refreshToken str
     * @return AuthResponse
     */
    @PostMapping("/refresh")
    @ResponseBody
    public AuthResponse refresh(@RequestParam String refreshToken) {
        // 当使用redisTemplate保存对象时,对象必须是一个可被序列化的对象
        Account account = (Account) redisTemplate.opsForValue().get(refreshToken);
        if (account == null) {
            return AuthResponse.builder()
                    .code(AuthResponseCode.USER_NOT_FOUND)
                    .build();
        }

        String jwt = jwtService.token(account);
        account.setToken(jwt);
        // 更新新的refreshToke
        account.setRefreshToken(UUID.randomUUID().toString());
        // 将原来的删除
        redisTemplate.delete(refreshToken);
        // 添加新的token
        redisTemplate.opsForValue().set(account.getRefreshToken(), account);

        return AuthResponse.builder()
                .account(account)
                .code(AuthResponseCode.SUCCESS)
                .build();
    }

    @GetMapping("/verify")
    public AuthResponse verify(@RequestParam String token,
                               @RequestParam String username) {

        boolean success = jwtService.verify(token, username);

        return AuthResponse.builder()
                // TODO 此处最好用invalid token之类的错误信息
                .code(success ? AuthResponseCode.SUCCESS : AuthResponseCode.INVALID_TOKEN)
                .build();
    }

}
